/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author samuel
 */
public class Album {

    public final int id;
    public final String title_en;
    public final String title_jp;
    final String type;
    public Date date;
    public String coverUrlMedium;
    private Image cover;

    private boolean gotFullDetails = false;

    public Album(int id, String title_en, String title_jp, String type,
            Date date) {
        this.id = id;
        this.title_en = title_en;
        this.title_jp = title_jp;
        this.type = type;
        this.date = date;
        this.coverUrlMedium = null;
        this.cover = null;
    }

    public Album(int id, String title_en, String title_jp, String type, java.sql.Date date, String coverMedium) {
        this(id, title_en, title_jp, type, date);
        this.coverUrlMedium = coverMedium;
        gotFullDetails = true;
    }

    public void getFullDetails() {
        if (gotFullDetails) {
            return;
        }
        Album fullAlbum = VGMdbAPI.getAlbumById(id);
        if (fullAlbum != null) {
            this.coverUrlMedium = fullAlbum.getCoverUrl();
            // TODO
            gotFullDetails = true;
        }
    }

    public Image getImage() {
        if (cover == null) {
            if (coverUrlMedium == null) {
                getFullDetails();
            }
            String[] spl = coverUrlMedium.split("\\.");
            String ext = spl[spl.length - 1];
            File file = new File(VGMdbAPI.CACHE_DIR + File.separator + "album"
                    + File.separator + "covers" + File.separator + id
                    + "_medium" + "." + ext);
            if (!file.exists()) {
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

    private String getCoverUrl() {
        return coverUrlMedium;
    }

}
