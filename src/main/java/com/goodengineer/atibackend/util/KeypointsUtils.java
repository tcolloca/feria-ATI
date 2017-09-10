package com.goodengineer.atibackend.util;

import com.goodengineer.atibackend.model.Band;

import java.util.List;

public class KeypointsUtils {

  public static void paintPoints(Band band, int[] rgb, List<Point> points) {
    int color;
    switch (band.getName()) {
      case "R":
        color = rgb[0];
        break;
      case "G":
        color = rgb[1];
        break;
      case "B":
        color = rgb[2];
        break;
      case "gray":
        color = (rgb[0] + rgb[1] + rgb[2]) / 3;
        break;
      default:
        throw new IllegalStateException();
    }
    for (Point point: points) {
      boolean painted = false;
      if (point.x - 1 >= 0) {
        band.setPixel(point.x - 1, point.y, color);
        painted = true;
      }
      if (point.y - 1 >= 0) {
        band.setPixel(point.x, point.y - 1, color);
        painted = true;
      }
      if (point.x + 1 < band.getWidth()) {
        band.setPixel(point.x + 1, point.y, color);
        painted = true;
      }
      if (point.y + 1 < band.getHeight()) {
        band.setPixel(point.x, point.y + 1, color);
        painted = true;
      }
      
      if (!painted) {
    	  band.setPixel(point.x, point.y, color);
      }
    }
  }
}
