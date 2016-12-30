package ancolle.io;

import ancolle.items.Album;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import org.json.simple.parser.JSONParser;

/**
 * Contains constants and static methods related to I/O
 *
 * @author samuel
 */
public class IO {

    public static final JSONParser JSON_PARSER = new JSONParser();
    public static final String BASE_DIR = "ancolle";

    /**
     * Get an image, downloading it from the given URL and storing it in the
     * cache. If the image is already present in the cache, load it from disk.
     *
     * @param url the URL of the image
     * @param itemDirName the name of the subdirectory inside the cache
     * directory in which this image should be stored
     * @param fileName the filename of the image to be stored in the cache
     * @return the {@link Image} or null if either it failed to be retrieved or
     * does not exist.
     */
    public static Image retrievePicture(String url, String itemDirName, String fileName) {
        Image picture = null;
        // Build the path to the cached image file
        String[] spl = url.split("\\.");
        String ext = spl[spl.length - 1];
        File file = new File(VgmdbApi.CACHE_DIR + File.separator + itemDirName + File.separator + "pictures" + File.separator + fileName + "." + ext);
        // Download image if it cannot be found in the cache
        if (!file.exists()) {
            Logger.getLogger(Album.class.getName()).log(Level.FINE, "Downloading image");
            file.getParentFile().mkdirs();
            VgmdbApi.download(url, file);
        } else {
            Logger.getLogger(Album.class.getName()).log(Level.FINE, "Loading image file from cache");
        }
        // Load the image
        try (final FileInputStream fis = new FileInputStream(file)) {
            picture = new Image(fis);
        } catch (IOException ex) {
            Logger.getLogger(Album.class.getName()).log(Level.SEVERE, null, ex);
        }
        return picture;
    }

}
