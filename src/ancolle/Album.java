package ancolle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 * Full album details
 *
 * @author samuel
 */
public class Album {

    public final int id;
    public final String title_en;
    public final String title_ja;
    public final String title_ja_latn; // Romanized name
    public final String type;
    public final Date date;
    public final String picture_small_url;
    private Image picture;

    public Album(int id, String title_en, String title_ja, String title_ja_latn,
            String type, Date date, String picture_small_url) {
        super();
        this.id = id;
        this.title_en = title_en;
        this.title_ja = title_ja;
        this.title_ja_latn = title_ja_latn;
        this.type = type;
        this.date = date;
        this.picture_small_url = picture_small_url;
        this.picture = null; // Load image only when needed
    }

    public Image getPicture() {
        if (picture == null) {
            if (picture_small_url == null) {
                return null;
            }
            String[] spl = picture_small_url.split("\\.");
            String ext = spl[spl.length - 1];
            File file = new File(VGMdbAPI.CACHE_DIR + File.separator + "album"
                    + File.separator + "covers" + File.separator + id
                    + "_medium" + "." + ext);
            if (!file.exists()) {
                Logger.getLogger(Album.class.getName()).log(Level.FINE,
                        "Downloading album cover");
                file.getParentFile().mkdirs();
                try {
                    VGMdbAPI.download(new URL(picture_small_url), file);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Album.class.getName()).log(Level.SEVERE,
                            null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Album.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            } else {
                Logger.getLogger(Album.class.getName()).log(Level.FINE,
                        "Loading cached cover");
            }
            try {
                picture = new Image(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Album.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
        return picture;
    }

}
