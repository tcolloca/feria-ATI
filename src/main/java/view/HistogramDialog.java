package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class HistogramDialog {

  private final float[] values;

  public HistogramDialog(float[] values) {
    this.values = values;
  }

  public void show() {
    Dialog dialog = new Dialog();
    dialog.setTitle("Histogram");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    BarChart<Number, Number> histogram = buildHistogram();

    grid.add(histogram, 0, 0);

    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
    closeButton.managedProperty().bind(closeButton.visibleProperty());
    closeButton.setVisible(false);

    dialog.getDialogPane().setContent(grid);
    dialog.show();
  }

  private BarChart buildHistogram() {
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final BarChart<String, Number> barChart = new BarChart<>(xAxis,yAxis);
    barChart.setCategoryGap(1);
    barChart.setBarGap(1);

    xAxis.setLabel("Colors");
    yAxis.setLabel("Relative Frequency");

    XYChart.Series series1 = new XYChart.Series();
    for (int i = 0; i < values.length; i++) {
      series1.getData().add(new XYChart.Data(String.valueOf(i), values[i]));
    }
    barChart.getData().addAll(series1);

    return barChart;
  }
}
