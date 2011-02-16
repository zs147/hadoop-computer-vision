Running hadoop: 
=============== 

../hadoop-0.21.0/bin/hadoop fs -put input input
../hadoop-0.21.0/bin/hadoop fs -rmr output
../hadoop-0.21.0/bin/hadoop jar hadoop-computer-vision.jar edu.vt.example.Histogram input output




OpenCV Installation notes: 
========================== 

For the list of prerequisities and more detailed installation guide, please, see http://opencv.willowgarage.com/wiki/InstallGuide 

Here is the procedure at glance: 
-------------------------------- 
1. tar -xjf OpenCV-2.1.0.tar.bz2 
2. mkdir opencv.build 
3. cd opencv.build 
4. cmake [<extra_options>] ../OpenCV-2.1.0 # CMake-way 
5. make -j 2 
6. sudo make install 
7. sudo ldconfig # linux only 
