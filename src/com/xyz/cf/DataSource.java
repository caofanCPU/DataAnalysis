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
	 * ����ƥ�䴦����ȡ����
	 * 1.��ȡ�ı�������StringBuilder��������
	 * 2.����ƥ�䣺�����ַ�����β�հ��ַ�
	 * 			���пհ��ַ�'\\s+'ȫ���滻Ϊ','
	 * 			�����滻���򣺲���С�����'.'����','����'\\d'�ո�'\x0B'����'\n'�͸���'-'�������ַ����滻Ϊ��
	 * 			ע�⣺����'-'��������棬�������������a-z�������
	 * 3.����ָ������һ���ո�'\x0B+'Ϊ��׼�����зָ�
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
			throw new RuntimeException("ԭʼ��������ȱʧ�����飡");
		}
		return line;
	}
	
	public StringBuilder readFile(File dataFile) {
		if (!dataFile.exists()) {
			throw new RuntimeException("ԭʼ�����ļ�·��������ļ������ڣ�");
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
			sop(ioe.toString() + "\n��ȡ�ļ�ʧ�ܣ�");
		}
		finally {
			try {
				if (null != bufr) {
					bufr.close();
				}
			}
			catch (IOException ioe) {
				sop(ioe.toString() + "\n��ȡ�ļ��ر�ʧ�ܣ�");
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
				//������
				throw new RuntimeException("Ŀ���ļ��������Ҵ���ʧ�ܣ�");
			}
		}
		BufferedWriter bufw = null;
		try {
			//�������䶼���ܲ����쳣
			bufw = new BufferedWriter(new FileWriter(resultFile));
			//д�������뻺�������ȴ�ˢ�²�����������
			bufw.write(sb.toString());
			//ˢ��д��������д������������д���ļ�
			bufw.flush();
		}
		catch(IOException ioe) {
			//������
			throw new RuntimeException("Ŀ���ļ�д��ʧ�ܣ�");
		}
		finally {
			try {
				if (null != bufw) {
					//bufw.close()ִ��ǰ��������bufw.flush()
					//��Ϊ�˲����������з���ǿ�ҽ���bufw.write();�����bufw.flush()
					bufw.close();	
				}
			}
			catch(IOException ioe) {
				//������
				throw new RuntimeException("Ŀ���ļ�д��ر�ʧ�ܣ�");
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
