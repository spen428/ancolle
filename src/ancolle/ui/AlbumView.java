package ancolle.ui;

import ancolle.io.VgmdbApi;
import ancolle.items.Album;
import ancolle.items.AlbumPreview;
import ancolle.main.Main;
import ancolle.ui.concurrency.AnColleTask;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AlbumView<T> extends TilePaneView {

	private static final Logger LOG = Logger.getLogger(AlbumView.class.getName());

	protected T parent;

	public final ConcurrentHashMap<AlbumPreview, Album> fullAlbumMap;
	private final Set<AlbumPreview> albumChildren;

	public AlbumView(ApplicationRoot ancolle) {
		this(ancolle, null);
	}

	public AlbumView(ApplicationRoot ancolle, T parent) {
		super(ancolle);
		this.fullAlbumMap = new ConcurrentHashMap<>(20);
		this.albumChildren = new HashSet<>(20);
		getStyleClass().add("album-view");
		setTParent(parent);
	}

	private void populateAlbumsFromTParent() {
		getParentAlbums().forEach(this::addAlbum);
	}

	public void setTParent(T parent) {
		if (this.parent == parent) {
			return;
		}
		this.parent = parent;
		if (parent != null) {
			fullAlbumMap.clear();
			albumChildren.clear();
			getChildren().clear();
			populateAlbumsFromTParent();
		}
	}

	public T getTParent() {
		return this.parent;
	}

	private AlbumNode createAlbumNode(AlbumPreview album) {
		AlbumNode node = new AlbumNode(this);
		node.setAlbum(album);
		node.setHidden(Main.settings.hiddenAlbumIds.contains(album.id));
		node.setWished(Main.settings.wishedAlbumIds.contains(album.id));
		node.setCollected(Main.settings.collectedAlbumIds.contains(album.id));

		node.label1.setText(album.title_en);

		// Get date and set date label
		String dateString = "";
		if (album.date != null) {
			dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
		}
		node.label2.setText(dateString);

		// Fetch album cover in the background
		submitBackgroundTask(new AnColleTask(() -> {
			LOG.log(Level.FINE, "Fetching album cover for album #", album.id);
			Album fullAlbum = fullAlbumMap.get(album);
			if (fullAlbum == null) {
				fullAlbum = VgmdbApi.getAlbumById(album.id);
				if (fullAlbum != null) {
					fullAlbumMap.put(album, fullAlbum);
				}
			}
			if (fullAlbum != null) {
				final Image image = fullAlbum.getPicture();
				LOG.log(Level.FINE, "Fetched album cover for album #", album.id);
				Platform.runLater(() -> {
					node.imageViewContainer.setImage(image);
				});
			} else {
				LOG.log(Level.FINE, "Failed to fetch full album details for "
						+ "album #", album.id);
			}
		}, 1, this, "album_" + album.id + "_picture"));
		return node;
	}

	public void updateHiddenItems() {
		if (Main.settings.isShowHiddenItems()) {
			refreshItems();
		} else {
			getChildren().removeIf(child -> ((AlbumNode) child).isHidden());
		}
	}

	@Override
	public void refreshItems() {
		cancelQueuedTasks();
		getChildren().clear();
		albumChildren.clear();
		populateAlbumsFromTParent();
	}

	public void removeAlbum(AlbumPreview album) {
		fullAlbumMap.remove(album);
		albumChildren.remove(album);
		getChildren().removeIf(child -> ((AlbumNode) child).getAlbum().equals(album));
	}

	public void addAlbum(AlbumPreview album) {
		if (!Main.settings.isShowHiddenItems()
				&& Main.settings.hiddenAlbumIds.contains(album.id)) {
			LOG.log(Level.FINE, "Filtered hidden album with id #{0} from "
					+ "being added to the AlbumView", album.id);
		} else if (albumChildren.contains(album)) {
			LOG.log(Level.FINE, "Filtered duplicate album with id #{0} "
					+ "from being added to the AlbumView", album.id);
		} else {
			AlbumNode node = createAlbumNode(album);
			insertChildPreservingSortOrder(node);
			albumChildren.add(album);
		}
	}

	private void insertChildPreservingSortOrder(AlbumNode node) {
		int insertIdx;
		for (insertIdx = 0; insertIdx < getChildren().size(); insertIdx++) {
			Node child = getChildren().get(insertIdx);
			if (ItemNodeComparators.ALBUM_NODE_COMPARATOR.compare(node, child) <= 0) {
				break;
			}
		}
		getChildren().add(insertIdx, node);
	}

	protected abstract List<AlbumPreview> getParentAlbums();
}
