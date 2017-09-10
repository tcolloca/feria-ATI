package com.goodengineer.atibackend.transformation.flip;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class VerticalFlipTransformation implements Transformation {

  @Override
  public void transform(Band band) {
    for (int x = 0; x < band.getWidth(); x++) {
      for (int y = 0; y < band.getHeight() / 2; y++) {
        double p0 = band.getRawPixel(x, y);
        double pf = band.getRawPixel(x, band.getHeight() - y - 1);
        band.setRawPixel(x, band.getHeight() - y - 1, p0);
        band.setRawPixel(x, y, pf);
      }
    }
  }
}
