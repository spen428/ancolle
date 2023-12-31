package ancolle.io;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains constants and static methods related to I/O
 *
 * @author lykat
 */
public class IO {

	/**
	 * The result of calling {@link System#getProperty} with the parameter
	 * "user.home". On Windows this should return the expansion of
	 * {@code %USERPROFILE%}, on UNIX systems it should return the expansion of
	 * {@code ~}.
	 */
	public static final String USER_HOME = System.getProperty("user.home");

	/**
	 * The root of the directory used for storage of this program's settings and
	 * cached files.
	 */
	public static final String BASE_DIR = USER_HOME + File.separator + "ancolle";

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(IO.class.getName());

	/**
	 * Disable image loading, for debugging
	 */
	private static final boolean DISABLE_IMAGES = false;

	/**
	 * Get an image, downloading it from the given URL and storing it in the
	 * cache. If the image is already present in the cache, load it from disk.
	 *
	 * @param url         the URL of the image
	 * @param itemDirName the name of the subdirectory inside the cache
	 *                    directory in which this image should be stored
	 * @param fileName    the filename of the image to be stored in the cache
	 * @return the {@link Image} or null if either it failed to be retrieved or
	 * does not exist.
	 */
	public static Image retrievePicture(String url, String itemDirName,
	                                    String fileName) {
		return retrievePicture(url, itemDirName, fileName, false);
	}

	/**
	 * Get an image, downloading it from the given URL and storing it in the
	 * cache. If the image is already present in the cache, load it from disk.
	 *
	 * @param url         the URL of the image
	 * @param itemDirName the name of the subdirectory inside the cache
	 *                    directory in which this image should be stored
	 * @param fileName    the filename of the image to be stored in the cache
	 * @param cacheOnly   return {@code null} if the item is not in the cache
	 * @return the {@link Image} or null if either it failed to be retrieved or
	 * does not exist.
	 */
	public static Image retrievePicture(String url, String itemDirName,
	                                    String fileName, boolean cacheOnly) {
		if (DISABLE_IMAGES) {
			return null;
		}

		Image picture = null;
		// Build the path to the cached image file
		File file = new File(getImageCachePath(url, itemDirName, fileName));
		// Download image if it cannot be found in the cache
		if (!file.exists()) {
			if (cacheOnly) {
				return null;
			}

			LOG.log(Level.FINE, "Downloading image");
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				boolean success = parent.mkdirs();
				if (!success) {
					LOG.log(Level.SEVERE, "Failed to create directory {0}.",
							parent.toString());
					return null;
				}
			}
			VgmdbApi.download(url, file);
		} else {
			LOG.log(Level.FINE, "Loading image file from cache");
		}
		// Load the image
		try (final FileInputStream fis = new FileInputStream(file)) {
			picture = new Image(fis);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
		return picture;
	}

	public static String getImageCachePath(String url, String itemDirName,
	                                       String fileName) {
		String[] spl = url.split("\\.");
		String ext = spl[spl.length - 1];
		return VgmdbApi.CACHE_DIR + File.separator + itemDirName
				+ File.separator + "pictures" + File.separator + fileName
				+ "." + ext;
	}

	private IO() {
	}

}
