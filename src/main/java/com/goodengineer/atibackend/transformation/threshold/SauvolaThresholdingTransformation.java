package com.goodengineer.atibackend.transformation.threshold;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;
import com.goodengineer.atibackend.util.Statistics;

public class SauvolaThresholdingTransformation implements Transformation {

	private final double k;
	private final int R;
	private final int b;
	
	public SauvolaThresholdingTransformation(double k, int r, int b) {
		super();
		this.k = k;
		R = r;
		this.b = b;
	}

	@Override
	public void transform(Band band) {
		Band original = band.clone();
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				double[] pixels = FilterUtils.getPixelsInMask(original, x, y, b);
				Statistics stats = new Statistics(pixels);
				double threshold = stats.getMean() + Math.round(1 + k * (stats.getVariance() / R - 1));
				if (original.getPixel(x, y) < threshold + 3) {
					band.setPixel(x, y, 0);
				} else {
					band.setPixel(x, y, 255);
				}
			}
		}
	}
	
	
}
