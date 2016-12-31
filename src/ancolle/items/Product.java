package ancolle.items;

import ancolle.io.IO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;

/**
 * Full product details
 *
 * @author samuel
 */
public class Product extends ProductPreview {

    private Image picture;

    public final String pictureUrlSmall;

    private final List<AlbumPreview> albums;

    public Product(int id, String title_en, String title_ja, ProductType type,
            String pictureUrlSmall, Collection<AlbumPreview> albums) {
        super(id, title_en, title_ja, type);
        this.albums = new ArrayList<>(albums);
        this.pictureUrlSmall = pictureUrlSmall;
        this.picture = null;
    }
    
    /**
     * Get a list of albums associated with this {@link Product}
     *
     * @return an unmodifiable list of albums
     */
    public List<AlbumPreview> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    /**
     * Get the image associated with this {@link Product} (typically the front
     * album cover).
     *
     * @return the {@link Image} or null if either it failed to be retrieved or
     * does not exist.
     */
    public Image getPicture() {
        if (picture == null) {
            if (pictureUrlSmall == null) {
                // No URL, can't retrive anything
                return null;
            }
            picture = IO.retrievePicture(pictureUrlSmall, "product",
                    id + "_small");
        }
        return picture;
    }

}
