package edu.vt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import static com.googlecode.javacv.jna.cxcore.v21.*;

public class Image implements Writable {

	private static final Log LOG = LogFactory.getLog(Image.class);

	// IPL image
	private IplImage image = null;
	private IplROI window = null;
	
	// Create Image from IplImage
	public Image(IplImage image){
		this.image = image;
		this.window = null;
	}

	// Create Image from IplImage and IplROI
	public Image(IplImage image, IplROI window){
		this.image = image;
		this.window = window;
	}
	
	public IplImage getImage(){
		return image;
	}
	
	// get window where image came from
	public IplROI getWindow(){
		return window;
	}
	
	// set window image came from
	public void setWindow(IplROI window){
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
		return image.depth;
	}
	
	// Number of channels.
	public int getNumChannel(){
		return image.nChannels;
	}
	
	// Image height in pixels
	public int getHeight(){
		return image.height;
	}
	
	// Image width in pixels
	public int getWidth(){
		return image.width;
	}
	
	// The size of an aligned image row, in bytes
	public int getWidthStep(){
		return image.widthStep;
	}
	
	// Image data size in bytes.
	public int getImageSize() {
		return image.imageSize;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// Read in all the data
		int height = WritableUtils.readVInt(in);
		int width = WritableUtils.readVInt(in);
		int depth = WritableUtils.readVInt(in);
		int nChannels = WritableUtils.readVInt(in);
		int imageSize = WritableUtils.readVInt(in);
		
		byte [] bytes = new byte[imageSize];
		in.readFully(bytes, 0, imageSize);
		
		// Recreate the image
		image = cvCreateImage(cvSize(width, height), depth, nChannels);
		image.imageData.write(0, bytes, 0, imageSize);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeVInt(out, image.height);
		WritableUtils.writeVInt(out, image.width);
		WritableUtils.writeVInt(out, image.depth);
		WritableUtils.writeVInt(out, image.nChannels);
		WritableUtils.writeVInt(out, image.imageSize);
		
		byte [] bytes = image.imageData.getByteArray(0, getImageSize());
		out.write(bytes, 0, image.imageSize);
	}

}
