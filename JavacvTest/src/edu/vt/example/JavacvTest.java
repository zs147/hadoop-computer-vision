package edu.vt.example;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;
import static com.googlecode.javacv.jna.highgui.*;

public class JavacvTest {
	public static void main(String[] args) {
		IplImage image = cvLoadImage("test.png", 1);
		if (image == null) {
			System.err.println("Could not load image file.");
		} else {
			cvSmooth(image, image, CV_GAUSSIAN, 3, 0, 0, 0);
			// ...
		}
	}
}
