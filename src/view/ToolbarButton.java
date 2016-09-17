package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

class ToolbarButton {

  private final Button button;

  ToolbarButton(String tooltip, ImageView icon, EventHandler<ActionEvent> eventEventHandler) {
    this.button = new Button();
    button.setTooltip(new Tooltip(tooltip));
    button.setGraphic(icon);
    button.setOnAction(eventEventHandler);
    button.setFocusTraversable(false);
  }

  Node getNode() {
    return button;
  }
}
