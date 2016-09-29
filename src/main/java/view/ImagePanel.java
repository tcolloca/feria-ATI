package view;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import model.ImageManager;
import util.ImageEventAdapter;

import static view.ViewConstants.TOOLBAR_SPACING;

public class ImagePanel extends ImageEventAdapter {

  private final GridPane gridPane = new GridPane();
  private final ImageView originalImageView;
  private final ImageView imageView;

  private final Group imageLayer;
  private final RubberBandSelection rubberBandSelection;

  private final ImageManager imageManager;

  ImagePanel(ImageManager imageManager) {
    this.imageManager = imageManager;

    originalImageView = new ImageView();
    imageView = new ImageView();

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(100);
    gridPane.getColumnConstraints().addAll(col1);

    RowConstraints row1 = new RowConstraints();
    row1.setPercentHeight(100);
    gridPane.getRowConstraints().add(row1);

    HBox imageBox = new HBox();
    imageBox.setSpacing(5);
    imageBox.setAlignment(Pos.CENTER);
    imageBox.setStyle("-fx-background-color: #9BD;");
    imageLayer = new Group();
    imageLayer.getChildren().add(imageView);
    imageBox.getChildren().add(originalImageView);
    imageBox.getChildren().add(imageLayer);
    rubberBandSelection = new RubberBandSelection(imageLayer, imageManager);

    gridPane.add(imageBox, 0, 0);
    imageView.setPreserveRatio(true);
    imageView.maxHeight(imageBox.widthProperty().doubleValue());
    imageView.maxWidth(imageBox.heightProperty().doubleValue() / 2);
//    imageView.fitWidthProperty().bind(imageBox.widthProperty());
//    imageView.fitHeightProperty().bind(imageBox.heightProperty());

    imageManager.setImagePanel(this);

    originalImageView.setPreserveRatio(true);
    originalImageView.maxHeight(imageBox.widthProperty().doubleValue());
    originalImageView.maxWidth(imageBox.heightProperty().doubleValue()/2);
  }

  public void crop() {
    rubberBandSelection.reset();
    Bounds bounds = rubberBandSelection.getBounds();
    imageManager.selectFromOriginal(
        (int) Math.round(bounds.getMinX()), (int) Math.round(bounds.getMinY()),
        (int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
  }

  public void undo() {
    imageManager.undo();
  }

  public void showModified() {
    originalImageView.setImage(imageManager.getOriginalImage());
    imageView.setImage(imageManager.getModifiableImage());
    imageView.setStyle("-fx-border-color: #FFF;");
  }

  public void showOriginal() {
    originalImageView.setImage(imageManager.getOriginalImage());
    imageView.setImage(imageManager.getOriginalImage());
  }

  Node getNode() {
    return gridPane;
  }
}
