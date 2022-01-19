package ancolle.ui;

import ancolle.io.VgmdbApi;
import ancolle.items.Album;
import ancolle.items.AlbumPreview;
import ancolle.main.Main;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

import java.util.logging.Logger;

/**
 * @author lykat
 */
public class AlbumNode extends ItemNode<AlbumPreview> {

	private static final ContextMenu ALBUM_NODE_CONTEXT_MENU;

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(AlbumNode.class.getName());

	static {
		ALBUM_NODE_CONTEXT_MENU = new ContextMenu();

		MenuItem menuItemReload = new MenuItem("Force reload");
		menuItemReload.setOnAction(evt -> {
			AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
			node.reloadAlbum();
		});
		ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemReload);

		MenuItem menuItemHide = new MenuItem("Toggle hidden");
		menuItemHide.setOnAction(evt -> {
			AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
			node.toggleHidden();
		});
		ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemHide);

		MenuItem menuItemCollect = new MenuItem("Toggle collected");
		menuItemCollect.setOnAction(evt -> {
			AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
			node.toggleCollected();
		});
		ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemCollect);

		MenuItem menuItemWish = new MenuItem("Toggled wished");
		menuItemWish.setOnAction(evt -> {
			AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
			node.toggledWished();
		});
		ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemWish);

		ALBUM_NODE_CONTEXT_MENU.getItems().add(new MenuItem("Cancel"));
		ALBUM_NODE_CONTEXT_MENU.setAutoHide(true);
	}

	private final AlbumView albumView;

	private boolean collected = false;
	private boolean wished = false;

	public AlbumNode(AlbumView albumView) {
		super();
		this.albumView = albumView;
		getStyleClass().add("album-node");

		setOnMouseClicked(evt -> {
			switch (evt.getButton()) {
				case PRIMARY:
					if (evt.isControlDown()) {
						openDetails(true);
					} else if (evt.isShiftDown()) {
						toggledWished();
					} else {
						toggleCollected();
					}
					break;
				case MIDDLE:
					openDetails();
					break;
				case SECONDARY:
					showContextMenu(evt);
					break;
				default:
					break;
			}
		});
	}

	/**
	 * Set the `collected` status of this {@link AlbumNode}.
	 *
	 * @param collected the value to set
	 */
	public void setCollected(boolean collected) {
		this.collected = collected;
		if (collected) {
			getStyleClass().add("collected");
		} else {
			getStyleClass().remove("collected");
		}
	}

	/**
	 * Whether this {@link AlbumNode} has been marked as "collected"
	 *
	 * @return the `collected` status
	 */
	public boolean isCollected() {
		return this.collected;
	}

	@Override
	protected ContextMenu getContextMenu() {
		return ALBUM_NODE_CONTEXT_MENU;
	}

	@Override
	public void applyAdditionalStyles(AlbumPreview item) {

	}

	private void toggleCollected() {
		AlbumPreview album = getAlbum();
		if (album == null) {
			return;
		}

		if (wished) {
			setWished(false);
			Main.settings.wishedAlbumIds.remove(album.id);
		}

		boolean contains = Main.settings.collectedAlbumIds.contains(album.id);
		if (!contains) {
			Main.settings.collectedAlbumIds.add(album.id);
		} else {
			Main.settings.collectedAlbumIds.remove(album.id);
		}
		contains = !contains;
		setCollected(contains);
	}

	public AlbumPreview getAlbum() {
		return getItem();
	}

	public void setAlbum(AlbumPreview album) {
		setItem(album);
	}

	private void reloadAlbum() {
		AlbumPreview album = getAlbum();
		if (album == null) {
			return;
		}

		Platform.runLater(() -> {
			albumView.removeAlbum(album);
			VgmdbApi.removeFromCache(album);
			albumView.addAlbum(album);
		});
	}

	private void toggledWished() {
		AlbumPreview album = getAlbum();
		if (album == null) {
			return;
		}

		if (collected) {
			setCollected(false);
			Main.settings.collectedAlbumIds.remove(album.id);
		}

		boolean status = Main.settings.wishedAlbumIds
				.contains(album.id);
		if (!status) {
			Main.settings.wishedAlbumIds.add(album.id);
			Main.settings.collectedAlbumIds.remove(album.id);
		} else {
			Main.settings.wishedAlbumIds.remove(album.id);
		}
		status = !status;
		setWished(status);
	}

	void setWished(boolean wished) {
		this.wished = wished;
		if (wished) {
			getStyleClass().add("wished");
		} else {
			getStyleClass().remove("wished");
		}
	}

	private void toggleHidden() {
		AlbumPreview album = getAlbum();
		if (album == null) {
			return;
		}
		boolean status = Main.settings.hiddenAlbumIds
				.contains(album.id);
		if (!status) {
			Main.settings.hiddenAlbumIds.add(album.id);
		} else {
			Main.settings.hiddenAlbumIds.remove(album.id);
		}
		status = !status;
		setHidden(status);
		albumView.updateHiddenItems();
	}

	private void openDetails() {
		openDetails(false);
	}

	private void openDetails(boolean selectTab) {
		// If full album details are loaded, open details in a new tab
		Album fullAlbum = (Album) albumView.fullAlbumMap.get(getAlbum());
		if (fullAlbum != null) {
			AlbumDetailsView adv = new AlbumDetailsView(fullAlbum);
			Tab tab = albumView.ancolle.newTab(fullAlbum.title_ja,
					adv, "adv_" + fullAlbum.id);
			if (tab != null) {
				tab.getStyleClass().add("album-details-tab");
				if (selectTab) {
					albumView.ancolle.setSelectedTab(tab);
				}
			}
		}
	}

}
