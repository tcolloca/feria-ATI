package com.goodengineer.atibackend.transformation.filter;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;

public class FilterTransformation implements Transformation {

	private final double[][] mask;

	public FilterTransformation(double[][] mask) {
		if (mask.length % 2 == 0) {
			throw new IllegalArgumentException("Filter must be odd sized.");
		}
		this.mask = mask;
	}

	@Override
	public void transform(Band band) {
		double[][] newPixels = new double[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				newPixels[x][y] = FilterUtils.applyMask(band, mask, x, y);
			}
		}
		band.setPixels(newPixels);
	}
}
