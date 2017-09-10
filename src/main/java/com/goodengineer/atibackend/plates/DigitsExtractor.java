package com.goodengineer.atibackend.plates;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.threshold.OtsuThresholdingTransformation;
import com.goodengineer.atibackend.util.Point;

public class DigitsExtractor {
	
	private static final double MIN_ASPECT_RATIO = 1/3.0;
	private static final double MAX_ASPECT_RATIO = 2/3.0;
	private static final int DIGIT_WIDTH = 9;
	private static final int DIGIT_HEIGHT = 12;
	
	public static List<Band> extract(Band band, int digitsAmount) {
		Band original = band.clone();
		
		new OtsuThresholdingTransformation().transform(band);
		List<Band> digits = new ArrayList<>();
		int whiteCount = 0;
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				if (band.getPixel(x, y) == 255) {
					whiteCount++;
				}
			}
		}
		System.out.println(whiteCount);
		if (whiteCount < 1200 || whiteCount > 11000) {
			return digits;
		}
		
		System.out.println("Finding components...");
		List<Component> rawComponents = ComponentFinder.findComponents(band, 255);
		System.out.println(rawComponents.size() + " components found.");
		
		List<Component> components = new ArrayList<>();
		System.out.println("Selecting components...");
		for (Component component : rawComponents) {	
			System.out.println(component.size());
			if (200 <= component.size() 
					&& component.size() <= 1500
					&& component.height() > 32
					&& MIN_ASPECT_RATIO <= component.getAspectRatio()
					&& component.getAspectRatio() <= MAX_ASPECT_RATIO) {
				components.add(component);
			}
		}
		System.out.println(components.size() + " components left.");
		
		if (components.size() < digitsAmount || components.size() > digitsAmount + 2) {
			return digits;
		}
		
		System.out.println("Finding corners...");
		for (Component component : components) {
			List<Point> corners = component.getRectangleCorners();
			Band resizedBand = ImageResizer.resizeQuad(original, corners, DIGIT_WIDTH, DIGIT_HEIGHT);
			digits.add(resizedBand);
		}
		return digits;
//		System.out.println("Finished!");
	}
}
