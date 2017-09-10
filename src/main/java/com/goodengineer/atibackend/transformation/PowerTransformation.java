package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class PowerTransformation implements Transformation {

	private double gamma;

	public PowerTransformation(double gamma) {
		this.gamma = gamma;
	}

	@Override
	public void transform(Band band) {

		int maxColor = (int) band.getMax();
		int minColor = (int) band.getMin();
		double normalizationConstant = 255.0 / Math.pow(maxColor - minColor, gamma);
		
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				int newColor = (int) Math.round(normalizationConstant * Math.pow(band.getRawPixel(x, y) - minColor, gamma));
				band.setRawPixel(x, y, newColor);
			}
		}
	}
	
}
