package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class BucketTransformation implements Transformation {

	private final int x;
	private final int y;
	private final double[] color;
	
	public BucketTransformation(int x, int y, double[] color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public void transform(Band band) {
		double newColor;
		switch (band.getName()) {
		case "R":
			newColor = color[0];
			break;
		case "G":
			newColor = color[0];
			break;
		case "B":
		default:
			newColor = color[0];
			break;
		}
		
		double oldColor = band.getRawPixel(x, y);
		for (int w = 0; w < band.getWidth(); w++) {
			for (int h = 0; w < band.getHeight(); h++) {
				if (band.getRawPixel(w, h) == oldColor) {
					band.setRawPixel(w, h, newColor);
				}
			}	
		}
	}
}
