package com.xyz.cf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import org.ujmp.core.Matrix;

/**
 * @author CY_XYZ
 * DataVisualization类：项目绘图类
 * 功能：把曲线坐标数据可视化为函数图形
 */

@SuppressWarnings({ "serial", "unused" })
public class DataVisualization extends JFrame {
	private static final int START_WIDTH = 50;
	private static final int START_HEIGHT = 50;
	private static final int MAX_WIDTH = 1800;
	private static final int MAX_HEIGHT = 800;
	private static final double MAGNIFICATION_X = 1.2;
	private static final double MAGNIFICATION_Y = 3;
	private static final int DET_X = 0;
	private static final int DET_Y = 0;
	private Matrix mX;
	private Matrix mY;
	private Graphics g = null;
	
	public DataVisualization(Matrix mX, Matrix mY, String title) {
		this.mX = mX;
		this.mY = mY;
		initFrame(title);
		paint(this.g);
	}
	
	public void initFrame(String title) {
		this.setBounds(START_WIDTH, START_HEIGHT, MAX_WIDTH, MAX_HEIGHT);
		Container p = this.getContentPane();
		p.setBackground(new Color(0xf5f5f5));
		this.setLayout(null);
		this.setResizable(false);
		this.setTitle(title);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.g = this.getGraphics();
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		//设置画笔(线条)的颜色
		g2.setColor(Color.MAGENTA);
		g2.setStroke(new BasicStroke(2));
		int[] coordinate = new int[] {0, 0, 0, 0};
		int ratio = 5;
		for (int i = 0; i < mX.getSize()[1]-1; ++i) {
			coordinate = pixelData(mX.getAsDouble(0, i), mY.getAsDouble(0, i),
								   mX.getAsDouble(0, i+1), mY.getAsDouble(0, i+1));
			g2.fillOval(coordinate[0]-ratio, coordinate[1]-ratio, 2*ratio, 2*ratio);
			g2.drawLine(coordinate[0], coordinate[1],
						coordinate[2], coordinate[3]);
		}
		g2.fillOval(coordinate[2]-ratio, coordinate[3]-ratio, 2*ratio, 2*ratio);
	}
	
	/**
	 * 横坐标放大MAGNIFICATION_X倍，纵坐标放大MAGNIFICATION_Y倍，横坐标偏移DET_X，纵坐标偏移DET_Y
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 */
	public int[] pixelData(double startX, double startY, double endX, double endY) {
		int[] coordinate = new int[] {0, 0, 0, 0};
		coordinate[0] = (int)((startX * MAGNIFICATION_X + DET_X) * 1);
		coordinate[2] = (int)((endX * MAGNIFICATION_X + DET_X) * 1);
		coordinate[1] = (int)((startY * MAGNIFICATION_Y + DET_Y) * 1);
		coordinate[3] = (int)((endY * MAGNIFICATION_Y + DET_Y) * 1);
		return coordinate;
	}
}