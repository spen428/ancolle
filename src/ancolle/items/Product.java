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
package ancolle.items;

import ancolle.io.IO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 * Full product details
 *
 * @author lykat
 */
public class Product extends ProductPreview {

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(Product.class.getName());

    private Image picture;

    public final String pictureUrlSmall;

    private final List<AlbumPreview> albums;

    public Product(int id, String title_en, String title_ja, ProductType type,
	    String pictureUrlSmall, Collection<AlbumPreview> albums) {
	super(id, title_en, title_ja, type);
	if (albums != null) {
	    this.albums = new ArrayList<>(albums);
	} else {
	    this.albums = new ArrayList<>(0);
	}
	this.pictureUrlSmall = pictureUrlSmall;
	this.picture = null;
    }

    /**
     * Get a list of albums associated with this {@link Product}
     *
     * @return an unmodifiable list of albums
     */
    public List<AlbumPreview> getAlbums() {
	return Collections.unmodifiableList(albums);
    }

    /**
     * Get the image associated with this {@link Product} (typically the front
     * album cover).
     *
     * @return the {@link Image} or null if either it failed to be retrieved or
     * does not exist.
     */
    public Image getPicture() {
	if (picture == null) {
	    if (pictureUrlSmall == null) {
		// No URL, can't retrive anything
		return null;
	    }
	    picture = IO.retrievePicture(pictureUrlSmall, "product",
		    id + "_small");
	}
	return picture;
    }

}
