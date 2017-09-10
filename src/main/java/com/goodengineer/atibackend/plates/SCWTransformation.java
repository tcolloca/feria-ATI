package com.goodengineer.atibackend.plates;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;
import com.goodengineer.atibackend.util.MaskFactory;

public class SCWTransformation implements Transformation {

	private final int x1;
	private final int y1;
	private final double threshold;
	
	public SCWTransformation(int x1, int y1, double threshold) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.threshold = threshold;
	}

	@Override
	public void transform(Band band) {
		double[][] mask1 = MaskFactory.average(2*x1 + 1, 2*y1 + 1);
		
		double[][] IandPixels = new double[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				double mean1 = FilterUtils.applyMask(band, mask1, x, y);
				IandPixels[x][y] = Math.abs(band.getPixel(x, y) - mean1) <= threshold ? 0 : 255;
			}
		}
		band.setPixels(IandPixels);
	}
	
}
