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
import ancolle.items.ProductPreview;
import ancolle.items.ProductType;
import ancolle.main.AnColle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.stage.Modality;

/**
 * View tracked products as tiled {@link ProductNode} objects on a
 * {@link TilePaneView}
 *
 * @author lykat
 */
public class ProductView extends TilePaneView {

    private static final double MIN_TILE_WIDTH_PX = 50;
    private static final double MAX_TILE_WIDTH_PX = 250;
    private static final Logger LOG = Logger.getLogger(ProductView.class.getName());

    private final Set<Product> products;

    public ProductView(AnColle ancolle) {
	super(ancolle);
	this.products = new HashSet<>();

	// Button for adding new products to track
	getChildren().add(createProductAdderNode());

	setPadding(new Insets(PANE_PADDING_PX));
	setAlignment(Pos.BASELINE_CENTER);
    }

    /**
     * Add a product to the view. If a product already exists, a duplicate will
     * not be added. WARNING: This method should only be called by the JavaFX
     * thread
     *
     * @param product the product
     * @return true if successfully added
     */
    public boolean addProduct(Product product) {
	return addProduct(product, -1);
    }

    /**
     * Add a product to the view, inserting it at the specified position. If a
     * product already exists, a duplicate will not be added. WARNING: This
     * method should only be called by the JavaFX thread
     *
     * @param product the product
     * @param idx where to insert the product node, -1 to automatically insert
     * in sorting order
     * @return true if successfully added
     */
    public boolean addProduct(Product product, int idx) {
	if (this.products.contains(product)) {
	    return false;
	}
	this.products.add(product);
	ProductNode node = createProductNode(product.title_en, product.title_ja);
	node.setProduct(product);

	// Styling
	if (product.type == ProductType.FRANCHISE) {
	    node.label1.setStyle("-fx-font-weight: bold;");
	}

	// Get product logo
	submitBackgroundTask(() -> {
	    LOG.log(Level.FINE, "Fetching product cover for product #", product.id);
	    final Image image = product.getPicture();
	    LOG.log(Level.FINE, "Fetched product cover for product #", product.id);
	    Platform.runLater(() -> {
		node.imageView.setImage(image);
	    });
	});

	// Insert into view
	if (idx != -1) {
	    // Insert with provided idx value
	    getChildren().add(idx, node);
	} else {
	    // Insert sorted
	    insertProduct(node);
	}

	return true;
    }

    /**
     * Create an "adder" node, which when clicked will bring up a dialog to add
     * a new product to the tracker.
     *
     * @return the adder node
     */
    private Node createProductAdderNode() {
	ItemNode<Object> node = new ItemNode<Object>() {
	    @Override
	    protected ContextMenu getContextMenu() {
		return null;
	    }
	};

	node.getChildren().remove(node.label2);
	node.label1.setText("+");
	node.label1.setAlignment(Pos.CENTER);
	node.label1.setFont(new Font("Arial", 40));
	node.setAlignment(Pos.CENTER);

	node.setOnMouseClicked(evt -> {
	    if (evt.getButton() == MouseButton.PRIMARY) {
		doAddProductDialog();
	    }
	});

	return node;
    }

    /**
     * Create a new {@link ProductNode} with the default min and max widths
     *
     * @param label1text the upper label text
     * @param label2text the lower label text
     * @return the node
     */
    private ProductNode createProductNode(String label1text, String label2text) {
	double minWidth = MIN_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
	double maxWidth = MAX_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
	ProductNode node = new ProductNode(minWidth, maxWidth, this);
	node.label1.setText(label1text);
	node.label2.setText(label2text);
	return node;
    }

    /**
     * Add a product to the view by id, looking it up by using the
     * {@link VgmdbApi}. A placeholder will be displayed while the details are
     * loading, which will be automatically replaced with the full details on
     * completion.
     *
     * @param id the product id
     */
    public void addProductById(int id) {
	final Node placeholder = createProductNode("Product #" + id, "Loading...");
	// Insert before "Add"  button
	getChildren().add(getChildren().size() - 1, placeholder);
	submitBackgroundTask(() -> {
	    final Product product = VgmdbApi.getProductById(id);
	    // Ensure that UI operations occur on the correct thread.
	    Platform.runLater(() -> {
		boolean added = addProduct(product);
		if (!added) {
		    LOG.log(Level.INFO, "Product with id #{0} was not "
			    + "added. Possible duplicate?", product.id);
		}
		getChildren().remove(placeholder);
	    });
	});
    }

    public void doAddProductDialog() {
	TextInputDialog inputDialog = new TextInputDialog();
	inputDialog.initOwner(ancolle.getMainWindow());
	inputDialog.initModality(Modality.WINDOW_MODAL);
	inputDialog.setTitle("Track a new product");
	inputDialog.setContentText("Enter a product name:");
	centreDialog(inputDialog);

	ProductPreview chosenProduct = null;
	Optional<String> result = inputDialog.showAndWait();
	if (result.isPresent()) {
	    String text = result.get();
	    List<ProductPreview> searchResults = VgmdbApi.searchProducts(text);
	    if (searchResults.isEmpty()) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initOwner(ancolle.getMainWindow());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setTitle("No search results");
		alert.setContentText("No products could be found that "
			+ "matched the search terms: \"" + text + "\"");
		centreDialog(alert);

		alert.showAndWait();
	    } else {
		// Build list of choices
		int defaultChoice = 0;
		List<String> choices = new ArrayList<>(searchResults.size());
		for (int idx = 0; idx < searchResults.size(); idx++) {
		    ProductPreview p = searchResults.get(idx);
		    String str = String.format("#%d (%s) %s | %s", p.id,
			    p.type.typeString, p.title_en, p.title_ja);
		    choices.add(str);

		    // Use first Franchise as default choice
		    if (defaultChoice == 0 && p.type == ProductType.FRANCHISE) {
			defaultChoice = idx;
		    }
		}

		// Show choice dialog
		ChoiceDialog<String> resultsChooser = new ChoiceDialog<>();
		resultsChooser.getItems().addAll(choices);
		resultsChooser.setSelectedItem(choices.get(defaultChoice));
		resultsChooser.initOwner(ancolle.getMainWindow());
		resultsChooser.initModality(Modality.WINDOW_MODAL);
		resultsChooser.setTitle("Select a product");
		resultsChooser.setContentText("Select the product from the list"
			+ " that you wish to add to the product tracker.");
		centreDialog(resultsChooser);

		Optional<String> choice = resultsChooser.showAndWait();
		if (choice.isPresent()) {
		    String c = choice.get();
		    chosenProduct = searchResults.get(choices.indexOf(c));
		}
	    }
	}

	if (chosenProduct != null) {
	    final ProductPreview p = chosenProduct;
	    // Add to tracked list
	    Platform.runLater(() -> {
		ancolle.addTrackedProduct(p.id);
	    });
	}
    }

    /**
     * Centre a {@link Dialog} relative to the location of the parent
     * {@link Scene}
     *
     * @param dialog the dialog to centre
     */
    private void centreDialog(Dialog<?> dialog) {
	Scene scene = getScene();
	double x = scene.getX() + scene.getWidth() / 2;
	double y = scene.getY() + scene.getHeight() / 2;
	x -= dialog.getWidth() / 2;
	y -= dialog.getHeight() / 2;
	dialog.setX(x);
	dialog.setY(y);
    }

    /**
     * Remove a product from the tracker
     *
     * @param product the {@link Product} to remove
     * @return whether the product was removed
     */
    boolean removeProduct(Product product) {
	if (!this.products.contains(product)) {
	    return false;
	}

	// Be careful to use Integer object here, otherwise the remove by idx
	// method will be called
	ancolle.getSettings().trackedProductIds.remove((Integer) product.id);
	this.products.remove(product);
	// Find and remove the product node
	boolean removed = getChildren()
		.removeIf(child -> (child instanceof ProductNode)
		&& ((ProductNode) child).getProduct() == product);

	return removed;
    }

    public void updateHiddenItems() {
	// TODO
    }

    /**
     * Insert the given {@link ProductNode} into the view such that the sorting
     * order of the child nodes is preserved.
     *
     * @param node the node to insert
     */
    private void insertProduct(ProductNode node) {
	int insertIdx;
	for (insertIdx = 0; insertIdx < getChildren().size(); insertIdx++) {
	    Node child = getChildren().get(insertIdx);
	    if (ItemNodeComparators.PRODUCT_NODE_COMPARATOR.compare(node, child) <= 0) {
		break;
	    }
	}
	getChildren().add(insertIdx, node);
    }

}
