package ancolle.items;

import ancolle.io.IO;
import javafx.scene.image.Image;

import java.util.*;
import java.util.logging.Logger;

/**
 * Full album details
 *
 * @author lykat
 */
public class Album extends AlbumPreview implements ItemWithPicture {

	/**
	 * Romanised version of the Japanese album name
	 */
	public final String title_ja_latn;

	/**
	 * URL of this album's cover
	 */
	public final String pictureUrlSmall;

	/**
	 * {@link Image} containing this album's cover. It is set to {@code null} by
	 * default and only loaded and assigned when requested with a call to
	 * {@link Album#getPicture()}.
	 */
	private Image picture;

	private boolean pictureLoaded = false;

	private final List<Track> trackList;

	public Album(int id, String title_en, String title_ja, String title_ja_latn,
	             String type, Date date, String pictureUrlSmall,
	             Collection<Track> tracks, String[] roles, String catalog, boolean reprint) {
		super(id, title_en, title_ja, type, roles, catalog, reprint, date);
		this.title_ja_latn = title_ja_latn;
		this.pictureUrlSmall = pictureUrlSmall;
		this.picture = null; // Load image only when needed
		if (tracks != null) {
			this.trackList = new ArrayList<>(tracks.size());
			this.trackList.addAll(tracks);
		} else {
			this.trackList = new ArrayList<>(0);
		}
	}

	@Override
	public Image getPicture() {
		if (!pictureLoaded) {
			pictureLoaded = true;
			if (pictureUrlSmall == null) {
				// No URL, can't retrive anything
				return null;
			}
			picture = IO.retrievePicture(pictureUrlSmall, "album",
					id + "_medium");
		}
		return picture;
	}

	/**
	 * Return the (sorted) track list as an unmodifiable list
	 *
	 * @return the track list
	 */
	public List<Track> getTracks() {
		Collections.sort(trackList);
		return Collections.unmodifiableList(trackList);
	}

}
