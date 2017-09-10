package com.goodengineer.atibackend.transformation;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.util.Point;

public class CustomCornerDetector {

	public static List<Point> findCorners(Band band) {
		List<Point> corners = new ArrayList<Point>();
		for (int x = 3; x < band.getWidth() - 3; x++) {
			for (int y = 3; y < band.getHeight() - 3; y++) {
				int region0 = 0;
				int region45 = 0;
				int region90 = 0;
				int region135 = 0;
				int region180 = 0;
				int region225 = 0;
				int region270 = 0;
				int region315 = 0;
				for (int i = -3; i <= 3; i++) {
					for (int j = -3; j <= 3; j++) {
						if (i == 0 && j == 0 || band.getPixel(x + i, y + j) != 255) {
							continue;
						}
						if (Math.abs(j) <= 1) {
							if (i > 0) {
								region0++;
							} else {
								region180++;
							}
						}
						if (Math.abs(i) <= 1) {
							if (j > 0) {
								region90++;
							} else {
								region270++;
							}
						}
						if (j > 0) {
							if (x > 0) {
								region45++;
							} else {
								region135++;
							}
						} else if (j < 0) {
							if (x > 0) {
								region315++;
							} else {
								region225++;
							}
						}
					}
				}
				int opposite = 0;
				if (region0 >= 3 && region180 <= 1) {
					opposite++;
				}
				if (region45 >= 3 && region225 <= 1) {
					opposite++;
				}
				if (region90 >= 3 && region270 <= 1) {
					opposite++;
				}
				if (region135 >= 3 && region315 <= 1) {
					opposite++;
				}
				if (region0 <= 1 && region180 >= 3) {
					opposite++;
				}
				if (region45 <= 1 && region225 >= 3) {
					opposite++;
				}
				if (region90 <= 1 && region270 >= 3) {
					opposite++;
				}
				if (region135 <= 1 && region315 >= 3) {
					opposite++;
				}
				if (opposite >= 1 && opposite <= 2) {
					corners.add(new Point(x, y));
				}
			}
		}
		return corners;
	}
}
