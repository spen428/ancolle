package ancolle.items;

import ancolle.io.IO;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Product extends ProductPreview implements ItemWithPicture {

	private Image picture;

	public final String pictureUrlSmall;

	private final List<AlbumPreview> albums;

	public Product(int id, String title_en, String title_ja, ProductType type,
	               String pictureUrlSmall, Collection<AlbumPreview> albums) {
		super(id, title_en, title_ja, type);
		if (albums != null) {
			this.albums = new ArrayList<>(albums);
		} else {
			this.albums = new ArrayList<>(0);
		}
		this.pictureUrlSmall = pictureUrlSmall;
		this.picture = null;
	}

	public List<AlbumPreview> getAlbums() {
		return Collections.unmodifiableList(albums);
	}

	@Override
	public Image getPicture() {
		if (picture == null) {
			if (pictureUrlSmall == null) {
				// No URL, can't retrieve anything
				return null;
			}
			picture = IO.retrievePicture(pictureUrlSmall, "product",
					id + "_small");
		}
		return picture;
	}

}
