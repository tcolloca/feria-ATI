package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class PaintPixelTransformation implements Transformation {

	private int x;
	private int y;
	private int color;

	public PaintPixelTransformation(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public void transform(Band band) {
		band.setPixel(x, y, color);
	}
}
