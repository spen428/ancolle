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

import ancolle.items.Product;
import static ancolle.ui.TilePaneView.TILE_PADDING;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * @author lykat
 */
public class ProductNode extends ItemNode<Product> {

    public static final Background NEWLY_ADDED_BACKGROUND = new Background(
	    new BackgroundFill(Color.LEMONCHIFFON, CornerRadii.EMPTY, Insets.EMPTY));
    public static final double DEFAULT_MIN_WIDTH = ProductView.MIN_TILE_WIDTH
	    + (2 * TILE_PADDING);
    public static final double DEFAULT_MAX_WIDTH = ProductView.MAX_TILE_WIDTH
	    + (2 * TILE_PADDING);
    public static final double DEFAULT_MAX_HEIGHT = DEFAULT_MAX_WIDTH / 4;
    public static final String CLASS_PRODUCT_NODE = "product-node";

    private static final ContextMenu PRODUCT_NODE_CONTEXT_MENU;

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(ProductNode.class.getName());

    static {
	PRODUCT_NODE_CONTEXT_MENU = new ContextMenu();
	MenuItem menuItemRemoveProduct = new MenuItem("Remove Product");
	menuItemRemoveProduct.setOnAction(evt -> {
	    ProductNode anchor = (ProductNode) PRODUCT_NODE_CONTEXT_MENU.getOwnerNode();
	    if (anchor != null) {
		anchor.getProductView().removeProduct(anchor.getProduct());
	    }
	});
	PRODUCT_NODE_CONTEXT_MENU.getItems().add(menuItemRemoveProduct);

	MenuItem menuItemReload = new MenuItem("Reload Product");
	menuItemReload.setOnAction(evt -> {
	    // TODO
	});
//	PRODUCT_NODE_CONTEXT_MENU.getItems().add(menuItemReload);

	PRODUCT_NODE_CONTEXT_MENU.getItems().add(new MenuItem("Cancel"));
	PRODUCT_NODE_CONTEXT_MENU.setAutoHide(true);
    }

    private final ProductView productView;

    public ProductNode(ProductView productView) {
	super();
	this.productView = productView;
	getStyleClass().add(CLASS_PRODUCT_NODE);
	setMinWidth(DEFAULT_MIN_WIDTH);
	setMaxWidth(DEFAULT_MAX_WIDTH);
	setMaxHeight(DEFAULT_MAX_HEIGHT);

	setProduct(null);

	// Mouse/key handlers
	setOnMouseClicked(evt -> {
	    if (evt.getButton() == MouseButton.PRIMARY) {
		productView.ancolle.view(getProduct());
	    } else if (evt.getButton() == MouseButton.SECONDARY) {
		showContextMenu(evt);
	    }
	});
    }

    public Product getProduct() {
	return getItem();
    }

    public void setProduct(Product product) {
	this.setItem(product);
    }

    @Override
    protected ContextMenu getContextMenu() {
	return PRODUCT_NODE_CONTEXT_MENU;
    }

    private ProductView getProductView() {
	return productView;
    }

}
