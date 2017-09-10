package com.goodengineer.atibackend.ml;

import java.util.List;

public interface Classifier {
	void train(List<TrainingExample> trainingSet);
	int classify(double[] X);
	double prob(double[] X);
	double[] theta();
	void setTheta(double[] theta);
	List<double[]> normalization();
	void setNormalization(List<double[]> normalization);
}
