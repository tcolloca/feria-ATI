package com.goodengineer.atibackend.transformation.filter;

import java.util.Arrays;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class MedianFilterTransformation implements Transformation {

	private int size;

	public MedianFilterTransformation(int size) {
		this.size = size;
	}

	@Override
	public void transform(Band band) {
		double[][] newPixels = new double[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				newPixels[x][y] = getMedian(band, x, y);
			}
		}
		band.setPixels(newPixels);
	}

	private double getMedian(Band band, int x, int y) {
		double[] numArray = new double[size * size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				numArray[i * size + j] = getMatrixValue(band, x, y, i, j);
			}
		}
		Arrays.sort(numArray);
		return numArray[numArray.length / 2];
	}

	private double getMatrixValue(Band band, int x, int y, int i, int j) {
		int xOffset = -(size - 1) / 2 + i;
		int yOffset = -(size - 1) / 2 + j;
		int relativeX = x + xOffset;
		int relativeY = y + yOffset;
		if (relativeX < 0 || relativeX >= band.getWidth() || relativeY < 0 || relativeY >= band.getHeight()) {
			return band.getValidMin();
		}
		return band.getPixel(relativeX, relativeY);
	}
}
