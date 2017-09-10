package com.goodengineer.atibackend.transformation.filter;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.MaskFactory;

public class FilterAndZeroCrossTransformation implements Transformation {

	private double threshold;
	private double[][] mask;

	public FilterAndZeroCrossTransformation(double threshold, double[][] mask) {
		this.threshold = threshold;
		this.mask = mask;
	}
	
	public FilterAndZeroCrossTransformation(double[][] mask) {
		this(0, mask);
	}
	
	public FilterAndZeroCrossTransformation() {
		this(0, MaskFactory.laplacian());
	}
	
	public FilterAndZeroCrossTransformation(double threshold) {
		this(threshold, MaskFactory.laplacian());
	}

	@Override
	public void transform(Band band) {
		new FilterTransformation(mask).transform(band);
		new ZeroCrossTransformation(threshold).transform(band);
	}
}
