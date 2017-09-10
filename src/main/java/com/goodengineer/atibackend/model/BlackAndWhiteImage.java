package com.goodengineer.atibackend.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.goodengineer.atibackend.transformation.Transformation;

public class BlackAndWhiteImage implements Image {
	
	private Band grays;
	
	public BlackAndWhiteImage(Band grays) {
		this.grays = grays;
	}
	
	@Override
	public void transform(Transformation transformation) {
		transformation.transform(grays);
	}
	
	public int getPixel(int x, int y) {
		return grays.getPixel(x, y);
	}

	@Override
	public int getWidth() {
		return grays.getWidth();
	}

	@Override
	public int getHeight() {
		return grays.getHeight();
	}
	
	public Band getBand() {
		return grays;
	}

	@Override
	public Image clone() {
		Band red = this.grays.clone();
		return new BlackAndWhiteImage(red);
	}

	@Override
	public List<Band> getBands() {
		return Arrays.asList(grays);
	}

	@Override
	public double[] getColor(int x, int y) {
		return new double[] {getPixel(x, y)};
	}
}
