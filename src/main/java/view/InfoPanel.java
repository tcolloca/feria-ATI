package view;

import com.goodengineer.atibackend.transformation.RectBorderTransformation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ImageManager;
import util.ColorHelper;
import util.ImageEventAdapter;
import util.ImageEventDispatcher;

import static view.ViewConstants.COLOR_BOX_SIZE;
import static view.ViewConstants.TOOLBAR_SPACING;

public class InfoPanel extends ImageEventAdapter {

	private final ImageManager imageManager;
	private final HBox hBox = new HBox();

	private Rectangle currentColorBox;
	private Text plateNumber;
	int selectionX;
	int selectionY;
	int selectionWidth;
	int selectionHeight;

	private Rectangle newColorBox;

	InfoPanel(ImageManager imageManager) {
		this.imageManager = imageManager;

		hBox.setAlignment(Pos.CENTER);
		hBox.setStyle("-fx-background-color: #69B;");
		hBox.setVisible(true);

		HBox plateNumberBox = new HBox();
		plateNumberBox.setMaxHeight(ViewConstants.INFO_MIN_HEIGHT - 20);
		plateNumberBox.setMinWidth(300);
		plateNumberBox.setAlignment(Pos.CENTER);
		plateNumberBox.setStyle("-fx-background-color: #EEE;");
		plateNumberBox.setVisible(true);
		
		plateNumber = new Text("-");
		
		plateNumberBox.getChildren().addAll(plateNumber);
		hBox.getChildren().addAll(plateNumberBox);

		ImageEventDispatcher.addListener(this);
	}
	
	public void setText(String text) {
		plateNumber.setText(text);
	}

	Node getNode() {
		return hBox;
	}
}