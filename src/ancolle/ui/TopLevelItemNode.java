package ancolle.ui;

import ancolle.io.VgmdbApi;
import ancolle.items.Item;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;

public abstract class TopLevelItemNode<T extends Item, TNode extends ItemNode<T>, TPreview extends Item>
		extends ItemNode<T> {

	protected final TopLevelItemView<T, TNode, TPreview> view;

	public TopLevelItemNode(TopLevelItemView<T, TNode, TPreview> view) {
		super();
		this.view = view;

		setItem(null);

		// Mouse/key handlers
		setOnMouseClicked(evt -> {
			if (evt.getButton() == MouseButton.PRIMARY) {
				if (getItem() != null) {
					getStyleClass().remove("new");
					view.ancolle.view(getItem());
				} else {
					// TODO: Move to top of priority queue
				}
			} else if (evt.getButton() == MouseButton.SECONDARY) {
				showContextMenu(evt);
			}
		});
	}

	protected void reload() {
		Platform.runLater(() -> {
			T item = getItem();
			if (view.removeItem(item)) {
				VgmdbApi.removeFromCache(item);
				view.addItemById(item.id, true);
			}
		});
	}
}
