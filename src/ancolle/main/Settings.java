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

/**
 * Stores program settings
 *
 * @author lykat
 */
public class Settings {

	/**
	 * The path to the program's main settings file.
	 */
	public static final String SETTINGS_PATH = IO.BASE_DIR + File.separator + "settings.json";

	// Keys (for JSON load/saving)
	private static final String TRACKED_PRODUCTS_KEY = "trackedProducts";
	private static final String COLLECTED_ALBUMS_KEY = "collectedAlbums";
	private static final String WISHED_ALBUMS_KEY = "wishedAlbums";
	private static final String HIDDEN_ALBUMS_KEY = "hiddenAlbums";
	private static final String SHOW_HIDDEN_ITEMS_KEY = "showHiddenItems";

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(Settings.class.getName());

	/**
	 * Load all integers from a JSON array into a collection. If successful, the
	 * collection will be cleared in the process.
	 *
	 * @param root       the root JSON object containing the JSON array
	 * @param key        the key pointing to the JSON array
	 * @param collection the {@link Collection} into which to load the values
	 * @return true if successful, false if an array could not be found for the
	 * given key
	 */
	private static boolean loadIntegersFromJSONArray(JSONObject root, String key,
	                                                 Collection<Integer> collection) {
		JSONArray arr = (JSONArray) root.get(key);
		return loadIntegersFromJSONArray(arr, collection);
	}

	/**
	 * Load all integers from a JSON array into a collection. If successful, the
	 * collection will be cleared in the process.
	 *
	 * @param arr        the {@link JSONArray}
	 * @param collection the {@link Collection} into which to load the values
	 * @return true if successful, false if an array could not be found for the
	 * given key
	 */
	private static boolean loadIntegersFromJSONArray(JSONArray arr,
	                                                 Collection<Integer> collection) {
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

	/**
	 * Create a new {@link JSONArray} and fill it with the values from the given
	 * {@link Collection}
	 *
	 * @param collection the collection
	 * @return the created array
	 */
	@SuppressWarnings("unchecked")
	private static JSONArray createJSONArray(Collection<?> collection) {
		JSONArray arr = new JSONArray();
		collection.forEach(item -> {
			arr.add(item);
		});
		return arr;
	}

	// Members
	public final List<Integer> trackedProductIds;
	public final Set<Integer> collectedAlbumIds;
	public final Set<Integer> wishedAlbumIds;
	public final Set<Integer> hiddenAlbumIds;
	private boolean showHiddenItems;

	public Settings() {
		this.trackedProductIds = new ArrayList<>(10);
		this.collectedAlbumIds = new HashSet<>(50);
		this.wishedAlbumIds = new HashSet<>(50);
		this.hiddenAlbumIds = new HashSet<>(20);
		this.showHiddenItems = false;
	}

	/**
	 * Load settings from {@link Settings#SETTINGS_PATH} into this
	 * {@link Settings} instance. If successful, this will overwrite any
	 * existing data in this settings instance
	 *
	 * @return false if the settings file does not exist or was empty, or
	 * loading was otherwise unsuccessful, else true
	 */
	@SuppressWarnings("unchecked")
	public boolean load() {
		File file = new File(SETTINGS_PATH);
		if (!file.exists() || file.length() == 0) {
			return false;
		}

		try (Reader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			JSONObject root = (JSONObject) new JSONParser().parse(r);
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

	/**
	 * Save this {@link Settings} object to {@link Settings#SETTINGS_PATH} in
	 * the JSON format.
	 *
	 * @return success
	 */
	@SuppressWarnings("unchecked")
	public boolean save() {
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

		try (Writer w = new OutputStreamWriter(new FileOutputStream(file),
				StandardCharsets.UTF_8)) {
			JSONObject root = new JSONObject();
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
