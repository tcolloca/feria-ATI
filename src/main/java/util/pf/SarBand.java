package util.pf;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.util.Function;
import com.goodengineer.atibackend.util.LinearFunction;

import java.util.HashMap;
import java.util.Map;

public class SarBand extends Band {

  public SarBand(double[][] pixels, String name) {
    super(pixels, name);
  }

  @Override
  public void updateTranslationFunctions() {
    rawToNormal = new SarTransformFunction(pixels, min, max);
    normalToRaw = new LinearFunction(0, min, 255, max);
  }

  private static class SarTransformFunction implements Function<Double, Double> {
    private double pixels[][];
    private double min;
    private double max;
    private Map<Double, Double> normPixels;

    public SarTransformFunction(double[][] pixels, double min, double max) {
      this.pixels = pixels;
      this.min = min;
      this.max = max;
    }

    @Override
    public Double apply(Double input) {
      if (normPixels == null) {
        normPixels = new HashMap<>();
        Function<Double, Double> normFunction = new LinearFunction(min, 0.0, max, 255.0);
        double[][] newM = equalize(pixels, min, max);
        for (int i = 0; i < pixels.length; i++) {
          for (int j = 0; j < pixels[0].length; j++) {
            if (!normPixels.containsKey(pixels[i][j])) {
              normPixels.put(pixels[i][j], (double) Math.round(normFunction.apply(newM[i][j])));
            }
          }
        }
      }
      return normPixels.get(input);
    }
  }

  public static double[][] equalize(double[][] m, double min, double max) {
    double[][] newM = new double[m.length][m[0].length];
    int buckets = m.length * m[0].length;
    double bucketSize = (max - min) / (buckets  - 1);
    double[] histogram = new double[buckets];
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

    double[] relativeAcum = new double[buckets];
    relativeAcum[0] = histogram[0];
    for (int i = 1; i < histogram.length; i++) {
      relativeAcum[i] = histogram[i] + relativeAcum[i - 1];
    }
    relativeAcum[buckets - 1] = 1;

    double fMin = relativeAcum[0];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        newM[i][j] = (max - min) * ((relativeAcum[getBucket(m[i][j], min, bucketSize)] - fMin) / (1 - fMin)) + min;
      }
    }

    histogram = new double[buckets];
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
    return (int) Math.floor((f - min) / bucketSize);
  }

  @Override
  public SarBand clone() {
    double[][] newPixels = new double[pixels.length][pixels[0].length];
    for (int i = 0; i < pixels.length; i++) {
      for (int j = 0; j < pixels[0].length; j++) {
        newPixels[i][j] = pixels[i][j];
      }
    }
    return new SarBand(newPixels, name);
  }
}
