package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ImageManager;
import util.ColorHelper;
import util.ImageEventAdapter;
import util.ImageEventDispatcher;

import static view.ViewConstants.COLOR_BOX_SIZE;
import static view.ViewConstants.OPTIONS_SPACING;

public class InfoPanel extends ImageEventAdapter {

  private final ImageManager imageManager;
  private final HBox hBox = new HBox();

  private Rectangle currentColorBox;
  private Text selectionInfoText;
  private int selectionX;
  private int selectionY;
  private int selectionWidth;
  private int selectionHeight;

  private Rectangle newColorBox;

  InfoPanel(ImageManager imageManager) {
    this.imageManager = imageManager;

    hBox.setSpacing(OPTIONS_SPACING);
    hBox.setAlignment(Pos.CENTER);
    hBox.setStyle("-fx-background-color: #69B;");
    hBox.setVisible(true);

    currentColorBox = initColorBox();
    newColorBox = initColorBox();

    selectionInfoText = new Text("-");
    selectionInfoText.setWrappingWidth(200);

    hBox.getChildren().addAll(
        currentColorBox,
        selectionInfoText,
        newColorBox,
        initOpenColorPickerButton(),
        initApplyColorButton());

    ImageEventDispatcher.addListener(this);
  }

  @Override
  public void onCheckPixelsColor(int x, int y, int width, int height, int rgb) {
    currentColorBox.setFill(ColorHelper.convertToColor(rgb));
    selectionX = x;
    selectionY = y;
    selectionWidth = width;
    selectionHeight = height;
    selectionInfoText.setText(
        String.format("X: %d, Y: %d, W: %d, H: %d, Pixels: %d\n Avg: #%06x R: %d G: %d B: %d",
            x, y, width, height, width*height, rgb & 0xFFFFFF,
            ColorHelper.getRed(rgb), ColorHelper.getGreen(rgb), ColorHelper.getBlue(rgb)));
  }

  private Rectangle initColorBox() {
    Rectangle rectangle = new Rectangle();
    rectangle.setWidth(COLOR_BOX_SIZE);
    rectangle.setHeight(COLOR_BOX_SIZE);
    rectangle.setFill(Color.WHITE);
    return rectangle;
  }

  private Button initOpenColorPickerButton() {
    Button button = new Button("Pick Color...");
    button.setOnAction((e1) -> {
      ColorPicker colorPicker = new ColorPicker();
      colorPicker.setOnAction((e2) -> {
        newColorBox.setFill(colorPicker.getValue());
      });
      Stage stage = new Stage();
      Scene scene = new Scene(colorPicker);
      stage.setScene(scene);
      stage.show();
    });
    return button;
  }

  private Button initApplyColorButton() {
    Button button = new Button("Apply Color");
    button.setOnAction((e1) -> {
      imageManager.paintPixels(selectionX, selectionY, selectionWidth, selectionHeight,
          ColorHelper.convertToRgb(((Color) newColorBox.getFill())));
    });
    return button;
  }


  Node getNode() {
    return hBox;
  }
}
