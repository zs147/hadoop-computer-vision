package edu.vt.example;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.nio.ByteBuffer;

public class JavacvTest {
	public static void main(String[] args) {
		IplImage image = cvLoadImage("test.png", 1);
		byte [] data = image.imageData().getStringBytes();
		
		ByteBuffer bb = image.getByteBuffer();
		
		int cnt = 0;
		while(bb.hasRemaining()){
			bb.get();
			cnt++;
		}
		System.out.println(cnt);
		
		
		System.out.println("image size " + image.imageSize());
		System.out.println("width: " + image.width());
		System.out.println("width step: " + image.widthStep());
		System.out.println("height: " + image.height());
	}
}
