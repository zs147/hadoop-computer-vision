//============================================================================
// Name        : faceDetect.cpp
// Author      : Eric Frohnhoefer
// Version     :
// Description : Face detection in C++
//============================================================================

#include <sys/types.h>
#include <dirent.h>
#include <errno.h>
#include <iostream>
#include <string>
#include <vector>
#include <iomanip>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;

// Create memory for calculations
static CvMemStorage* storage = 0;

// Create a new Haar classifier
static CvHaarClassifierCascade* cascade = 0;

string classifierName[3] = {
		"./classifiers/haarcascade_frontalface_alt.xml",
		"./classifiers/haarcascade_frontalface_alt2.xml",
		"./classifiers/haarcascade_profileface.xml" };

// Function prototypes for detecting an object and draw a box around object
void load_detect_draw_and_save(string filename, string inputDir, string outputDir);
void detect_and_draw(IplImage* image);

int getDir (string dir, vector<string> &files);

int main(int argc, char** argv) {
	string inputDir;
	string outputDir;

	if (argc == 3) {
		inputDir = argv[1];
		outputDir = argv[2];
	} else {
		cerr << "USAGE: faceDetect input_dir output_dir classifier";
		return -1;
	}

	// Load the HaarClassifierCascade
	cascade = (CvHaarClassifierCascade*) cvLoad(classifierName[0].c_str(), 0,
			0, 0);

	if (!cascade) {
		cerr << "ERROR: Could not load classifier cascade\n";
		return -1;
	}

	// Allocate the memory storage
	storage = cvCreateMemStorage(0);

	int64 t = cvGetTickCount();

	vector<string> files = vector<string>();
	getDir(inputDir, files);

	for(unsigned int i = 0; i < files.size(); i++){
		cout << "Processing: " << files.at(i) << endl;
		load_detect_draw_and_save(files.at(i),inputDir,outputDir);
	}

	t = cvGetTickCount() - t;
	cout << "Time: " << setprecision(4) << setiosflags(ios::fixed) << t/cvGetTickFrequency()/1000000 << "sec" << endl;

	return 0;
}

void load_detect_draw_and_save(string filename, string inputDir, string outputDir) {
	string infile = inputDir + "/" + filename;
	string outfile = outputDir + "/" + filename;

	IplImage* image = cvLoadImage(infile.c_str(), 1);
	if (image) {
		// Detect and draw the face from the image
		detect_and_draw(image);

		// Save the image to output directory
		cvSaveImage(outfile.c_str(), image);

		// Release the memory
		cvReleaseImage(&image);
	}
}

void detect_and_draw(IplImage* img) {
	// Create two points to represent the face locations
	CvPoint pt1, pt2;

	// Clear the memory storage which was used before
	cvClearMemStorage(storage);

	// Find whether the cascade is loaded, to find the faces. If yes, then:
	if (cascade) {

		// There can be more than one face in an image. So create a growable sequence of faces.
		// Detect the objects and store them in the sequence
		CvSeq* faces = cvHaarDetectObjects(img, cascade, storage, 1.1, 3,
				CV_HAAR_DO_CANNY_PRUNING);

		// Loop the number of faces found.
		for (int i = 0; i < (faces ? faces->total : 0); i++) {
			// Create a new rectangle for drawing the face
			CvRect* r = (CvRect*) cvGetSeqElem(faces, i);

			// Find the dimensions of the face,and scale it if necessary
			pt1.x = r->x;
			pt2.x = (r->x + r->width);
			pt1.y = r->y;
			pt2.y = (r->y + r->height);

			// Draw the rectangle in the input image
			cvRectangle(img, pt1, pt2, CV_RGB(255,0,0), 1, 8, 0);
		}
	}
}

int getDir (string dir, vector<string> &files)
{
    DIR *dp;
    struct dirent *dirp;
    if((dp  = opendir(dir.c_str())) == NULL) {
        cerr << "Error(" << errno << ") opening " << dir << endl;
        return errno;
    }

    while ((dirp = readdir(dp)) != NULL) {
    	string str = dirp->d_name;
    	if(str != "." && str != ".."){
    		files.push_back(str);
    	}
    }
    closedir(dp);
    return 0;
}

