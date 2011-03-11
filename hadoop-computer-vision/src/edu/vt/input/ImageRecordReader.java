package edu.vt.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.vt.io.Image;

import static com.googlecode.javacv.jna.cxcore.v21.*;
import static com.googlecode.javacv.jna.highgui.v21.*;

public class ImageRecordReader extends RecordReader<Text, Image> {

	private static final Log LOG = LogFactory.getLog(ImageRecordReader.class);

	private float status;
	
	// Image information
	private String fileName = null;
	private Image image = null;
	
	// Key/Value pair
	private Text key = null;
	private Image value = null;

	// Configuration parameters
	// By default use percentage for splitting
	boolean byPixel = false;
	float overlapPercent = 0;
	float sizePercent = 0;
	int overlapPixel = 0;
	int sizePixel = 0;
	
	@Override
	public void close() throws IOException {

	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {

		return key;
	}

	@Override
	public Image getCurrentValue() throws IOException, InterruptedException {

		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {

		return status;
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		FileSplit split = (FileSplit) genericSplit;
		Configuration job = context.getConfiguration();
		Path file = split.getPath();
		FileSystem fs = file.getFileSystem(job);

		// Can only read from local file system
		if(fs instanceof LocalFileSystem){
			fileName = ((LocalFileSystem) fs).pathToFile(file).getAbsolutePath();
		}
		
		// Read configuration parameters
		overlapPercent = job.getFloat("mapreduce.imagerecordreader.windowoverlappercent", 0);
		sizePercent = job.getFloat("mapreduce.imagerecordreader.windowsizepercent", 100);
		overlapPixel = job.getInt("mapreduce.imagerecordreader.windowoverlappixel", 0);
		sizePixel = job.getInt("mapreduce.imagerecordreader.windowsizepixel", Integer.MAX_VALUE);
		byPixel = job.getBoolean("mapreduce.imagerecordreader.windowbypixel", false);
		
		// Open the file
		image = new Image(cvLoadImage(fileName, 1));
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		/*
		// Calculate new ROI
		CvRect window = getWindow();
		 
		// sets the ROI
		IplImage img1 = image.getImage();
		cvSetImageROI(img1, window.byValue());
		 
		// create destination image 
		IplImage img2 = cvCreateImage(cvGetSize(img1), img1.depth, img1.nChannels);
		 
		// copy sub-image
		cvCopy(img1, img2, null);
		 
		// reset the ROI
		cvResetImageROI(img1);
		
		key = new Text(fileName);
		value = new Image(img2, new WindowInfo(img1.width, img.height, window.x, window.y));
		*/

		if (status != 100 && fileName != null) {

			key = new Text(fileName);
			value = image;

			status = 100;
			return true;
		}

		return false;
	}
	
	private CvRect getWindow(){
		if(byPixel){
			return getWindowByPixel();
		}else{
			return getWindowByPct();
		}
	}
	
	private CvRect getWindowByPct(){
		int width = Math.round(image.getWidth() * overlapPercent);
		int height = Math.round(image.getHeight() * overlapPercent);
		return new CvRect();
	}
	
	private CvRect getWindowByPixel(){
		return new CvRect();
	}
}
