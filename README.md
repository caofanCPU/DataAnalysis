# [DataAnalysis](https://github.com/caofanCPU/DataAnalysis)
[UJMP + mysql connection pools + IO Stream + TreeMap]

## Version 0.8 
***
## Introduction
&ensp;&emsp;&ensp;&emsp;In this [paper](https://github.com/caofanCPU/DataAnalysis/tree/master/doc/弦轴孔定位算法原理.pdf), with the project commissioned units Linhai jinlang hardware plastic factory cooperation, yards from the piano grams actual production processes of constructing a mathematical model of peg hole positioning, determine peg hole positioning deviation problems mainly due to the defects of the peg hole positioning technology, and with high accuracy the string axle hole positioning technology as the goal, research on traditional **rotation and translation algorithm**, **least square algorithm**. Determine the deviations of the traditional rotation and translation method basically can't overcome the measurement error of the measuring instrument, and by selecting the optimal locating peg holes, which can improve the positioning error of the rotation and translation method is proposed. A least square method to determine the overall performance of the minimum deviation, can greatly enhance the positioning accuracy of peg hole positioning technology. The preparation of peg hole positioning **matlab simulation** and **Java programming**, the program features include, peg hole distribution and peg hole deviation data calculation and analysis, the important data document preservation.  
***
## Components
- [regular expression](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/cf/DataSource.java)
- [IO Stream including File read&write](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/cf/DataSource.java)
- [Java Matrix operation](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/cf/DataMatrix.java)
- [GUI plot](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/cf/DataVisualization.java)
- [Database operation by c3p0Utils and mysql connector](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/util/C3P0Util.java)
- [Encapsulating Data to a entity into TreeMap storage](https://github.com/caofanCPU/DataAnalysis/tree/master/src/com/xyz/domain/CoordinateData.java)
***
##Highligths
![ErrorAnalysis](http://i2.muimg.com/588926/89788ed6c927ae95.jpg)  
![DataMatrix](http://i1.piimg.com/588926/ae052d785b911c5f.jpg)  

***
## Remarks
&ensp;&emsp;&ensp;&emsp;Reading the reference document [**Algorithm principle**](https://github.com/caofanCPU/DataAnalysis/tree/master/doc/弦轴孔定位算法原理.pdf) careful to know the algorithm principle and steps of coding before your quick start. If you're all eagerness to see results, just executive [`DataAnalysis.jar`](https://github.com/caofanCPU/DataAnalysis/tree/master/jar/DataAnalysis.jar).
