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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;

/**
 * A franchise is a {@link Product} that itself comprises a number of products
 *
 * @author samuel
 */
public class Franchise extends Product {

    private final List<Product> products;

    public Franchise(int id, String title_en, String title_ja,
	    Collection<Product> products) {
	super(id, title_en, title_ja, ProductType.FRANCHISE, null, null);
	if (products != null) {
	    this.products = new ArrayList<>(products);
	} else {
	    this.products = new ArrayList<>(0);
	}
    }

    /**
     * Return a list of products associated with this franchise
     *
     * @return an unmodifiable list of products
     */
    public List<Product> getProducts() {
	return Collections.unmodifiableList(products);
    }

    @Override
    public Image getPicture() {
	// TODO: Collage of product pictures?
	if (!products.isEmpty()) {
	    // Get the picture associated with child product with the most albums.
	    int mostAlbums = 0;
	    Image bestPicture = null;
	    for (Product child : products) {
		Image childPicture = child.getPicture();
		if (childPicture != null) {
		    int numAlbums = child.getAlbums().size();
		    if (numAlbums > mostAlbums) {
			mostAlbums = numAlbums;
			bestPicture = childPicture;
		    }
		}
	    }
	    return bestPicture;
	}
	return null;
    }

    @Override
    public List<AlbumPreview> getAlbums() {
	ArrayList<AlbumPreview> allAlbums = new ArrayList<>(100);
	products.forEach((child) -> {
	    allAlbums.addAll(child.getAlbums());
	});
	return Collections.unmodifiableList(allAlbums);
    }

}
