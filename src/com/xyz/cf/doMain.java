package com.xyz.cf;

import java.io.File;
import java.util.TreeMap;

public class doMain {
	
	public static void main(String[] args) {
		String parStr = System.getProperty("user.dir");
		//System.out.println(parStr);
		new DataMatrix(dataOption(parStr));		
	}
	
	public static TreeMap<Integer, Coordinate> dataOption(String parStr) {
		String[] line = new DataSource(new File(parStr, "ͼֽ����.txt"))
													  .dataOpration();
		TreeMap<Integer, Coordinate> tm = new TreeMap<Integer, Coordinate>(); 
		/**
		 * ������ȡ��ʽ��XYXYXY...�������������飬ѭ������i += 2
		 */
		for (int i = 0; i < line.length; i += 2) {
			tm.put((i/2 + 1), new Coordinate(Double.parseDouble(line[i]),
										 	 Double.parseDouble(line[i+1])));
		}
		return tm;
	}
}
