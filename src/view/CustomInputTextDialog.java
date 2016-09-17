package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomInputTextDialog {

  private final String title;
  private final List<Field> fields;
  private Optional<List<String>>  results = Optional.empty();

  public CustomInputTextDialog(String title, List<Field> fields) {
    this.title = title;
    this.fields = fields;
  }

  public void show() {
    // Create the custom dialog.
    Dialog<List<String>> dialog = new Dialog<>();
    dialog.setTitle(title);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    int i = 0;
    List<TextField> inputs = new ArrayList<>();
    for (Field field: fields) {
      TextField textField = new TextField();
      inputs.add(textField);
      textField.setPromptText(field.getHint());
      grid.add(new Label(field.getName()), 0, i);
      grid.add(textField, 1, i++);
    }

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> inputs.stream().map(
        textField -> textField.getText()).collect(Collectors.toList()));

    results = dialog.showAndWait();
  }

  public <T> T getResult(int index, Class<T> clazz) {
    if (clazz.equals(Integer.class)) {
      return clazz.cast(Integer.parseInt(results.get().get(index)));
    } else if (clazz.equals(Double.class)) {
      return clazz.cast(Double.parseDouble(results.get().get(index)));
    } else if (clazz.equals(Float.class)) {
      return clazz.cast(Float.parseFloat(results.get().get(index)));
    } else if (clazz.equals(Long.class)) {
      return clazz.cast(Long.parseLong(results.get().get(index)));
    } else if (clazz.equals(String.class)) {
      return clazz.cast(results.get().get(index));
    } else {
      throw new IllegalStateException();
    }
  }
}
