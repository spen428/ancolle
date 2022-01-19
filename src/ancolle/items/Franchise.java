package ancolle.items;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A franchise is a {@link Product} that itself comprises a number of products
 *
 * @author lykat
 */
public class Franchise extends Product {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(Franchise.class.getName());

	private final List<Product> products;

	/**
	 * Instantiate a new instance of {@link Franchise}, a sub-class of
	 * {@link Product}. The inherited fields {@link Product#pictureUrlSmall} and
	 * {@link Product#albums} will be set to {@code null} and the field
	 * {@link Product#type} will be set to {@link ProductType#FRANCHISE}.
	 *
	 * @param id       the product id
	 * @param title_en the English title
	 * @param title_ja the Japanese title
	 * @param products a collection of {@link Product}s that come under this
	 *                 {@link Franchise}
	 */
	public Franchise(int id, String title_en, String title_ja,
	                 Collection<Product> products) {
		super(id, title_en, title_ja, ProductType.FRANCHISE, null, null);
		if (products != null) {
			this.products = new ArrayList<>(products);
		} else {
			this.products = new ArrayList<>(0);
		}
	}

	/**
	 * Return a list of products associated with this franchise
	 *
	 * @return an unmodifiable list of products
	 */
	public List<Product> getProducts() {
		return Collections.unmodifiableList(products);
	}

	@Override
	public Image getPicture() {
		// TODO: Collage of product pictures?
		if (!products.isEmpty()) {
			// Get the picture associated with child product with the most albums.
			int mostAlbums = 0;
			Image bestPicture = null;
			for (Product child : products) {
				Image childPicture = child.getPicture();
				if (childPicture != null) {
					int numAlbums = child.getAlbums().size();
					if (numAlbums > mostAlbums) {
						mostAlbums = numAlbums;
						bestPicture = childPicture;
					}
				}
			}
			return bestPicture;
		}
		return null;
	}

	@Override
	public List<AlbumPreview> getAlbums() {
		ArrayList<AlbumPreview> allAlbums = new ArrayList<>(100);
		products.forEach((child) -> {
			allAlbums.addAll(child.getAlbums());
		});
		return Collections.unmodifiableList(allAlbums);
	}

}
