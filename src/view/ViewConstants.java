package view;

import java.awt.*;

public final class ViewConstants {

  public static final String WINDOW_TITLE = "ATI GUI";

  public static final int MAIN_MIN_WIDTH = 1200;
  public static final int MAIN_MIN_HEIGHT = 600;

  public static final int IMAGE_MIN_HEIGHT = 470;
  public static final int INFO_MIN_HEIGHT = 100;
  public static final int TOOLBAR_MIN_HEIGHT = MAIN_MIN_HEIGHT - IMAGE_MIN_HEIGHT - INFO_MIN_HEIGHT;

  public static final int FILE_CHOOSER_WIDTH = 900;
  public static final int FILE_CHOOSER_HEIGHT = 600;
  public static final Font FILE_CHOOSER_FONT = new Font("Arial", Font.PLAIN, 24);

  public static final int COLOR_BOX_SIZE = 60;

  public static final int TOOLBAR_SPACING = 10;
}
