package com.goodengineer.atibackend.plates.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileHelper {

  private static final String DEFAULT_EXT = "png";

  public static void saveImage(BufferedImage image, String fileName) {
	      String ext = DEFAULT_EXT;
	      if (fileName.matches(".*\\.(png|bmp|ppm|raw)$")) {
	        ext = fileName.split("\\.")[1];
	      }
	      File outputFile = new File(fileName.split("\\.")[0] + "." + ext);
	      try {
	        ImageIO.write(image, ext, outputFile);
	      } catch (IOException e) {
	        // TODO: Auto-generated code.
	        e.printStackTrace();
	      }
	}
}
