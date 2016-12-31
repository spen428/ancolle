package ancolle.items;

/**
 * @author samuel
 */
public class ProductPreview extends Item {

    public final ProductType type;

    public ProductPreview(int id, String title_en, String title_ja,
            ProductType type) {
        super(id, title_en, title_ja);
        this.type = type;
    }

}
