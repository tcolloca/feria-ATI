package com.goodengineer.atibackend.plates;

import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.util.EquationSolver;
import com.goodengineer.atibackend.util.Point;
import com.goodengineer.atibackend.util.VectorUtils;

public class ImageResizer {

	public static Band resizeQuad(Band band, List<Point> corners, int width, int height) {
		Point minCorner = corners.get(0);
		Point maxCorner = corners.get(0);
		Point topCorner = null;
		Point bottomCorner = null;
		for (Point corner : corners) {
			if (corner.x + corner.y < minCorner.x + minCorner.y) {
				minCorner = corner;
			}
			if (corner.x + corner.y > maxCorner.x + maxCorner.y) {
				maxCorner = corner;
			}
		}
		for (Point corner : corners) {
			if (corner != minCorner && corner != maxCorner) {
				if (topCorner == null) {
					topCorner = corner;
				} else {
					bottomCorner = corner;
				}
			}
		}
		if (bottomCorner.y > topCorner.y) {
			Point aux = bottomCorner;
			bottomCorner = topCorner;
			topCorner = aux;
		}
		
		double[][] T1 = new double[2][2];
		double[][] T2 = new double[2][2];
		
		double[][] m = new double[2][3];
		m[0][0] = maxCorner.x - minCorner.x;
		m[0][1] = maxCorner.y - minCorner.y;
		m[0][2] = width;
		m[1][0] = bottomCorner.x - minCorner.x;
		m[1][1] = bottomCorner.y - minCorner.y;
		m[1][2] = width;
		T1[0] = EquationSolver.solve(m);
		
		m[0][2] = height;
		m[1][2] = 0;
		T1[1] = EquationSolver.solve(m);
		
		m[1][0] = topCorner.x - minCorner.x;
		m[1][1] = topCorner.y - minCorner.y;
		m[0][2] = width;
		m[1][2] = 0;
		T2[0] = EquationSolver.solve(m);
		
		m[1][2] = height;
		m[0][2] = height;
		T2[1] = EquationSolver.solve(m);
		
		T1 = VectorUtils.inv(T1);
		T2 = VectorUtils.inv(T2);
		
		double[][] pixels = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double[] mappedPoint;
				if (j < i * height / (double) width) {
					mappedPoint = VectorUtils.multiply(T1, new double[] {i, j});
				} else {
					mappedPoint = VectorUtils.multiply(T2, new double[] {i, j});
				}
				mappedPoint[0] += minCorner.x;
				mappedPoint[1] += minCorner.y;
				pixels[i][j] = getPixel(band, mappedPoint);
			}
		}
		
		return new Band(pixels);
	}

	private static double getPixel(Band band, double[] mappedPoint) {
		double pX = mappedPoint[0] - Math.floor(mappedPoint[0]);
		int floorX = (int) Math.floor(mappedPoint[0]);
		int ceilX = pX == 0 ? floorX : floorX + 1;
		double pY = mappedPoint[1] - Math.floor(mappedPoint[1]);
		int floorY = (int) Math.floor(mappedPoint[1]);
		int ceilY = pY == 0 ? floorY : floorY + 1;
		
		double p1 = band.getRawPixel(floorX, floorY);
		double p2 = band.getRawPixel(ceilX, floorY);
		double p3 = band.getRawPixel(floorX, ceilY);
		double p4 = band.getRawPixel(ceilX, ceilY);
		
		return (1 - pX) * (1 - pY) * p1 
				+ pX * (1 - pY) * p2 
				+ (1 - pX) * pY * p3 
				+ pX *pY * p4;
	}

}
