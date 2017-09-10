package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class ScaleTransformation implements Transformation {

	private int scalar;
	
	public ScaleTransformation(int scalar) {
		this.scalar = scalar;
	}
	
	@Override
	public void transform(Band band) {
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				band.setRawPixel(x, y, band.getRawPixel(x, y) * scalar);
			}
		}
	}

	
}
