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

import ancolle.io.VgmdbApi;
import ancolle.items.Album;
import ancolle.items.AlbumPreview;
import ancolle.items.Product;
import ancolle.main.AnColle;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

/**
 * View albums belonging to a Product
 *
 * @author samuel
 */
public class AlbumView extends TilePaneView {

    private static final double MAX_TILE_WIDTH_PX = 100;
    private static final Logger LOG = Logger.getLogger(AlbumView.class.getName());

    private Product product;

    private final ConcurrentHashMap<AlbumPreview, Album> fullAlbumMap;

    public AlbumView(AnColle ancolle) {
	this(ancolle, null);
    }

    public AlbumView(AnColle ancolle, Product product) {
	super(ancolle);
	this.fullAlbumMap = new ConcurrentHashMap<>(20);
	setPadding(new Insets(PANE_PADDING_PX));
	setAlignment(Pos.BASELINE_CENTER);
	setProduct(product);
    }

    private void updateProduct() {
	getChildren().clear();
	if (product == null) {
	    return;
	}

	fullAlbumMap.clear(); // Clear album cache
	List<AlbumPreview> albums = product.getAlbums();
	albums.forEach((album) -> {
	    AlbumNode node = createAlbumNode(album);
	    node.setCollected(ancolle.settings.collectedAlbumIds.contains(album.id));
	    getChildren().add(node);
	});
    }

    public void setProduct(Product product) {
	if (this.product == product) {
	    return;
	}
	this.product = product;
	updateProduct();
    }

    public Product getProduct() {
	return this.product;
    }

    private AlbumNode createAlbumNode(AlbumPreview album) {
	double maxWidth = MAX_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
	AlbumNode node = new AlbumNode(maxWidth);
	node.label1.setText(album.title_en);

	// Get date and set date label
	String dateString = "";
	if (album.date != null) {
	    dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
	}
	node.label2.setText(dateString);

	// Mouse listener
	// TODO: would this be better in AlbumNode class?
	node.setOnMouseClicked(evt -> {
	    if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() > 1) {
		// If full album details are loaded, open details in a new tab
		Album fullAlbum = fullAlbumMap.get(album);
		if (fullAlbum != null) {
		    AlbumDetailsView adv = new AlbumDetailsView(fullAlbum);
		    Tab tab = ancolle.newTab(fullAlbum.title_ja, adv);
		    ancolle.setSelectedTab(tab);
		}
	    } else if (evt.getButton() == MouseButton.SECONDARY) {
		final int album_id = album.id;
		Platform.runLater(() -> {
		    boolean contains = ancolle.settings.collectedAlbumIds.contains(album_id);
		    if (!contains) {
			ancolle.settings.collectedAlbumIds.add(album_id);
		    } else {
			ancolle.settings.collectedAlbumIds.remove(album_id);
		    }
		    contains = !contains;
		    node.setCollected(contains);
		});
	    }
	});

	// Fetch album cover in the background
	submitBackgroundTask(() -> {
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
		    node.albumCover.setImage(image);
		});
	    } else {
		LOG.log(Level.FINE, "Failed to fetch full album details for album #", album.id);
	    }
	});
	return node;
    }

}
