package com.goodengineer.atibackend.ml;

public class LogisticCost implements Cost{

	@Override
	public double eval(double x, double y) {
		return -y*Math.log(x) - (1 - y)*Math.log(1 - x);
	}
}
