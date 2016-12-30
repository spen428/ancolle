package ancolle.items;

/**
 * @author samuel
 */
public class ProductPreview {

    public final int id;
    public final String title_en;
    public final String title_ja;
    public final ProductType type;

    public ProductPreview(int id, String title_en, String title_ja,
            ProductType type) {
        this.id = id;
        this.title_en = title_en;
        this.title_ja = title_ja;
        this.type = type;
    }

}
