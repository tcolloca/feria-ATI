package model;

import static util.pf.SarImageLoader.readSarImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javafx.scene.image.Image;
import javafx.util.Pair;

import javax.imageio.ImageIO;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.special.Gamma;

import util.BufferedImageColorImageTranslator;
import util.ColorHelper;
import util.ImageEventDispatcher;
import util.pf.SarBand;
import util.pf.SarImage;
import view.ImagePanel;

import com.goodengineer.atibackend.ImageUtils;
import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.translator.Translator;
import com.goodengineer.atibackend.util.FilterUtils;

public class ImageManager {

  private static final int RADIUS = 50;

  private ColorImage originalImage;
  private ColorImage modifiableImage;
  private ImagePanel imagePanel;

  private final Translator<BufferedImage, ColorImage> translator;

  private final List<Pair<Transformation, BandType>> transformations = new ArrayList<>();
  private boolean isGrayScale = false;

  public ImageManager() {
    this.translator = new BufferedImageColorImageTranslator();
  }

  public void setImagePanel(ImagePanel imagePanel) {
    this.imagePanel = imagePanel;
  }

  public void setImageFile(File imageFile) {
    try {
      String fileName = imageFile.getAbsolutePath();
      if (fileName.endsWith(".flt") || fileName.endsWith(".img")) {
        setSarImageFile(imageFile, 1, 1, "");
      } else {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        isGrayScale = false;
        originalImage = translator.translateForward(bufferedImage);
        modifiableImage = (ColorImage) originalImage.clone();
        transformations.clear();
        imagePanel.showOriginal();
      }
    } catch (IOException e) {
      // TODO: Auto-generated code.
      e.printStackTrace();
    }
  }
  
  public void setSarImageFile(File imageFile, int reducerWidth, int reducerHeight, String type) {
    try {
      originalImage = readSarImage(imageFile, reducerWidth, reducerHeight, type);
      modifiableImage = (ColorImage) originalImage.clone();
      transformations.clear();
      imagePanel.showOriginal();
    } catch (IOException e) {
      // TODO: Auto-generated code.
      e.printStackTrace();
    }
  }

  public void applyTransformation(Transformation transformation) {
	  BandType bandType = BandType.ALL;
	  if (isGrayScale) {
		  bandType = BandType.GRAY;
	  }
	  applyTransformation(transformation, bandType);
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
    modifiableImage = (ColorImage) originalImage.clone();
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
    isGrayScale = true;
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

  public com.goodengineer.atibackend.model.Image getModifiableBackendImage() {
	  return modifiableImage;
  }
  
  public void refresh() {
	  imagePanel.showModified();
  }
  
  public void createSyntheticImage(double L, double alpha1, double gamma1, double alpha2, double gamma2) {
	GammaDistribution xVar = new GammaDistribution(L, L);
    GammaDistribution y1Var = new GammaDistribution(-alpha1, 1 / gamma1);
    GammaDistribution y2Var = new GammaDistribution(-alpha2, 1 / gamma2);

    double[][] pixels = new double[200][200];
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 200; y++) {
        if (Math.max(Math.abs(x - 100), Math.abs(y - 100)) < RADIUS) {
          pixels[x][y] = xVar.sample() / y1Var.sample();
        } else {
          pixels[x][y] = xVar.sample() / y2Var.sample();
        }
      }
    }

    originalImage = new SarImage(new SarBand(pixels, "Gray"));
    modifiableImage = (ColorImage) originalImage.clone();
    imagePanel.showOriginal();
    imagePanel.showModified();
  }
  
  public void createSyntheticImageFromOriginal(double L, double alpha1, double gamma1, double alpha2, double gamma2) {
	GammaDistribution xVar = new GammaDistribution(L, L);
    GammaDistribution y1Var = new GammaDistribution(-alpha1, 1 / gamma1);
    GammaDistribution y2Var = new GammaDistribution(-alpha2, 1 / gamma2);

    double[][] pixels = new double[200][200];
    for (int x = 0; x < originalImage.getWidth(); x++) {
      for (int y = 0; y < originalImage.getHeight(); y++) {
        if (originalImage.getGray(x, y) == 255) {
          pixels[x][y] = xVar.sample() / y1Var.sample();
        } else {
          pixels[x][y] = xVar.sample() / y2Var.sample();
        }
      }
    }

    originalImage = new SarImage(new SarBand(pixels, "Gray"));
    modifiableImage = (ColorImage) originalImage.clone();
    imagePanel.showOriginal();
    imagePanel.showModified();
  }
  
  public Function<Double, Double> gioPdf(double L, double alpha, double gamma) {
    return new Function<Double, Double>() {
    	@Override
    	public Double apply(Double x) {
    		return (Math.pow(L, L) * Gamma.gamma(L - alpha) * Math.pow(x, L - 1))
    			/ (Math.pow(gamma, alpha) * Gamma.gamma(-alpha) * Gamma.gamma(L) * Math.pow(gamma + x * L, L - alpha)); 
    	}
	};
  }
  
  public Band getAlphasMap(int maskSize, int L) {
	 int offset = maskSize - 1; 
	 Band band = modifiableImage.getBands().get(0);
	 double[] alphas = IntStream.rangeClosed(-40, -4).mapToDouble(x -> x / 2.0).toArray();
	 
	 double[][] alphasMap = new double[band.getWidth() - 2*offset][band.getHeight() - 2*offset];
	 
	 System.out.println(band.getWidth());
	 System.out.println(band.getHeight());
	 
	 for (int x = offset; x < band.getWidth() - offset; x++) {
		 for (int y = offset; y < band.getHeight() - offset; y++) {
			 double[] pixelsArr = FilterUtils.getPixelsInMask(band, x, y, maskSize);
			 double avg = DoubleStream.of(pixelsArr).average().getAsDouble();
			 double max = Double.NEGATIVE_INFINITY;
			 double alphaMax = 0;
			 for (double alpha : alphas) {
				 Function<Double, Double> gioPdf = gioPdf(L, alpha, -1 - alpha);
				 double newVal = DoubleStream.of(pixelsArr)
						 .map(v -> v / avg)
						 .map(val -> gioPdf.apply(val)).reduce(1, (v1, v2) -> v1 * v2);
				System.out.println(String.format("max: %f, newVal: %f", max, newVal));
				if (max < newVal) {
					 max = newVal;
					 alphaMax = alpha;
				 } 
			 }
			 System.out.println(String.format("x: %d, y: %d, alpha: %f", x, y, alphaMax));
			 alphasMap[x - offset][y - offset] = alphaMax;
		 } 
	 }
	 
	 return new Band(alphasMap, "Gray");
  }

  public float[] createHistogram() {
    return ImageUtils.createHistogram(modifiableImage.getBands().get(0));
  }

  public enum BandType {
    RED, GREEN, BLUE, GRAY, ALL;
  }

	public boolean isGrayScale() {
		return isGrayScale;
	}
}
