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
    public final String title_jp;
    public final String type;
    public final Date date;
    public final String coverUrlMedium;
    private Image cover;

    public Album(int id, String title_en, String title_jp, String type,
            Date date) {
        this(id, title_en, title_jp, type, date, null);
    }

    public Album(int id, String title_en, String title_jp, String type,
            Date date, String coverMedium) {
        super();
        this.id = id;
        this.title_en = title_en;
        this.title_jp = title_jp;
        this.type = type;
        this.date = date;
        this.coverUrlMedium = null;
        this.cover = null;
    }

    public Image getImage() {
        if (cover == null) {
            if (coverUrlMedium == null) {
                return null;
            }
            String[] spl = coverUrlMedium.split("\\.");
            String ext = spl[spl.length - 1];
            File file = new File(VGMdbAPI.CACHE_DIR + File.separator + "album"
                    + File.separator + "covers" + File.separator + id
                    + "_medium" + "." + ext);
            if (!file.exists()) {
                Logger.getLogger(Album.class.getName()).log(Level.FINE, "Downloading album cover");
                file.getParentFile().mkdirs();
                try {
                    VGMdbAPI.download(new URL(coverUrlMedium), file);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Album.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Album.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(Album.class.getName()).log(Level.FINE, "Loading cached cover");
            }
            try {
                cover = new Image(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Album.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cover;
    }

}
