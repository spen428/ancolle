package ancolle.items;

import ancolle.io.IO;
import java.util.Date;
import javafx.scene.image.Image;

/**
 * Full album details
 *
 * @author samuel
 */
public class Album extends AlbumPreview {

    public final String title_ja_latn; // Romanized name
    public final String pictureUrlSmall;
    private Image picture;

    public Album(int id, String title_en, String title_ja, String title_ja_latn,
            String type, Date date, String pictureUrlSmall) {
        super(id, title_en, title_ja, type, date);
        this.title_ja_latn = title_ja_latn;
        this.pictureUrlSmall = pictureUrlSmall;
        this.picture = null; // Load image only when needed
    }

    /**
     * Get the image associated with this {@link Album} (typically the front
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
            picture = IO.retrievePicture(pictureUrlSmall, "album",
                    id + "_medium");
        }
        return picture;
    }

}
