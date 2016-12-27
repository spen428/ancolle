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
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
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

    private static final JSONParser parser = new JSONParser();

    public static void download(URL url, File file) throws IOException {
        InputStream in = url.openStream();
        FileOutputStream fos = new FileOutputStream(file);
        int length = -1;
        byte[] buffer = new byte[1024];// buffer for portion of data from
        // connection
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
            return (JSONObject) parser.parse(new FileReader(file));
        } catch (IOException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(VGMdbAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Product getProductById(int id) {
        JSONObject jo = request("product", id);
        if (jo != null) {
            ArrayList<Album> albums = new ArrayList<>();
            try {
                JSONArray arr = (JSONArray) jo.get("albums");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    String link = (String) obj.get("link");
                    String[] spl = link.split("/");
                    int album_id = Integer.valueOf(spl[spl.length - 1]);
                    JSONObject titles = (JSONObject) obj.get("titles");
                    String title_en = (String) titles.get("en");
                    String title_jp = (String) titles.get("jp");
                    String type = (String) obj.get("type");
                    String dateString = (String) obj.get("date");
                    Date date = Date.valueOf(dateString);
                    albums.add(new Album(album_id, title_en, title_jp, type, date));
                }
                return new Product(id, albums);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static Album getAlbumById(int id) {
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
                return new Album(album_id, title_en, title_jp, type, null, coverMedium);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
