package view;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.ImageManager;

import java.io.IOException;

import static view.ViewConstants.*;

public class Main extends Application {

  private ImageManager imageManager;
  private ImagePanel imagePanel;

  public static void main(String[] args) throws IOException {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle(WINDOW_TITLE);

    imageManager = new ImageManager();

    MenuBar menuBar = new GuiMenuBar(this).getMenuBar();

    GridPane mainGridPane = initMainGridPane();
    imagePanel = new ImagePanel(imageManager);
    Node imagePane = imagePanel.getNode();
    Node infoPane = new InfoPanel(imageManager).getNode();
    Node optionsPane = new OptionsPanel(imageManager).getNode();

    mainGridPane.add(imagePane, 0, 0);
    mainGridPane.add(infoPane, 0, 1);
    mainGridPane.add(optionsPane, 0, 2);

    VBox vBox = new VBox();
    vBox.getChildren().addAll(menuBar, mainGridPane);

    Scene scene = new Scene(vBox, Color.WHITE);
    stage.setScene(scene);
    stage.setMinWidth(2*MAIN_MIN_WIDTH);
    stage.setMinHeight(2*MAIN_MIN_HEIGHT + 70);
    stage.show();
  }

  private static GridPane initMainGridPane() {
    GridPane gridPane = new GridPane();

    RowConstraints row1 = new RowConstraints();
    row1.setMinHeight(IMAGE_MIN_HEIGHT);
    row1.setVgrow(Priority.ALWAYS);
    RowConstraints row2 = new RowConstraints();
    row2.setMinHeight(INFO_MIN_HEIGHT);
    RowConstraints row3 = new RowConstraints();
    row3.setMinHeight(OPTIONS_MIN_HEIGHT);
    gridPane.getRowConstraints().addAll(row1,row2,row3);

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(100);
    gridPane.getColumnConstraints().addAll(col1);

    return gridPane;
  }

  ImagePanel getImagePanel() {
    return imagePanel;
  }

  ImageManager getImageManager() {
    return imageManager;
  }
}
