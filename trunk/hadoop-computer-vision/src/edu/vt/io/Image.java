package edu.vt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import com.googlecode.javacpp.BytePointer;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class Image implements Writable {

	private static final Log LOG = LogFactory.getLog(Image.class);

	// IPL image
	private IplImage image = null;
	private WindowInfo window = null;
	
	// Create Image from IplImage
	public Image(IplImage image){
		this.image = image;
		this.window = null;
	}

	// Create Image from IplImage and IplROI
	public Image(IplImage image, WindowInfo window){
		this.image = image;
		this.window = window;
	}
	
	public IplImage getImage(){
		return image;
	}
	
	// get window where image came from
	public WindowInfo getWindow(){
		return window;
	}
	
	// set window image came from
	public void setWindow(WindowInfo window){
		this.window = window;
	}
	
	// Pixel depth in bits
	// PL_DEPTH_8U - Unsigned 8-bit integer
	// IPL_DEPTH_8S - Signed 8-bit integer
	// IPL_DEPTH_16U - Unsigned 16-bit integer
	// IPL_DEPTH_16S - Signed 16-bit integer
	// IPL_DEPTH_32S - Signed 32-bit integer
	// IPL_DEPTH_32F - Single-precision floating point
	// IPL_DEPTH_64F - Double-precision floating point
	public int getDepth(){
		return image.depth();
	}
	
	// Number of channels.
	public int getNumChannel(){
		return image.nChannels();
	}
	
	// Image height in pixels
	public int getHeight(){
		return image.height();
	}
	
	// Image width in pixels
	public int getWidth(){
		return image.width();
	}
	
	// The size of an aligned image row, in bytes
	public int getWidthStep(){
		return image.widthStep();
	}
	
	// Image data size in bytes.
	public int getImageSize() {
		return image.imageSize();
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// Read image information
		int height = WritableUtils.readVInt(in);
		int width = WritableUtils.readVInt(in);
		int depth = WritableUtils.readVInt(in);
		int nChannels = WritableUtils.readVInt(in);
		int imageSize = WritableUtils.readVInt(in);
		
		//Read window information
		int windowXOffest = WritableUtils.readVInt(in);
		int windowYOffest = WritableUtils.readVInt(in);
		int windowHeight = WritableUtils.readVInt(in);
		int windowWidth = WritableUtils.readVInt(in);
		window = new WindowInfo(windowXOffest, windowYOffest, windowHeight, windowWidth);
		
		// Read image bytes
		byte [] bytes = new byte[imageSize];
		in.readFully(bytes, 0, imageSize);

		image = cvCreateImage(cvSize(width, height), depth, nChannels);
		image.imageData(new BytePointer(bytes));
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// Write image information
		WritableUtils.writeVInt(out, image.height());
		WritableUtils.writeVInt(out, image.width());
		WritableUtils.writeVInt(out, image.depth());
		WritableUtils.writeVInt(out, image.nChannels());
		WritableUtils.writeVInt(out, image.imageSize());
		
		// Write window information
		WritableUtils.writeVInt(out, window.getXOffset());
		WritableUtils.writeVInt(out, window.getYOffset());
		WritableUtils.writeVInt(out, window.getHeight());
		WritableUtils.writeVInt(out, window.getWidth());
		
		// Write image bytes
		byte [] bytes = image.imageData().getStringBytes();
		out.write(bytes, 0, image.imageSize());
	}

}
