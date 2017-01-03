/*  AnColle, an anime and video game music collection tracker
 *  Copyright (C) 2016-17  lykat
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String BASE_DIR = USER_HOME + File.separator + "ancolle";
    private static final Logger LOG = Logger.getLogger(IO.class.getName());

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

    private IO() {
    }

}
