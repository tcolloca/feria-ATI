package util;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.translator.ColorImageTranslator;

import java.awt.image.BufferedImage;

public class BufferedImageColorImageTranslator implements ColorImageTranslator<BufferedImage> {

  @Override
  public ColorImage translateForward(BufferedImage bufferedImage) {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    double[][] red = new double[width][height];
    double[][] green = new double[width][height];
    double[][] blue = new double[width][height];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = bufferedImage.getRGB(i, j);
        red[i][j] = ColorHelper.getRed(rgb);
        green[i][j] = ColorHelper.getGreen(rgb);
        blue[i][j] = ColorHelper.getBlue(rgb);
      }
    }

    return new ColorImage(new Band(red), new Band(green), new Band(blue));
  }

  @Override
  public BufferedImage translateBackward(ColorImage colorImage) {
    BufferedImage bufferedImage =
        new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < colorImage.getWidth(); i++) {
      for (int j = 0; j < colorImage.getHeight(); j++) {
        bufferedImage.setRGB(i, j, ColorHelper.convertToRgb(
            colorImage.getRed(i, j), colorImage.getGreen(i, j), colorImage.getBlue(i, j)));
      }
    }

    return bufferedImage;
  }
}
