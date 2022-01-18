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

/**
 * Product types as found in the database
 *
 * @author lykat
 */
public enum ProductType {
	FRANCHISE("Franchise"), GAME("Game"), VIDEO("Video"), ANIMATION("Animation"),
	OTHER("Other"), RADIO_AND_DRAMA("Radio & Drama"), UNKNOWN(""),
	PRINT_PUBLICATION("Print Publication");

	private static final ProductType[] values = ProductType.values();

	public final String typeString;

	ProductType(String typeString) {
		this.typeString = typeString;
	}

	/**
	 * Find the {@link ProductType} whose type string equals the given string.
	 *
	 * @param str the string
	 * @return the {@link ProductType} or {@link ProductType#UNKNOWN} if none
	 * was found
	 */
	public static ProductType getProductTypeFromString(String str) {
		for (ProductType pt : values) {
			if (pt.typeString.equals(str)) {
				return pt;
			}
		}
		return UNKNOWN;
	}

}
