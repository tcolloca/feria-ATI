package view;

import javafx.scene.control.MenuBar;

public class GuiMenuBar {

  private final MenuBar menuBar = new MenuBar();

  GuiMenuBar(Main main) {
    menuBar.getMenus().addAll(
        new FileMenu(main.getImageManager()).getMenu(),
        new EditMenu(main).getMenu());
  }

  public MenuBar getMenuBar() {
    return menuBar;
  }
}
