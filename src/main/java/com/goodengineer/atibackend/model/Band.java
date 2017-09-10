package com.goodengineer.atibackend.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

import com.goodengineer.atibackend.util.Function;
import com.goodengineer.atibackend.util.LinearFunction;

public class Band {
	public double[][] pixels;

	protected double min;
	protected double max;
	private int minCount;
	private int maxCount;
	protected String name;

	protected Function<Double, Double> rawToNormal;
	protected Function<Double, Double> normalToRaw;

	public Band(double[][] pixels) {
		this(pixels, null);
	}

	public Band(double[][] pixels, String name) {
		this.pixels = pixels;
		this.name = name;
		findMinAndMax();
	}
	
	public Band clone() {
		double[][] newPixels = new double[pixels.length][pixels[0].length];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				newPixels[i][j] = pixels[i][j];
			}
		}
		return new Band(newPixels, name);
	}

	public int getWidth() {
		return pixels.length;
	}

	public int getHeight() {
		return pixels[0].length;
	}
	
	public void setPixels(double[][] pixels) {
		this.pixels = pixels;
		findMinAndMax();
	}

	public void normalize() {
		if (!hasInvalidValues())
			return;
		for (int x = 0; x < pixels.length; x++) {
			for (int y = 0; y < pixels[0].length; y++) {
				pixels[x][y] = rawToNormal.apply(pixels[x][y]);
			}
		}
		findMinAndMax();
	}

	public double getRawPixel(int x, int y) {
		return pixels[x][y];
	}

	public void setRawPixel(int x, int y, double newColor) {
		double oldColor = pixels[x][y];
		if (newColor == oldColor) {
			return;
		}
		if (oldColor == max) {
			maxCount--;
		}
		if (oldColor == min) {
			minCount--;
		}
		pixels[x][y] = newColor;

		if (minCount == 0 || maxCount == 0) {
			findMinAndMax();
			return;
		}

		if (newColor > max) {
			max = newColor;
			maxCount = 1;
			updateTranslationFunctions();
		} else if (newColor == max) {
			maxCount++;
		}

		if (newColor < min) {
			min = newColor;
			minCount = 1;
			updateTranslationFunctions();
		} else if (newColor == min) {
			minCount++;
		}
	}

	public int getPixel(int x, int y) {
		if (hasInvalidValues()) {
			double pixel = getRawPixel(x, y);
			return (int) Math.round(rawToNormal.apply(pixel));
		}
		return (int) Math.round(getRawPixel(x, y));
	}
	
	public int getPixelNormalized(int x, int y) {
		double pixel = getRawPixel(x, y);
		return (int) Math.round(rawToNormal.apply(pixel));
	}

	public void setPixel(int x, int y, int color) {
		if (hasInvalidValues()) {
			setRawPixel(x, y, normalToRaw.apply((double) color));
			return;
		}
		setRawPixel(x, y, color);
	}

	public double map(int color) {
		if (hasInvalidValues()) {
			return normalToRaw.apply((double) color);
		}
		return (double) color;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getValidMin() {
		return hasInvalidValues() ? min : 0;
	}

	public double getValidMax() {
		return hasInvalidValues() ? max : 255;
	}

	private void findMinAndMax() {
		min = pixels[0][0];
		max = pixels[0][0];
		minCount = 0;
		maxCount = 0;
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (pixels[x][y] < min) {
					min = pixels[x][y];
					minCount = 1;
				}
				if (pixels[x][y] == min) {
					minCount++;
				}
				if (pixels[x][y] > max) {
					max = pixels[x][y];
					maxCount = 1;
				}
				if (pixels[x][y] == max) {
					maxCount++;
				}
			}
		}

		updateTranslationFunctions();
	}
	
	public boolean isValidMin(double value) {
		return Math.abs(value - getValidMin()) < 1e-4;
	}
	
	public boolean isValidMax(double value) {
		return Math.abs(value - getValidMax()) < 1e-4;
	}

	protected void updateTranslationFunctions() {
		rawToNormal = new LinearFunction(min, 0, max, 255);
		normalToRaw = new LinearFunction(0, min, 255, max);
	}

	private boolean hasInvalidValues() {
		return min < 0 || max > 255;
	}

	public String getName() {
		return name == null ? "gray" : name;
	}
	
	public boolean isValid(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}
	

	public List<int[]> neighbours(int x, int y, boolean use8Neigh) {
		List<int[]> neighDirs = new ArrayList<>();
		neighDirs.addAll(Arrays.asList(new int[][]{{1, 0},{0, 1}, {-1, 0}, {0, -1}}));
		List<int[]> neigh8Dirs = new ArrayList<>();
		neigh8Dirs.addAll(Arrays.asList(new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}}));
		if (use8Neigh) {
			neighDirs.addAll(neigh8Dirs);
		}
		List<int[]> neighs = new ArrayList<>();
		for (int[] neighDir : neighDirs) {
			if (isValid(x + neighDir[0], y + neighDir[1])) {
				neighs.add(new int[]{x + neighDir[0], y + neighDir[1]});
			}
		}
		return neighs;
	}

	public Band subRegion(int minX, int minY, int maxX, int maxY) {
		minX = Math.max(0, minX);
		minY = Math.max(0, minY);
		maxX = Math.min(getWidth() - 1, maxX);
		maxY = Math.min(getHeight() - 1, maxY);
		double[][] subPixels = new double[maxX - minX + 1][maxY - minY + 1];
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				subPixels[x - minX][y - minY] = pixels[x][y];
			}
		}
		return new Band(subPixels);
	}
}
