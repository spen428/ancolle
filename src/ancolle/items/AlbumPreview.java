package ancolle.items;

import java.util.Date;
import java.util.logging.Logger;

public class AlbumPreview extends Item {

	public final String type;
	public final String[] roles;
	public final String catalog;
	public final boolean reprint;
	public final Date date;

	public AlbumPreview(int id, String title_en, String title_ja, String type,
	                    String[] roles, String catalog, boolean reprint, Date date) {
		super(id, title_en, title_ja);
		this.type = type;
		this.roles = roles;
		this.catalog = catalog;
		this.reprint = reprint;
		this.date = date;
	}

	@Override
	public String getSubPath() {
		return "album";
	}
}
