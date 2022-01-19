package ancolle.ui;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public class ViewTab extends Tab {

	private final ViewScrollPane scrollPane;

	public ViewTab(String title, Node view, Object userData) {
		super(title);
		setContent(scrollPane = new ViewScrollPane(view));
		setUserData(userData);
	}

	public void scrollIntoView(Node node) {
		double w = scrollPane.getContent().getBoundsInLocal().getWidth();
		double h = scrollPane.getContent().getBoundsInLocal().getHeight();
		double x = node.getBoundsInParent().getMaxX();
		double y = node.getBoundsInParent().getMaxY();
		scrollPane.setVvalue(y / h);
		scrollPane.setHvalue(x / w);
	}
}
