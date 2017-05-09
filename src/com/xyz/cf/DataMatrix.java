package com.xyz.cf;

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

import com.xyz.dao.CoordinateDataDao;
import com.xyz.domain.CoordinateData;

public class DataMatrix {
	/**
	 * 矩阵DataMatrix.coorX：第一行，图纸坐标
	 * 			第二行，实际坐标
	 * 			第三行，测量坐标
	 * 			第四行，旋转坐标
	 * 			第五行，二乘坐标
	 */
	private static Matrix coorX = Matrix.Factory.zeros(0, 0);
	private static Matrix coorY = Matrix.Factory.zeros(0, 0);
	public DataMatrix(TreeMap<Integer, Coordinate> tm) {
		drawingPaper(tm);
		errorAnalysis();
		saveData();
		save2DB();
		/**
		 * 由于Matrix.showGUI();并非同步操作，因而其显示的是该矩阵最后的结果
		 * 所以：要么开辟新的空间存储矩阵数据
		 *     引用传递矩阵时，只能在所有矩阵操作结束后调用.showGUI();
		 * BUG：打为jar包，执行jar包时.showGUI()报错，原因可能是UJMP的GUI代码有漏洞
		 * 	         更可能是.showGUI()有限制条件
		 */
		DataMatrix.coorY.showGUI();	//调用矩阵的GUI显示，必须导入ujmp-gui-0.3.0.jar包
	}
	
	/**
	 * 误差分析
	 */
	public void errorAnalysis() {
		/**
		 * 误差计算依据：每个弦轴孔偏差距离的期望
		 */
		long len = DataMatrix.coorX.getColumnCount();
		Matrix RTm = Matrix.Factory.zeros(1, len);
		Matrix LSm = Matrix.Factory.zeros(1, len);
		Matrix IDm = Matrix.Factory.zeros(1, len);
		double mx, my, rx, ry, sx, sy;
		double errorRT, errorLS;
		for (long i = 0; i < len; i++) {
			mx = DataMatrix.coorX.getAsDouble(2, i);
			my = DataMatrix.coorY.getAsDouble(2, i);
			rx = DataMatrix.coorX.getAsDouble(3, i);
			ry = DataMatrix.coorY.getAsDouble(3, i);
			sx = DataMatrix.coorX.getAsDouble(4, i);
			sy = DataMatrix.coorY.getAsDouble(4, i);
			errorRT = Math.sqrt(Math.pow(rx - mx, 2) + Math.pow(ry - my, 2));
			errorLS = Math.sqrt(Math.pow(sx - mx, 2) + Math.pow(sy - my, 2));
			RTm.setAsDouble(errorRT, 0, i);
			LSm.setAsDouble(errorLS, 0, i);
			IDm.setAsDouble(i + 1, 0, i);
		}
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, IDm).appendVertically(Ret.NEW, IDm);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, RTm).appendVertically(Ret.NEW, LSm);
		new ErrorPlot(IDm, RTm, LSm, "弦轴孔误差数据");
		/** 
		 * 显示全部矩阵数据
		 * 在Eclipse中运行正常
		 * 打jar包运行时，会报错，原因暂不明
		 */
	}
	
	private void saveData() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		double x, y;
		sb.append("----弦轴孔坐标数据集----\n");
		for (int i = 0; i < DataMatrix.coorX.getRowCount(); i++) {
			if (0 == i) {
				sb.append("-->>图纸坐标集合<<--\n");
			}
			if (1 == i) {
				sb.append("-->>实际坐标集合<<--\n");
			}
			if (2 == i) {
				sb.append("-->>测量坐标集合<<--\n");
			}
			if (3 == i) {
				sb.append("-->>旋转坐标集合<<--\n");
			}
			if (4 == i) {
				sb.append("-->>二乘坐标集合<<--\n");
			}
			if (5 == i) {
				sb.append("-->>旋转误差数据<<--\n");
			}
			if (6 == i) {
				sb.append("-->>二乘误差数据<<--\n");
			}
			for (long j = 0; j < DataMatrix.coorX.getColumnCount(); j++) {
				x = DataMatrix.coorX.getAsDouble(i, j);
				y = DataMatrix.coorY.getAsDouble(i, j);
				sb.append(x + ", " + y + ",\n");
			}
		}
		String parStr = System.getProperty("user.dir");
		new DataSource().write2File(new File(parStr, "弦轴孔坐标数据集.txt"), sb);
	}
	
	/**
	 * 将数据存储至数据库
	 * 
	 */
	public void save2DB() {
		double x, y;
		long columnLen = DataMatrix.coorX.getColumnCount();
		int rowLen = (int) DataMatrix.coorX.getRowCount();
		double[][] coor = new double[2][rowLen];
		/**
		 * 循环遍历矩阵，j为矩阵列标，i为矩阵行标
		 */
		for (long j = 0; j < columnLen; j++){			 
				for (int i = 0; i < rowLen; i++) {
				x = DataMatrix.coorX.getAsDouble(i, j);
				y = DataMatrix.coorY.getAsDouble(i, j);
				coor[0][i] = x;
				coor[1][i] = y;
			}
			/**
			 * 将coor数组数据封装到CoordinateData对象中
			 */
			CoordinateData	coorData = new CoordinateData();
			coorData.setPaperX(coor[0][0]);
			coorData.setPaperY(coor[1][0]);
			coorData.setRealX(coor[0][1]);
			coorData.setRealY(coor[1][1]);
			coorData.setSurveyX(coor[0][2]);
			coorData.setSurveyY(coor[1][2]);
			coorData.setRotationX(coor[0][3]);
			coorData.setRotationY(coor[1][3]);
			coorData.setSquareX(coor[0][4]);
			coorData.setSquareY(coor[1][4]);
			coorData.setRotationError(coor[1][5]);
			coorData.setSquareError(coor[1][6]);
			CoordinateDataDao coorDataDao = new CoordinateDataDao();
			try {
				coorDataDao.addCoordinateData(coorData);
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}

	public void drawingPaper(TreeMap<Integer, Coordinate> tm) {
//		/**
//		 * 格式化输出坐标数据信息：
//		 */
//		for (Iterator<Map.Entry<Integer, Coordinate>> entry
//			 = tm.entrySet().iterator(); entry.hasNext();) {
//			Map.Entry<Integer, Coordinate> me = entry.next();
//			System.out.println("[" + String.format("%-3d", me.getKey())
//							   + ", <" + String.format("%-6.2f", me.getValue().getX())
//							   +", " + String.format("%6.2f", me.getValue().getY()) + ">]");
//		}
		/**
		 * 提取图纸坐标
		 * 转换为矩阵
		 */
		double[][] coordinateArray = new double[2][tm.size()];
		int i = 0;
		Coordinate coor = null;
		for (Iterator<Map.Entry<Integer, Coordinate>> entry
			 = tm.entrySet().iterator(); entry.hasNext();) {
			Map.Entry<Integer, Coordinate> me = entry.next();
			i = me.getKey() - 1;
			coor = me.getValue();
			coordinateArray[0][i] = coor.getX();
			coordinateArray[1][i] = coor.getY();
		}
		Matrix paperX = Matrix.Factory.importFromArray(coordinateArray[0]);
		Matrix paperY = Matrix.Factory.importFromArray(coordinateArray[1]);
		/**
		 * 		this.DataMatrix.coorX = paperX;
		 *		this.DataMatrix.coorY = paperY;
		 * 出错原因：this.DataMatrix.coorX未创建空间，而是从最开始的矩阵paperX得到一个引用
		 * 		     随后，矩阵paperX作为引用参数被后续函数修改内容
		 * 		     根据本例，矩阵.showGUI()得到的并非同步信息
		 * 经验积累：存储数据的引用变量自己开辟新空间，将结果数据添加到空间中
		 */
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, paperX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, paperY);
		//绘制图纸坐标
		//new DataVisualization(paperX, paperY, "弦轴孔图纸坐标");
		drawingReal(paperX, paperY);
	}
	
	/**
	 * 实际坐标点
	 */
	public void drawingReal(Matrix mX, Matrix mY) {
		/**
		 * 图纸绕点(paperX[numID], paperY[numID])旋转degrees°
		 * 平移(moveX-paperX[numID],moveY-paperY[numID])
		 */
		//mY.showGUI();
		final int numID = 7;
		final int degrees = 8;
		final int moveX = 80;
		final int moveY = 50;
		double baseX = mX.getAsDouble(0, numID);
		double baseY = mY.getAsDouble(0, numID);
		double theta = Math.toRadians(degrees);
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double detX, detY, dx, dy;
		for (int i = 0; i < mX.getSize()[1]; i++) {
			detX = mX.getAsDouble(0, i) - baseX;
			detY = mY.getAsDouble(0, i) - baseY;
			dx = detX * cosTheta - detY * sinTheta + moveX;
			dy = detX * sinTheta + detY * cosTheta + moveY;
			mX.setAsDouble(dx, 0, i);
			mY.setAsDouble(dy, 0, i);
		}
		//new DataVisualization(mX, mY, "弦轴孔实际坐标");
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, mX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, mY);
		drawingSurvey(mX, mY);
	}
	
	/**
	 * 弦轴孔测量坐标
	 */
	public void drawingSurvey(Matrix mX, Matrix mY) { 
		/**
		 * 模拟随机误差
		 */
		long numLen = mX.getSize()[1];
		double theta, r, dx, dy;
		for (int i = 0; i < numLen; i++) {
			theta = Math.random() * (2*Math.PI);
			r = Math.random() * 0.5;
			dx = mX.getAsDouble(0, i) + r*Math.cos(theta);
			dy = mY.getAsDouble(0, i) + r*Math.sin(theta);
			mX.setAsDouble(dx, 0, i);
			mY.setAsDouble(dy, 0, i);
		}
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, mX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, mY);
		//new DataVisualization(mX, mY, "弦轴孔测量坐标");
		rotationTranslation(mX, mY);
	}
	
	/**
	 * 计算旋转坐标
	 * @param mX
	 * @param mY
	 */
	public void rotationTranslation(Matrix mX, Matrix mY) {
		/**
		 * 旋转平移法
		 * 		控制点序号为1、219
		 */
		int numP = 1 - 1;
		int numQ = 219 - 1;
		double detXP, detYP, detXQ, detYQ;
		double midX = (DataMatrix.coorX.getAsDouble(0, numP) + DataMatrix.coorX.getAsDouble(0, numQ)) / 2;
		double midY = (DataMatrix.coorY.getAsDouble(0, numP) + DataMatrix.coorY.getAsDouble(0, numQ)) / 2;
		detXP = DataMatrix.coorX.getAsDouble(0, numP) - midX;
		detYP = DataMatrix.coorY.getAsDouble(0, numP) - midY;
		detXQ = DataMatrix.coorX.getAsDouble(0, numQ) - midX;
		detYQ = DataMatrix.coorY.getAsDouble(0, numQ) - midY; 
		double[][] A = { { detXP, -detYP, 1, 0 },
						 { detYP, detXP, 0, 1  },
						 { detXQ, -detYQ, 1, 0 },
						 { detYQ, detXQ, 0, 1  }
					   };
		double[] B = { DataMatrix.coorX.getAsDouble(2, numP),
					   DataMatrix.coorY.getAsDouble(2, numP),
					   DataMatrix.coorX.getAsDouble(2, numQ),
					   DataMatrix.coorY.getAsDouble(2, numQ)
					 };
		Matrix mA = Matrix.Factory.importFromArray(A); 
		Matrix mb = Matrix.Factory.importFromArray(B).reshape(Ret.NEW, 4, 1);
		//求解旋转平移参数，转置为1行4列
		Matrix ansX = mA.solve(mb).transpose();
		//计算旋转坐标
		double dx, dy, ds;
		for (int i = 0; i < mX.getColumnCount(); i++) {
			dx = DataMatrix.coorX.getAsDouble(0, i) - midX;
			dy = DataMatrix.coorY.getAsDouble(0, i) - midY;
			ds = dx * ansX.getAsDouble(0, 0)
				 - dy * ansX.getAsDouble(0, 1)
				 + ansX.getAsDouble(0, 2);
			mX.setAsDouble(ds, 0, i);
			ds = dx * ansX.getAsDouble(0, 1)
				 + dy * ansX.getAsDouble(0, 0)
				 + ansX.getAsDouble(0, 3);
			mY.setAsDouble(ds, 0, i);
		}
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, mX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, mY);
		//new DataVisualization(mX, mY, "弦轴孔旋转坐标");
		leastSquare();
	}
	
	/**
	 * 计算二乘坐标
	 */
	public void leastSquare() {
		int num = 7;
		int[] numID = { 1-1, 46-1, 97-1,
						154-1, 175-1, 197-1,
						219-1
					  };
		double A = 0, B = 0, C = 0,
			   D = 0, E = 0, F = 0,
			   G = 0, H = 0, I = 0,
			   J = 0, K = 0;
		double px, py, sx, sy;
		/**
		 * 构建系数矩阵的值
		 */
		for (int i = 0; i < num; i++) {
			px = DataMatrix.coorX.getAsDouble(0, numID[i]);	//ui
			py = DataMatrix.coorY.getAsDouble(0, numID[i]);	//vi
			sx = DataMatrix.coorX.getAsDouble(2, numID[i]);	//xt
			sy = DataMatrix.coorY.getAsDouble(2, numID[i]);	//yt
			A += (px * px);
			B += (px * py);
			C += px;
			D += (px * sx);
			E += (py * py);
			F += py;
			G += (py * sx);
			H += sx;
			I += (px * sy);
			J += (py * sy);
			K += sy;
		}
		double[][] mArray = { {A, B, C},
							  {B, E, F},
							  {C, F, num}
							};
		double[] bXArray = { D, G, H };
		double[] bYArray = { I, J, K };
		Matrix mA = Matrix.Factory.importFromArray(mArray);
		Matrix mB = Matrix.Factory.importFromArray(bXArray).transpose();
		Matrix ansX = mA.solve(mB);
		mB = Matrix.Factory.importFromArray(bYArray).transpose();
		Matrix ansY = mA.solve(mB);
		//计算二乘坐标
		long leng = DataMatrix.coorX.getColumnCount();
		Matrix mX = Matrix.Factory.zeros(1, leng);
		Matrix mY = Matrix.Factory.zeros(1, leng);
		double dx, dy;
		for (long i = 0; i < leng; i++) {
			dx = ansX.getAsDouble(0, 0) * DataMatrix.coorX.getAsDouble(0, i)
				 + ansX.getAsDouble(1, 0) * DataMatrix.coorY.getAsDouble(0, i)
				 + ansX.getAsDouble(2, 0);
			mX.setAsDouble(dx, 0, i);
			dy = ansY.getAsDouble(0, 0) * DataMatrix.coorX.getAsDouble(0, i)
					 + ansY.getAsDouble(1, 0) * DataMatrix.coorY.getAsDouble(0, i)
					 + ansY.getAsDouble(2, 0);
			mY.setAsDouble(dy, 0, i);
		}
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, mX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, mY);
		//new DataVisualization(mX, mY, "弦轴孔二乘坐标");
	}
}
