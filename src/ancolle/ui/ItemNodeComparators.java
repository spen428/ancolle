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

import ancolle.items.AlbumPreview;
import ancolle.items.ItemComparators;
import ancolle.items.ProductPreview;
import java.util.Comparator;
import java.util.logging.Logger;
import javafx.scene.Node;

/**
 * Comparators for subclasses of {@link ItemNode}
 *
 * @author lykat
 */
public class ItemNodeComparators {

    public static final Comparator<Node> PRODUCT_NODE_COMPARATOR;
    public static final Comparator<Node> ALBUM_NODE_COMPARATOR;

    private static final Comparator<ItemNode<?>> ITEM_NODE_COMPARATOR;

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(ItemNodeComparators.class.getName());

    static {
	ITEM_NODE_COMPARATOR = (ItemNode<?> o1, ItemNode<?> o2) -> {
	    int c = o1.label1.getText().compareTo(o2.label1.getText());
	    if (c != 0) {
		return c;
	    }
	    return o1.label2.getText().compareTo(o2.label2.getText());
	};

	PRODUCT_NODE_COMPARATOR = (Node o1, Node o2) -> {
	    if (!(o1 instanceof ProductNode)) {
		if (o2 instanceof ProductNode) {
		    return 1;
		}
		return 0;
	    }
	    if (!(o2 instanceof ProductNode)) {
		return -1;
	    }

	    ProductNode pn1 = (ProductNode) o1;
	    ProductNode pn2 = (ProductNode) o2;
	    ProductPreview product1 = pn1.getProduct();
	    ProductPreview product2 = pn2.getProduct();
	    if (product1 != null && product2 != null) {
		return ItemComparators.PRODUCT_PREVIEW_COMPARATOR
			.compare(product1, product2);
	    }

	    return ITEM_NODE_COMPARATOR.compare(pn1, pn2);
	};

	ALBUM_NODE_COMPARATOR = (Node o1, Node o2) -> {
	    if (!(o1 instanceof AlbumNode)) {
		if (o2 instanceof AlbumNode) {
		    return 1;
		}
		return 0;
	    }
	    if (!(o2 instanceof AlbumNode)) {
		return -1;
	    }

	    AlbumNode an1 = (AlbumNode) o1;
	    AlbumNode an2 = (AlbumNode) o2;
	    AlbumPreview album1 = an1.getAlbum();
	    AlbumPreview album2 = an2.getAlbum();
	    if (album1 != null && album2
		    != null) {
		return ItemComparators.ALBUM_PREVIEW_COMPARATOR
			.compare(album1, album2);
	    }

	    return ITEM_NODE_COMPARATOR.compare(an1, an2);
	};
    }

    private ItemNodeComparators() {
    }

}
