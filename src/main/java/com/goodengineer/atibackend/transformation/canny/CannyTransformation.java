package com.goodengineer.atibackend.transformation.canny;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.transformation.filter.FilterTransformation;
import com.goodengineer.atibackend.transformation.threshold.HysteresisTransformation;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.Statistics;
import com.sun.org.glassfish.external.statistics.Statistic;

public class CannyTransformation implements Transformation {

	private int size;
	private double sigma;
	private int l1;
	private int l2;
	
	public CannyTransformation(int size, double sigma, int l1, int l2) {
		this.size = size;
		this.sigma = sigma;
		this.l1 = l1;
		this.l2 = l2;
	}
	
	@Override
	public void transform(Band band) {
		double[] data = new double[band.getWidth() * band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				data[x * band.getHeight() + y] = band.getPixel(x, y);
			}
		}
		Statistics stats = new Statistics(data);
		int l1 = (int) (stats.getMean() - stats.getStdDev()); 
		int l2 = (int) (stats.getMean() + stats.getStdDev());
		l1 = l1 < 50 ? 50 : l1;
		l2 = l2 > 200 ? 200 : l2;
		new FilterTransformation(MaskFactory.gauss(size, sigma)).transform(band);
		new NoMaxTransformation().transform(band);
		new HysteresisTransformation(l1, l2).transform(band);
		
		
		System.out.println("min: " + band.getMin());
		System.out.println("max: " + band.getMax());
	}
}
