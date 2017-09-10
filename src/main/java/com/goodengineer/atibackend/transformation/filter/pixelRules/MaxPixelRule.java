package com.goodengineer.atibackend.transformation.filter.pixelRules;

import java.util.Collections;
import java.util.List;

import com.goodengineer.atibackend.transformation.filter.MultiFilterTransformation.PixelRule;

public class MaxPixelRule implements PixelRule {

	@Override
	public double calculate(List<Double> values) {
		return Collections.max(values);
	}
}
