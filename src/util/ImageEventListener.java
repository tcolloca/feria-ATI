package util;

public interface ImageEventListener {

  void onCheckPixelsColor(int x, int y, int width, int height, int red, int green, int blue);
}
