package ancolle;


/**
 * Album information as it appears when retrieving Product information
 *
 * @author samuel
 */
public class AlbumPreview {

    public final int id;
    public final String title_en;
    public final String title_jp;
    public final String type;
    public final java.util.Date date;

    public AlbumPreview(int id, String title_en, String title_jp, String type,
            java.util.Date date) {
        this.id = id;
        this.title_en = title_en;
        this.title_jp = title_jp;
        this.type = type;
        this.date = date;
    }

}
