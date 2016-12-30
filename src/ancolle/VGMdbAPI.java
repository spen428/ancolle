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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author samuel
 */
public class VGMdbAPI {

    public static final String CACHE_DIR = "ancolle" + File.separator + "cache";
    private static final String API_URL = "http://vgmdb.info";
    private static final JSONParser JSON_PARSER = new JSONParser();

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
        String filePath = CACHE_DIR + File.separator + subpath + File.separator + id + ".json";
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
            return (JSONObject) JSON_PARSER.parse(new FileReader(file));
        } catch (IOException | ParseException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Product getProductById(int id) {
        JSONObject jo = request("product", id);
        if (jo != null) {
            String title_en = (String) jo.get("name");
            String title_jp = (String) jo.get("name_real");
            String typeString = (String) jo.get("type");
            String pictureUrl = (String) jo.get("picture_small");
            // Get albums
            ArrayList<AlbumPreview> albums = new ArrayList<>();
            try {
                JSONArray arr = (JSONArray) jo.get("albums");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    String link = (String) obj.get("link");
                    String[] spl = link.split("/");
                    int album_id = Integer.valueOf(spl[spl.length - 1]);
                    JSONObject titles = (JSONObject) obj.get("titles");
                    String album_title_en = (String) titles.get("en");
                    String album_title_jp = (String) titles.get("jp");
                    String type = (String) obj.get("type");
                    String dateString = (String) obj.get("date");
                    Date date = null;
                    if (dateString != null) {
                        try {
                            date = SDF.parse(dateString);
                        } catch (java.text.ParseException ex) {
                            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
                                    "Failed to parse date string: " + dateString, ex);
                            // Try parsing just the year and month yyyy-MM
                            try {
                                date = SDF2.parse(dateString);
                            } catch (java.text.ParseException ex2) {
                                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE,
                                        "Failed to parse date string: " + dateString, ex2);
                            }
                        }
                    }
                    albums.add(new AlbumPreview(album_id, album_title_en, album_title_jp, type, date));
                }
                return new Product(id, title_en, title_jp, typeString, pictureUrl, albums);
            } catch (Exception ex) {
                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM");

    public static Album getAlbumById(int id) {
        JSONObject obj = request("album", id);
        if (obj != null) {
            try {
                String link = (String) obj.get("link");
                String[] spl = link.split("/");
                int album_id = Integer.valueOf(spl[spl.length - 1]);
                JSONObject titles = (JSONObject) obj.get("names");
                String title_en = (String) titles.get("en");
                String title_jp = (String) titles.get("jp");
                String type = (String) obj.get("type");
                String coverMedium = (String) obj.get("picture_small");
                String dateString = (String) obj.get("date");
                Date date = null;
                if (dateString != null) {
                    try {
                        date = SDF.parse(dateString);
                    } catch (java.text.ParseException ex) {
                        Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return new Album(album_id, title_en, title_jp, type, date, coverMedium);
            } catch (NumberFormatException ex) {
                Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

}
