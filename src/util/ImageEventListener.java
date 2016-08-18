package util;

import java.io.File;

public interface ImageEventListener {

  void onLoadImage(File imageFile);

  void onCheckPixelsColor(int x, int y, int width, int height, int color);

  void onImageChange(byte[] byteArray);
}
