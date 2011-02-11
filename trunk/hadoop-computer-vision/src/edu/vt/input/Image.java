package edu.vt.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.io.WritableComparable;

public class Image extends BinaryComparable implements
		WritableComparable<BinaryComparable> {
	
	private static final Log LOG= LogFactory.getLog(Image.class);
	
	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void readFields(DataInput in) throws IOException {

	}

	@Override
	public void write(DataOutput out) throws IOException {

	}

}
