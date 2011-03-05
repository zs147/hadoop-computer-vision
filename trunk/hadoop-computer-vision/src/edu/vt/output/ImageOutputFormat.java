package edu.vt.output;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.vt.io.Image;

public class ImageOutputFormat extends FileOutputFormat<Text, Image> {

	@Override
	public RecordWriter<Text, Image> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {

		return null;
	}

}
