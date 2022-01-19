package ancolle.ui;

import ancolle.items.AlbumPreview;
import ancolle.items.Product;

import java.util.List;

public class ProductAlbumView extends AlbumView<Product> {
	public ProductAlbumView(ApplicationRoot applicationRoot) {
		super(applicationRoot);
	}

	@Override
	protected List<AlbumPreview> getParentAlbums() {
		return parent.getAlbums();
	}
}
