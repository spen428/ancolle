package ancolle.items;

public class ProductPreview extends Item {

	public final ProductType type;

	public ProductPreview(int id, String title_en, String title_ja,
	                      ProductType type) {
		super(id, title_en, title_ja);
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("#%d (%s) %s | %s", id, type.typeString, title_en, title_ja);
	}

	@Override
	public String getSubPath() {
		return "product";
	}
}
