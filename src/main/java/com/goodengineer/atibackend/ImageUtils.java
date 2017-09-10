package com.goodengineer.atibackend;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.BlackAndWhiteImage;
import com.goodengineer.atibackend.model.ColorImage;

import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

	public static BlackAndWhiteImage crop(BlackAndWhiteImage image, int startX, int startY, int width, int height) {
		return new BlackAndWhiteImage(crop(image.getBand(), startX, startY, width, height));
	}

	public static ColorImage crop(ColorImage image, int startX, int startY, int width, int height) {
		List<Band> newBands = new ArrayList<>();
		for (Band band : image.getBands()) {
			newBands.add(crop(band, startX, startY, width, height));
		}
		return new ColorImage(newBands.get(0), newBands.get(1), newBands.get(2));
	}

	public static Band crop(Band band, int startX, int startY, int width, int height) {
		Band newBand = new Band(new double[width][height]);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				newBand.setRawPixel(x, y, band.getRawPixel(startX + x, startY + y));
			}
		}
		return newBand;
	}

	public static float[] createHistogram(Band band) {
		float[] histogram = new float[256];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				histogram[band.getPixel(x, y)]++;
			}
		}
		int totalPixels = band.getWidth() * band.getHeight();
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = histogram[i] / totalPixels;
		}
		return histogram;
	}
}
