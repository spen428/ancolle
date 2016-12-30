package ancolle.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A franchise is a {@link Product} that itself comprises a number of products
 *
 * @author samuel
 */
public class Franchise extends Product {

    private final List<Product> products;

    public Franchise(int id, String title_en, String title_ja,
            Collection<Product> products) {
        super(id, title_en, title_ja, ProductType.FRANCHISE, null, null);
        this.products = new ArrayList<>(products);
    }

    /**
     * Return a list of products associated with this franchise
     *
     * @return an unmodifiable list of products
     */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

}
