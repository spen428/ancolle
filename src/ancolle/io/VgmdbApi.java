package ancolle.io;

import ancolle.items.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VgmdbApi {

	public static final String CACHE_DIR = IO.BASE_DIR + File.separator + "cache";

	private static final String API_URL = "http://vgmdb.info";
	private static final int DOWNLOAD_BUFFER_SIZE_BYTES = 1024;

	private static final Logger LOG = Logger.getLogger(VgmdbApi.class.getName());
	private static final Pattern DISC_REGEX = Pattern.compile("Disc (\\d+).*");

	public static void download(String url, File file) {
		try {
			download(new URL(url), file);
		} catch (MalformedURLException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

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

	public static Product getProductById(int id) {
		return getProductById(id, false);
	}

	public static Product getProductById(int id, boolean cacheOnly) {
		JSONObject jo = request("product", id, cacheOnly);
		if (jo != null) {
			String title_en = (String) jo.get("name");
			String title_ja = (String) jo.get("name_real");
			String typeString = (String) jo.get("type");
			String pictureUrl = (String) jo.get("picture_small");
			// Get albums
			ArrayList<AlbumPreview> albums = getAlbumPreviews(jo, "albums");
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

	public static Album getAlbumById(int id) {
		return getAlbumById(id, false);
	}

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

			Matcher matcher = DISC_REGEX.matcher(discName);
			if (matcher.matches()) {
				discNumber = Integer.parseInt(matcher.group(1));
			} else {
				LOG.log(Level.SEVERE, "Failed to parse disc number from disc name: {0}", discName);
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
				date, picture_small, trackList, null, null, false);
	}

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
			String title_en = (String) titles.getOrDefault("en", "");
			String title_ja = (String) titles.getOrDefault("ja", "");
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

	public static List<ArtistPreview> searchArtists(String text) {
		JSONObject rootObj = search("artists", text);
		if (rootObj == null) {
			return new ArrayList<>(0);
		}

		JSONObject resultsObj = (JSONObject) rootObj.get("results");
		JSONArray artistsObjs = (JSONArray) resultsObj.get("artists");
		if (artistsObjs == null) {
			return new ArrayList<>(0);
		}

		ArrayList<ArtistPreview> results = new ArrayList<>(artistsObjs.size());
		for (int i = 0; i < artistsObjs.size(); i++) {
			JSONObject product = (JSONObject) artistsObjs.get(i);

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
			if (title_en == null) {
				title_en = "";
			}
			String title_ja = (String) titles.get("ja");
			if (title_ja == null) {
				title_ja = "";
			}

			JSONArray aliasesArr = (JSONArray) product.get("aliases");
			String[] aliases = new String[0];
			if (aliasesArr != null) {
				aliases = (String[]) aliasesArr.toArray(new String[aliasesArr.size()]);
			}
			results.add(new ArtistPreview(id, title_en, title_ja, aliases));
		}
		return results;
	}

	public static void removeFromCache(String subPath, int id) {
		File file = new File(getFilePath(subPath, id));
		if (file.exists()) {
			file.delete();
		}
	}

	private static String getFilePath(String subPath, int id) {
		return CACHE_DIR + File.separator + subPath + File.separator + id + ".json";
	}

	private VgmdbApi() {
	}

	public static Artist getArtistById(int id, boolean cacheOnly) {
		JSONObject jo = request("artist", id, cacheOnly);
		if (jo == null) {
			return null;
		}

		String title_en = (String) jo.get("name");
		String title_ja = (String) jo.get("name_real");
		String typeString = (String) jo.get("type");
		String sex = (String) jo.get("sex");
		String birthplace = (String) jo.get("birthplace");
		String birthdateString = (String) jo.get("birthdate");
		String pictureUrl = (String) jo.get("picture_small");
		String notes = (String) jo.get("notes");

		ArrayList<AlbumPreview> discography = getAlbumPreviews(jo, "discography");
		ArrayList<AlbumPreview> featured_on = getAlbumPreviews(jo, "featured_on");
		return new Individual(id, title_en, title_ja, pictureUrl, discography, featured_on);
	}

	private static ArrayList<AlbumPreview> getAlbumPreviews(JSONObject jo, String discography) {
		JSONArray arr = (JSONArray) jo.get(discography);
		ArrayList<AlbumPreview> albums = new ArrayList<>(arr.size());
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);
			String catalog = (String) obj.getOrDefault("catalog", "");
			boolean reprint = (boolean) obj.getOrDefault("reprint", false);
			String link = (String) obj.get("link");
			String[] spl = link.split("/");
			int album_id = Integer.parseInt(spl[spl.length - 1]);
			JSONObject titles = (JSONObject) obj.get("titles");
			String album_title_en = (String) titles.getOrDefault("en", "");
			String album_title_ja = (String) titles.getOrDefault("ja", "");
			String albumTypeString = (String) obj.getOrDefault("type", "");
			String dateString = (String) obj.get("date");
			Date date = null;
			if (dateString != null) {
				date = parseDate(dateString);
			}
			JSONArray roleArr = (JSONArray) obj.getOrDefault("roles", null);
			ArrayList<String> roles = new ArrayList<>(4);
			if (roleArr != null) {
				for (Object role : roleArr) {
					roles.add((String) role);
				}
			}
			albums.add(new AlbumPreview(album_id, album_title_en, album_title_ja, albumTypeString,
					roles.toArray(new String[roles.size()]), catalog, reprint, date));
		}
		return albums;
	}

	public static <T extends Item> T getById(String itemTypeName, int id) {
		if ("product".equals(itemTypeName)) {
			return (T) getProductById(id);
		}

		if ("album".equals(itemTypeName)) {
			return (T) getAlbumById(id);
		}

		if ("artist".equals(itemTypeName)) {
			return (T) getArtistById(id, false);
		}

		throw new NotImplementedException();
	}

	public static <TPreview extends Item> List<TPreview> searchFor(String itemTypeName, String text) {
		if ("product".equals(itemTypeName)) {
			return (List<TPreview>) searchProducts(text);
		}

		if ("artist".equals(itemTypeName)) {
			return (List<TPreview>) searchArtists(text);
		}

		throw new NotImplementedException();
	}

	public static <T extends Item> void removeFromCache(T item) {
		removeFromCache(item.getSubPath(), item.id);
	}
}
