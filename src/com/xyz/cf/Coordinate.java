package com.xyz.cf;

public class Coordinate {
	private double x;
	private double y;
	public Coordinate(double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	/**
	 * @return x
	 */
	public double getX() {
		return x;
	}
	/**
	 * @param x Ҫ���õ� x
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * @return y
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param y Ҫ���õ� y
	 */
	public void setY(double y) {
		this.y = y;
	}
}
