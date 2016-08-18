package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageEventDispatcher {

  private static final List<ImageEventListener> imageEventListeners = new ArrayList<>();

  public static void loadImage(File imageFile) {
    for (ImageEventListener listener : imageEventListeners) {
      listener.onLoadImage(imageFile);
    }
  }

  public static void checkPixelsColor(int x, int y, int width, int height, int color) {
    for (ImageEventListener listener : imageEventListeners) {
      listener.onCheckPixelsColor(x, y, width, height, color);
    }
  }

  public static void imageChange(byte[] byteArray) {
    for (ImageEventListener listener : imageEventListeners) {
      listener.onImageChange(byteArray);
    }
  }

  public static void addListener(ImageEventListener listener) {
    imageEventListeners.add(listener);
  }
}
