package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class DecreaseResolutionTransformation implements Transformation {

	private final int size;

	public DecreaseResolutionTransformation(int size) {
		super();
		this.size = size;
	}

	@Override
	public void transform(Band band) {
		band.setPixels(band.subRegion(0, 0, (band.getWidth() / size) * size - 1, (band.getHeight() / size) * size - 1).pixels);
		for (int x = 0; x < band.getWidth() / size; x++) {
			for (int y = 0; y < band.getHeight() / size; y++) {
				double avg = 0;
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						avg += band.getRawPixel(x * size + i, y * size + j);
					}
				}
				avg /= (double) (size * size);
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						band.setRawPixel(x * size + i, y * size + j, avg);
					}
				}
			}
		}
	};
	
	
}
