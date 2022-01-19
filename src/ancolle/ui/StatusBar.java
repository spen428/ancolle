package ancolle.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author lykat
 */
public class StatusBar extends HBox {

	public final Label statusLabel;

	public StatusBar() {
		super();
		getStyleClass().add("status-bar");

		statusLabel = new Label("Ready.");
		statusLabel.getStyleClass().add("status-label");
		getChildren().add(statusLabel);
	}

}
