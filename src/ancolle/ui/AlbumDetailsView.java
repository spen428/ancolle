package ancolle.ui;

import ancolle.items.Album;
import javafx.css.PseudoClass;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * View an album
 *
 * @author lykat
 */
public final class AlbumDetailsView extends HBox {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(AlbumDetailsView.class.getName());

	private static final Album BLANK_ALBUM = new Album(-1, null, null, null,
			null, null, null, null, null, null, false);

	/**
	 * A context menu with just a "Copy" option, for copying the text values of
	 * labels in this {@link AlbumView}.
	 */
	private static final ContextMenu COPY_CONTEXT_MENU;

	static {
		COPY_CONTEXT_MENU = new ContextMenu();
		MenuItem menuItemCopy = new MenuItem("Copy to clipboard");
		menuItemCopy.setOnAction(evt -> {
			Label anchor = (Label) COPY_CONTEXT_MENU.getOwnerNode();
			if (anchor != null) {
				// Copy label text to the system clipboard
				String labelText = anchor.getText();
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(labelText);
				clipboard.setContent(content);
			}
		});
		COPY_CONTEXT_MENU.getItems().add(menuItemCopy);
		COPY_CONTEXT_MENU.setAutoHide(true);
	}

	private Album album;

	private final ImageViewContainer albumCoverContainer;
	private final VBox detailsVbox;

	private final Label labelTitleEn;
	private final Label labelTitleJa;
	private final Label labelTitleJaLatn;
	private final Label labelReleaseDate;

	private final VBox trackList;
	private final PseudoClass pseudoClassHover;
	private final Label labelAlbumlabel1;
	private final Label labelAlbumlabel2;

	/**
	 * Instantiate a new {@link AlbumDetailsView} to display details about the
	 * given {@link Album}
	 *
	 * @param album the album whose details to display
	 */
	public AlbumDetailsView(Album album) {
		this.detailsVbox = new VBox();
		pseudoClassHover = new PseudoClass() {
			@Override
			public String getPseudoClassName() {
				return "hover";
			}
		};
		getStyleClass().add("album-details-view");

		// VBox that holds the album cover
		this.albumCoverContainer = new ImageViewContainer();
		getChildren().add(albumCoverContainer);

		// Labels underneath album cover
		labelAlbumlabel1 = new Label();
		labelAlbumlabel1.getStyleClass().add("label1");
		labelAlbumlabel2 = new Label();
		labelAlbumlabel2.getStyleClass().add("label2");
		albumCoverContainer.getChildren().addAll(labelAlbumlabel1, labelAlbumlabel2);

		// Scrollpane that holds the Details VBox
		final ScrollPane detailsScrollPane = new ScrollPane();
		detailsScrollPane.getStyleClass().add("details-scrollpane");
		detailsScrollPane.setContent(detailsVbox);
		getChildren().add(detailsScrollPane);
		HBox.setHgrow(detailsScrollPane, Priority.ALWAYS);
		VBox.setVgrow(detailsScrollPane, Priority.ALWAYS);
		detailsVbox.getStyleClass().add("details");

		// Details
		labelTitleEn = addNewDetailsLabel();
		labelTitleEn.getStyleClass().add("title-en");
		labelTitleJa = addNewDetailsLabel();
		labelTitleJa.getStyleClass().add("title-ja");
		labelTitleJaLatn = addNewDetailsLabel();
		labelTitleJaLatn.getStyleClass().add("title-ja-latn");
		labelReleaseDate = addNewDetailsLabel();
		labelReleaseDate.getStyleClass().add("release-date");

		// Track List
		trackList = new VBox();
		trackList.getStyleClass().add("track-list");
		detailsVbox.getChildren().add(trackList);

		setAlbum(album);
	}

	/**
	 * Create a new label and add it to the details VBox.
	 *
	 * @return the new {@link Label}
	 */
	private Label addNewDetailsLabel() {
		Label label = createNewDetailsLabel();
		detailsVbox.getChildren().add(label);
		return label;
	}

	/**
	 * Create a new details label
	 *
	 * @return the new {@link Label}
	 */
	private Label createNewDetailsLabel() {
		Label label = new Label();
		label.setOnMouseEntered(evt -> {
			label.pseudoClassStateChanged(pseudoClassHover, true);
			COPY_CONTEXT_MENU.hide();
		});
		label.setOnMouseExited(evt -> {
			label.pseudoClassStateChanged(pseudoClassHover, false);
		});
		label.setOnMouseClicked(evt -> {
			if (evt.getButton() == MouseButton.SECONDARY) {
				COPY_CONTEXT_MENU.show(label, Side.TOP, evt.getX(), evt.getY());
			}
		});
		return label;
	}

	/**
	 * Update the {@link AlbumDetailsView} to display details about the given
	 * {@link Album}.
	 *
	 * @param album the {@link Album} whose details to display
	 */
	public void setAlbum(Album album) {
		if (this.album == album) {
			return;
		}
		if (album == null) {
			// Avoid NPE by using a placeholder album
			album = BLANK_ALBUM;
		}
		this.album = album;

		albumCoverContainer.setImage(album.getPicture());
		labelAlbumlabel1.setText(album.title_ja);

		// Titles
		labelTitleEn.setText("English Title: " + album.title_en);
		labelTitleJa.setText("Japanese Title: " + album.title_ja);
		labelTitleJaLatn.setText("Romanized Title: " + album.title_ja_latn);

		// Date
		String dateString = "";
		if (album.date != null) {
			dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
		}
		labelReleaseDate.setText("Release Date: " + dateString);
		labelAlbumlabel2.setText(dateString);

		// Track List
		populateTrackList();
	}

	public Album getAlbum() {
		return this.album;
	}

	private void populateTrackList() {
		trackList.getChildren().clear();
		Label trackListHeader = createNewDetailsLabel();
		trackListHeader.getStyleClass().add(".track-list-header");
		trackListHeader.setText("Track List:");
		trackList.getChildren().add(trackListHeader);
		album.getTracks().stream().map((track) -> {
			Label trackLabel = createNewDetailsLabel();
			trackLabel.getStyleClass().add("track");
			String trackLengthString = "";
			if (!track.trackLength.equals("Unknown")) {
				trackLengthString = "[" + track.trackLength + "]";
			}
			String text = String.format("    %02d-%02d. %s %s", track.discNumber,
					track.trackNumber, track.name, trackLengthString);
			trackLabel.setText(text);
			return trackLabel;
		}).forEachOrdered((trackLabel) -> {
			trackList.getChildren().add(trackLabel);
		});
	}

}
