package com.goodengineer.atibackend.plates.hough;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.plates.Line;

public class CustomLineHough {
	
	private final double maxAngle;
	private final int slopeCount;
	private final double eps;
	private final int t1;
	private final int t2;

	private int o1Count;
	private int o2Count;
	
	public CustomLineHough(double maxAngle, double eps, int t1, int t2) {
		super();
		this.maxAngle = maxAngle;
		this.slopeCount = 50;
		this.eps = eps;
		this.t1 = t1;
		this.t2 = t2;
	}

	public List<Line> getLines(Band band) {
		double maxSlope = Math.tan(maxAngle * Math.PI / 180.0);
		o1Count = band.getHeight();
		o2Count = band.getWidth();

		int[][] acum1 = new int[slopeCount][o1Count];
		for (int slopeIndex = 0; slopeIndex < slopeCount; slopeIndex++) {
			double slope = getSlope(slopeIndex, maxSlope);
			for (int originIndex = 0; originIndex < o1Count; originIndex++) {
				double origin = getOrigin1(originIndex, band.getHeight());
				
				for (int x = 0; x < band.getWidth(); x++) {
					for (int y = 0; y < band.getHeight(); y++) {
						double pixel = band.getPixel(x, y);
						if (pixel == 255) {
							if (Math.abs(slope * x + origin - y) < eps) {
								acum1[slopeIndex][originIndex]++;
							}	
						}
					}
				}
			}
		}
		
		int[][] acum2 = new int[slopeCount][o2Count];
		for (int slopeIndex = 0; slopeIndex < slopeCount; slopeIndex++) {
			double slope = getSlope(slopeIndex, maxSlope);
			for (int originIndex = 0; originIndex < o2Count; originIndex++) {
				double origin = getOrigin2(originIndex, band.getWidth());
				for (int x = 0; x < band.getHeight(); x++) {
					for (int y = 0; y < band.getWidth(); y++) {
						double pixel = band.getPixel(y, x);
						if (pixel == 255) {
							if (Math.abs(slope * x + origin - y) < eps) {
								acum2[slopeIndex][originIndex]++;
							}	
						}
					}
				}
			}
		}
		
		List<Line> lines = new ArrayList<>();
		for (int slopeIndex = 0; slopeIndex < slopeCount; slopeIndex++) {
			for (int originIndex = 0; originIndex < o1Count; originIndex++) {
				if (acum1[slopeIndex][originIndex] > t1) {
					lines.add(new Line(getSlope(slopeIndex, maxSlope), 
							getOrigin1(originIndex, band.getHeight()), false, acum1[slopeIndex][originIndex]));
				}
			}
		}
		
		for (int slopeIndex = 0; slopeIndex < slopeCount; slopeIndex++) {
			for (int originIndex = 0; originIndex < o2Count; originIndex++) {
				if (acum2[slopeIndex][originIndex] > t2) {
					lines.add(new Line(getSlope(slopeIndex, maxSlope), 
							getOrigin2(originIndex, band.getWidth()), true, acum2[slopeIndex][originIndex]));
				}
			}
		}
		return lines;
	}
	
	private double getSlope(int slopeIndex, double maxSlope) {
		return 2 * maxSlope / (double) (slopeCount - 1) * slopeIndex - maxSlope;
	}
	
	private int getOrigin1(int originIndex, double maxLength) {
		return (int) Math.round(maxLength / (double) (o1Count - 1) * originIndex);
	}
	
	private int getOrigin2(int originIndex, double maxLength) {
		return (int) Math.round(maxLength / (double) (o2Count - 1) * originIndex);
	}
	
	public static void paintLines(Band band, List<Line> lines) {
		for (Line line : lines) {
			if (!line.isVertical()) {
				for (int x = 0; x < band.getWidth(); x++) {
					for (int y = 0; y < band.getHeight(); y++) {
						if (Math.abs(line.getSlope() * x + line.getOrigin() - y) < 1) {
							band.setPixel(x, y, 127);
						}
					}
				}
			} else {
				for (int x = 0; x < band.getHeight(); x++) {
					for (int y = 0; y < band.getWidth(); y++) {
						if (Math.abs(line.getSlope() * x + line.getOrigin() - y) < 1) {
							band.setPixel(y, x, 127);
						}
					}
				}
			}
		}
	}
}
