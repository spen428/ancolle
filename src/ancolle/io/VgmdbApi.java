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
import ancolle.items.AlbumPreview;
import ancolle.items.Franchise;
import ancolle.items.Product;
import ancolle.items.ProductPreview;
import ancolle.items.ProductType;
import ancolle.items.Track;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Static methods for interfacing with VGMdb, the Video Game (and Anime) Music
 * database.
 *
 * @author lykat
 */
public class VgmdbApi {

    /**
     * The directory in which cached files for this program are stored.
     */
    public static final String CACHE_DIR = IO.BASE_DIR + File.separator + "cache";

    private static final String API_URL = "http://vgmdb.info";
    private static final int DOWNLOAD_BUFFER_SIZE_BYTES = 1024;

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(VgmdbApi.class.getName());

    /**
     * Download the data found at the given URL and save it to the given file
     *
     * @param url the URL, as a string
     * @param file the output file
     */
    public static void download(String url, File file) {
	try {
	    download(new URL(url), file);
	} catch (MalformedURLException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Download the data found at the given URL and save it to the given file
     *
     * @param url the URL
     * @param file the output file
     */
    public static void download(URL url, File file) {
	try (InputStream in = url.openStream();
		FileOutputStream fos = new FileOutputStream(file)) {
	    int length;
	    byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE_BYTES];
	    while ((length = in.read(buffer)) > -1) {
		fos.write(buffer, 0, length);
	    }
	    fos.close();
	    in.close();
	} catch (IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Perform a request on the database at the given URL sub-path. This will
     * request data from {@link VgmdbApi#API_URL}/{@code subpath}/{@code id} and
     * return the results in the JSON format.
     *
     * <p>
     * The sub-path determines what data is looked up, for example to look up an
     * album the sub-path should be "album" and the id should be the album id.
     *
     * @param subPath the sub-path
     * @param id the id of the item requested
     * @param cacheOnly return {@code null} if the item is not in the cache
     * @return a {@link JSONObject} representing the result of the query
     */
    public static JSONObject request(String subPath, int id, boolean cacheOnly) {
	String filePath = getFilePath(subPath, id);
	File file = new File(filePath);
	if (!file.exists()) {
	    if (cacheOnly) {
		return null;
	    }

	    File parent = file.getParentFile();
	    if (parent != null && !parent.exists()) {
		boolean success = parent.mkdirs();
		if (!success) {
		    LOG.log(Level.SEVERE, "Failed to create directory {0}.",
			    parent.toString());
		    return null;
		}
	    }
	    String url = API_URL + "/" + subPath + "/" + id + "?format=json";
	    download(url, file);
	}

	try (Reader r = new InputStreamReader(new FileInputStream(file),
		StandardCharsets.UTF_8)) {
	    return (JSONObject) new JSONParser().parse(r);
	} catch (org.json.simple.parser.ParseException | IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
	return null;
    }

    /**
     * Search the database for full details of the product with the given id
     *
     * @param id the product id
     * @return an instance of {@link Product} representing the product, or null
     * if the query failed
     */
    public static Product getProductById(int id) {
	return getProductById(id, false);
    }

    /**
     * Search the database for full details of the product with the given id
     *
     * @param id the product id
     * @param cacheOnly return {@code null} if the item is not in the cache
     * @return an instance of {@link Product} representing the product, or null
     * if the query failed
     */
    public static Product getProductById(int id, boolean cacheOnly) {
	JSONObject jo = request("product", id, cacheOnly);
	if (jo != null) {
	    String title_en = (String) jo.get("name");
	    String title_ja = (String) jo.get("name_real");
	    String typeString = (String) jo.get("type");
	    String pictureUrl = (String) jo.get("picture_small");
	    // Get albums
	    JSONArray arr = (JSONArray) jo.get("albums");
	    ArrayList<AlbumPreview> albums = new ArrayList<>(arr.size());
	    for (int i = 0; i < arr.size(); i++) {
		JSONObject obj = (JSONObject) arr.get(i);
		String link = (String) obj.get("link");
		String[] spl = link.split("/");
		int album_id = Integer.parseInt(spl[spl.length - 1]);
		JSONObject titles = (JSONObject) obj.get("titles");
		String album_title_en = (String) titles.get("en");
		String album_title_ja = (String) titles.get("ja");
		String albumTypeString = (String) obj.get("type");
		String dateString = (String) obj.get("date");
		Date date = null;
		if (dateString != null) {
		    date = parseDate(dateString);
		}
		albums.add(new AlbumPreview(album_id, album_title_en,
			album_title_ja, albumTypeString, date));
	    }
	    ProductType type = ProductType.getProductTypeFromString(typeString);
	    if (type == ProductType.UNKNOWN) {
		LOG.log(Level.WARNING, "Unknown ProductType string: {0}", typeString);
	    } else if (type == ProductType.FRANCHISE) {
		LOG.log(Level.FINE, "Fetching products associated with "
			+ "franchise ({0}) {1}", new Object[]{id, title_en});
		JSONArray titles = (JSONArray) jo.get("titles");
		ArrayList<Product> products = new ArrayList<>(titles.size());
		for (int i = 0; i < titles.size(); i++) {
		    JSONObject title = (JSONObject) titles.get(i);
		    String link = (String) title.get("link");
		    String[] spl = link.split("/");
		    int productId = Integer.parseInt(spl[spl.length - 1]);
		    Product product = getProductById(productId);
		    if (product != null) {
			products.add(product);
		    }
		}
		return new Franchise(id, title_en, title_ja, products);
	    }
	    return new Product(id, title_en, title_ja, type, pictureUrl, albums);
	}
	return null;
    }

    /**
     * Attempt to parse a UTC date string into a {@link Date} object. Supported
     * forms are yyyy-MM-dd, yyyy-MM, and yyyy.
     *
     * @param dateString the date string, e.g. "2000-01-02" or "2003-06"
     * @return the {@link Date} object, or null if the string failed to parse
     */
    private static Date parseDate(String dateString) {
	try {
	    return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	} catch (java.text.ParseException ex) {
	}

	// Try parsing just the year and month
	try {
	    return new SimpleDateFormat("yyyy-MM").parse(dateString);
	} catch (java.text.ParseException ex) {
	}

	// Try parsing just the year
	try {
	    return new SimpleDateFormat("yyyy").parse(dateString);
	} catch (java.text.ParseException ex) {
	    LOG.log(Level.SEVERE, "Failed to parse date string: "
		    + dateString, ex);
	}

	return null;
    }

    /**
     * Search the database for full details of the album with the given id
     *
     * @param id the album id
     * @return an instance of {@link Album} representing the album, or null if
     * the query failed
     */
    public static Album getAlbumById(int id) {
	return getAlbumById(id, false);
    }

    /**
     * Search the database for full details of the album with the given id
     *
     * @param id the album id
     * @param cacheOnly return {@code null} if the item is not in the cache
     * @return an instance of {@link Album} representing the album, or null if
     * the query failed
     */
    public static Album getAlbumById(int id, boolean cacheOnly) {
	JSONObject obj = request("album", id, cacheOnly);
	if (obj == null) {
	    LOG.log(Level.INFO, "Failed to retrieve album with id {0}", id);
	    return null;
	}

	String link = (String) obj.get("link");

	// The link above contains the album id, parse it
	String[] spl = link.split("/");
	String album_id_str = spl[spl.length - 1];
	int album_id = -1;
	try {
	    album_id = Integer.parseInt(album_id_str);
	} catch (NumberFormatException ex) {
	    LOG.log(Level.SEVERE, "Failed to parse string as integer {0}",
		    album_id_str);
	}
	if (album_id == -1 || album_id != id) {
	    LOG.log(Level.SEVERE, "Album id mismatch {0} != {1}, using id {1}",
		    new int[]{album_id, id});
	    album_id = id;
	}

	JSONObject titles = (JSONObject) obj.get("names");
	String title_en = (String) titles.get("en");
	String title_ja = (String) titles.get("ja");
	String title_ja_latn = (String) titles.get("ja-latn");
	String type = (String) obj.get("classification");
	String picture_small = (String) obj.get("picture_small");
	String dateString = (String) obj.get("release_date");
	Date date = null;
	if (dateString != null) {
	    date = parseDate(dateString);
	}

	// Get tracks
	List<Track> trackList = new ArrayList<>();
	JSONArray discs = (JSONArray) obj.get("discs");
	for (int i = 0; i < discs.size(); i++) {
	    JSONObject disc = (JSONObject) discs.get(i);
	    String discName = (String) disc.get("name");
	    int discNumber = 0;
	    try {
		discNumber = Integer.parseInt(discName.split("Disc ")[1]);
	    } catch (NumberFormatException ex) {
		LOG.log(Level.SEVERE, "Failed to parse disc number from disc "
			+ "name: {0}", discName);
	    }
	    JSONArray tracks = (JSONArray) disc.get("tracks");
	    for (int j = 0; j < tracks.size(); j++) {
		JSONObject track = (JSONObject) tracks.get(j);
		String length = (String) track.get("track_length");
		JSONObject names = (JSONObject) track.get("names");
		String name;
		// TODO: This assumption is bad
		// Assume Japanese name is canonical
		name = (String) names.get("Japanese");
		if (name == null) {
		    // Try again, if this fails, just accept that it is null
		    name = (String) names.get("English");
		}
		int trackNumber = j + 1;
		trackList.add(new Track(name, length, trackNumber, discNumber));
	    }
	}

	return new Album(album_id, title_en, title_ja, title_ja_latn, type,
		date, picture_small, trackList);
    }

    /**
     * Perform a search query on the database at the given URL sub-path. See
     * also {@link VgmdbApi#request} for more information about the sub-path.
     *
     * @param subPath the sub-path
     * @param searchString the search query
     * @return a {@link JSONObject} representing the result of the query
     * @see VgmdbApi#request
     */
    private static JSONObject search(String subPath, String searchString) {
	String filePath = CACHE_DIR + File.separator + subPath + File.separator
		+ "search.json";
	File file = new File(filePath);
	if (!file.exists()) {
	    File parent = file.getParentFile();
	    if (parent != null && !parent.exists()) {
		boolean success = parent.mkdirs();
		if (!success) {
		    LOG.log(Level.SEVERE, "Failed to create directory {0}.",
			    parent.toString());
		    return null;
		}
	    }
	} else {
	    boolean success = file.delete();
	    if (!success) {
		LOG.log(Level.WARNING, "Failed to delete file {0}",
			file.toString());
	    }
	}

	String encodedSearchString;
	try {
	    encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
	    // The URLEncoder above does not encode spaces as expected, fix below
	    encodedSearchString = encodedSearchString.replace("+", "%20");
	    String url = API_URL + "/search/" + subPath + "/"
		    + encodedSearchString + "?format=json";
	    download(url, file);
	} catch (UnsupportedEncodingException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}

	try (Reader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
	    return (JSONObject) new JSONParser().parse(r);
	} catch (IOException | org.json.simple.parser.ParseException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}
	return null;
    }

    /**
     * Search the database for products matching the given search terms
     *
     * @param text the search query
     * @return a list of {@link ProductPreview} representing the products that
     * matched the query
     */
    public static List<ProductPreview> searchProducts(String text) {
	JSONObject rootObj = search("products", text);
	if (rootObj == null) {
	    return new ArrayList<>(0);
	}

	JSONObject resultsObj = (JSONObject) rootObj.get("results");
	JSONArray prodsObjs = (JSONArray) resultsObj.get("products");
	if (prodsObjs == null) {
	    return new ArrayList<>(0);
	}

	ArrayList<ProductPreview> results = new ArrayList<>(prodsObjs.size());
	for (int i = 0; i < prodsObjs.size(); i++) {
	    JSONObject product = (JSONObject) prodsObjs.get(i);

	    String link = (String) product.get("link");
	    // The link above contains the album id, parse it
	    String[] spl = link.split("/");
	    String id_str = spl[spl.length - 1];
	    int id = -1;
	    try {
		id = Integer.parseInt(id_str);
	    } catch (NumberFormatException ex) {
		LOG.log(Level.SEVERE, "Failed to parse string as integer {0}",
			id_str);
	    }

	    JSONObject titles = (JSONObject) product.get("names");
	    String title_en = (String) titles.get("en");
	    String title_ja = (String) titles.get("ja");
	    String typeString = (String) product.get("type");
	    ProductType type = ProductType.getProductTypeFromString(typeString);
	    if (type == ProductType.UNKNOWN) {
		LOG.log(Level.WARNING, "Unknown ProductType string: {0}",
			typeString);
	    }
	    results.add(new ProductPreview(id, title_en, title_ja, type));
	}
	return results;
    }

    public static void removeFromCache(String subPath, int id) {
	File file = new File(getFilePath(subPath, id));
	if (file.exists()) {
	    file.delete();
	}
    }

    public static void removeFromCache(AlbumPreview album) {
	removeFromCache("album", album.id);
    }

    public static void removeFromCache(Product product) {
	removeFromCache("product", product.id);
    }

    private static String getFilePath(String subPath, int id) {
	return CACHE_DIR + File.separator + subPath + File.separator + id + ".json";
    }

    private VgmdbApi() {
    }

}
