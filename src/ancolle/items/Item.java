package ancolle.items;

import java.util.Objects;

/**
 * @author lykat
 */
public abstract class Item {

	public final int id;
	public final String title_en;
	public final String title_ja;

	/**
	 * Instantiate a new {@link Item}
	 *
	 * @param id       the item id as it appears in the database
	 * @param title_en the English title
	 * @param title_ja the Japanese title
	 */
	public Item(int id, String title_en, String title_ja) {
		this.id = id;
		this.title_en = title_en;
		this.title_ja = title_ja;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + this.id;
		hash = 37 * hash + Objects.hashCode(this.title_en);
		hash = 37 * hash + Objects.hashCode(this.title_ja);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Item other = (Item) obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.title_en, other.title_en)) {
			return false;
		}
		return Objects.equals(this.title_ja, other.title_ja);
	}

	public abstract String getSubPath();
}
