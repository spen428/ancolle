package ancolle.io;

import ancolle.items.AlbumPreview;
import ancolle.items.Artist;

import java.util.ArrayList;

public class Individual extends Artist {

	public Individual(int id, String title_en, String title_ja, String pictureUrl, ArrayList<AlbumPreview> discography, ArrayList<AlbumPreview> featured_on) {
		super(id, title_en, title_ja, pictureUrl, discography, featured_on);
	}
}
