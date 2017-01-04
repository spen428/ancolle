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

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Comparators for subclasses of {@link Item}
 *
 * @author lykat
 */
public class ItemComparators {

    /**
     * Compares by: title_en < title_jp < type.typeString
     */
    public static final Comparator<ProductPreview> PRODUCT_PREVIEW_COMPARATOR;

    /**
     * Compares by: date < title_en < title_jp < type
     */
    public static final Comparator<AlbumPreview> ALBUM_PREVIEW_COMPARATOR;

    /**
     * The logger for this class
     */
    private static final Logger LOG = Logger.getLogger(ItemComparators.class.getName());

    static {
	PRODUCT_PREVIEW_COMPARATOR = (ProductPreview o1, ProductPreview o2) -> {
	    int c = o1.title_en.compareTo(o2.title_en);
	    if (c != 0) {
		return c;
	    }
	    c = o1.title_ja.compareTo(o2.title_ja);
	    if (c != 0) {
		return c;
	    }
	    return o1.type.typeString.compareTo(o2.type.typeString);
	};

	ALBUM_PREVIEW_COMPARATOR = (AlbumPreview o1, AlbumPreview o2) -> {
	    int c = o1.date.compareTo(o2.date);
	    if (c != 0) {
		return c;
	    }
	    c = o1.title_en.compareTo(o2.title_en);
	    if (c != 0) {
		return c;
	    }
	    c = o1.title_ja.compareTo(o2.title_ja);
	    if (c != 0) {
		return c;
	    }
	    return o1.type.compareTo(o2.type);
	};
    }

    private ItemComparators() {
    }
}
