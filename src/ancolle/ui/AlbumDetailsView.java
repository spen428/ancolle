package ancolle.ui;

import ancolle.items.Album;
import java.text.SimpleDateFormat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * View an album
 *
 * @author samuel
 */
public class AlbumDetailsView extends HBox {

    private static final int PANE_PADDING_PX = 25;
    private static final double COVER_WIDTH_PX = 350;
    private static final double COVER_PADDING_PX = 10;
    private static final double LABEL_PADDING_PX = 20;

    private static final Album BLANK_ALBUM = new Album(-1, null, null, null,
            null, null, null);

    private Album album;

    private final ImageView albumCover = new ImageView();
    private final VBox detailsVbox = new VBox();

    private final Label labelTitleEn;
    private final Label labelTitleJa;
    private final Label labelTitleJaLatn;
    private final Label labelReleaseDate;

    public AlbumDetailsView(Album album) {
        setPadding(new Insets(PANE_PADDING_PX));
        setAlignment(Pos.BASELINE_CENTER);

        // VBox that holds the album cover
        final VBox albumCoverContainer = new VBox();
        VBox.setVgrow(albumCoverContainer, Priority.ALWAYS);
        albumCoverContainer.setPadding(new Insets(COVER_PADDING_PX));
        albumCoverContainer.setMaxWidth(COVER_WIDTH_PX + (2 * COVER_PADDING_PX));
        albumCoverContainer.setAlignment(Pos.BOTTOM_CENTER);
        albumCoverContainer.getChildren().add(albumCover);
        getChildren().add(albumCoverContainer);

        // The album cover
        albumCover.setSmooth(true);
        albumCover.setPreserveRatio(true);
        albumCover.setFitWidth(COVER_WIDTH_PX);
        albumCover.setFitHeight(COVER_WIDTH_PX);

        // Scrollpane that holds the Details VBox
        final ScrollPane detailsScrollPane = new ScrollPane();
        detailsScrollPane.setFitToWidth(true);
        detailsScrollPane.setContent(detailsVbox);
        detailsScrollPane.setFitToWidth(true);
        detailsScrollPane.setFitToHeight(true);
        getChildren().add(detailsScrollPane);
        HBox.setHgrow(detailsScrollPane, Priority.ALWAYS);
        VBox.setVgrow(detailsScrollPane, Priority.ALWAYS);
        detailsVbox.setFillWidth(true);

        // Details
        labelTitleEn = addNewDetailsLabel();
        labelTitleJa = addNewDetailsLabel();
        labelTitleJaLatn = addNewDetailsLabel();
        labelReleaseDate = addNewDetailsLabel();

        setAlbum(album);
    }

    /**
     * Create a new label and add it to the details VBox.
     *
     * @return the new {@link Label}
     */
    private Label addNewDetailsLabel() {
        Label label = new Label();
        label.maxWidthProperty().bind(detailsVbox.widthProperty());
        label.setAlignment(Pos.BOTTOM_CENTER);
        label.setPadding(new Insets(LABEL_PADDING_PX));
        label.setFont(new Font("Meiryo", 16));
        detailsVbox.getChildren().add(label);
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

        albumCover.setImage(album.getPicture());

        // Titles
        labelTitleEn.setText(album.title_en);
        labelTitleJa.setText(album.title_ja);
        labelTitleJaLatn.setText(album.title_ja_latn);

        // Date
        String dateString = "";
        if (album.date != null) {
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
        }
        labelReleaseDate.setText("Release Date: " + dateString);
    }

    public Album getAlbum() {
        return this.album;
    }

}
