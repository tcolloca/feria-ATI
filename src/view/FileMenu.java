package view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import model.ImageManager;
import util.FileHelper;

import java.io.File;

public class FileMenu {

  private final Menu fileMenu = new Menu("_File");
  private final ImageManager imageManager;

  FileMenu(ImageManager imageManager) {
    this.imageManager = imageManager;
    fileMenu.setMnemonicParsing(true);
    fileMenu.getItems().addAll(
        openImageMenuItem(),
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
