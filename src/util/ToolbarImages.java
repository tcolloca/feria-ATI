package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public final class ToolbarImages {

  public static final ImageView NEGATIVE = getImage("negative.png");
  public static final ImageView THRESHOLD = getImage("threshold.png");

  private static ImageView getImage(String name) {
    try {
      ImageView imageView = new ImageView(new Image(new FileInputStream("resources/" + name)));
      imageView.setFitWidth(15);
      imageView.setFitHeight(15);
      return imageView;
    } catch (FileNotFoundException e) {
      // TODO: Auto-generated code.
      e.printStackTrace();
      return null;
    }
  }
}
