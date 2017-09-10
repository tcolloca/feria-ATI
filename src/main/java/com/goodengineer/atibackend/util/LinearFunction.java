package com.goodengineer.atibackend.util;

public class LinearFunction implements Function<Double, Double> {

	private double x1;
	private double y1;
	private double x2;
	private double y2;

	public LinearFunction(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public Double apply(Double input) {
		double m = (y2 - y1) / (x2 - x1);
		double b = y1 - m * x1;
//		System.out.println(m * input + b);
		return m * input + b;
	}
}
