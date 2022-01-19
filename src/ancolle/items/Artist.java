package ancolle.items;

import ancolle.io.IO;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Artist extends ArtistPreview implements ItemWithPicture {

	private Image picture;
	public final ArrayList<AlbumPreview> discography;
	public final ArrayList<AlbumPreview> featured_on;
	public final String pictureUrlSmall;

	public Artist(int id, String title_en, String title_ja, String pictureUrlSmall,
	              ArrayList<AlbumPreview> discography, ArrayList<AlbumPreview> featured_on) {
		super(id, title_en, title_ja);
		this.discography = discography;
		this.featured_on = featured_on;
		this.pictureUrlSmall = pictureUrlSmall;
	}

	@Override
	public Image getPicture() {
		if (picture == null) {
			if (pictureUrlSmall == null) {
				// No URL, can't retrieve anything
				return null;
			}
			picture = IO.retrievePicture(pictureUrlSmall, "artist",
					id + "_small");
		}
		return picture;
	}
}
