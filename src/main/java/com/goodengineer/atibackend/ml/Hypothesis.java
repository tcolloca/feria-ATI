package com.goodengineer.atibackend.ml;

public interface Hypothesis {
	double eval(double[] theta, double[] X);
}
