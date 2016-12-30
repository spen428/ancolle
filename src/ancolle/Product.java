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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author samuel
 */
public class Product {

    public final int id;
    public final String title_en;
    public final String title_jp;
    public final String type;
    private Image cover;
    public final String pictureUrlSmall;
    private final List<AlbumPreview> albums;

    public Product(int id, String title_en, String title_jp, String type,
            String coverUrl, Collection<AlbumPreview> albums) {
        this.id = id;
        this.title_en = title_en;
        this.title_jp = title_jp;
        this.type = type;
        this.pictureUrlSmall = coverUrl;
        this.cover = null;
        this.albums = new ArrayList<>(albums);
    }

    public List<AlbumPreview> albums() {
        return Collections.unmodifiableList(albums);
    }

    public Image getImage() {
        if (cover == null) {
            if (pictureUrlSmall == null) {
                return null;
            }
            String[] spl = pictureUrlSmall.split("\\.");
            String ext = spl[spl.length - 1];
            File file = new File(VGMdbAPI.CACHE_DIR + File.separator + "product"
                    + File.separator + "pictures" + File.separator + id
                    + "_small" + "." + ext);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    VGMdbAPI.download(new URL(pictureUrlSmall), file);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(Product.class.getName()).log(Level.FINE, "Loading cached cover");
            }
            try {
                cover = new Image(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cover;
    }
}
