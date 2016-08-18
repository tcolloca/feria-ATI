package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import model.ImageManager;
import util.FileHelper;

import static view.ViewConstants.OPTIONS_SPACING;

public class OptionsPanel {

  private final HBox hBox = new HBox();
  private final ImageManager imageManager;

  OptionsPanel(ImageManager imageManager) {
    this.imageManager = imageManager;

    hBox.setSpacing(OPTIONS_SPACING);
    hBox.setAlignment(Pos.CENTER);
    hBox.setStyle("-fx-background-color: #369;");
    hBox.setVisible(true);

    hBox.getChildren().addAll(initCreateCircleButton(), initCreateRectangleButton());
  }

  private Button initCreateCircleButton() {
    Button button = new Button("Create Circle Image...");
    button.setOnAction((actionEvent) -> {
          FileHelper.saveImage(imageManager.getCircleImage());
        }
    );
    return button;
  }

  private Button initCreateRectangleButton() {
    Button button = new Button("Create Rectangle Image...");
    button.setOnAction((actionEvent) -> {
          FileHelper.saveImage(imageManager.getRectangleImage());
        }
    );
    return button;
  }

  public Node getNode() {
    return hBox;
  }
}
