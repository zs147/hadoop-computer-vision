package edu.vt.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.googlecode.javacpp.BytePointer;

import edu.vt.io.Image;
import edu.vt.io.WindowInfo;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

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
	int overlapPercent = 0;
	int sizePercent = 0;
	int overlapPixel = 0;
	int sizePixel = 0;
	
	// splits based on configuration parameters
	int totalXSplits = 0;
	int totalYSplits = 0;
	
	// Current split
	int currentSplit = 0;
	
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

		return (float)(totalXSplits * totalYSplits) / (float)currentSplit;
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		// Get file split
		FileSplit split = (FileSplit) genericSplit;
		Configuration conf = context.getConfiguration();
		
		// Read configuration parameters
		getConfig(conf);
		
		// Open the file
		Path file = split.getPath();
		FileSystem fs = file.getFileSystem(conf);
		FSDataInputStream fileIn = fs.open(split.getPath());
		
		// Read file and decode image
		byte [] b = new byte[fileIn.available()];
		fileIn.readFully(b);
		image = new Image(cvDecodeImage(cvMat(1, b.length, CV_8UC1, new BytePointer(b)))); 
		
		// Get filename to use as key
		fileName = split.getPath().toString();
		
		// Calculate the number of splits
		CalculateTotalSplits();
		currentSplit = 0;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (currentSplit < (totalXSplits * totalYSplits) && fileName != null) {

			key = new Text(fileName);
			
			if(totalXSplits * totalYSplits == 1){
				value = image;
			}else{
				value = getSubWindow();
			}

			currentSplit += 1;
			return true;
		}

		return false;
	}
	
	private Image getSubWindow(){
		CvRect window = getWindow();
		 
		// sets the ROI
		IplImage img1 = image.getImage();
		cvSetImageROI(img1, window);
		 
		// create destination image 
		IplImage img2 = cvCreateImage(cvGetSize(img1), img1.depth(), img1.nChannels());
		 
		// copy sub-image
		cvCopy(img1, img2, null);
		 
		// reset the ROI
		cvResetImageROI(img1);
		
		return new Image(img2, new WindowInfo(window.x(), window.y(), img1.height(), img1.width()));
	}
	
	private void getConfig(Configuration conf){
		// Ensure that percentage is between 0 and 100
		overlapPercent = conf.getInt("mapreduce.imagerecordreader.windowoverlappercent", 0);
		if(overlapPercent < 0 || overlapPercent > 100){
			overlapPercent = 0;
		}
		
		// Ensure that percentage is between 0 and 100
		sizePercent = conf.getInt("mapreduce.imagerecordreader.windowsizepercent", 100);
		if(sizePercent < 0 || sizePercent > 100){
			sizePercent = 100;
		}
		
		// Ensure that value is not negative
		overlapPixel = conf.getInt("mapreduce.imagerecordreader.windowoverlappixel", 0);
		if(overlapPixel < 0){
			overlapPixel = 0;
		}
		
		// Ensure that value is not negative
		sizePixel = conf.getInt("mapreduce.imagerecordreader.windowsizepixel", Integer.MAX_VALUE);
		if(sizePixel < 0){
			sizePixel = 0;
		}
		
		byPixel = conf.getBoolean("mapreduce.imagerecordreader.windowbypixel", false);
	}
	
	private CvRect getWindow(){
		if(byPixel){
			return getWindowByPixel();
		}else{
			return getWindowByPct();
		}
	}
	
	private void CalculateTotalSplits(){
		if(byPixel){
			totalXSplits = (int)Math.ceil(image.getWidth() / Math.min(sizePixel, image.getWidth()));
			totalYSplits = (int)Math.ceil(image.getHeight() / Math.min(sizePixel, image.getHeight()));
		}else{
			totalXSplits = totalYSplits = (int)Math.ceil(100.0 / sizePercent);
		}
	}
	
	private CvRect getWindowByPct(){
		int x = currentSplit % totalXSplits;
		int y = currentSplit / totalYSplits;
		
		int width = (int) Math.ceil(image.getWidth() * (sizePercent / 100.0));
		int height = (int) Math.ceil(image.getHeight() * (sizePercent / 100.0));
		
		return cvRect(x * width, y * height, width, height);
	}
	
	private CvRect getWindowByPixel(){
		return cvRect(0,0,1,1);
	}
}
