package ancolle.main;

import ancolle.io.IO;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    // Members
    public final List<Integer> trackedProducts;

    public Settings() {
        this.trackedProducts = new ArrayList<>();
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

    /**
     * Save the {@link Settings} object to {@link Settings#SETTINGS_PATH} in the
     * JSON format.
     *
     * @param settings the {@link Settings} instance
     * @return success
     */
    public static boolean saveSettings(Settings settings) {
        Logger.getLogger(IO.class.getName()).log(Level.INFO, "Saving settings");
        File file = new File(SETTINGS_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter fw = new FileWriter(file)) {
            JSONObject jo = new JSONObject();
            JSONArray arr = new JSONArray();
            settings.trackedProducts.forEach((Integer id) -> {
                arr.add(id);
            });
            jo.put(Settings.TRACKED_PRODUCTS_KEY, arr);

            fw.write(jo.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
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
            JSONObject jo = (JSONObject) IO.JSON_PARSER.parse(fr);
            JSONArray arr = (JSONArray) jo.get(TRACKED_PRODUCTS_KEY);
            for (int i = 0; i < arr.size(); i++) {
                // Number literals in json are always longs, so we must cast down
                Long longVal = (Long) arr.get(i);
                int id = longVal.intValue();
                settings.trackedProducts.add(id);
            }
        } catch (ParseException | IOException ex) {
            Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return settings;
    }

}
