package com.goodengineer.atibackend.transformation.threshold;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class GlobalThresholdingTransformation implements Transformation {

	private final int dt;

	public GlobalThresholdingTransformation() {
		this.dt = 1;
	}

	public GlobalThresholdingTransformation(int dt) {
		this.dt = dt;
	}

	@Override
	public void transform(Band band) {
		boolean flag = false;
		int T = 128;
		while (!flag) {
			Band clone = band.clone();
			new ThresholdingTransformation(T).transform(clone);
			double m1 = 0;
			double m2 = 0;
			int blacks = 0;
			int whites = 0;
			for (int x = 0; x < clone.getWidth(); x++) {
				for (int y = 0; y < clone.getHeight(); y++) {
					double pixel = clone.getRawPixel(x, y);
					if (clone.isValidMin(pixel)) {
						blacks++;
						m1 += band.getPixel(x, y);
					} else {
						whites++;
						m2 += band.getPixel(x, y);
					}
				}
			}

			m1 = m1 / blacks;
			m2 = m2 / whites;
			int newT = (int)((m1 + m2)/2.0);
			if (Math.abs(T - newT) < dt) {
				flag = true;
			} else {
				T = newT;
			}
		}
		System.out.println(String.format("Band: %s, Threshold: %d", band.getName(), T));
		new ThresholdingTransformation(T).transform(band);
	}
}
