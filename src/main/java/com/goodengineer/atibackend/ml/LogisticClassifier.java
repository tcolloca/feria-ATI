package com.goodengineer.atibackend.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogisticClassifier implements Classifier {

	private static final double DIF = 1e-5;
	private static final int MAX_NO_DIF = 25;
	
	private static final double DEFAULT_ALPHA = 0.25;
	private static final double DEFAULT_LAMBDA = 0.001;

	private double[] theta;
	private List<double[]> normalization;
	private final Hypothesis hypothesis = new LogisticHypothesis();
	private final Cost cost = new LogisticCost();
	
	private double alpha;
	private double lambda;
	
	public LogisticClassifier() {
		this(DEFAULT_ALPHA, DEFAULT_LAMBDA);
	}
	
	public LogisticClassifier(double alpha, double lambda) {
		this.alpha = alpha;
		this.lambda = lambda;
	}

	@Override
	public void train(List<TrainingExample> trainingSet) {
		// Init theta
		int m = trainingSet.size();
		int featuresAmount = trainingSet.get(0).X.length;
		theta = new double[featuresAmount + 1];
		int iterationsStuck = 0;
		normalization = normalize(trainingSet);
		List<TrainingExample> normalizedTrainingSet = new ArrayList<>();
		for (TrainingExample trainingExample: trainingSet) {
			normalizedTrainingSet.add(new TrainingExample(normalize(trainingExample.X), trainingExample.Y));
		}

		double error = J.evalReg(theta, cost, hypothesis, normalizedTrainingSet, lambda);
		int iteration = 1;
		while (error >= 1e-5) {
			System.out.println(String.format("iteration %d, error: %.10f, theta: %s", iteration, error, Arrays.toString(theta)));

			double[] newTheta = new double[theta.length];

			for (int j = 0; j < theta.length; j++) {
				newTheta[j] = theta[j]*(1 - (alpha*lambda/m)) - alpha * partialDerivative(normalizedTrainingSet, j);
			}

			System.arraycopy(newTheta, 0, theta, 0, theta.length);
			iteration++;
			double newError = J.evalReg(theta, cost, hypothesis, normalizedTrainingSet, lambda);
			if (Math.abs(newError - error) >= DIF)
				iterationsStuck = 0;
			else
				iterationsStuck++;

			error = newError;
			
			if (iterationsStuck >= MAX_NO_DIF)
				break;

//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	@Override
	public int classify(double[] X) {
		if (theta == null || X == null || theta.length - 1 != X.length)
			throw new IllegalStateException();
		return prob(X) >= 0.5 ? 1 : 0;
	}

	private double partialDerivative(List<TrainingExample> trainingSet, int j) {
		double acum = 0;
		for (int i = 0; i < trainingSet.size(); i++) {
			double x = j == 0 ? 1 : trainingSet.get(i).X[j - 1];
			acum += ((hypothesis.eval(theta, trainingSet.get(i).X) - trainingSet.get(i).Y) * x);
		}
		return acum;
	}

	@Override
	public double prob(double[] X) {
		return hypothesis.eval(theta, normalize(X));
	}
	
	private double[] normalize(double[] X) {
		double[] ans = new double[X.length];
		for (int i = 0; i < X.length; i++) {
			double min = normalization.get(i)[0];
			double max = normalization.get(i)[1];
			ans[i] = ((X[i] - min)/(max - min))*2 - 1;
		}
		return ans;
	}
	
	private static List<double[]> normalize(List<TrainingExample> dataSet) {
		List<double[]> normalization = new ArrayList<>();
		int featuresAmount = dataSet.get(0).X.length;
		int m = dataSet.size();
		for (int i = 0; i < featuresAmount; i++) {
			double min = dataSet.get(0).X[i];
			double max = dataSet.get(0).X[i];
			for (TrainingExample trainingExample: dataSet) {
				if (trainingExample.X[i] < min) {
					min = trainingExample.X[i];
				}
				if (trainingExample.X[i] > max) {
					max = trainingExample.X[i];
				}
			}
			normalization.add(new double[]{min, max});
		}
		return normalization;
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public double[] theta() {
		return theta;
	}

	@Override
	public void setTheta(double[] theta) {
		this.theta = theta;
	}

	@Override
	public void setNormalization(List<double[]> normalization) {
		this.normalization = normalization;
	}

	@Override
	public List<double[]> normalization() {
		return normalization;
	}
}
