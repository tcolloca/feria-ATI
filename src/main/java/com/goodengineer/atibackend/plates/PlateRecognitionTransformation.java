package com.goodengineer.atibackend.plates;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.ml.ClassifierLoader;
import com.goodengineer.atibackend.ml.FeatureExtractor;
import com.goodengineer.atibackend.ml.LogisticMulticlassClassifier;
import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.plates.util.BufferedImageColorImageTranslator;
import com.goodengineer.atibackend.plates.util.FileHelper;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.transformation.threshold.OtsuThresholdingTransformation;
import com.goodengineer.atibackend.transformation.threshold.SauvolaThresholdingTransformation;
import com.goodengineer.atibackend.util.Point;

public class PlateRecognitionTransformation implements Transformation {

	private static final double MIN_ASPECT_RATIO = 1.5;
	private static final double MAX_ASPECT_RATIO = 5;
	private static final double MIN_EULER = 3;
	private static final double MAX_EULER = 13;
	private static final double MAX_COMPONENT_SIZE_RATIO = 0.0100; 
	private static final double MIN_COMPONENT_SIZE_RATIO = 0.0008;
	private static final int PLATE_WIDTH = 228;
	private static final int PLATE_HEIGHT = 75;
	
	private LogisticMulticlassClassifier letterClassifier;
	private LogisticMulticlassClassifier numClassifier;
	
	public PlateRecognitionTransformation() {
		super();
		letterClassifier = ClassifierLoader.loadLogisticMulticlass("classifiers/lettersClassifier.txt");
		numClassifier = ClassifierLoader.loadLogisticMulticlass("classifiers/numClassifier.txt");
	}

	@Override
	public void transform(Band band) {
		Band original = band.clone();
		
		StringBuilder plateNumber = new StringBuilder();
		
		System.out.println("Thresholding with Sauvola...");
		new SauvolaThresholdingTransformation(0.5, 128, 21).transform(band);
		
		System.out.println("Finding components...");
		List<Component> rawComponents = ComponentFinder.findComponents(band, 0);
		System.out.println(rawComponents.size() + " components found.");
		
		int size = band.getHeight() * band.getWidth();
		List<Component> components = new ArrayList<>();
		System.out.println("Selecting components...");
		for (Component component : rawComponents) {	
//			minComponentSize <= component.size() 
//					&& component.size() <= maxComponentSize
//					&& 
			if (MIN_ASPECT_RATIO <= component.getAspectRatio()
					&& component.getAspectRatio() <= MAX_ASPECT_RATIO
					&& MIN_EULER <= component.eulerNumber()
					&& component.eulerNumber() <= MAX_EULER
					) {
				components.add(component);
			}
		}
		System.out.println(components.size() + " components left.");
//		double[][] pixels = new double[band.getWidth()][band.getHeight()];
//		for (Component component: components) {
//			for (int[] pixel: component.getPixels()) {
//				pixels[pixel[0]][pixel[1]] = 255;
//			}
//		}
//		band.setPixels(pixels);
		
		System.out.println("Finding corners...");
		for (Component component : components) {
			List<Point> corners = component.getCorners();
			System.out.println("get corners");
			if (corners.size() == 4) {
				Band resizedBand = ImageResizer.resizeQuad(original, corners, PLATE_WIDTH, PLATE_HEIGHT);
				List<Band> digits = DigitsExtractor.extract(resizedBand, 6);
				if (digits.size() < 6) continue;
				for (int i = 0; i < 6; i++) {
					Band digit = digits.get(i);
					new OtsuThresholdingTransformation().transform(digit);
					ColorImage image = new ColorImage(digit, digit, digit);
					BufferedImage buffImage = new BufferedImageColorImageTranslator().translateBackward(image);
					String fileName = "_temp_" + i + ".png";
					FileHelper.saveImage(buffImage, fileName);
					
					if (i <= 2) {
//						letter
						plateNumber.append(letterClassifier.classify(FeatureExtractor.extract(fileName)));
					} else {
//						digit
						plateNumber.append(numClassifier.classify(FeatureExtractor.extract(fileName)));
					}
				}
			}
		}
		System.out.println("Finished! Plate number is: " + plateNumber.toString());
	}
}
