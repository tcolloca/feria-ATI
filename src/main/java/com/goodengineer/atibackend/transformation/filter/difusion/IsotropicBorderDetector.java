package com.goodengineer.atibackend.transformation.filter.difusion;

public class IsotropicBorderDetector implements BorderDetector {

	@Override
	public double detect(double x) {
		return 1;
	}
}
