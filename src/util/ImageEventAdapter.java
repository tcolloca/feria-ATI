package util;

import java.io.File;

public class ImageEventAdapter implements ImageEventListener {

  @Override
  public void onLoadImage(File imageFile) {
  }

  @Override
  public void onCheckPixelsColor(int x, int y, int width, int height, int color) {
  }

  @Override
  public void onImageChange(byte[] byteArray) {
  }
}
