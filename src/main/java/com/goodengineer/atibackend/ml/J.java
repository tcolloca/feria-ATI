package com.goodengineer.atibackend.ml;

import java.util.List;

public class J {
	public static double eval(double[] theta, Cost cost, Hypothesis h, List<TrainingExample> dataSet) {
		int m  = dataSet.size();
		double acum = 0;
		for (TrainingExample trainingExample: dataSet) {
			acum += cost.eval(h.eval(theta, trainingExample.X), trainingExample.Y);
		}
		return acum/m;
	}
	
	public static double evalReg(double[] theta, Cost cost, Hypothesis h, List<TrainingExample> dataSet, double lambda) {
		int m  = dataSet.size();
		double acum = 0;
		for (TrainingExample trainingExample: dataSet) {
			acum += cost.eval(h.eval(theta, trainingExample.X), trainingExample.Y);
		}
		for (int i = 0; i < theta.length; i++) {
			acum += (lambda/2.0)*theta[i]*theta[i];
		}
		return acum/m;
	}
}
