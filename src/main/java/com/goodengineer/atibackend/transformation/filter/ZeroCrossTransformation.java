package com.goodengineer.atibackend.transformation.filter;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class ZeroCrossTransformation implements Transformation {

	private double threshold;

	public ZeroCrossTransformation(double threshold) {
		this.threshold = threshold;
	}
	
	public ZeroCrossTransformation() {
		this(0);
	}
	
	@Override
	public void transform(Band band) {
		double[][] dxPixels = new double[band.getWidth()][band.getHeight()];
		double[][] dyPixels = new double[band.getWidth()][band.getHeight()];
		double[][] finalPixels = new double[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				double current = band.getRawPixel(x, y);
				if (Math.abs(current) < 1e-5) {
					dxPixels[x][y] = 0;
					continue;
				}
				double nextX = x + 1;
				if (nextX >= band.getWidth()) {
					dxPixels[x][y] = 0;
					continue;
				}
				double next = band.getRawPixel(x + 1, y);
				if (Math.abs(next) < 1e-5) {
					nextX = x + 2;
					if (nextX >= band.getWidth()) {
						dxPixels[x][y] = 0;
						continue;
					}
					next = band.getRawPixel(x + 2, y);
				}
				if (Math.abs(next) < 1e-5) {
					dxPixels[x][y] = 0;
					continue;
				}

				double borderValue = borderValue(current, next);
				for (int k = x; k <= nextX; k++) {
					dxPixels[k][y] = borderValue;
				}
			}
		}

		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				double current = band.getRawPixel(x, y);
				if (Math.abs(current) < 1e-5)
					continue;
				double nextY = y + 1;
				if (nextY >= band.getHeight()) {
					dyPixels[x][y] = 0;
					continue;
				}
				double next = band.getRawPixel(x, y + 1);
				if (Math.abs(next) < 1e-5) {
					nextY = y + 2;
					if (nextY >= band.getHeight()) {
						dyPixels[x][y] = 0;
						continue;
					}
					next = band.getRawPixel(x, y + 2);
				}
				if (Math.abs(next) < 1e-5){
					dyPixels[x][y] = 0;
					continue;
				}

				double borderValue = borderValue(current, next);
				for (int k = y; k <= nextY; k++) {
					dyPixels[x][k] = borderValue;
				}
			}
		}

		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				finalPixels[x][y] = Math.max(dxPixels[x][y], dyPixels[x][y]);
			}
		}
		
		band.setPixels(finalPixels);
	}
	
	private double borderValue(double v1, double v2) {
		if (v1 * v2 >= 0) {
			return 0;
		}
		return Math.abs(v2 - v1) >= threshold ? 255 : 0;
	}
}
