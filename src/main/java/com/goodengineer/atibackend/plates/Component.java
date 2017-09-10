package com.goodengineer.atibackend.plates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.plates.hough.CustomLineHough;
import com.goodengineer.atibackend.plates.util.LineUtils;
import com.goodengineer.atibackend.transformation.filter.MultiFilterTransformation;
import com.goodengineer.atibackend.transformation.filter.pixelRules.NormPixelRule;
import com.goodengineer.atibackend.transformation.threshold.OtsuThresholdingTransformation;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.MaskFactory.Direction;
import com.goodengineer.atibackend.util.Point;


class Component {
	
	private static final double MAX_ANGLE = 30;
	
	private static final int MIN_COMPONENT_SIZE = 20;
	
	private final Band band;
	private final List<int[]> pixels = new ArrayList<>();
	private int minCol = Integer.MAX_VALUE;
	private int minRow = Integer.MAX_VALUE;
	private int maxCol;
	private int maxRow;
	private int eulerNumber = -1;
	
	public Component(Band band) {
		super();
		this.band = band;
	}

	void addPixel(int x, int y) {
		pixels.add(new int[]{x, y});
		if (x < minCol) {
			minCol = x;
		}
		if (y < minRow) {
			minRow = y;
		}
		if (x >= maxCol) {
			maxCol = x;
		}
		if (y >= maxRow) {
			maxRow = y;
		}
	}
	
	List<int[]> getPixels() {
		return pixels;
	}

	double getAspectRatio() {
		return  (maxCol - minCol + 1)/ (double)(maxRow - minRow + 1);
	}
	
	int size() {
		return pixels.size();
	}

	public int eulerNumber() {
		if (eulerNumber < 0) {
			Band subBand = band.subRegion(minCol - 1, minRow - 1, maxCol + 2, maxRow + 2);
			List<Component> subComponents = ComponentFinder.findComponents(subBand, 255);
			List<Component> components = new ArrayList<>();
			for (Component subComponent : subComponents) {
				if (subComponent.size() > MIN_COMPONENT_SIZE) {
					components.add(subComponent);
				}
			}
			eulerNumber = components.size();
		}
		return eulerNumber - 1;
	}
	
	public List<Point> getCorners() {
		int minSubRegionCol = minCol == 0 ? minCol: minCol - 1;
		int minSubRegionRow = minRow == 0 ? minRow: minRow - 1;
		Band subBand = band.subRegion(minSubRegionCol, minSubRegionRow, maxCol + 1, maxRow + 1);
		int width = subBand.getWidth();
		int height = subBand.getHeight();
		double[][] newSubPixels = new double[width][height];
		for (int[] pixel : pixels) {
			newSubPixels[pixel[0] - minSubRegionCol][pixel[1] - minSubRegionRow] = 255;
		}
		subBand.setPixels(newSubPixels);

		System.out.println("	Finding subcomponents...");
	    List<Component> subComponents = ComponentFinder.findComponents(subBand, 0);
	    Set<Component> toRemove = new HashSet<>();
	    for (Component subC : subComponents) {
	    	for (int[] pixel : subC.pixels) {
				if (pixel[0] == 0 || pixel[0] == width - 1) {
					toRemove.add(subC);
				}
				if (pixel[1] == 0 || pixel[1] == height - 1) {
					toRemove.add(subC);
				}
			}
	    }
	    subComponents.removeAll(toRemove);
	    
		for (Component subC : subComponents) {
			for (int[] pixel : subC.pixels) {
				subBand.setPixel(pixel[0], pixel[1], 255);
			}
		}
		
		System.out.println("	Finding borders...");
		MultiFilterTransformation.PixelRule rule = new NormPixelRule();
		List<double[][]> masks = Arrays.asList(MaskFactory.sobel(Direction.E), MaskFactory.sobel(Direction.S));
	    new MultiFilterTransformation(rule, masks).transform(subBand);
	    new OtsuThresholdingTransformation().transform(subBand);
	    
	    System.out.println("	Finding lines...");
		List<Line> lines = new CustomLineHough(MAX_ANGLE, 1, (int) (width * 0.8), (int) (height * 0.5)).getLines(subBand);
//		CustomLineHough.paintLines(subBand, lines);
//		band.setPixels(subBand.pixels);
//		System.out.println(lines);
		System.out.println("	Finding corners...");
		List<Point> corners = LineUtils.getCorners(lines);
		List<Point> fixedCorners = new ArrayList<>();
		for (Point c: corners) {
			int x = c.x;
			int y = c.y;
			if (x < 0) {
				x = 0;
			} 
			if (x >= width) {
				x = width - 1;
			}
			if (y < 0) {
				y = 0;
			} 
			if (y >= height) {
				y = height - 1;
			}
			fixedCorners.add(new Point(x + minSubRegionCol, y + minSubRegionRow));
		}
		return fixedCorners;
	}

	public List<Point> getRectangleCorners() {
		List<Point> corners = new ArrayList<>();
		corners.add(new Point(minCol, minRow));
		corners.add(new Point(maxCol, minRow));
		corners.add(new Point(minCol, maxRow));
		corners.add(new Point(maxCol, maxRow));
		return corners;
	}

	public int height() {
		return maxRow - minRow + 1;
	}
}
