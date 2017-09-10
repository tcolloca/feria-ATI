package com.goodengineer.atibackend.transformation.filter.difusion;

import com.goodengineer.atibackend.util.FilterUtils;

public class LorentzianBorderDetector implements BorderDetector {

	private double sigma;
	
	public LorentzianBorderDetector(double sigma) {
		this.sigma = sigma;
	}
	
	@Override
	public double detect(double x) {
		return FilterUtils.lorentzian(x, sigma);
	}
}
