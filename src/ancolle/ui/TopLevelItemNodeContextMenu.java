package ancolle.ui;

import ancolle.items.Item;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TopLevelItemNodeContextMenu extends ContextMenu {
	public TopLevelItemNodeContextMenu() {
		MenuItem menuItemReload = new MenuItem("Force reload");
		menuItemReload.setOnAction(evt -> {
			TopLevelItemNode anchor = (TopLevelItemNode) getOwnerNode();
			if (anchor != null) {
				anchor.reload();
			}
		});
		getItems().add(menuItemReload);

		MenuItem menuItemRemoveProduct = new MenuItem("Remove product");
		menuItemRemoveProduct.setOnAction(evt -> {
			TopLevelItemNode anchor = (TopLevelItemNode) getOwnerNode();
			if (anchor != null) {
				anchor.view.removeItem((Item) anchor.getItem());
			}
		});
		getItems().add(menuItemRemoveProduct);

		getItems().add(new MenuItem("Cancel"));
		setAutoHide(true);
	}
}
