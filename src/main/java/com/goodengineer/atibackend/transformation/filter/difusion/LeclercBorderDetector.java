package com.goodengineer.atibackend.transformation.filter.difusion;

import com.goodengineer.atibackend.util.FilterUtils;

public class LeclercBorderDetector implements BorderDetector {

	private double sigma;
	
	public LeclercBorderDetector(double sigma) {
		this.sigma = sigma;
	}
	
	@Override
	public double detect(double x) {
		return FilterUtils.leclerc(x, sigma);
	}
}
