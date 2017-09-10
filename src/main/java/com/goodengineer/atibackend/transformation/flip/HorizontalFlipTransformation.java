package com.goodengineer.atibackend.transformation.flip;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class HorizontalFlipTransformation implements Transformation {

  @Override
  public void transform(Band band) {
    for (int x = 0; x < band.getWidth() / 2; x++) {
      for (int y = 0; y < band.getHeight(); y++) {
        double p0 = band.getRawPixel(x, y);
        double pf = band.getRawPixel(band.getWidth() - x - 1, y);
        band.setRawPixel(band.getWidth() - x - 1, y, p0);
        band.setRawPixel(x, y, pf);
      }
    }
  }
}
