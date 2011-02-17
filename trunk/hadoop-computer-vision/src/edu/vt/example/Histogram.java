package edu.vt.example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static com.googlecode.javacv.jna.cxcore.v21.*;
import static com.googlecode.javacv.jna.cv.v21.*;

import edu.vt.io.Image;
import edu.vt.input.ImageInputFormat;

public class Histogram extends Configured implements Tool {
	public static class Map extends
			Mapper<Text, Image, Text, LongWritable> {
		private final static LongWritable one = new LongWritable(1);

		@Override
		public void map(Text key, Image value, Context context)
				throws IOException, InterruptedException {
			
			// Convert to gray scale image
			IplImage im1 = value.getImage();
			IplImage im2 = cvCreateImage(cvSize(im1.width,im1.height), IPL_DEPTH_8U, 1);
			cvCvtColor(im1, im2, CV_BGR2GRAY);
			
			// Compute histogram
			byte [] bytes = im2.imageData.getByteArray(0, im2.imageSize);
			for(int i = 0; i < bytes.length; i++){
				context.write(key, one);
			}
		}
	}

	public static class Reduce extends
			Reducer<Text, LongWritable, Text, LongWritable> {
		
		@Override
		public void reduce(Text key, Iterable<LongWritable> values,
				Context context) throws IOException, InterruptedException {
			long sum = 0;
			
			Iterator<LongWritable> it = values.iterator();
			while (it.hasNext()) {
				sum += it.next().get();
			}
			context.write(key, new LongWritable(sum));
		}
	}

	public int run(String[] args) throws Exception {
		Job job = new Job(getConf());
		job.setJarByClass(Histogram.class);

		// Specify various job-specific parameters 
		job.setJobName("Histogram");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Histogram(), args);
		System.exit(res);
	}
}
