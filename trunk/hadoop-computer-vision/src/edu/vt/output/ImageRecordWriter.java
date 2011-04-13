package edu.vt.output;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import edu.vt.io.Image;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class ImageRecordWriter extends RecordWriter<Text, Image> {

	private Path outputPath = null;
	private FileSystem fs = null;
	
	ImageRecordWriter(Path outputPath, FileSystem fs){
		this.outputPath = outputPath;
		this.fs = fs;
	}
	
	@Override
	public void close(TaskAttemptContext context) throws IOException,
			InterruptedException {
		
	}

	@Override
	public void write(Text key, Image value) throws IOException,
			InterruptedException {
		
		// An optional 0-terminated list of JPG parameter pairs <param id, value>
		int jpeg_params[] = { CV_IMWRITE_JPEG_QUALITY, 80, 0 };
		
		// Encode image into a single-row matrix of CV_8UC1
		CvMat imageBuffer = cvEncodeImage(".jpeg", value.getImage(), jpeg_params);
		
		// Create output file
		Path filePath = new Path(outputPath, "test.jpg");
		FSDataOutputStream fileStream = fs.create(filePath, false);
		
		// Write the image to file
		ByteBuffer buffer = imageBuffer.getByteBuffer();
		while(buffer.hasRemaining()){
			fileStream.write(buffer.get());
		}
		
		// Close the file stream
		fileStream.close();
	}

}
