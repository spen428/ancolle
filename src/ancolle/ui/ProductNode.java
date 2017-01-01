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
import static ancolle.ui.TilePaneView.TILE_PADDING_PX;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author samuel
 */
public class ProductNode extends VBox {

    public static final Background COLOR_HOVERING = new Background(new BackgroundFill(Color.AQUAMARINE, null, null));
    private static final ContextMenu PRODUCT_NODE_CONTEXT_MENU;
    private static final Logger LOG = Logger.getLogger(ProductNode.class.getName());

    static {
	PRODUCT_NODE_CONTEXT_MENU = new ContextMenu();
	MenuItem menuItemRemoveProduct = new MenuItem("Remove Product");
	menuItemRemoveProduct.setOnAction(evt -> {
	    ProductNode anchor = (ProductNode) PRODUCT_NODE_CONTEXT_MENU.getOwnerNode();
	    if (anchor != null) {
		anchor.removeFromProductView();
	    }
	});
	PRODUCT_NODE_CONTEXT_MENU.getItems().add(menuItemRemoveProduct);
	PRODUCT_NODE_CONTEXT_MENU.getItems().add(new MenuItem("Cancel"));
	PRODUCT_NODE_CONTEXT_MENU.setAutoHide(true);
    }

    public final ImageView imageView;
    public final Label label1;
    public final Label label2;

    private final ProductView productView;

    /**
     * The {@link Product} represented by this node.
     */
    private Product product;

    public ProductNode(double minWidth, double maxWidth, ProductView productView) {
	super();
	this.productView = productView;
	this.product = null;

	setPadding(new Insets(TILE_PADDING_PX));
	setMinWidth(minWidth);
	setMaxWidth(maxWidth);
	setAlignment(Pos.BOTTOM_CENTER);

	imageView = new ImageView();
	imageView.setSmooth(true);
	imageView.setPreserveRatio(true);
	imageView.setFitWidth(maxWidth);
	imageView.setFitHeight(maxWidth / 4);
	getChildren().add(imageView);

	label1 = new Label();
	label1.maxWidthProperty().bind(widthProperty());
	label1.setAlignment(Pos.BOTTOM_CENTER);
	getChildren().add(label1);

	label2 = new Label();
	label2.maxWidthProperty().bind(widthProperty());
	label2.setAlignment(Pos.BOTTOM_CENTER);
	getChildren().add(label2);

	// Mouse/key handlers
	setOnMouseEntered(evt -> {
	    setBackground(COLOR_HOVERING);
	    PRODUCT_NODE_CONTEXT_MENU.hide();
	});
	setOnMouseExited(evt -> {
	    setBackground(Background.EMPTY);
	});
	setOnMouseClicked(evt -> {
	    if (evt.getButton() == MouseButton.PRIMARY) {
		productView.ancolle.view(product);
	    } else if (evt.getButton() == MouseButton.SECONDARY) {
		double dx = evt.getX();
		double dy = evt.getY();
		PRODUCT_NODE_CONTEXT_MENU.show(this, Side.TOP, dx, dy);
	    }
	});
    }

    public Product getProduct() {
	return product;
    }

    public void setProduct(Product product) {
	this.product = product;
    }

    /**
     * Remove self from product view
     */
    private void removeFromProductView() {
	productView.removeProduct(product);
    }

}
