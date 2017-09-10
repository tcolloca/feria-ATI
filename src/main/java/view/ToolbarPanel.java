package view;

import static view.ViewConstants.TOOLBAR_SPACING;

import java.util.List;

import com.goodengineer.atibackend.plates.PlateRecognitionTransformation;
import com.goodengineer.atibackend.transformation.Transformation;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ImageManager;
import util.ToolbarImages;

public class ToolbarPanel {

	private final HBox hFiltersBox = new HBox();
	private final VBox vBox = new VBox();
	private final ImageManager imageManager;
	private InfoPanel infoPanel;

	private List<String> allPaths;
	private int currentImage;

	ToolbarPanel(ImageManager imageManager, InfoPanel infoPanel) {
		this.imageManager = imageManager;
		this.infoPanel = infoPanel;

		hFiltersBox.setSpacing(TOOLBAR_SPACING);
		hFiltersBox.setAlignment(Pos.CENTER);
		hFiltersBox.setStyle("-fx-background-color: #DDD;");
		hFiltersBox.setVisible(true);

		hFiltersBox.getChildren().addAll(recognizePlatesButton());

		vBox.getChildren().add(hFiltersBox);
	}

	private Node recognizePlatesButton() {
		return new ToolbarButton("Recognize Plates", ToolbarImages.PLATES, actionEvent -> {
			new Thread(() -> {
				imageManager.getValueBand();
				Transformation PRT = new PlateRecognitionTransformation(imageManager, infoPanel);
				imageManager.applyTransformation(PRT);
			}).start();
		}).getNode();
	}

	public Node getNode() {
		return vBox;
	}
}
