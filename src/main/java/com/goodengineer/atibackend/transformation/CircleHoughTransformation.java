package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class CircleHoughTransformation implements Transformation {

	private final int widthCount;
	private final int heightCount;
	private final int radiusCount;
	private final int radiusStart;
	private final int radiusEnd;
	private final double eps;
	private final int threshold;
	
	public CircleHoughTransformation(int widthCount, int heightCount, int radiusCount, 
			int radiusStart, int radiusEnd, double eps, int threshold) {
		super();
		this.widthCount = widthCount;
		this.heightCount = heightCount;
		this.radiusCount = radiusCount;
		this.radiusStart = radiusStart;
		this.radiusEnd = radiusEnd;
		this.eps = eps;
		this.threshold = threshold;
	}

	@Override
	public void transform(Band band) {
		int[][][] acum = new int[widthCount][heightCount][radiusCount];
		int max = 0;
		for (int widthIndex = 0; widthIndex < widthCount; widthIndex++) {
			double width = getWidth(widthIndex, band);
			for (int heightIndex = 0; heightIndex < heightCount; heightIndex++) {
				double height = getHeight(heightIndex, band);
				for (int radiusIndex = 0; radiusIndex < radiusCount; radiusIndex++) {
					double radius = getRadius(radiusIndex);
					for (int w = 0; w < band.getWidth(); w++) {
						for (int h = 0; h < band.getHeight(); h++) {
							double pixel = band.getPixel(w, h);
							if (pixel == 255) {
								double xDist = width - w;
								double yDist = height - h;
								
								if (Math.abs(xDist * xDist + yDist * yDist - radius * radius) < eps) {
									acum[widthIndex][heightIndex][radiusIndex]++;
								}	
							}
						}
					}
					if (acum[widthIndex][heightIndex][radiusIndex] > max) {
						max = acum[widthIndex][heightIndex][radiusIndex];
					}
				}
			}
		}
		
		for (int widthIndex = 0; widthIndex < widthCount; widthIndex++) {
			for (int heightIndex = 0; heightIndex < heightCount; heightIndex++) {
				for (int radiusIndex = 0; radiusIndex < radiusCount; radiusIndex++) {
					if (acum[widthIndex][heightIndex][radiusIndex] > threshold) {
						System.out.println(String.format("w: %g, h: %g; r: %g, #: %d", 
								getWidth(widthIndex, band), getHeight(heightIndex, band), 
								getRadius(radiusIndex), acum[widthIndex][heightIndex][radiusIndex]));
						drawCircle(getWidth(widthIndex, band), getHeight(heightIndex, band), 
								getRadius(radiusIndex), band);
					}
				}
			}
		}
	}
	
	private double getWidth(int widthIndex, Band band) {
		return band.getWidth() / (double) (widthCount - 1) * widthIndex;
	}
	
	private double getHeight(int heightIndex, Band band) {
		return band.getHeight() / (double) (heightCount - 1) * heightIndex;
	}
	
	private double getRadius(int radiusIndex) {
		return (radiusEnd - radiusStart) / (double) (radiusCount - 1) * radiusIndex + radiusStart;
	}
	
	private void drawCircle(double width, double height, double radius, Band band) {
		for (int w = 0; w < band.getWidth(); w++) {
			for (int h = 0; h < band.getHeight(); h++) {
				double xDist = width - w;
				double yDist = height - h;
				if (Math.abs(xDist * xDist + yDist * yDist - radius * radius) < eps) {
					band.setPixel(w, h, 127);
				}	
			}
		}
	}
}
