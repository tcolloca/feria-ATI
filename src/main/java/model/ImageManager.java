package model;

import com.goodengineer.atibackend.ImageUtils;
import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.translator.Translator;
import com.goodengineer.atibackend.util.Function;
import com.goodengineer.atibackend.util.LinearFunction;
import com.google.common.io.Files;
import javafx.scene.image.Image;
import javafx.util.Pair;
import util.BufferedImageColorImageTranslator;
import util.ColorHelper;
import util.ImageEventDispatcher;
import view.ImagePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ImageManager {

  private static final int RADIUS = 50;

  private BufferedImage bufferedOriginalImage;
  private ColorImage originalImage;
  private ColorImage modifiableImage;
  private ImagePanel imagePanel;

  private final Translator<BufferedImage, ColorImage> translator;

  private final List<Pair<Transformation, BandType>> transformations = new ArrayList<>();

  public ImageManager() {
    this.translator = new BufferedImageColorImageTranslator();
  }

  public void setImagePanel(ImagePanel imagePanel) {
    this.imagePanel = imagePanel;
  }

  public void setImageFile(File imageFile) {
    try {
      String fileName = imageFile.getAbsolutePath();
      if (fileName.endsWith(".flt")) {
        int[][] m = readFlt(imageFile);
        bufferedOriginalImage = new BufferedImage(494, 459, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < m[0].length; x++) {
          for (int y = 0; y < m.length; y++) {
            int gray = ColorHelper.getGrayInRgb(m[y][x] / 255f);
            bufferedOriginalImage.setRGB(x, m.length - y - 1, gray);
          }
        }
      } else {
        bufferedOriginalImage = ImageIO.read(imageFile);
      }
      originalImage = translator.translateForward(bufferedOriginalImage);
      modifiableImage = translator.translateForward(bufferedOriginalImage);
      transformations.clear();
    } catch (IOException e) {
      // TODO: Auto-generated code.
      e.printStackTrace();
    }
    imagePanel.showModified();
  }

  public void applyTransformation(Transformation transformation) {
    applyTransformation(transformation, BandType.ALL);
  }

  public void applyTransformation(Transformation transformation, BandType bandType) {
    switch (bandType) {
      case ALL:
        modifiableImage.transform(transformation);
        break;
      case RED:
        transformation.transform(modifiableImage.getBands().get(0));
        break;
      case GREEN:
        transformation.transform(modifiableImage.getBands().get(1));
        break;
      case BLUE:
        transformation.transform(modifiableImage.getBands().get(2));
        break;
      case GRAY:
        Band blue = modifiableImage.getBands().get(0);
        transformation.transform(blue);
        modifiableImage = new ColorImage(blue, blue, blue);
        break;
    }
    transformations.add(new Pair<>(transformation, bandType));
    imagePanel.showModified();
  }

  public void undo() {
    if (transformations.isEmpty()) {
      return;
    }
    Pair<Transformation, BandType> removed = transformations.remove(transformations.size() - 1);
    if (!removed.getValue().equals(BandType.ALL) && !removed.getValue().equals(BandType.GRAY)) {
      transformations.remove(transformations.size() - 1);
      transformations.remove(transformations.size() - 1);
    }
    List<Pair<Transformation, BandType>> oldTransformations = new ArrayList<>();
    oldTransformations.addAll(transformations);
    transformations.clear();
    modifiableImage = translator.translateForward(bufferedOriginalImage);
    for (Pair<Transformation, BandType> pair : oldTransformations) {
      applyTransformation(pair.getKey(), pair.getValue());
    }
    imagePanel.showModified();
  }

  public void checkPixelsColor(int x, int y, int width, int height) {
    int red = 0;
    int green = 0;
    int blue = 0;
    for (int w = x; w < width + x; w++) {
      for (int h = y; h < height + y; h++) {
        red += modifiableImage.getRed(w, h);
        green += modifiableImage.getGreen(w, h);
        blue += modifiableImage.getBlue(w, h);
      }
    }
    red /= width * height;
    green /= width * height;
    blue /= width * height;

    ImageEventDispatcher.checkPixelsColor(x, y, width, height, red, green, blue);
  }

  public void selectFromOriginal(int startX, int startY, int width, int height) {
    modifiableImage = ImageUtils.crop(modifiableImage, startX, startY, width, height);
    imagePanel.showModified();
  }

  public Image getModifiableImage() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(translator.translateBackward(modifiableImage), "png", baos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new Image(new ByteArrayInputStream(baos.toByteArray()));
  }

  public Image getOriginalImage() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(translator.translateBackward(originalImage), "png", baos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new Image(new ByteArrayInputStream(baos.toByteArray()));
  }

  public BufferedImage getModifiableBufferedImage() {
    return translator.translateBackward(modifiableImage);
  }

  public void decomposeRgb() {
    int width = originalImage.getWidth();
    BufferedImage buffModifiableImage = new BufferedImage(width*3, originalImage.getHeight(),
        BufferedImage.TYPE_INT_RGB);

    for (int w = 0; w < originalImage.getWidth(); w++) {
      for (int h = 0; h < originalImage.getHeight(); h++) {
        buffModifiableImage.setRGB(w, h, originalImage.getRed(w, h) << 16);
        buffModifiableImage.setRGB(w + width, h, originalImage.getGreen(w, h) << 8);
        buffModifiableImage.setRGB(w + 2*width, h, originalImage.getBlue(w, h));
      }
    }
    modifiableImage = translator.translateForward(buffModifiableImage);

    imagePanel.showModified();
  }

  public void decomposeHsv() {
    int width = originalImage.getWidth();
    BufferedImage buffOriginalImage = translator.translateBackward(originalImage);
    BufferedImage buffModifiableImage = new BufferedImage(width*3, originalImage.getHeight(),
        BufferedImage.TYPE_INT_RGB);

    for (int w = 0; w < originalImage.getWidth(); w++) {
      for (int h = 0; h < originalImage.getHeight(); h++) {
        float[] hsv = ColorHelper.getHSV(buffOriginalImage.getRGB(w, h));
        buffModifiableImage.setRGB(w, h, ColorHelper.getGrayInRgb(hsv[0]));
        buffModifiableImage.setRGB(w + width, h, ColorHelper.getGrayInRgb(hsv[1]));
        buffModifiableImage.setRGB(w + 2*width, h, ColorHelper.getGrayInRgb(hsv[2]));
      }
    }
    modifiableImage = translator.translateForward(buffModifiableImage);

    imagePanel.showModified();
  }

  public void getValueBand() {
    int width = originalImage.getWidth();
    BufferedImage buffOriginalImage = translator.translateBackward(originalImage);
    BufferedImage buffModifiableImage = new BufferedImage(width, originalImage.getHeight(),
        BufferedImage.TYPE_INT_RGB);

    for (int w = 0; w < originalImage.getWidth(); w++) {
      for (int h = 0; h < originalImage.getHeight(); h++) {
        float[] hsv = ColorHelper.getHSV(buffOriginalImage.getRGB(w, h));
        buffModifiableImage.setRGB(w, h, ColorHelper.getGrayInRgb(hsv[2]));
      }
    }
    modifiableImage = translator.translateForward(buffModifiableImage);

    imagePanel.showModified();
  }

  public BufferedImage getCircleImage() {
    BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 200; y++) {
        if (Math.round(Math.pow((x-100), 2) + Math.pow((y-100), 2)) < Math.pow(RADIUS, 2)) {
          img.setRGB(x, y, 0);
        } else {
          img.setRGB(x, y, 0xFFFFFF);
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
          img.setRGB(x, y, 0);
        } else {
          img.setRGB(x, y, 0xFFFFFF);
        }
      }
    }
    return img;
  }

  public float[] createHistogram() {
    return ImageUtils.createHistogram(modifiableImage.getBands().get(0));
  }

  public enum BandType {
    RED, GREEN, BLUE, GRAY, ALL;
  }

  public static int[][] readFlt(File file) throws IOException {
    byte[] bytes;
    bytes = Files.toByteArray(file);
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    PrintWriter writer = new PrintWriter("sar.csv", "UTF-8");
    double[][] m = new double[459][494];
    for (int l = 0; l < 459; l++) {
      for (int s = 0; s < 494; s++) {
        byte[] floatBytes = new byte[4];
        for (int b = 0; b < 4; b++) {
          floatBytes[b] = bytes[l * 494 * 4 + s * 4 + b];
        }
        float f = ByteBuffer.wrap(floatBytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        if (f < min) {
          min = f;
        }
        if (f > max) {
          max = f;
        }
        m[l][s] = f;
        writer.println(f);
      }
    }
    writer.close();
    Function<Double, Double> normFunction = new LinearFunction(min, 0.0, max, 255.0);
    double[][] newM = equalize(m, min, max);
    int[][] normM = new int[459][494];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        normM[i][j] = (int) Math.round(normFunction.apply(newM[i][j]));
      }
    }
    return normM;
  }

  public static double[][] equalize(double[][] m, double min, double max) {
    System.out.println(min);
    System.out.println(max);
    double[][] newM = new double[m.length][m[0].length];
    int BUCKETS = m.length * m[0].length;
    double bucketSize = (max - min) / (BUCKETS  - 1);
    double[] histogram = new double[BUCKETS];
    int count = 0;

    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        histogram[getBucket(m[i][j], min, bucketSize)]++;
        count++;
      }
    }

    for (int i = 0; i < histogram.length; i++) {
      histogram[i] = histogram[i]/count;
    }

    double[] relativeAcum = new double[BUCKETS];
    relativeAcum[0] = histogram[0];
    for (int i = 1; i < histogram.length; i++) {
      relativeAcum[i] = histogram[i] + relativeAcum[i - 1];
    }
    relativeAcum[BUCKETS - 1] = 1;

    double fMin = relativeAcum[0];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        newM[i][j] = (max - min) * ((relativeAcum[getBucket(m[i][j], min, bucketSize)] - fMin) / (1 - fMin)) + min;
      }
    }

    histogram = new double[BUCKETS];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        histogram[getBucket(newM[i][j], min, bucketSize)]++;
      }
    }

    for (int i = 0; i < histogram.length; i++) {
      histogram[i] = histogram[i]/count;
    }

    return newM;
  }

  public static int getBucket(double f, double min, double bucketSize) {
    if ((int) Math.floor((f - min) / bucketSize) == 226751) {
      System.out.println("invalid: " + f);
    }
    return (int) Math.floor((f - min) / bucketSize);
  }
}
