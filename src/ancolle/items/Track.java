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
public class Track implements Comparable<Track> {

    private static final Logger LOG = Logger.getLogger(Track.class.getName());

    public final String trackLength;
    public final String name; // Canonical name
    public final int trackNumber;
    public final int discNumber;

    public Track(String name, String length, int trackNumber, int discNumber) {
	this.name = name;
	this.trackLength = length;
	this.trackNumber = trackNumber;
	this.discNumber = discNumber;

    }

    @Override
    public int compareTo(Track t) {
	int c = Integer.compare(discNumber, t.discNumber);
	if (c != 0) {
	    return c;
	}
	c = Integer.compare(trackNumber, t.trackNumber);
	if (c != 0) {
	    return c;
	}
	c = name.compareTo(t.name);
	return c;
    }

}
