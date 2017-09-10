package com.goodengineer.atibackend.plates;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.threshold.OtsuThresholdingTransformation;
import com.goodengineer.atibackend.util.Point;

import model.ImageManager;
import view.InfoPanel;

public class DigitsExtractor {
	
	private static final double MIN_ASPECT_RATIO = 1/3.0;
	private static final double MAX_ASPECT_RATIO = 2/3.0;
	private static final int DIGIT_WIDTH = 9;
	private static final int DIGIT_HEIGHT = 12;
	
	public static List<Band> extract(ImageManager imageManager, InfoPanel infoPanel, Band band, int digitsAmount) {
		Band aux, original = band.clone();
		
		new OtsuThresholdingTransformation().transform(band);
		imageManager.update(band);
		List<Band> digits = new ArrayList<>();
		int whiteCount = 0;
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				if (band.getPixel(x, y) == 255) {
					whiteCount++;
				}
			}
		}
		if (whiteCount < 1200 || whiteCount > 11000) {
			return digits;
		}
		
		List<Component> rawComponents = ComponentFinder.findComponents(band, 255);
		infoPanel.setText(rawComponents.size() + " components found.");
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		
		List<Component> components = new ArrayList<>();
		for (Component component : rawComponents) {	
			if (200 <= component.size() 
					&& component.size() <= 1500
					&& component.height() > 32
					&& MIN_ASPECT_RATIO <= component.getAspectRatio()
					&& component.getAspectRatio() <= MAX_ASPECT_RATIO) {
				components.add(component);
			}
		}
		infoPanel.setText(components.size() + " components left.");
		
		aux = band.newBlack();
		for (Component component : components) {
			for (int[] pixel : component.getPixels()) {
				aux.setPixel(pixel[0], pixel[1], 255);
			}
		}
		imageManager.update(aux);
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		
		if (components.size() < digitsAmount || components.size() > digitsAmount + 2) {
			return digits;
		}
		
		for (Component component : components) {
			List<Point> corners = component.getRectangleCorners();
			Band resizedBand = ImageResizer.resizeQuad(original, corners, DIGIT_WIDTH, DIGIT_HEIGHT);
			digits.add(resizedBand);
		}
		return digits;
//		System.out.println("Finished!");
	}
}
