package ancolle.main;

import ancolle.io.IO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {

	public static final String SETTINGS_PATH = IO.BASE_DIR + File.separator + "settings.json";

	// Keys (for JSON load/saving)
	private static final String TRACKED_ARTISTS_KEY = "trackedArtists";
	private static final String TRACKED_PRODUCTS_KEY = "trackedProducts";
	private static final String COLLECTED_ALBUMS_KEY = "collectedAlbums";
	private static final String WISHED_ALBUMS_KEY = "wishedAlbums";
	private static final String HIDDEN_ALBUMS_KEY = "hiddenAlbums";
	private static final String SHOW_HIDDEN_ITEMS_KEY = "showHiddenItems";

	private static final Logger LOG = Logger.getLogger(Settings.class.getName());

	private static boolean loadIntegersFromJSONArray(JSONObject root, String key, Collection<Integer> collection) {
		JSONArray arr = (JSONArray) root.get(key);
		return loadIntegersFromJSONArray(arr, collection);
	}

	private static boolean loadIntegersFromJSONArray(JSONArray arr, Collection<Integer> collection) {
		if (arr == null) {
			return false;
		}

		collection.clear();
		for (int i = 0; i < arr.size(); i++) {
			Long longVal = (Long) arr.get(i);
			int id = longVal.intValue();
			collection.add(id);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray createJSONArray(Collection<?> collection) {
		JSONArray arr = new JSONArray();
		arr.addAll(collection);
		return arr;
	}

	// Members
	public final List<Integer> trackedProductIds;
	public final List<Integer> trackedArtistIds;
	public final Set<Integer> collectedAlbumIds;
	public final Set<Integer> wishedAlbumIds;
	public final Set<Integer> hiddenAlbumIds;
	private boolean showHiddenItems;

	public Settings() {
		this.trackedProductIds = new ArrayList<>(10);
		this.trackedArtistIds = new ArrayList<>(10);
		this.collectedAlbumIds = new HashSet<>(50);
		this.wishedAlbumIds = new HashSet<>(50);
		this.hiddenAlbumIds = new HashSet<>(20);
		this.showHiddenItems = false;
	}

	@SuppressWarnings("unchecked")
	public boolean reload() {
		File file = new File(SETTINGS_PATH);
		if (!file.exists() || file.length() == 0) {
			return false;
		}

		try (Reader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			JSONObject root = (JSONObject) new JSONParser().parse(r);
			loadIntegersFromJSONArray(root, TRACKED_ARTISTS_KEY, trackedArtistIds);
			loadIntegersFromJSONArray(root, TRACKED_PRODUCTS_KEY, trackedProductIds);
			loadIntegersFromJSONArray(root, COLLECTED_ALBUMS_KEY, collectedAlbumIds);
			loadIntegersFromJSONArray(root, WISHED_ALBUMS_KEY, wishedAlbumIds);
			loadIntegersFromJSONArray(root, HIDDEN_ALBUMS_KEY, hiddenAlbumIds);
			showHiddenItems = (boolean) root.getOrDefault(SHOW_HIDDEN_ITEMS_KEY, false);
		} catch (ParseException | IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean saveToDisk() {
		LOG.log(Level.INFO, "Saving settings to {0}", SETTINGS_PATH);
		File file = new File(SETTINGS_PATH);
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				boolean success = parent.mkdirs();
				if (!success) {
					LOG.log(Level.SEVERE, "Failed to create directory {0}.",
							parent.toString());
					return false;
				}
			}
		}

		try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			JSONObject root = new JSONObject();
			root.put(Settings.TRACKED_ARTISTS_KEY, createJSONArray(trackedArtistIds));
			root.put(Settings.TRACKED_PRODUCTS_KEY, createJSONArray(trackedProductIds));
			root.put(Settings.COLLECTED_ALBUMS_KEY, createJSONArray(collectedAlbumIds));
			root.put(Settings.WISHED_ALBUMS_KEY, createJSONArray(wishedAlbumIds));
			root.put(Settings.HIDDEN_ALBUMS_KEY, createJSONArray(hiddenAlbumIds));
			root.put(Settings.SHOW_HIDDEN_ITEMS_KEY, showHiddenItems);

			w.write(root.toJSONString());
			w.flush();
			w.close();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
			return false;
		}

		return true;
	}

	public boolean isShowHiddenItems() {
		return showHiddenItems;
	}

	public void setShowHiddenItems(boolean value) {
		this.showHiddenItems = value;
	}

}
