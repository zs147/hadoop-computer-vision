package edu.vt.example;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.vt.input.ImageInputFormat;
import edu.vt.io.Image;
import edu.vt.output.ImageOutputFormat;

public class EqualizeHist2 extends Configured implements Tool {
	public static class Map extends Mapper<Text, Image, Text, Image> {

		@Override
		public void map(Text key, Image value, Context context)
				throws IOException, InterruptedException {

			IplImage src = value.getImage();
			IplImage dest = cvCreateImage(cvSize(src.width(), src.height()),
					src.depth(), src.nChannels());

			CvHistogram hist;
			int[] hist_size = { 256 };
			float[][] ranges = { { 0, 256 } };
			IplImage[] srcArr = {src};

			// Calculate the histogram
			hist = cvCreateHist(1, hist_size, CV_HIST_ARRAY, ranges, 1);
			cvCalcHist(srcArr, hist, 0, null);
			
			// Equalize the histogram
			CvMat lut = cvCreateMat( 1, 256, CV_8UC1 );
			float scale = 255.0f/(src.width() * src.height());
		    int sum = 0;

		    for( int i = 0; i < 256; i++ )
		    {
		        sum += (int)cvGetReal1D(hist.bins(), i);
		        int val = Math.round(sum*scale);
		        cvSetReal2D(lut,0,i,val);
		    }

			// Apply to image
		    cvSetReal2D(lut,0,0,0);
			cvLUT( src, dest, lut );

			context.write(key, new Image(dest,value.getWindow()));
		}
	}

	public static class Reduce extends Reducer<Text, Image, Text, Image> {

		@Override
		public void reduce(Text key, Iterable<Image> values, Context context)
				throws IOException, InterruptedException {

			// Sum the parts
			Iterator<Image> it = values.iterator();
			Image img = null;
			Image part = null;
			while (it.hasNext()) {
				part = (Image) it.next();
				if (img == null) {
					int height = part.getHeight();
					int width = part.getWidth();
					if (part.getWindow().isParentInfoValid()) {
						height = part.getWindow().getParentHeight();
						width = part.getWindow().getParentWidth();
					}
					int depth = part.getDepth();
					int nChannel = part.getNumChannel();
					img = new Image(height, width, depth, nChannel);
				}
				img.insertImage(part);
			}

			context.write(key, img);
		}
	}

	public int run(String[] args) throws Exception {
		// Set various configuration settings
		Configuration conf = getConf();
		conf.setInt("mapreduce.imagerecordreader.windowsizepercent", 100);
		conf.setInt("mapreduce.imagerecordreader.borderPixel", 0);
		conf.setInt("mapreduce.imagerecordreader.iscolor", 0);

		// Create job
		Job job = new Job(conf);

		// Specify various job-specific parameters
		job.setJarByClass(EqualizeHist2.class);
		job.setJobName("EqualizeHist");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Image.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(ImageOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner
				.run(new Configuration(), new EqualizeHist2(), args);
		System.exit(res);
	}
}