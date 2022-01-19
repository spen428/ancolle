package ancolle.ui;

import ancolle.items.Product;
import ancolle.items.ProductPreview;
import ancolle.items.ProductType;
import ancolle.main.Main;

import java.util.List;

public final class ProductView extends TopLevelItemView<Product, ProductNode, ProductPreview> {

	public ProductView(ApplicationRoot applicationRoot) {
		super(applicationRoot);
	}

	@Override
	public void refreshItems() {

	}

	@Override
	protected String getItemTypeName() {
		return "product";
	}

	@Override
	protected ProductNode instantiateTNode() {
		return new ProductNode(this);
	}

	@Override
	protected List<Integer> getTrackedTopLevelItemSettings() {
		return Main.settings.trackedProductIds;
	}

	@Override
	protected Class<ProductNode> getTNodeClass() {
		return ProductNode.class;
	}

	@Override
	protected int getDefaultChoice(List<ProductPreview> searchResults) {
		for (int i = 0; i < searchResults.size(); i++) {
			ProductPreview p = searchResults.get(i);
			if (p.type == ProductType.FRANCHISE) {
				return i;
			}
		}
		return 0;
	}
}
