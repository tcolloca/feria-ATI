package com.goodengineer.atibackend.util;


public class Statistics {
	final double[] data;
	final int size;
	final double mean;
	final double variance;
	
	public Statistics(double[] data) {
		this.data = data;
		size = data.length;
		
		double sum = 0;
		double sumSq = 0;
		for (double a : data) {
			sum += a;
			sumSq += a*a;
		}
		mean = sum / size;
		variance = (sumSq - (sum*sum) / size) / size;
	}

	public double getMean() {
		return mean;
	}

	public double getVariance() {
		return variance;
	}

	public double getStdDev() {
		return Math.sqrt(getVariance());
	}
}
