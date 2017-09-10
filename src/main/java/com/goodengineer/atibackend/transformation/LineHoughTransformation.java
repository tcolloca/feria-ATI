package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class LineHoughTransformation implements Transformation {

	private final int angleCount;
	private final int distCount;
	private final double eps;
	private final int threshold;
	
	public LineHoughTransformation(int angleCount, int distCount, double eps, int threshold) {
		super();
		this.angleCount = angleCount;
		this.distCount = distCount;
		this.eps = eps;
		this.threshold = threshold;
	}

	@Override
	public void transform(Band band) {
		int[][] acum = new int[angleCount][distCount];
		double diag = Math.sqrt(band.getWidth() * band.getWidth() + band.getHeight() * band.getHeight());
		int max = 0;
		for (int angleIndex = 0; angleIndex < angleCount; angleIndex++) {
			double angle = getAngle(angleIndex);
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			for (int distIndex = 0; distIndex < distCount; distIndex++) {
				double dist = getDist(diag, distIndex);
				
				for (int w = 0; w < band.getWidth(); w++) {
					for (int h = 0; h < band.getHeight(); h++) {
						double pixel = band.getPixel(w, h);
						if (pixel == 255) {
							if (Math.abs(w * cos + h * sin - dist) < eps) {
								acum[angleIndex][distIndex]++;
							}	
						}
					}
				}
				
				if (acum[angleIndex][distIndex] > max) {
					max = acum[angleIndex][distIndex];
				}
			}
			
		}
		
		for (int angleIndex = 0; angleIndex < angleCount; angleIndex++) {
			for (int distIndex = 0; distIndex < distCount; distIndex++) {
//				System.out.println(String.format("angle: %g, dist: %g, count: %d", getAngle(angleIndex),
//						getDist(diag, distIndex), acum[angleIndex][distIndex]));
				if (acum[angleIndex][distIndex] > threshold) {
					drawLine(getAngle(angleIndex), getDist(diag, distIndex), band);
				}
			}
		}
	}
	
	private double getAngle(int angleIndex) {
		return 2 * Math.PI / angleCount * angleIndex;
	}
	
	private double getDist(double diag, int distIndex) {
		return diag / (double) (distCount - 1) * distIndex;
	}
	
	private void drawLine(double angle, double dist, Band band) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		for (int w = 0; w < band.getWidth(); w++) {
			for (int h = 0; h < band.getHeight(); h++) {
				if (Math.abs(w * cos + h* sin - dist) < eps) {
					band.setPixel(w, h, 200);
				}
			}
		}
	}
}
