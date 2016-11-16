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
        createSyntheticImageItem(),
        addSpeckleNoiseItem(),
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

  private MenuItem createSyntheticImageItem() {
    MenuItem menuItem = new MenuItem("Create Synthetic Image...");
    menuItem.setOnAction((actionEvent) -> {
      CustomInputTextDialog dialog = new CustomInputTextDialog("Synthetic Image Parameters", Arrays.asList(
              new Field("alpha1:", "-1.1"),
              new Field("gamma1:", "1"),
              new Field("alpha2:", "-10"),
              new Field("gamma2:", "1"),
              new Field("L:", "1")));
      dialog.show();
      double alpha1 = dialog.getResult(0, Double.class);
      double gamma1 = dialog.getResult(1, Double.class);
      double alpha2 = dialog.getResult(2, Double.class);
      double gamma2 = dialog.getResult(3, Double.class);
      int L = dialog.getResult(4, Integer.class);
      imageManager.createSyntheticImage(L, alpha1, gamma1, alpha2, gamma2);
    });
    return menuItem;
  }
  
  private MenuItem addSpeckleNoiseItem() {
	    MenuItem menuItem = new MenuItem("Add Speckle Noise...");
	    menuItem.setOnAction((actionEvent) -> {
	      CustomInputTextDialog dialog = new CustomInputTextDialog("Synthetic Image Parameters", Arrays.asList(
	              new Field("alpha1:", "-1.1"),
	              new Field("gamma1:", "1"),
	              new Field("alpha2:", "-10"),
	              new Field("gamma2:", "1"),
	              new Field("L:", "1")));
	      dialog.show();
	      double alpha1 = dialog.getResult(0, Double.class);
	      double gamma1 = dialog.getResult(1, Double.class);
	      double alpha2 = dialog.getResult(2, Double.class);
	      double gamma2 = dialog.getResult(3, Double.class);
	      int L = dialog.getResult(4, Integer.class);
	      imageManager.createSyntheticImageFromOriginal(L, alpha1, gamma1, alpha2, gamma2);
	    });
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
