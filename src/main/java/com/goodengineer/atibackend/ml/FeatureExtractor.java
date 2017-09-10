package com.goodengineer.atibackend.ml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FeatureExtractor {
	
	public static double[] extract(String imgPath) {
		return extract(new File(imgPath));
	}
	
	public static double[] extract(File imgFile) {
		BufferedImage img = null;

		try 
		{
		    img = ImageIO.read(imgFile);
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
		double[] X1 = new double[img.getWidth()];
		for (int x = 0; x < img.getWidth(); x++) {
			double acum = 0;
			for (int y = 0; y < img.getHeight(); y++) {
				if ((img.getRGB(x, y) & 0x0000FF) > 0) acum++;
			}
			X1[x] = acum/img.getHeight();
		}
		
		double[] X2 = new double[img.getHeight()];
		for (int y = 0; y < img.getHeight(); y++) {
			double acum = 0;
			for (int x = 0; x < img.getWidth(); x++) {
				if ((img.getRGB(x, y) & 0x0000FF) > 0) acum++;
			}
			X2[y] = acum/img.getWidth();
		}
		double[] X = new double[img.getWidth() + img.getHeight()];
		System.arraycopy(X1, 0, X, 0, X1.length);
		System.arraycopy(X2, 0, X, X1.length, X2.length);
		return X;
	}
}
