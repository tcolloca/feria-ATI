package com.goodengineer.atibackend.transformation.filter.pixelRules;

import java.util.List;

import com.goodengineer.atibackend.transformation.filter.MultiFilterTransformation.PixelRule;

public class NormPixelRule implements PixelRule {

	@Override
	public double calculate(List<Double> values) {
		double acum = 0;
		for (Double value : values) {
			acum += value * value;
		}
		return Math.sqrt(acum);
	}
}
