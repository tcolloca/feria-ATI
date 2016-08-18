package model;

import util.ColorHelper;
import util.ImageEventDispatcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static util.ColorHelper.getBlue;
import static util.ColorHelper.getGreen;
import static util.ColorHelper.getRed;

public class ImageManager {

  private static final int RADIUS = 50;

  private BufferedImage originalImage;
  private BufferedImage modifiableImage;

  public void setImageFile(File imageFile) {
    try {
      originalImage = modifiableImage = ImageIO.read(imageFile);
    } catch (IOException e) {
      // TODO: Auto-generated code.
      e.printStackTrace();
    }
    ImageEventDispatcher.loadImage(imageFile);
  }

  public void checkPixelsColor(int x, int y, int width, int height) {
    int red = 0;
    int green = 0;
    int blue = 0;
    for (int w = 0; w < width; w++) {
      for (int h = 0; h < height; h++) {
        int rgb = modifiableImage.getRGB(x + w, y + h);
        red += getRed(rgb);
        green += getGreen(rgb);
        blue += getBlue(rgb);
      }
    }
    red /= width*height;
    green /= width*height;
    blue /= width*height;

    ImageEventDispatcher.checkPixelsColor(x, y, width, height,
        ColorHelper.convertToRgb(red, green, blue));
  }

  public void paintPixels(int x, int y, int width, int height, int rgb) {
    System.out.println(String.format("paint %x", rgb));
    for (int w = 0; w < width; w++) {
      for (int h = 0; h < height; h++) {
        modifiableImage.setRGB(x + w, y + h, rgb);
      }
    }
    refresh();
  }

  public void selectFromOriginal(int startX, int startY, int endX, int endY) {
    modifiableImage = originalImage.getSubimage(startX, startY, (endX - startX), (endY - startY));
    refresh();
  }

  public BufferedImage getModifiableImage() {
    return modifiableImage;
  }

  private void refresh() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(modifiableImage, "png", baos);
    } catch (IOException e) {
    }
    ImageEventDispatcher.imageChange(baos.toByteArray());
  }

  public void decomposeRgb() {
    int width = originalImage.getWidth();
    modifiableImage = new BufferedImage(width*3, originalImage.getHeight(),
        BufferedImage.TYPE_INT_RGB);

    for (int w = 0; w < originalImage.getWidth(); w++) {
      for (int h = 0; h < originalImage.getHeight(); h++) {
        int rgb = originalImage.getRGB(w, h);
        modifiableImage.setRGB(w, h, rgb & 0x00FF0000); // red image
        modifiableImage.setRGB(w + width, h, rgb & 0x0000FF00); // green image
        modifiableImage.setRGB(w + 2*width, h, rgb & 0x000000FF); // blue image
      }
    }
    refresh();
  }

  public void decomposeHsv() {
    int width = originalImage.getWidth();
    modifiableImage = new BufferedImage(width*3, originalImage.getHeight(),
        BufferedImage.TYPE_BYTE_GRAY);

    for (int w = 0; w < originalImage.getWidth(); w++) {
      for (int h = 0; h < originalImage.getHeight(); h++) {
        int rgb = originalImage.getRGB(w, h);
        float[] hsv = ColorHelper.getHSV(rgb);

        modifiableImage.setRGB(w, h, ColorHelper.getGrayInRgb(hsv[0]));
        modifiableImage.setRGB(w + width, h, ColorHelper.getGrayInRgb(hsv[1]));
        modifiableImage.setRGB(w + 2*width, h, ColorHelper.getGrayInRgb(hsv[2]));
      }
    }
    refresh();
  }

  public BufferedImage getCircleImage() {
    BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 200; y++) {
        if (Math.round(Math.pow((x-100), 2) + Math.pow((y-100), 2)) < Math.pow(RADIUS, 2)) {
          img.setRGB(x, y, 0xFFFFFF);
        } else {
          img.setRGB(x, y, 0);
        }
      }
    }
    return img;
  }

  public BufferedImage getRectangleImage() {
    BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 200; y++) {
        if (Math.max(Math.abs(x - 100), Math.abs(y - 100)) < RADIUS) {
          img.setRGB(x, y, 0xFFFFFF);
        } else {
          img.setRGB(x, y, 0);
        }
      }
    }
    return img;
  }
}
