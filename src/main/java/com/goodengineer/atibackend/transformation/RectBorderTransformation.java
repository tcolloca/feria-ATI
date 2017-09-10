package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class RectBorderTransformation implements Transformation {

	private int left;
	private int top;
	private int right;
	private int bottom;
	private int color;
	
	public RectBorderTransformation(int left, int top, int right, int bottom, int color) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.color = color;
	}

	public RectBorderTransformation(RectBorderTransformation rectBorderTransformation, int color) {
		this.left = rectBorderTransformation.left;
		this.top = rectBorderTransformation.top;
		this.right = rectBorderTransformation.right;
		this.bottom = rectBorderTransformation.bottom;
		this.color = color;
	}

	/**
	 * Draws an empty rectangle in the specified position
	 */
	@Override
	public void transform(Band band) {
		for (int x = left; x <= right; x++) {
			band.setPixel(x, top, color);
			band.setPixel(x, bottom, color);
		}
		for (int y = top; y <= bottom; y++) {
			band.setPixel(left, y, color);
			band.setPixel(right, y, color);
		}
	}

	public int getColor() {
		return color;
	}
}
