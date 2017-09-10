package com.goodengineer.atibackend.plates.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.plates.Line;
import com.goodengineer.atibackend.util.Point;

public class LineUtils {

	public static List<Point> getCorners(List<Line> lines) {
		List<Line> hLines = new ArrayList<>();
		List<Line> vLines = new ArrayList<>();
		
		for (Line line : lines) {
			if (line.isVertical()) {
				vLines.add(line);
			} else {
				hLines.add(line);
			}
		}
		List<Point> corners = new ArrayList<>();

		if (vLines.size() < 2 || hLines.size() < 2) {
			return corners;
		}
		
		List<Line> extremeHLines = getExtremeLines(hLines);
		List<Line> extremeVLines = getExtremeLines(vLines);
		
		for (Line hLine : extremeHLines) {
			int b1 = hLine.getOrigin();
			double m1 = hLine.getSlope();
			for (Line vLine : extremeVLines) {
				int b2 = vLine.getOrigin();
				double m2 = vLine.getSlope();
				int x;
				int y;
				if (m2 == 0) {
					x = b2;
				} else {
					x = (int) Math.round((b1 + b2/m2) / (1/m2 - m1));
				}
				y = (int) Math.round(m1 * x + b1);
				corners.add(new Point(x, y));
			}
		}
		return corners;
	}
	
	public static List<Line> getExtremeLines(List<Line> lines) {
		Line minLine = lines.get(0);
		Line maxLine = lines.get(0);
		
		for (Line line : lines) {
			if (line.getOrigin() < minLine.getOrigin()) {
				minLine = line;
			}
			if (line.getOrigin() > maxLine.getOrigin()) {
				maxLine = line;
			}
		}
		Comparator<Line> comparator = new Comparator<Line>() {
			@Override
			public int compare(Line o1, Line o2) {
				return o1.getCount() - o2.getCount();
			}
		};
		NavigableSet<Line> minLines = new TreeSet<>(comparator).descendingSet();
		NavigableSet<Line> maxLines = new TreeSet<>(comparator).descendingSet();
		
		for (Line line : lines) {
			if (line.getOrigin() <= minLine.getOrigin() + 3) {
				minLines.add(line);
			}
			if (line.getOrigin() >= maxLine.getOrigin() - 3) {
				maxLines.add(line);
			}
		}
		
		List<Line> extremeLines = new ArrayList<>();
		extremeLines.add(minLines.first());
		extremeLines.add(maxLines.first());
		
		return extremeLines;
	}
	
	public static void drawLine(Band band, Line line, double eps) {
		for (int x = 0; x < (line.isVertical() ? band.getHeight() : band.getWidth()); x++) {
			for (int y = 0; y < (line.isVertical() ? band.getWidth() : band.getHeight()); y++) {
				if (Math.abs(line.getSlope() * x + line.getOrigin() - y) < eps) {
					if (line.isVertical()) {
						band.setPixel(y, x, 100);
					} else {
						band.setPixel(x, y, 100);
					}
				}
			}
		}
	}
}
