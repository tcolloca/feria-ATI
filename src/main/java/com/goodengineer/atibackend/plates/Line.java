package com.goodengineer.atibackend.plates;

public class Line {

	private final double slope;
	private final int origin;
	private final boolean isVertical;
	private final int count;

	public Line(double slope, int origin, boolean isVertical) {
		super();
		this.slope = slope;
		this.origin = origin;
		this.isVertical = isVertical;
		this.count = 0;
	}
	
	public Line(double slope, int origin, boolean isVertical, int count) {
		super();
		this.slope = slope;
		this.origin = origin;
		this.isVertical = isVertical;
		this.count = count;
	}

	public double getSlope() {
		return slope;
	}

	public int getOrigin() {
		return origin;
	}

	public boolean isVertical() {
		return isVertical;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return "Line [slope=" + slope + ", origin=" + origin + ", isVertical="
				+ isVertical + ", count=" + count + "]";
	}
}
