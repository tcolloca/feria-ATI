package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.util.Function;
import com.goodengineer.atibackend.util.LinearFunction;

/**
 * I really doubt this transformation is well implemented. Ask Juliana if the
 * result looks ok.
 */
public class ConstrastTransformation implements Transformation {

	private int r1;
	private int r2;

	public ConstrastTransformation(int r1, int r2) {
		this.r1 = r1;
		this.r2 = r2;
	}

	@Override
	public void transform(Band band) {
		final double rawR1 = band.map(r1);
		final double rawR2 = band.map(r2);

		double s1 = (rawR1 + band.getValidMin()) / 2.0;
		double s2 = (rawR2 + band.getValidMax()) / 2.0;

		final Function<Double, Double> f1 = new LinearFunction(band.getValidMin(), band.getValidMin(), rawR1, s1);
		final Function<Double, Double> f2 = new LinearFunction(rawR1, s1, rawR2, s2);
		final Function<Double, Double> f3 = new LinearFunction(rawR2, s2, band.getValidMax(), band.getValidMax());

		Function<Double, Double> function = new Function<Double, Double>() {
			@Override
			public Double apply(Double x) {
				if (x <= rawR1) {
					return f1.apply(x);
				} else if (x <= rawR2) {
					return f2.apply(x);
				}
				return f3.apply(x);
			}
		};

		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				band.setRawPixel(x, y, function.apply(band.getRawPixel(x, y)));
			}
		}
	}
}
