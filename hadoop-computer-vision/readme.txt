Running Hadoop: 
=============== 

../hadoop-0.21.0/bin/hadoop fs -put input input
../hadoop-0.21.0/bin/hadoop fs -rmr output
../hadoop-0.21.0/bin/hadoop jar hadoop-computer-vision.jar edu.vt.example.Histogram input output




OpenCV Installation: 
==================== 

For the list of prerequisites and more detailed installation guide, please, see http://opencv.willowgarage.com/wiki/InstallGuide 

The procedure at glance: 
------------------------
1. tar -xjf OpenCV-2.1.0.tar.bz2 
2. mkdir opencv.build 
3. cd opencv.build 
4. cmake ../OpenCV-2.1.0
5. make -j 2 
6. sudo make install 
7. sudo ldconfig 
