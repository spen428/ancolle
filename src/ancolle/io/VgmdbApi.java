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

import ancolle.items.ProductPreview;
import ancolle.items.Album;
import ancolle.items.Product;
import ancolle.items.AlbumPreview;
import ancolle.items.ProductType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Static methods for interfacing with VGMdb, the Video Game (and Anime) Music
 * database.
 *
 * @author samuel
 */
public class VgmdbApi {

    public static final String CACHE_DIR = IO.BASE_DIR + File.separator + "cache";

    private static final String API_URL = "http://vgmdb.info";
    private static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_YYYY_MM = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat SDF_YYYY = new SimpleDateFormat("yyyy");
    private static final int DOWNLOAD_BUFFER_SIZE_BYTES = 1024;

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
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JSONObject request(String subpath, int id) {
        String filePath = CACHE_DIR + File.separator + subpath + File.separator + id + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            String url = API_URL + "/" + subpath + "/" + id + "?format=json";
            download(url, file);
        }

        try (FileReader fr = new FileReader(file)) {
            return (JSONObject) IO.JSON_PARSER.parse(fr);
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Product getProductById(int id) {
        JSONObject jo = request("product", id);
        if (jo != null) {
            String title_en = (String) jo.get("name");
            String title_ja = (String) jo.get("name_real");
            String typeString = (String) jo.get("type");
            String pictureUrl = (String) jo.get("picture_small");
            // Get albums
            ArrayList<AlbumPreview> albums = new ArrayList<>();
            JSONArray arr = (JSONArray) jo.get("albums");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                String link = (String) obj.get("link");
                String[] spl = link.split("/");
                int album_id = Integer.valueOf(spl[spl.length - 1]);
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
                Logger.getLogger(VgmdbApi.class.getName()).log(Level.WARNING,
                        "Unknown ProductType string: {0}", typeString);
            }
            return new Product(id, title_en, title_ja, type, pictureUrl, albums);
        }
        return null;
    }

    /**
     * Attempt to parse a UTC date string into a {@link Date} object. Supported
     * forms are yyyy-MM-dd and yyyy-MM.
     *
     * @param dateString the date string, e.g. "2000-01-02" or "2003-06"
     * @return the {@link Date} object, or null if the string failed to parse
     */
    private static Date parseDate(String dateString) {
        try {
            return SDF_YYYY_MM_DD.parse(dateString);
        } catch (java.text.ParseException ex) {
        }

        // Try parsing just the year and month yyyy-MM
        try {
            return SDF_YYYY_MM.parse(dateString);
        } catch (java.text.ParseException ex) {
        }

        // Try parsing just the year yyyy
        try {
            return SDF_YYYY.parse(dateString);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE,
                    "Failed to parse date string: " + dateString, ex);
        }

        return null;
    }

    public static Album getAlbumById(int id) {
        JSONObject obj = request("album", id);
        if (obj == null) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.INFO,
                    "Failed to retrieve album with id {0}", id);
            return null;
        }

        String link = (String) obj.get("link");

        // The link above contains the album id, parse it
        String[] spl = link.split("/");
        String album_id_str = spl[spl.length - 1];
        int album_id = -1;
        try {
            album_id = Integer.valueOf(album_id_str);
        } catch (NumberFormatException ex) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE,
                    "Failed to parse string as integer {0}", album_id_str);
        }
        if (album_id == -1 || album_id != id) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE,
                    "Album id mismatch {0} != {1}, using id {1}",
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
        return new Album(album_id, title_en, title_ja, title_ja_latn, type,
                date, picture_small);
    }

    public static JSONObject search(String subpath, String searchString) {
        String filePath = CACHE_DIR + File.separator + subpath + File.separator + "search.json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            file.delete();
        }

        String url = API_URL + "/search/" + subpath + "/" + searchString + "?format=json";
        download(url, file);

        try {
            return (JSONObject) IO.JSON_PARSER.parse(new FileReader(file));
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<ProductPreview> searchProducts(String text) {
        ArrayList<ProductPreview> results = new ArrayList<>();
        JSONObject jo = search("products", text);
        if (jo != null) {
            JSONObject resultsObj = (JSONObject) jo.get("results");
            JSONArray prods = (JSONArray) resultsObj.get("products");
            for (int i = 0; i < prods.size(); i++) {
                JSONObject product = (JSONObject) prods.get(i);

                String link = (String) product.get("link");
                // The link above contains the album id, parse it
                String[] spl = link.split("/");
                String id_str = spl[spl.length - 1];
                int id = -1;
                try {
                    id = Integer.valueOf(id_str);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(VgmdbApi.class.getName()).log(Level.SEVERE,
                            "Failed to parse string as integer {0}", id_str);
                }

                JSONObject titles = (JSONObject) product.get("names");
                String title_en = (String) titles.get("en");
                String title_ja = (String) titles.get("ja");
                String typeString = (String) product.get("type");
                ProductType type = ProductType.getProductTypeFromString(typeString);
                if (type == ProductType.UNKNOWN) {
                    Logger.getLogger(VgmdbApi.class.getName()).log(Level.WARNING,
                            "Unknown ProductType string: {0}", typeString);
                }
                results.add(new ProductPreview(id, title_en, title_ja, type));
            }
        }
        return results;
    }

}
