package com.xyz.dao;

import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import com.xyz.domain.CoordinateData;
import com.xyz.util.C3P0Util;

public class CoordinateDataDao {
	public void addCoordinateData(CoordinateData coor) throws SQLException {
		QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
		String sql = "INSERT INTO `coordinateData`("
				+ "paperX,paperY,realX,realY,surveyX,surveyY,"
				+ "rotationX,rotationY,squareX,squareY,"
				+ "rotationError,squareError)"
				+ "VALUES(?,?,?,?,?,?,"
				+ "?,?,?,?,"
				+ "?,?)";
		qr.update(sql,
				coor.getPaperX(), coor.getPaperY(), coor.getRealX(), coor.getRealY(),
				coor.getSurveyX(), coor.getSurveyY(), coor.getRotationX(), coor.getRotationY(),
				coor.getSquareX(), coor.getSquareY(),
				coor.getRotationError(), coor.getSquareError());		
	}
}
