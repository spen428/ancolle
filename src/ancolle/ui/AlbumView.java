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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;

/**
 * View albums belonging to a Product
 *
 * @author lykat
 */
public final class AlbumView extends TilePaneView {

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(AlbumView.class.getName());

    private Product product;

    public final ConcurrentHashMap<AlbumPreview, Album> fullAlbumMap;

    public AlbumView(AnColle ancolle) {
	this(ancolle, null);
    }

    public AlbumView(AnColle ancolle, Product product) {
	super(ancolle);
	this.fullAlbumMap = new ConcurrentHashMap<>(20);
	getStyleClass().add("album-view");
	setProduct(product);
	startWorkerThread();
    }

    private void addAlbums() {
	boolean showHidden = ancolle.getSettings().isShowHiddenItems();
	List<AlbumPreview> albums = product.getAlbums();
	albums.forEach((album) -> {
	    if (!showHidden && ancolle.getSettings().hiddenAlbumIds.contains(album.id)) {
		// Don't add hidden item
	    } else {
		AlbumNode node = createAlbumNode(album);
		node.setCollected(ancolle.getSettings().collectedAlbumIds.contains(album.id));
		getChildren().add(node);
	    }
	});
    }

    public void setProduct(Product product) {
	if (this.product == product) {
	    return;
	}
	this.product = product;
	if (product != null) {
	    fullAlbumMap.clear();
	    getChildren().clear();
	    addAlbums();
	}
    }

    public Product getProduct() {
	return this.product;
    }

    private AlbumNode createAlbumNode(AlbumPreview album) {
	AlbumNode node = new AlbumNode(this);
	node.setAlbum(album);
	node.setHidden(ancolle.getSettings().hiddenAlbumIds.contains(album.id));

	node.label1.setText(album.title_en);

	// Get date and set date label
	String dateString = "";
	if (album.date != null) {
	    dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
	}
	node.label2.setText(dateString);

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
		    node.imageView.setImage(image);
		});
	    } else {
		LOG.log(Level.FINE, "Failed to fetch full album details for album #", album.id);
	    }
	});
	return node;
    }

    public void updateHiddenItems() {
	if (ancolle.getSettings().isShowHiddenItems()) {
	    getChildren().clear();
	    addAlbums();
	} else {
	    getChildren().removeIf(child -> ((AlbumNode) child).isHidden());
	}
    }

}
