package view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.ImageManager;

public class EditMenu {

  private final Main main;
  private final ImageManager imageManager;
  private final Menu fileMenu = new Menu("Edit");

  EditMenu(Main main) {
    this.main = main;
    this.imageManager = main.getImageManager();
    fileMenu.getItems().addAll(
        cropImageMenuItem(),
        decomposeRgbMenuItem(),
        decomposeHsvMenuItem());
  }

  private MenuItem cropImageMenuItem() {
    MenuItem menuItem = new MenuItem("Crop");
    menuItem.setOnAction((actionEvent) -> {
      main.getImagePanel().crop();
    });
    return menuItem;
  }

  private MenuItem decomposeRgbMenuItem() {
    MenuItem menuItem = new MenuItem("Decompose RGB");
    menuItem.setOnAction((actionEvent) -> {
      imageManager.decomposeRgb();
    });
    return menuItem;
  }

  private MenuItem decomposeHsvMenuItem() {
    MenuItem menuItem = new MenuItem("Decompose HSV");
    menuItem.setOnAction((actionEvent) -> {
      imageManager.decomposeHsv();
    });
    return menuItem;
  }

  public Menu getMenu() {
    return fileMenu;
  }
}