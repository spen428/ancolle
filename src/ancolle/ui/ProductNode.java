/*  AnColle, an anime and video game music collection tracker
 *  Copyright (C) 2016-17  lykat
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ancolle.ui;

import ancolle.io.VgmdbApi;
import ancolle.items.Product;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

import java.util.logging.Logger;

/**
 * @author lykat
 */
public class ProductNode extends ItemNode<Product> {

	private static final ContextMenu PRODUCT_NODE_CONTEXT_MENU;

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(ProductNode.class.getName());

	static {
		PRODUCT_NODE_CONTEXT_MENU = new ContextMenu();

		MenuItem menuItemReload = new MenuItem("Force reload");
		menuItemReload.setOnAction(evt -> {
			ProductNode anchor = (ProductNode) PRODUCT_NODE_CONTEXT_MENU.getOwnerNode();
			if (anchor != null) {
				anchor.reloadProduct();
			}
		});
		PRODUCT_NODE_CONTEXT_MENU.getItems().add(menuItemReload);

		MenuItem menuItemRemoveProduct = new MenuItem("Remove product");
		menuItemRemoveProduct.setOnAction(evt -> {
			ProductNode anchor = (ProductNode) PRODUCT_NODE_CONTEXT_MENU.getOwnerNode();
			if (anchor != null) {
				anchor.getProductView().removeProduct(anchor.getProduct());
			}
		});
		PRODUCT_NODE_CONTEXT_MENU.getItems().add(menuItemRemoveProduct);

		PRODUCT_NODE_CONTEXT_MENU.getItems().add(new MenuItem("Cancel"));
		PRODUCT_NODE_CONTEXT_MENU.setAutoHide(true);
	}

	private final ProductView productView;

	public ProductNode(ProductView productView) {
		super();
		this.productView = productView;
		getStyleClass().add("product-node");

		setProduct(null);

		// Mouse/key handlers
		setOnMouseClicked(evt -> {
			if (evt.getButton() == MouseButton.PRIMARY) {
				if (getProduct() != null) {
					getStyleClass().remove("new");
					productView.ancolle.view(getProduct());
				} else {
					// TODO: Move to top of priority queue
				}
			} else if (evt.getButton() == MouseButton.SECONDARY) {
				showContextMenu(evt);
			}
		});
	}

	public Product getProduct() {
		return getItem();
	}

	public final void setProduct(Product product) {
		this.setItem(product);
	}

	@Override
	protected ContextMenu getContextMenu() {
		return PRODUCT_NODE_CONTEXT_MENU;
	}

	private ProductView getProductView() {
		return productView;
	}

	private void reloadProduct() {
		Platform.runLater(() -> {
			Product product = getProduct();
			if (productView.removeProduct(product)) {
				VgmdbApi.removeFromCache(product);
				productView.addProductById(product.id, true);
			}
		});
	}

}
