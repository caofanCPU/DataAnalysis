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
public class ErrorPlot extends JFrame { 
		private static final int START_WIDTH = 50;
		private static final int START_HEIGHT = 50;
		private static final int MAX_WIDTH = 1800;
		private static final int MAX_HEIGHT = 800;
		private static final double MAGNIFICATION_X = 8;
		private static final double MAGNIFICATION_Y = 100;
		private static final int DET_X = 10;
		private static final int DET_Y = 100;
		private Matrix mID;
		private Matrix mRT;
		private Matrix mLS;
		private Graphics g = null;
		
		public ErrorPlot(Matrix mID, Matrix mRT, Matrix mLS, String title) {
			this.mID = mID;
			this.mRT = mRT;
			this.mLS = mLS;
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
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//								RenderingHints.VALUE_ANTIALIAS_ON);
			//设置画笔(线条)的颜色
			g2.setColor(Color.GREEN);
			g2.setStroke(new BasicStroke(2));
			int[] coordinate = new int[] {0, 0, 0, 0};
			int ratio = 5;
			for (int i = 0; i < mID.getSize()[1]-1; ++i) {
				coordinate = pixelData(mID.getAsDouble(0, i), mRT.getAsDouble(0, i),
									   mID.getAsDouble(0, i+1), mRT.getAsDouble(0, i+1));
				g2.fillOval(coordinate[0]-ratio, coordinate[1]-ratio, 2*ratio, 2*ratio);
				g2.drawLine(coordinate[0], coordinate[1],
							coordinate[2], coordinate[3]);
			}
			g2.fillOval(coordinate[2]-ratio, coordinate[3]-ratio, 2*ratio, 2*ratio);
			
			g2.setColor(Color.BLUE);
			for (int i = 0; i < mID.getSize()[1]-1; ++i) {
				coordinate = pixelData(mID.getAsDouble(0, i), mLS.getAsDouble(0, i),
									   mID.getAsDouble(0, i+1), mLS.getAsDouble(0, i+1));
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