package edu.vt.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

import static com.googlecode.javacv.jna.cxcore.v21.*;

public class Image extends BinaryComparable implements
		WritableComparable<BinaryComparable> {

	private static final Log LOG = LogFactory.getLog(Image.class);

	// IPL image
	IplImage image = null;
	
	// Create Image from IplImage
	public Image(IplImage image){
		this.image = image;
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
	
	@Override
	public byte[] getBytes() {
		return image.getByteBuffer().array();
	}

	@Override
	public int getLength() {
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
		//in.readFully(bytes, 0, imageSize);
		
		// Recreate the image
		image = cvCreateImage(cvSize(width, height), depth, nChannels);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeVInt(out, image.height);
		WritableUtils.writeVInt(out, image.width);
		WritableUtils.writeVInt(out, image.depth);
		WritableUtils.writeVInt(out, image.nChannels);
		WritableUtils.writeVInt(out, image.imageSize);
		out.write(getBytes(), 0, image.imageSize);
	}

}
