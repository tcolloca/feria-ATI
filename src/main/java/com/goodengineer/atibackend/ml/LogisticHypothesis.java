package com.goodengineer.atibackend.ml;

public class LogisticHypothesis implements Hypothesis {

	@Override
	public double eval(double[] theta, double[] X) {
		double acum = theta[0];
		for (int i = 1; i < theta.length; i++) {
			acum += (theta[i]* X[i - 1]);
		}
		
		return 1.0 / (1 + Math.pow(Math.E, -acum));
	}
}
