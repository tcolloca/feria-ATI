package view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import model.ImageManager;
import util.FileHelper;

import java.io.File;
import java.util.Arrays;

public class FileMenu {

  private final Menu fileMenu = new Menu("_File");
  private final ImageManager imageManager;

  FileMenu(ImageManager imageManager) {
    this.imageManager = imageManager;
    fileMenu.setMnemonicParsing(true);
    fileMenu.getItems().addAll(
        openImageMenuItem(),
        openSarImageMenuItem(),
        saveImageMenuItem(),
        createCircleImageMenuItem(),
        createRectangleImageMenuItem(),
        showHistogramItem());
  }

  private MenuItem openImageMenuItem() {
    MenuItem menuItem = new MenuItem("Open...");
    menuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
    menuItem.setOnAction((actionEvent) -> {
          File imageFile = FileHelper.loadImageFile();
          imageManager.setImageFile(imageFile);
        }
    );
    return menuItem;
  }

  private MenuItem openSarImageMenuItem() {
    MenuItem menuItem = new MenuItem("Open SAR Image...");
    menuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+A"));
    menuItem.setOnAction((actionEvent) -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("SAR Image Options",
              Arrays.asList(
                  new Field("Reducer width: ", "2"),
                  new Field("Reducer height: ", "2"),
                  new Field("Reducer type: ", "avg|med|int")));
          dialog.show();
          int reducerWidth = dialog.getResult(0, Integer.class);
          int reducerHeight = dialog.getResult(1, Integer.class);
          String type = dialog.getResult(2, String.class);
          File imageFile = FileHelper.loadImageFile();
          imageManager.setSarImageFile(imageFile, reducerWidth, reducerHeight, type);
        }
    );
    return menuItem;
  }

  private MenuItem saveImageMenuItem() {
    MenuItem menuItem = new MenuItem("Save...");
    menuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+S"));
    menuItem.setOnAction((actionEvent) ->
        FileHelper.saveImage(imageManager.getModifiableBufferedImage())
    );
    return menuItem;
  }

  private MenuItem createCircleImageMenuItem() {
    MenuItem menuItem = new MenuItem("Create Circle Image...");
    menuItem.setOnAction((actionEvent) ->
          FileHelper.saveImage(imageManager.getCircleImage())
    );
    return menuItem;
  }

  private MenuItem createRectangleImageMenuItem() {
    MenuItem menuItem = new MenuItem("Create Rectangle Image...");
    menuItem.setOnAction((actionEvent) ->
          FileHelper.saveImage(imageManager.getRectangleImage())
    );
    return menuItem;
  }

  private MenuItem showHistogramItem() {
    MenuItem menuItem = new MenuItem("Show Histogram...");
    menuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+H"));
    menuItem.setOnAction((actionEvent) ->
       new HistogramDialog(imageManager.createHistogram()).show()
    );
    return menuItem;
  }

  public Menu getMenu() {
    return fileMenu;
  }
}
