package com.goodengineer.atibackend.util;

public class VectorUtils {

	public static double[] sum(double[] v1, double[] v2) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] + v2[i];
		}
		return v;
	}
	
	public static double[] sub(double[] v1, double[] v2) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] - v2[i];
		}
		return v;
	}
	
	public static double[] multiply(double[] v1, double k) {
		double[] v = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			v[i] = v1[i] * k;
		}
		return v;
	}
	
	public static double norm2(double[] v) {
		double acum = 0;
		for (int i = 0; i < v.length; i++) {
			acum += v[i] * v[i];
		}
		return Math.sqrt(acum);
	}

	public static double[] multiply(double[][] A, double[] v) {
		double[] u = new double[A.length];
		for (int i = 0; i < A.length; i++) {
			double acum = 0;
			for (int j = 0; j < A[0].length; j++) {
				acum += A[i][j] * v[j];
			}
			u[i] = acum;
		}
		return u;
	}

	public static double[][] inv(double[][] m) {
		double[][] ans = new double[2][2];
		double a = m[0][0];
		double b = m[0][1];
		double c = m[1][0];
		double d = m[1][1];
		double det = a*d - b*c;
		ans[0][0] = d / det;
		ans[0][1] = -b / det;
		ans[1][0] = -c / det;
		ans[1][1] = a / det;
		return ans;
	}
}
