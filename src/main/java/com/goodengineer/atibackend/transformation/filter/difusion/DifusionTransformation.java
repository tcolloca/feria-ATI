package com.goodengineer.atibackend.transformation.filter.difusion;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class DifusionTransformation implements Transformation {

	private static final double LAMBDA = 0.25;

	private int T;
	private BorderDetector borderDetector;

	public DifusionTransformation(int T, BorderDetector borderDetector) {
		this.T = T;
		this.borderDetector = borderDetector;
	}

	@Override
	public void transform(Band band) {
		for (int t = 0; t < T; t++) {
			forward(band);
		}
	}

	private void forward(Band band) {
		double[][] newPixles = new double[band.getWidth()][band.getHeight()];
		for (int x = 1; x < band.getWidth() - 1; x++) {
			for (int y = 1; y < band.getHeight() - 1; y++) {
				forwardPixel(band, newPixles, x, y);
			}
		}
		band.setPixels(newPixles);
	}

	private void forwardPixel(Band band, double[][] newPixels, int x, int y) {
		double current = band.getRawPixel(x, y);
		double Dn = band.getRawPixel(x, y - 1) - current;
		double Ds = band.getRawPixel(x, y + 1) - current;
		double De = band.getRawPixel(x + 1, y) - current;
		double Do = band.getRawPixel(x - 1, y) - current;

		double Cn = borderDetector.detect(Dn);
		double Cs = borderDetector.detect(Ds);
		double Ce = borderDetector.detect(De);
		double Co = borderDetector.detect(Do);

		newPixels[x][y] = current + LAMBDA * (Dn * Cn + Ds * Cs + De * Ce + Do * Co);
	}
}
