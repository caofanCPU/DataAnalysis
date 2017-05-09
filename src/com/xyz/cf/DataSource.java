package com.xyz.cf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author CY_XYZ
 *
 */
public class DataSource {
	private File fileName = null;
	public DataSource(File fileName) {
		fileCheck(fileName);
		this.setFileName(fileName);
	}
	public DataSource() {
		
	}
	
	public File getFileName() {
		return this.fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 正则匹配处理，获取数据
	 * 1.读取文本内容至StringBuilder缓存容器
	 * 2.正则匹配：跳过字符串首尾空白字符
	 * 			所有空白字符'\\s+'全部替换为','
	 * 			正则替换规则：不是小数点号'.'逗号','数字'\\d'空格'\x0B'换行'\n'和负号'-'的其他字符，替换为空
	 * 			注意：负号'-'放在最后面，否则会引起类似a-z歧义错误
	 * 3.正则分割：以至少一个空格'\x0B+'为基准，进行分割
	 * 4.
	 */
	
	public String[] dataOpration() {
		StringBuilder sb = readFile(this.fileName);
		String[] line = sb.toString().
						   trim().
						   replaceAll("\\s+", ",").
						   replaceAll("[^.,\\d-]", "").
						   split(",");
		//sop(line.length);
		if (0 != (line.length % 2)) {
			throw new RuntimeException("原始坐标数据缺失，请检查！");
		}
		return line;
	}
	
	public StringBuilder readFile(File dataFile) {
		if (!dataFile.exists()) {
			throw new RuntimeException("原始数据文件路径错误或文件不存在！");
		}
		BufferedReader bufr = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			bufr = new BufferedReader(new FileReader(dataFile));
			while (null != (line = bufr.readLine())) {
				sb.append(line + ",");
			}
		}
		catch (IOException ioe) {
			sop(ioe.toString() + "\n读取文件失败！");
		}
		finally {
			try {
				if (null != bufr) {
					bufr.close();
				}
			}
			catch (IOException ioe) {
				sop(ioe.toString() + "\n读取文件关闭失败！");
			}
		}
		return sb;
		
	}
	
	public void write2File(File resultFile, StringBuilder sb) {
		if (!resultFile.exists()) {
			try {
				resultFile.createNewFile();
			}
			catch(IOException ioe) {
				//待处理
				throw new RuntimeException("目标文件不存在且创建失败！");
			}
		}
		BufferedWriter bufw = null;
		try {
			//下面两句都可能产生异常
			bufw = new BufferedWriter(new FileWriter(resultFile));
			//写入流进入缓冲区，等待刷新操作出缓冲区
			bufw.write(sb.toString());
			//刷新写缓冲流，写缓冲流才真正写入文件
			bufw.flush();
		}
		catch(IOException ioe) {
			//待处理
			throw new RuntimeException("目标文件写入失败！");
		}
		finally {
			try {
				if (null != bufw) {
					//bufw.close()执行前会限制性bufw.flush()
					//当为了不再网络编程中犯错，强烈建议bufw.write();后紧跟bufw.flush()
					bufw.close();	
				}
			}
			catch(IOException ioe) {
				//待处理
				throw new RuntimeException("目标文件写入关闭失败！");
			}
		}
	}
	
	public void fileCheck(File fileName) {
		if (fileName.exists() && fileName.isFile()) {
		} else {
			throw new RuntimeException("FileNotFound Error!");
		}
	}
	
	public static void sop(Object obj) {
		System.out.println(obj);
	}
}
