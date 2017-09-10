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

import model.ImageManager;
import view.InfoPanel;

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
	private ImageManager imageManager;
	private InfoPanel infoPanel;
	
	public PlateRecognitionTransformation(ImageManager imageManager, InfoPanel infoPanel) {
		super();
		letterClassifier = ClassifierLoader.loadLogisticMulticlass("classifiers/lettersClassifier.txt");
		numClassifier = ClassifierLoader.loadLogisticMulticlass("classifiers/numClassifier.txt");
		this.imageManager = imageManager;
		this.infoPanel = infoPanel;
	}

	@Override
	public void transform(Band band) {
		Band aux, original = band.clone();
		
		StringBuilder plateNumber = new StringBuilder();
		
		infoPanel.setText("Thresholding with Sauvola...");
		new SauvolaThresholdingTransformation(imageManager, 0.5, 128, 21).transform(band);
		
		List<Component> rawComponents = ComponentFinder.findComponents(band, 0);
		infoPanel.setText(rawComponents.size() + " components found.");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		int size = band.getHeight() * band.getWidth();
		List<Component> components = new ArrayList<>();
		for (Component component : rawComponents) {	
			if (MIN_ASPECT_RATIO <= component.getAspectRatio()
					&& component.getAspectRatio() <= MAX_ASPECT_RATIO
					&& MIN_EULER <= component.eulerNumber()
					&& component.eulerNumber() <= MAX_EULER
					) {
				components.add(component);
			}
		}
		aux = band.newBlack();
		for (Component component : components) {
			for (int[] pixel : component.getPixels()) {
				aux.setPixel(pixel[0], pixel[1], 255);
			}
		}
		imageManager.update(aux);
		
		infoPanel.setText(components.size() + " components left.");
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		
//		double[][] pixels = new double[band.getWidth()][band.getHeight()];
//		for (Component component: components) {
//			for (int[] pixel: component.getPixels()) {
//				pixels[pixel[0]][pixel[1]] = 255;
//			}
//		}
//		band.setPixels(pixels);
		
		for (Component component : components) {
			List<Point> corners = component.getCorners(imageManager, infoPanel);
			if (corners.size() == 4) {
				Band resizedBand = ImageResizer.resizeQuad(original, corners, PLATE_WIDTH, PLATE_HEIGHT);
				
				imageManager.update(resizedBand);
				infoPanel.setText("Extracted area.");
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
				}
				
				infoPanel.setText("Extracting digits...");
				List<Band> digits = DigitsExtractor.extract(imageManager, infoPanel, resizedBand, 6);
				if (digits.size() < 6) {
					infoPanel.setText("Not a plate!");
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
					continue;
				}
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
				break;
			}
		}
		if (plateNumber.toString().trim().isEmpty()) {
			infoPanel.setText("Finished! Plate not found :(");
		} else {
			infoPanel.setText("Finished! Plate number is: " + plateNumber.toString().toUpperCase());
		}
	}
}
