package edu.vt.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

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

		return false;
	}

}
