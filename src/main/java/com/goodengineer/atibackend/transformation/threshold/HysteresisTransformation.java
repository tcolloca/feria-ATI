package com.goodengineer.atibackend.transformation.threshold;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class HysteresisTransformation implements Transformation {

	private int l1;
	private int l2;
	
	public HysteresisTransformation(int l1, int l2) {
		this.l1 = l1;
		this.l2 = l2;
	}
	
	@Override
	public void transform(Band band) { 
		double realL1 = band.map(l1);
		double realL2 = band.map(l2);
		double min = band.getValidMin();
		boolean[][] visited = new boolean[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				if (visited[x][y]) continue;
				if (band.getRawPixel(x, y) < realL1) {
					band.setRawPixel(x, y, min);
					visited[x][y] = true;
				}
				else if (band.getRawPixel(x, y) > realL2) {
					band.setRawPixel(x, y, band.getValidMax());
					visited[x][y] = true;
					markBorders(band, x+1, y, visited);
					markBorders(band, x-1, y, visited);
					markBorders(band, x, y+1, visited);
					markBorders(band, x, y-1, visited);
				}
			}
		}
		
//		Get rid of isolated l1<p<l2 pixels
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				if (!visited[x][y]) band.setRawPixel(x, y, min);
			}
		}
	}
	
	private void markBorders(Band band, int x, int y, boolean[][] visited) {
		if (x < 0 || y < 0 || x >= band.getWidth() || y >= band.getHeight()) return;
		if (visited[x][y]) return;
		double p = band.getRawPixel(x, y);
		double realL1 = band.map(l1);
		double realL2 = band.map(l2);
		if (!(realL1 <= p && p <= realL2)) return;
		visited[x][y] = true;
		band.setRawPixel(x, y, band.getValidMax());
		markBorders(band, x + 1, y, visited);
		markBorders(band, x - 1, y, visited);
		markBorders(band, x, y + 1, visited);
		markBorders(band, x, y - 1, visited);
	}
}
