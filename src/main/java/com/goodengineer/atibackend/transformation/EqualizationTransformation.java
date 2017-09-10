package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.ImageUtils;
import com.goodengineer.atibackend.model.Band;

public class EqualizationTransformation implements Transformation {

	@Override
	public void transform(Band band) {
		band.normalize();
		float[] histogram = ImageUtils.createHistogram(band);
		float[] relativeAcum = new float[256];
		relativeAcum[0] = histogram[0];
		for (int i = 1; i < histogram.length; i++) {
			relativeAcum[i] = histogram[i] + relativeAcum[i - 1];
		}

		int minColor = (int) band.getMin();
		float fMin = relativeAcum[minColor];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				int currentColor = band.getPixel(x, y);
				int color = Math.round(255 * ((relativeAcum[currentColor] - fMin) / (1 - fMin)));
				band.setRawPixel(x, y, color);
			}
		}
	}
}
