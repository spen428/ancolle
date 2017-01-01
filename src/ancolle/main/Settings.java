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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Stores program settings
 *
 * @author samuel
 */
public class Settings {

    public static final String SETTINGS_PATH = IO.BASE_DIR + File.separator + "settings.json";

    // Keys (for JSON load/saving)
    public static final String TRACKED_PRODUCTS_KEY = "trackedProducts";
    public static final String COLLECTED_ALBUMS_KEY = "collectedAlbums";

    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    /**
     * Save the {@link Settings} object to {@link Settings#SETTINGS_PATH} in the
     * JSON format.
     *
     * @param settings the {@link Settings} instance
     * @return success
     */
    @SuppressWarnings("unchecked")
    public static boolean saveSettings(Settings settings) {
	LOG.log(Level.INFO, "Saving settings to {0}", SETTINGS_PATH);
	File file = new File(SETTINGS_PATH);
	if (!file.exists()) {
	    file.getParentFile().mkdirs();
	}

	try (FileWriter fw = new FileWriter(file)) {
	    JSONObject root = new JSONObject();

	    JSONArray trackedProductIdsArr = new JSONArray();
	    settings.trackedProductIds.forEach((Integer id) -> {
		trackedProductIdsArr.add(id);
	    });
	    root.put(Settings.TRACKED_PRODUCTS_KEY, trackedProductIdsArr);

	    JSONArray collectedAlbumIdsArr = new JSONArray();
	    settings.collectedAlbumIds.forEach((Integer id) -> {
		collectedAlbumIdsArr.add(id);
	    });
	    root.put(Settings.COLLECTED_ALBUMS_KEY, collectedAlbumIdsArr);

	    fw.write(root.toJSONString());
	    fw.flush();
	    fw.close();
	} catch (IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	    return false;
	}

	return true;
    }

    /**
     * Load settings from {@link Settings#SETTINGS_PATH} into a new
     * {@link Settings} instance.
     *
     * @return the new {@link Settings} instance
     */
    public static Settings loadSettings() {
	final Settings settings = new Settings();
	File file = new File(SETTINGS_PATH);
	if (!file.exists() || file.length() == 0) {
	    return settings;
	}

	try (FileReader fr = new FileReader(file)) {
	    JSONObject root = (JSONObject) IO.JSON_PARSER.parse(fr);
	    JSONArray arr = (JSONArray) root.get(TRACKED_PRODUCTS_KEY);
	    if (arr != null) {
		for (int i = 0; i < arr.size(); i++) {
		    // Number literals in json are always longs, so we must cast down
		    Long longVal = (Long) arr.get(i);
		    int id = longVal.intValue();
		    settings.trackedProductIds.add(id);
		}
	    }
	    arr = (JSONArray) root.get(COLLECTED_ALBUMS_KEY);
	    if (arr != null) {
		for (int i = 0; i < arr.size(); i++) {
		    Long longVal = (Long) arr.get(i);
		    int id = longVal.intValue();
		    settings.collectedAlbumIds.add(id);
		}
	    }
	} catch (ParseException | IOException ex) {
	    LOG.log(Level.SEVERE, null, ex);
	}

	return settings;
    }

    // Members
    public final List<Integer> trackedProductIds;
    public final Set<Integer> collectedAlbumIds;

    public Settings() {
	this.trackedProductIds = new ArrayList<>(10);
	this.collectedAlbumIds = new HashSet<>(100);
    }

    /**
     * Save this {@link Settings} object to {@link Settings#SETTINGS_PATH} in
     * the JSON format.
     *
     * @return success
     */
    public boolean save() {
	return saveSettings(this);
    }

}
