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

import java.util.logging.Logger;

/**
 * @author lykat
 */
public class ProductPreview extends Item {

    private static final Logger LOG = Logger.getLogger(ProductPreview.class.getName());

    public final ProductType type;

    public ProductPreview(int id, String title_en, String title_ja,
	    ProductType type) {
	super(id, title_en, title_ja);
	this.type = type;
    }

}
