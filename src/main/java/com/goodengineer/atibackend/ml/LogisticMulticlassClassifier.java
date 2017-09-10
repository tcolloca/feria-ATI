package com.goodengineer.atibackend.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticMulticlassClassifier implements MulticlassClassifier {

	private static final double DEFAULT_ALPHA = 0.25;
	private static final double DEFAULT_LAMBDA = 0.001;
	
	private Map<String, Classifier> classifiers;
	private Map<String, ArrayList<TrainingExample>> trainingSets;
	
	private double alpha;
	private double lambda;

	public LogisticMulticlassClassifier() {
		this(DEFAULT_ALPHA, DEFAULT_LAMBDA);
	}
	
	public LogisticMulticlassClassifier(double alpha, double lambda) {
		this.alpha = alpha;
		this.lambda = lambda;
	}
	
	@Override
	public void train(List<MulticlassTrainingExample> trainingSet) {
		classifiers = new HashMap<>();
		trainingSets = new HashMap<>();
		for (MulticlassTrainingExample trainingExample: trainingSet) {
			if (!classifiers.containsKey(trainingExample.Y)) {
				classifiers.put(trainingExample.Y, new LogisticClassifier(alpha, lambda));
				trainingSets.put(trainingExample.Y, new ArrayList<TrainingExample>());
			}
		}
		for (MulticlassTrainingExample trainingExample: trainingSet) {
			for (String clazz: trainingSets.keySet()) {
				if (clazz.equals(trainingExample.Y)) {
					trainingSets.get(clazz).add(new TrainingExample(trainingExample.X, 1));
				} else {
					trainingSets.get(clazz).add(new TrainingExample(trainingExample.X, 0));
				}
			}
		}
		
		for (String clazz: classifiers.keySet()) {
			classifiers.get(clazz).train(trainingSets.get(clazz));
		}
		
		for (String clazz: trainingSets.keySet()) {
			double acum = 0;
			Classifier classifier = classifiers.get(clazz);
			for (TrainingExample trainingExample: trainingSets.get(clazz)) {
				if (classifier.classify(trainingExample.X) - trainingExample.Y < 1e-5) acum++;
			}
			System.out.println(clazz + " has " + ((acum/trainingSet.size())*100) + "% right");
		}
	}

	@Override
	public String classify(double[] X) {
		double max = -1;
		String ans = null;
		for (String clazz: classifiers.keySet()) {
			double prob = classifiers.get(clazz).prob(X);
			if (prob > max) {
				max = prob;
				ans = clazz;
			}
		}
		return ans;
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	public Map<String, Classifier> getClassifiers() {
		return classifiers;
	}
	
	public void setClassifiers(Map<String, Classifier> classifiers) {
		this.classifiers = classifiers;
	}
}
