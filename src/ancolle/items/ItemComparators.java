package ancolle.items;

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Comparators for subclasses of {@link Item}
 *
 * @author lykat
 */
public class ItemComparators {

	/**
	 * Compares by: title_en, then title_jp, then type.typeString
	 */
	public static final Comparator<ProductPreview> PRODUCT_PREVIEW_COMPARATOR;

	/**
	 * Compares by: date, then title_en, then title_jp, then type
	 */
	public static final Comparator<AlbumPreview> ALBUM_PREVIEW_COMPARATOR;

	/**
	 * The logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(ItemComparators.class.getName());

	static {
		PRODUCT_PREVIEW_COMPARATOR = (ProductPreview o1, ProductPreview o2) -> {
			int c = o1.title_en.compareTo(o2.title_en);
			if (c != 0) {
				return c;
			}
			c = o1.title_ja.compareTo(o2.title_ja);
			if (c != 0) {
				return c;
			}
			return o1.type.typeString.compareTo(o2.type.typeString);
		};

		ALBUM_PREVIEW_COMPARATOR = (AlbumPreview o1, AlbumPreview o2) -> {
			int c = o1.date.compareTo(o2.date);
			if (c != 0) {
				return c;
			}
			c = o1.title_en.compareTo(o2.title_en);
			if (c != 0) {
				return c;
			}
			c = o1.title_ja.compareTo(o2.title_ja);
			if (c != 0) {
				return c;
			}
			return o1.type.compareTo(o2.type);
		};
	}

	private ItemComparators() {
	}
}
