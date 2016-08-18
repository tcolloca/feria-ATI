package view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.ImageManager;
import util.FileHelper;

import java.io.File;

public class FileMenu {

  private final Menu fileMenu = new Menu("File");
  private final ImageManager imageManager;

  FileMenu(ImageManager imageManager) {
    this.imageManager = imageManager;
    fileMenu.getItems().addAll(
        openImageMenuItem(),
        saveImageMenuItem());
  }

  private MenuItem openImageMenuItem() {
    MenuItem menuItem = new MenuItem("Open...");
    menuItem.setOnAction((actionEvent) -> {
          File imageFile = FileHelper.loadImageFile();
          imageManager.setImageFile(imageFile);
        }
    );
    return menuItem;
  }

  private MenuItem saveImageMenuItem() {
    MenuItem menuItem = new MenuItem("Save...");
    menuItem.setOnAction((actionEvent) -> {
          FileHelper.saveImage(imageManager.getModifiableImage());
        }
    );
    return menuItem;
  }

  public Menu getMenu() {
    return fileMenu;
  }
}
