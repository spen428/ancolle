package ancolle.ui;

import ancolle.items.Product;
import ancolle.items.ProductPreview;
import ancolle.items.ProductType;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;

public class ProductNode extends TopLevelItemNode<Product, ProductNode, ProductPreview> {

	private static final ContextMenu CONTEXT_MENU = new TopLevelItemNodeContextMenu();

	public ProductNode(ProductView productView) {
		super(productView);
		getStyleClass().add("product-node");
	}

	@Override
	protected ContextMenu getContextMenu() {
		return CONTEXT_MENU;
	}

	@Override
	public void applyAdditionalStyles(Product item) {
		if (item.type == ProductType.FRANCHISE) {
			getStyleClass().add("franchise");
		}
	}

}
