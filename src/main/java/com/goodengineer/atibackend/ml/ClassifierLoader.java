package com.goodengineer.atibackend.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class ClassifierLoader {
	
	public static LogisticMulticlassClassifier loadLogisticMulticlass(String path) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(path));
			sc.useLocale(Locale.US);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LogisticMulticlassClassifier classifier = new LogisticMulticlassClassifier();
		Map<String, Classifier> classifiers = new HashMap<>();
		
		int classifiersAmount = sc.nextInt();
		int valuesPerClassifier = sc.nextInt();
		for (int i = 0; i < classifiersAmount; i++) {
			String clazz = sc.next();
			double[] theta = new double[valuesPerClassifier];
			for (int k = 0; k < valuesPerClassifier; k++) {
				theta[k] = sc.nextDouble();
			}
			List<double[]> normalization = new ArrayList<>();
			for (int k = 0; k < valuesPerClassifier - 1; k++) {
				double[] normalizationPair = new double[2];
				normalizationPair[0] = sc.nextDouble();
				normalizationPair[1] = sc.nextDouble();
				normalization.add(normalizationPair);
			}
			LogisticClassifier lc = new LogisticClassifier();
			lc.setTheta(theta);
			lc.setNormalization(normalization);
			classifiers.put(clazz, lc);
		}
		
		sc.close();
		
		classifier.setClassifiers(classifiers);
		return classifier;
	}
	
	public static void saveLogisticMulticlass(String path, LogisticMulticlassClassifier classifier) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Map<String, Classifier> classifiers = classifier.getClassifiers();
		int thetaSize = classifiers.values().iterator().next().theta().length;
		writer.println(classifiers.size() + " " + thetaSize);
		for (String clazz: classifiers.keySet()) {
			writer.println(clazz);
			double[] theta = classifiers.get(clazz).theta();
			for (double value: theta) {
				writer.print(value + " ");
			}
			writer.println();
			
			List<double[]> normalization = classifiers.get(clazz).normalization();
			for (double[] normalizationPair: normalization) {
				writer.print(normalizationPair[0] + " " + normalizationPair[1] + " ");
			}
			writer.println();
		}
		writer.close();
	}
}
