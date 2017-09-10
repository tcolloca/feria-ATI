package com.goodengineer.atibackend.util;


public class EquationSolver {

	public static double[] solve(double[][] matrix) {
		double[] ans = new double[2];
		double a = matrix[0][0];
		double b = matrix[0][1];
		double c = matrix[0][2];
		double d = matrix[1][0];
		double e = matrix[1][1];
		double f = matrix[1][2];
		ans[1] = (f - c * d / a) / (e - b * d / a);
		ans[0] = (c - ans[1] * b) / a;
		return ans;
	}
}
