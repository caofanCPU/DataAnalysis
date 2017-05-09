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
	 * ����DataMatrix.coorX����һ�У�ͼֽ����
	 * 			�ڶ��У�ʵ������
	 * 			�����У���������
	 * 			�����У���ת����
	 * 			�����У���������
	 */
	private static Matrix coorX = Matrix.Factory.zeros(0, 0);
	private static Matrix coorY = Matrix.Factory.zeros(0, 0);
	public DataMatrix(TreeMap<Integer, Coordinate> tm) {
		drawingPaper(tm);
		errorAnalysis();
		saveData();
		save2DB();
		/**
		 * ����Matrix.showGUI();����ͬ���������������ʾ���Ǹþ������Ľ��
		 * ���ԣ�Ҫô�����µĿռ�洢��������
		 *     ���ô��ݾ���ʱ��ֻ�������о���������������.showGUI();
		 * BUG����Ϊjar����ִ��jar��ʱ.showGUI()����ԭ�������UJMP��GUI������©��
		 * 	         ��������.showGUI()����������
		 */
		DataMatrix.coorY.showGUI();	//���þ����GUI��ʾ�����뵼��ujmp-gui-0.3.0.jar��
	}
	
	/**
	 * ������
	 */
	public void errorAnalysis() {
		/**
		 * ���������ݣ�ÿ�������ƫ����������
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
		new ErrorPlot(IDm, RTm, LSm, "������������");
		/** 
		 * ��ʾȫ����������
		 * ��Eclipse����������
		 * ��jar������ʱ���ᱨ��ԭ���ݲ���
		 */
	}
	
	private void saveData() {
		// TODO �Զ����ɵķ������
		StringBuilder sb = new StringBuilder();
		double x, y;
		sb.append("----������������ݼ�----\n");
		for (int i = 0; i < DataMatrix.coorX.getRowCount(); i++) {
			if (0 == i) {
				sb.append("-->>ͼֽ���꼯��<<--\n");
			}
			if (1 == i) {
				sb.append("-->>ʵ�����꼯��<<--\n");
			}
			if (2 == i) {
				sb.append("-->>�������꼯��<<--\n");
			}
			if (3 == i) {
				sb.append("-->>��ת���꼯��<<--\n");
			}
			if (4 == i) {
				sb.append("-->>�������꼯��<<--\n");
			}
			if (5 == i) {
				sb.append("-->>��ת�������<<--\n");
			}
			if (6 == i) {
				sb.append("-->>�����������<<--\n");
			}
			for (long j = 0; j < DataMatrix.coorX.getColumnCount(); j++) {
				x = DataMatrix.coorX.getAsDouble(i, j);
				y = DataMatrix.coorY.getAsDouble(i, j);
				sb.append(x + ", " + y + ",\n");
			}
		}
		String parStr = System.getProperty("user.dir");
		new DataSource().write2File(new File(parStr, "������������ݼ�.txt"), sb);
	}
	
	/**
	 * �����ݴ洢�����ݿ�
	 * 
	 */
	public void save2DB() {
		double x, y;
		long columnLen = DataMatrix.coorX.getColumnCount();
		int rowLen = (int) DataMatrix.coorX.getRowCount();
		double[][] coor = new double[2][rowLen];
		/**
		 * ѭ����������jΪ�����б꣬iΪ�����б�
		 */
		for (long j = 0; j < columnLen; j++){			 
				for (int i = 0; i < rowLen; i++) {
				x = DataMatrix.coorX.getAsDouble(i, j);
				y = DataMatrix.coorY.getAsDouble(i, j);
				coor[0][i] = x;
				coor[1][i] = y;
			}
			/**
			 * ��coor�������ݷ�װ��CoordinateData������
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
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
	}

	public void drawingPaper(TreeMap<Integer, Coordinate> tm) {
//		/**
//		 * ��ʽ���������������Ϣ��
//		 */
//		for (Iterator<Map.Entry<Integer, Coordinate>> entry
//			 = tm.entrySet().iterator(); entry.hasNext();) {
//			Map.Entry<Integer, Coordinate> me = entry.next();
//			System.out.println("[" + String.format("%-3d", me.getKey())
//							   + ", <" + String.format("%-6.2f", me.getValue().getX())
//							   +", " + String.format("%6.2f", me.getValue().getY()) + ">]");
//		}
		/**
		 * ��ȡͼֽ����
		 * ת��Ϊ����
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
		 * ����ԭ��this.DataMatrix.coorXδ�����ռ䣬���Ǵ��ʼ�ľ���paperX�õ�һ������
		 * 		     ��󣬾���paperX��Ϊ���ò��������������޸�����
		 * 		     ���ݱ���������.showGUI()�õ��Ĳ���ͬ����Ϣ
		 * ������ۣ��洢���ݵ����ñ����Լ������¿ռ䣬�����������ӵ��ռ���
		 */
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, paperX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, paperY);
		//����ͼֽ����
		//new DataVisualization(paperX, paperY, "�����ͼֽ����");
		drawingReal(paperX, paperY);
	}
	
	/**
	 * ʵ�������
	 */
	public void drawingReal(Matrix mX, Matrix mY) {
		/**
		 * ͼֽ�Ƶ�(paperX[numID], paperY[numID])��תdegrees��
		 * ƽ��(moveX-paperX[numID],moveY-paperY[numID])
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
		//new DataVisualization(mX, mY, "�����ʵ������");
		DataMatrix.coorX = DataMatrix.coorX.appendVertically(Ret.NEW, mX);
		DataMatrix.coorY = DataMatrix.coorY.appendVertically(Ret.NEW, mY);
		drawingSurvey(mX, mY);
	}
	
	/**
	 * ����ײ�������
	 */
	public void drawingSurvey(Matrix mX, Matrix mY) { 
		/**
		 * ģ��������
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
		//new DataVisualization(mX, mY, "����ײ�������");
		rotationTranslation(mX, mY);
	}
	
	/**
	 * ������ת����
	 * @param mX
	 * @param mY
	 */
	public void rotationTranslation(Matrix mX, Matrix mY) {
		/**
		 * ��תƽ�Ʒ�
		 * 		���Ƶ����Ϊ1��219
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
		//�����תƽ�Ʋ�����ת��Ϊ1��4��
		Matrix ansX = mA.solve(mb).transpose();
		//������ת����
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
		//new DataVisualization(mX, mY, "�������ת����");
		leastSquare();
	}
	
	/**
	 * �����������
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
		 * ����ϵ�������ֵ
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
		//�����������
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
		//new DataVisualization(mX, mY, "����׶�������");
	}
}
