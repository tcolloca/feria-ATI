package com.goodengineer.atibackend.transformation.threshold;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class ThresholdingTransformation implements Transformation {

	private int thresholdColor;

	public ThresholdingTransformation(int thresholdColor) {
		this.thresholdColor = thresholdColor;
	}

	@Override
	public void transform(Band band) {
		double rawThresholdColor = band.map(thresholdColor);
		double max = band.getValidMax();
		double min = band.getValidMin();

		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				double currentColor = band.getRawPixel(x, y);
				double newColor = currentColor >= rawThresholdColor ? max : min;
				band.setRawPixel(x, y, newColor);
			}
		}
	}

}
