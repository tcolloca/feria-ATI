package com.goodengineer.atibackend.transformation.filter;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;

public class MultiFilterTransformation implements Transformation {

	private double[][][] masks;
	private PixelRule pixelRule;

	public MultiFilterTransformation(PixelRule pixelRule, List<double[][]> masks) {
		this(pixelRule, (double[][][]) masks.toArray());
	}

	public interface PixelRule {
		double calculate(List<Double> values);
	}
	
	public MultiFilterTransformation(PixelRule pixelRule, double[][] ... masks) {
		this.masks = masks;
		this.pixelRule = pixelRule;
	}

	@Override
	public void transform(Band band) {
		double[][] newPixels = new double[band.getWidth()][band.getHeight()];
		List<Double> values = new ArrayList<>();
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				values.clear();
				for (double[][] mask: masks) {
					values.add(FilterUtils.applyMask(band, mask, x, y));
				}
				newPixels[x][y] = pixelRule.calculate(values);
			}
		}
		band.setPixels(newPixels);
	}
}
