package ancolle;

import java.util.Date;

/**
 * Album information as it appears when retrieving Product information
 *
 * @author samuel
 */
public class AlbumPreview {

    public final int id;
    public final String title_en;
    public final String title_ja;
    public final String type;
    public final Date date;

    public AlbumPreview(int id, String title_en, String title_jp, String type,
            Date date) {
        this.id = id;
        this.title_en = title_en;
        this.title_ja = title_jp;
        this.type = type;
        this.date = date;
    }

}
