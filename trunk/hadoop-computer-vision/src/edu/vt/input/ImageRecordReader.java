package edu.vt.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import static com.googlecode.javacv.jna.cxcore.v21.*;
import static com.googlecode.javacv.jna.highgui.v21.*;

public class ImageRecordReader extends RecordReader<LongWritable, Image>{

	private static final Log LOG = LogFactory.getLog(ImageRecordReader.class);
	
	private Image value = null;
	
	@Override
	public void close() throws IOException {
		
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {

		return null;
	}

	@Override
	public Image getCurrentValue() throws IOException, InterruptedException {

		return null;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {

		return 0;
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {

		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// load image
		IplImage img1 = null;
		
		// Calculate new ROI
		
		// sets the Region of Interest
		cvSetImageROI(img1, cvRect(10, 15, 150, 250));
		
		// create destination image
		IplImage img2 = cvCreateImage(cvGetSize(img1), img1.depth, img1.nChannels);
		
		// copy sub-image
		cvCopy(img1, img2, null);
		
		// reset the Region of Interest
		cvResetImageROI(img1);
		
		return false;
	}

}
