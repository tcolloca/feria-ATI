package com.goodengineer.atibackend.ml;

import java.util.List;

public interface MulticlassClassifier {
	void train(List<MulticlassTrainingExample> trainingSet);
	String classify(double[] X);
}
