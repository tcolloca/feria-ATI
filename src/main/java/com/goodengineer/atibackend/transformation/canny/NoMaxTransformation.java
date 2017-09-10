package com.goodengineer.atibackend.transformation.canny;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.transformation.filter.FilterTransformation;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.MaskFactory.Direction;

public class NoMaxTransformation implements Transformation {

	private enum PHI {
		P0(0, 1),
		P45(1, -1),
		P90(1, 0),
		P135(1, 1);
		
		public final int dx;
		public final int dy;
		
		PHI(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}
	
	@Override
	public void transform(Band band) {
		Band dx = band.clone();
		Band dy = band.clone();
		new FilterTransformation(MaskFactory.sobel(Direction.E)).transform(dx);
		new FilterTransformation(MaskFactory.sobel(Direction.S)).transform(dy);
		
		double[][] pixels = new double[band.getWidth()][band.getHeight()];
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				pixels[x][y] = borderValue(dx, dy, x, y);
			}
		}
		band.setPixels(pixels);
	}
	
	public static double borderValue(Band dx, Band dy, int x, int y) {
		if (!isBorder(dx, dy, x, y)) return 0;
		double py = dy.getRawPixel(x, y);
		double px = dx.getRawPixel(x, y);
		PHI phi = createPhi(px, py);
		
		int x1 = x + phi.dx;
		int y1 = y + phi.dy;
		int x2 = x - phi.dx;
		int y2 = y - phi.dy;
		
		if (x1 >= 0 && x1 < dx.getWidth() && y1 >= 0 && y1 < dx.getHeight()) {
			if (magnitude(dx, dy, x1, y1) > magnitude(dx, dy, x, y)) return 0;
		}
		if (x2 >= 0 && x2 < dx.getWidth() && y2 >= 0 && y2 < dx.getHeight()) {
			if (magnitude(dx, dy, x2, y2) > magnitude(dx, dy, x, y)) return 0;
		}
		
		return magnitude(dx, dy, x, y);
	}
	
	private static boolean isBorder(Band dx, Band dy, int x, int y) {
		return magnitude(dx, dy, x, y) > 1e-2;
	}
	
	private static double magnitude(Band dx, Band dy, int x, int y) {
		return Math.sqrt(dx.getRawPixel(x, y)*dx.getRawPixel(x, y) + dy.getRawPixel(x, y)*dy.getRawPixel(x, y));
	}
	
	private static PHI createPhi(double x, double y) {
		double degrees = Math.toDegrees(Math.atan2(y, x));
		if (degrees < 0.0) {
			degrees += 360.0;
		}
		return createPhi(degrees);
	}
	
	private static PHI createPhi(double degrees) {
		if (degrees >= 180) return createPhi(degrees - 180);
		
		if (degrees <= 22.5 || degrees >= 157.5) {
//			yellow = 0
			return PHI.P90;
		} else if (degrees <= 67.5) {
//			green = 45
			return PHI.P135;
		} else if (degrees <= 112.5) {
//			blue = 90
			return PHI.P0;
		} else {
//			red = 135
			return PHI.P45;
		}
	}
}
