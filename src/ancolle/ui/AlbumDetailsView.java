/*  AnColle, an anime and video game music collection tracker
 *  Copyright (C) 2016-17  lykat
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private static final double LABEL_PADDING_PX = 5;

    private static final Album BLANK_ALBUM = new Album(-1, null, null, null,
	    null, null, null, null);

    private Album album;

    private final ImageView albumCover = new ImageView();
    private final VBox detailsVbox = new VBox();

    private final Label labelTitleEn;
    private final Label labelTitleJa;
    private final Label labelTitleJaLatn;
    private final Label labelReleaseDate;

    private final VBox trackList;

    public AlbumDetailsView(Album album) {
	setPadding(new Insets(PANE_PADDING_PX));
	setAlignment(Pos.BASELINE_CENTER);

	// VBox that holds the album cover
	final VBox albumCoverContainer = new VBox();
	VBox.setVgrow(albumCoverContainer, Priority.ALWAYS);
	albumCoverContainer.setPadding(new Insets(COVER_PADDING_PX));
	albumCoverContainer.setMaxWidth(COVER_WIDTH_PX + (2 * COVER_PADDING_PX));
	albumCoverContainer.setAlignment(Pos.TOP_CENTER);
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

	// Track List
	trackList = new VBox();
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
	label.maxWidthProperty().bind(detailsVbox.widthProperty());
	label.setAlignment(Pos.BOTTOM_LEFT);
	label.setPadding(new Insets(LABEL_PADDING_PX));
	label.setFont(new Font("Meiryo", 16));
	label.setOnMouseEntered(evt -> {
	    label.setStyle("-fx-background-color: #9ec1ff;");
	});
	label.setOnMouseExited(evt -> {
	    label.setStyle("-fx-background-color: none;");
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

	albumCover.setImage(album.getPicture());

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

	// Track List
	trackList.getChildren().clear();
	Label trackListHeader = createNewDetailsLabel();
	trackListHeader.setText("Track List:");
	trackList.getChildren().add(trackListHeader);
	album.getTracks().stream().map((track) -> {
	    Label trackLabel = createNewDetailsLabel();
	    String trackLengthString = "";
	    if (!track.trackLength.equals("Unknown")) {
		trackLengthString = "[" + track.trackLength + "]";
	    }
	    String text = String.format("%02d-%02d. %s %s", track.discNumber,
		    track.trackNumber, track.name, trackLengthString);
	    trackLabel.setText(text);
	    return trackLabel;
	}).forEachOrdered((trackLabel) -> {
	    trackList.getChildren().add(trackLabel);
	});
    }

    public Album getAlbum() {
	return this.album;
    }

}
