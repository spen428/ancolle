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

import java.util.Objects;

/**
 * @author samuel
 */
public abstract class Item {

    public final int id;
    public final String title_en;
    public final String title_ja;

    public Item(int id, String title_en, String title_ja) {
	this.id = id;
	this.title_en = title_en;
	this.title_ja = title_ja;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 37 * hash + this.id;
	hash = 37 * hash + Objects.hashCode(this.title_en);
	hash = 37 * hash + Objects.hashCode(this.title_ja);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Item other = (Item) obj;
	if (this.id != other.id) {
	    return false;
	}
	if (!Objects.equals(this.title_en, other.title_en)) {
	    return false;
	}
	return Objects.equals(this.title_ja, other.title_ja);
    }

}
