package edu.vt.example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static com.googlecode.javacv.jna.cxcore.v21.*;
import static com.googlecode.javacv.jna.cv.v21.*;

import edu.vt.io.Image;
import edu.vt.io.LongArrayWritable;
import edu.vt.input.ImageInputFormat;
import edu.vt.output.ImageOutputFormat;

public class Resize extends Configured implements Tool {
	public static class Map extends
			Mapper<Text, Image, Text, Image> {
		private final static LongWritable one = new LongWritable(1);

		@Override
		public void map(Text key, Image value, Context context)
				throws IOException, InterruptedException {

			// Resize the image
			IplImage im1 = value.getImage();
			CvSize newSize = new CvSize((int)Math.round(0.5 * im1.width),(int)Math.round(0.5 * im1.height));
			IplImage im2 = cvCreateImage(newSize.byValue(), im1.depth, im1.nChannels);

			cvResize(im1,im2,CV_INTER_LINEAR);

			context.write(key, new Image(im2));
		}
	}

	public static class Reduce extends
			Reducer<Text, Image, Text, Image> {

		@Override
		public void reduce(Text key, Iterable<Image> values,
				Context context) throws IOException, InterruptedException {

			// Sum the parts
			Iterator<Image> it = values.iterator();
			Image part = null;
			if (it.hasNext()) {
				part = (Image) it.next();
			}

			context.write(key, part);
		}
	}

	public int run(String[] args) throws Exception {
		Job job = new Job(getConf());
		job.setJarByClass(Histogram.class);

		// Specify various job-specific parameters
		job.setJobName("Histogram");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Image.class);

		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(ImageOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Histogram(), args);
		System.exit(res);
	}
}
