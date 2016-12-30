/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancolle;

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
import org.json.simple.parser.ParseException;

/**
 *
 * @author samuel
 */
public class VGMdbAPI {

    private static final String API_URL = "http://vgmdb.info";
    private static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_YYYY_MM = new SimpleDateFormat("yyyy-MM");

    public static void download(URL url, File file) throws IOException {
        InputStream in = url.openStream();
        FileOutputStream fos = new FileOutputStream(file);
        int length = -1;
        byte[] buffer = new byte[1024];
        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        in.close();
    }

    public static JSONObject request(String subpath, int id) {
        String filePath = IO.CACHE_DIR + File.separator + subpath + File.separator + id + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            URL url;
            try {
                url = new URL(API_URL + "/" + subpath + "/" + id + "?format=json");
                download(url, file);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        try {
            return (JSONObject) IO.JSON_PARSER.parse(new FileReader(file));
        } catch (IOException | ParseException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
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
                String type = (String) obj.get("type");
                String dateString = (String) obj.get("date");
                Date date = null;
                if (dateString != null) {
                    date = parseDate(dateString);
                }
                albums.add(new AlbumPreview(album_id, album_title_en, album_title_ja, type, date));
            }
            return new Product(id, title_en, title_ja, typeString, pictureUrl, albums);
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
        Date date = null;
        try {
            date = SDF_YYYY_MM_DD.parse(dateString);
        } catch (java.text.ParseException ex) {
            // Try parsing just the year and month yyyy-MM
            try {
                date = SDF_YYYY_MM.parse(dateString);
            } catch (java.text.ParseException ex2) {
                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
                        "Failed to parse date string: " + dateString, ex2);
            }
        }
        return date;
    }

    public static Album getAlbumById(int id) {
        JSONObject obj = request("album", id);
        if (obj == null) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.INFO,
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
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
                    "Failed to parse string as integer {0}", album_id_str);
        }
        if (album_id == -1 || album_id != id) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
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
        String filePath = IO.CACHE_DIR + File.separator + subpath + File.separator + "search.json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        URL url;
        try {
            url = new URL(API_URL + "/search/" + subpath + "/" + searchString + "?format=json");
            download(url, file);
        } catch (MalformedURLException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            return (JSONObject) IO.JSON_PARSER.parse(new FileReader(file));
        } catch (IOException | ParseException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
                            "Failed to parse string as integer {0}", id_str);
                }

                JSONObject titles = (JSONObject) product.get("names");
                String title_en = (String) titles.get("en");
                String title_ja = (String) titles.get("ja");
                String title_ja_latn = (String) titles.get("ja-latn");
                String type = (String) product.get("type");

                results.add(new ProductPreview(id, title_en, title_ja,
                        title_ja_latn, type));
            }
        }
        return results;
    }

}
