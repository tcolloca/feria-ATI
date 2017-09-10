package com.goodengineer.atibackend.video;

import static com.goodengineer.atibackend.util.VectorUtils.multiply;
import static com.goodengineer.atibackend.util.VectorUtils.norm2;
import static com.goodengineer.atibackend.util.VectorUtils.sub;
import static com.goodengineer.atibackend.util.VectorUtils.sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.goodengineer.atibackend.model.Image;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.Point;

public class ObjectTracker {

	private static final int Na = 1000;
	private static final int Ng = 5;
	private static final int sigma = 1;
	
	private double[] inColorAverage;
	private double[] outColorAverage;
	private List<Pixel> lOut = new ArrayList<>();
	private List<Pixel> lIn = new ArrayList<>();
	private Map<Point, Pixel> pixelsMap = new HashMap<>();
	private double pk;
	private Image image;
	
	public ObjectTracker(Image image, int startW, int startH, int width, int height, double pk) {
		this.pk = pk;
		setImage(image);
		initMaps(image, startW, startH, width, height);
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public void track() {
		refreshPixels();
		double time = System.currentTimeMillis();
		firstCycle();
		secondCycle();
		System.out.println("Tracking time(s): " + (System.currentTimeMillis() - time)/1000);
		paintBorder(image);
	}
	
	private void firstCycle() {
		boolean hasChanged = true;
		for (int i = 0; i < Na && hasChanged; i++) {
			hasChanged = false;
			List<Pixel> lOutCopy = new ArrayList<>(lOut);
			for (Pixel pixel : lOutCopy) {
				if (forceD(pixel) > 0) {
					expand(pixel);
					hasChanged = true;
				}
			}
			fixLIn();
			
			List<Pixel> lInCopy = new ArrayList<>(lIn);
			for (Pixel pixel : lInCopy) {
				if (forceD(pixel) < 0) {
					contract(pixel);
					hasChanged = true;
				}
			}
			fixLOut();
		}
	}
	
	private void secondCycle() {
		boolean hasChanged = true;
		for (int i = 0; i < Na && hasChanged; i++) {
			hasChanged = false;
			List<Pixel> lOutCopy = new ArrayList<>(lOut);
			for (Pixel pixel : lOutCopy) {
				if (forceS(pixel) < 0) {
					expand(pixel);
					hasChanged = true;
				}
			}
			fixLIn();
			
			List<Pixel> lInCopy = new ArrayList<>(lIn);
			for (Pixel pixel : lInCopy) {
				if (forceS(pixel) > 0) {
					contract(pixel);
					hasChanged = true;
				}
			}
			fixLOut();
		}
	}
	
	public void paintBorder(Image baseImage) {
		for (Pixel pixel : lOut) {
			baseImage.getBands().get(0).setPixel(pixel.x, pixel.y, 255);
			if (baseImage.getBands().size() >= 3) {
				baseImage.getBands().get(1).setPixel(pixel.x, pixel.y, 170);
				baseImage.getBands().get(2).setPixel(pixel.x, pixel.y, 170);
			}
		}
		for (Pixel pixel : lIn) {
			baseImage.getBands().get(0).setPixel(pixel.x, pixel.y, 255);
			if (baseImage.getBands().size() >= 3) {
				baseImage.getBands().get(1).setPixel(pixel.x, pixel.y, 170);
				baseImage.getBands().get(2).setPixel(pixel.x, pixel.y, 170);
			}
		}
	}
	
	private void expand(Pixel pixel) {
		lOut.remove(pixel);
		lIn.add(pixel);
		pixel.pixelType = PixelType.LIN;
		for (Pixel neigh : neighbours(pixel)) {
			if (neigh.pixelType.equals(PixelType.OUT)) {
				neigh.pixelType = PixelType.LOUT;
				lOut.add(neigh);
			}
		}
	}
	
	private void fixLIn() {
		List<Pixel> toRemove = new ArrayList<>();
		for (Pixel pixel : lIn) {
			if (!isInLIn(pixel)) {
				toRemove.add(pixel);
				pixel.pixelType = PixelType.IN;
			}
		}
		lIn.removeAll(toRemove);
	}
	
	private void fixLOut() {
		List<Pixel> toRemove = new ArrayList<>();
		for (Pixel pixel : lOut) {
			if (!isInLOut(pixel)) {
				toRemove.add(pixel);
				pixel.pixelType = PixelType.OUT;
			}
		}
		lOut.removeAll(toRemove);
	}
	
	private boolean isInLIn(Pixel pixel) {
		for (Pixel neigh : neighbours(pixel)) {
			if (neigh.pixelType.equals(PixelType.OUT) 
					|| neigh.pixelType.equals(PixelType.LOUT)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isInLOut(Pixel pixel) {
		for (Pixel neigh : neighbours(pixel)) {
			if (neigh.pixelType.equals(PixelType.IN) 
					|| neigh.pixelType.equals(PixelType.LIN)) {
				return true;
			}
		}
		return false;
	}
	
	private void contract(Pixel pixel) {
		lIn.remove(pixel);
		lOut.add(pixel);
		pixel.pixelType = PixelType.LOUT;
		for (Pixel neigh : neighbours(pixel)) {
			if (neigh.pixelType.equals(PixelType.IN)) {
				neigh.pixelType = PixelType.LIN;
				lIn.add(neigh);
			}
		}
	}
	
	private void initMaps(Image image, int startW, int startH, int width, int height) {
		int inCount = 0;
		int outCount = 0;
		inColorAverage = new double[image.getColor(0, 0).length];
		outColorAverage = new double[image.getColor(0, 0).length];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				PixelType pixelType;
				Optional<List<Pixel>> list = Optional.empty();
				double[] color = image.getColor(i, j);
				if (startW <= i && i <= startW + width) {
					if (j == startH || j == startH + height
							|| ((i == startW || i == startW + width)
									&& startH <= j && j <= startH + height)) {
						pixelType = PixelType.LOUT;
						list = Optional.of(lOut);
					} else if (j == startH + 1 || j == startH + height - 1
							|| ((i == startW + 1 || i == startW + width - 1)
									&& startH + 1 <= j && j <= startH + height - 1)) {
						pixelType = PixelType.LIN;
						list = Optional.of(lIn);
					} else if (startH + 2 <= j && j <= startH + height - 2) {
						pixelType = PixelType.IN;
						inColorAverage = sum(inColorAverage, color);
						inCount++;
					} else {
						pixelType = PixelType.OUT;
						outColorAverage = sum(outColorAverage, color);
						outCount++;
					}
				} else {
					pixelType = PixelType.OUT;
					outColorAverage = sum(outColorAverage, color);
					outCount++;
				}
				Pixel pixel = new Pixel(i, j, color, pixelType);
				pixelsMap.put(new Point(i, j), pixel);
				if (list.isPresent()) {
					list.get().add(pixel);
				}
			}
		}
		inColorAverage = multiply(inColorAverage, 1.0 / inCount);
		outColorAverage = multiply(outColorAverage, 1.0 / outCount);
	}
	
	private double phiOr(int x, int y, Pixel pixel) {
		Pixel phiPixel;
		if (!pixelsMap.containsKey(new Point(x, y))) {
			phiPixel = pixel;
		} else {
			phiPixel = pixelsMap.get(new Point(x, y));
		}
		switch (phiPixel.pixelType) {
		case IN:
			return -3;
		case LIN:
			return -1;
		case LOUT:
			return 1;
		case OUT:
			return 3;
		default:
			throw new IllegalStateException();
		}
	}
	
	private double forceD(Pixel pixel) {
		if (isInRegion(inColorAverage, pixel) > 0.75) {
			return 1;
		} else {
			return -1;
		}
//		return Math.log(isInRegion(inColorAverage, pixel) / (isInRegion(outColorAverage, pixel)));
	}
	
	private double forceS(Pixel pixel) {
		double force = 0;
		double[][] gaussMatrix = MaskFactory.gauss(Ng, sigma);
		for (int i = 0; i < Ng; i++) {
			for (int j = 0; j < Ng; j++) {
				int xOfset = pixel.x + i - (int) Math.floor(Ng/2);
				int yOfset = pixel.y + j - (int) Math.floor(Ng/2);
				force += gaussMatrix[i][j] * phiOr(xOfset, yOfset, pixel);
			}
		}
		return force;
	}
	
	private double isInRegion (double[] regionAvg, Pixel pixel) {
		return 1 - norm2(sub(pixel.color, regionAvg)) / (256 * pk);
	}
	
	private List<Pixel> neighbours(Pixel pixel) {
		List<Pixel> neighs = new ArrayList<>();
		int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		for (int[] dir : dirs) {
			Point neighPoint = new Point(pixel.x + dir[0], pixel.y + dir[1]);
			if (pixelsMap.containsKey(neighPoint)) {
				neighs.add(pixelsMap.get(neighPoint));
			}
		}
		return neighs;
	}
	
	private void refreshPixels() {
		for (Pixel pixel: pixelsMap.values()) {
			pixel.color = image.getColor(pixel.x, pixel.y);
		}
	}
	
	private static class Pixel {
		private int x;
		private int y;
		private double[] color;
		private PixelType pixelType;
		
		public Pixel(int x, int y, double[] color, PixelType pixelType) {
			super();
			this.x = x;
			this.y = y;
			this.color = color;
			this.pixelType = pixelType;
		}

		@Override
		public String toString() {
			return "Pixel [x=" + x + ", y=" + y + ", color="
					+ Arrays.toString(color) + ", pixelType=" + pixelType + "]";
		}
	}
	
	private enum PixelType {
		IN, LIN, LOUT, OUT;
	}
}
