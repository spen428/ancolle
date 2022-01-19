package ancolle.items;

import java.util.logging.Logger;

/**
 * @author lykat
 */
public class Track implements Comparable<Track> {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(Track.class.getName());

	public final String trackLength;
	public final String name; // Canonical name
	public final int trackNumber;
	public final int discNumber;

	/**
	 * Instantiate a new {@link Track}
	 *
	 * @param name        the track name
	 * @param length      a String representation of the track length, e.g. "1:23"
	 * @param trackNumber the track number
	 * @param discNumber  the disc number
	 */
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
