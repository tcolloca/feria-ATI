package util;

import java.util.ArrayList;
import java.util.List;

public class ImageEventDispatcher {

  private static final List<ImageEventListener> imageEventListeners = new ArrayList<>();

  public static void checkPixelsColor(int x, int y, int width, int height,
                                      int red, int green, int blue) {
    for (ImageEventListener listener : imageEventListeners) {
      listener.onCheckPixelsColor(x, y, width, height, red, green, blue);
    }
  }

  public static void addListener(ImageEventListener listener) {
    imageEventListeners.add(listener);
  }
}
