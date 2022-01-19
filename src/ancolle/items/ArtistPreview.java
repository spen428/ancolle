package ancolle.items;

public class ArtistPreview extends Item {

	public final String[] aliases;

	public ArtistPreview(int id, String title_en, String title_ja,
	                     String... aliases) {
		super(id, title_en, title_ja);
		this.aliases = aliases;
	}

	@Override
	public String getSubPath() {
		return "artist";
	}

	@Override
	public String toString() {
		return String.format("#%d (%s) %s | %s", id, String.join(",", aliases), title_en, title_ja);
	}
}
