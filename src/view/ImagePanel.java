package view;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import model.ImageManager;
import util.ImageEventAdapter;
import util.ImageEventDispatcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImagePanel extends ImageEventAdapter {

  private final GridPane gridPane = new GridPane();
  private ImageView originalImageView;
  private ImageView modifiedImageView;

  private final Group originalImageLayer;
  private final RubberBandSelection originalRubberBandSelection;
  private final Group modifiedImageLayer;
  private final RubberBandSelection modifiedRubberBandSelection;
  
  private final ImageManager imageManager;

  ImagePanel(ImageManager imageManager) {
    this.imageManager = imageManager;

    initOriginalImageView();
    initModifiedImageView();

    gridPane.setGridLinesVisible(true);

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(50);
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(50);
    gridPane.getColumnConstraints().addAll(col1, col2);

    RowConstraints row1 = new RowConstraints();
    row1.setPercentHeight(100);
    gridPane.getRowConstraints().add(row1);

    HBox originalImageBox = new HBox();
    originalImageBox.setAlignment(Pos.CENTER);
    originalImageBox.setStyle("-fx-background-color: #FBB;");
    originalImageLayer = new Group();
    originalImageLayer.getChildren().add(originalImageView);
    originalImageBox.getChildren().add(originalImageLayer);
    originalRubberBandSelection = new RubberBandSelection(originalImageLayer);

    HBox modifiedImageBox = new HBox();
    modifiedImageBox.setAlignment(Pos.CENTER);
    modifiedImageBox.setStyle("-fx-background-color: #BFB;");
    modifiedImageBox.getChildren().add(modifiedImageView);
    modifiedImageLayer = new Group();
    modifiedImageLayer.getChildren().add(modifiedImageView);
    modifiedImageBox.getChildren().add(modifiedImageLayer);
    modifiedRubberBandSelection = new RubberBandSelection(modifiedImageLayer);
    modifiedImageLayer.setOnMouseClicked((event) -> {
      int width = (int) Math.round(modifiedRubberBandSelection.getBounds().getWidth());
      int height = (int) Math.round(modifiedRubberBandSelection.getBounds().getHeight());
      int pixelX = (int) Math.round(modifiedRubberBandSelection.getBounds().getMinX());
      int pixelY = (int) Math.round(modifiedRubberBandSelection.getBounds().getMinY());
      imageManager.checkPixelsColor(pixelX, pixelY, width, height);
    });

    gridPane.add(originalImageBox, 0, 0);
    gridPane.add(modifiedImageBox, 1, 0);

    ImageEventDispatcher.addListener(this);
  }

  @Override
  public void onLoadImage(File imageFile) {
    try {
      Image image = new Image(new FileInputStream(imageFile));
      originalImageView.setImage(image);
      modifiedImageView.setImage(image);
    } catch (FileNotFoundException e) {
      // TODO
    }
  }

  @Override
  public void onImageChange(byte[] byteArray) {
    modifiedImageView.setImage(new Image(new ByteArrayInputStream(byteArray)));
  }

  public void crop() {
    originalRubberBandSelection.reset();
    Bounds bounds = originalRubberBandSelection.getBounds();
    imageManager.selectFromOriginal(
        (int) Math.round(bounds.getMinX()), (int) Math.round(bounds.getMinY()),
        (int) Math.round(bounds.getMaxX()), (int) Math.round(bounds.getMaxY()));
  }

  private void initOriginalImageView() {
    originalImageView = new ImageView();
  }

  private void initModifiedImageView() {
    modifiedImageView = new ImageView();
  }

  Node getNode() {
    return gridPane;
  }
}
