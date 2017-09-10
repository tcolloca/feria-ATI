package com.goodengineer.atibackend.transformation.key_points;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;
import com.goodengineer.atibackend.util.KeypointsUtils;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.Point;

import java.util.ArrayList;
import java.util.List;

public class SusanTransformation implements Transformation {

  private final double threshold;
  private final double cornerLimit;

  public SusanTransformation(double threshold, double cornerLimit) {
    this.threshold = threshold;
    this.cornerLimit = cornerLimit;
  }

  @Override
  public void transform(Band band) {
    double[][] mask = MaskFactory.susan();
    int size = mask.length;
    int N = size * size;
    List<Point> corners = new ArrayList<Point>();
    for (int x = 0; x < band.getWidth(); x++) {
      for (int y = 0; y < band.getHeight(); y++) {
        int count = 0;
        double r0 = band.getRawPixel(x, y);
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            if (mask[i][j] == 1) {
              double r = FilterUtils.getWithOffset(band, x, y, i, j, size, size);
              count += Math.abs((r0 - r)) < threshold ? 1 : 0;
            }
          }
        }
        if (1 - count/(double) N > cornerLimit) {
          corners.add(new Point(x, y));
        }
      }
    }
    KeypointsUtils.paintPoints(band, new int[] {0, 0, 255}, corners);
  }
}
