package ancolle.items;

import java.util.Date;

/**
 * Album information as it appears when retrieving Product information
 *
 * @author samuel
 */
public class AlbumPreview extends Item {

    public final String type;
    public final Date date;

    public AlbumPreview(int id, String title_en, String title_ja, String type,
            Date date) {
        super(id, title_en, title_ja);
        this.type = type;
        this.date = date;
    }

}
