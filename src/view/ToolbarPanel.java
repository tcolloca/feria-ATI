package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import model.ImageManager;
import util.FileHelper;
import util.ToolbarImageFactory;

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

    hBox.getChildren().addAll(negativeButton(), initCreateRectangleButton());
  }

  private Button negativeButton() {
    Button button = new Button();
    button.setGraphic(ToolbarImageFactory.NEGATIVE);
    button.setMaxWidth(10);
    button.setOnAction((actionEvent) -> {
          FileHelper.saveImage(imageManager.getCircleImage());
        }
    );
    button.setFocusTraversable(false);
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
