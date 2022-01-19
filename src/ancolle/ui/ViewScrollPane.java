package ancolle.ui;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ViewScrollPane extends ScrollPane {
	public ViewScrollPane(Node content) {
		this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		VBox.setVgrow(this, Priority.ALWAYS);
		this.setContent(content);
	}
}
