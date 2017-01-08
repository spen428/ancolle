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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;

/**
 * View tracked products as tiled {@link ProductNode} objects on a
 * {@link TilePaneView}
 *
 * @author lykat
 */
public final class ProductView extends TilePaneView {

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(ProductView.class.getName());

    private final Set<Product> products;

    public ProductView(AnColle ancolle) {
	super(ancolle);
	getStyleClass().add("product-view");
	this.products = new HashSet<>(32);

	// Button for adding new products to track
	getChildren().add(new ProductAdderNode(this));

	startWorkerThread();
    }

    /**
     * Add a product to the view. If a product already exists, a duplicate will
     * not be added. WARNING: This method should only be called by the JavaFX
     * thread
     *
     * @param product the product
     * @param highlight whether to highlight this new product node by adding the
     * ".new" class to the node
     * @return true if successfully added
     */
    public boolean addProduct(Product product, boolean highlight) {
	return addProduct(product, -1, highlight);
    }

    /**
     * Add a product to the view, inserting it at the specified position. If a
     * product already exists, a duplicate will not be added. WARNING: This
     * method should only be called by the JavaFX thread
     *
     * @param product the product
     * @param idx where to insert the product node, -1 to automatically insert
     * in sorting order
     * @param highlight whether to highlight this new product node by adding the
     * ".new" class to the node
     * @return true if successfully added
     */
    public boolean addProduct(Product product, int idx, boolean highlight) {
	if (this.products.contains(product)) {
	    return false;
	}
	this.products.add(product);
	ProductNode node = createProductNode(product.title_en, product.title_ja);
	node.setProduct(product);

	// Styling
	if (product.type == ProductType.FRANCHISE) {
	    // TODO: Move to ProductNode class
	    node.getStyleClass().add("franchise");
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

	// Highlight as new
	if (highlight) {
	    node.getStyleClass().add("new");
	    ancolle.scrollIntoView(node);
	    node.requestFocus();
	}
	return true;
    }

    /**
     * Create a new {@link ProductNode} with the default min and max widths
     *
     * @param label1text the upper label text
     * @param label2text the lower label text
     * @return the node
     */
    private ProductNode createProductNode(String label1text, String label2text) {
	ProductNode node = new ProductNode(this);
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
	addProductById(id, false);
    }

    /**
     * Add a product to the view by id, looking it up by using the
     * {@link VgmdbApi}. A placeholder will be displayed while the details are
     * loading, which will be automatically replaced with the full details on
     * completion.
     *
     * @param id the product id
     * @param highlight whether to highlight the newly added {@link ProductNode}
     */
    public void addProductById(int id, boolean highlight) {
	final Node placeholder = createProductNode("Product #" + id, "Loading...");
	// Insert before "Add"  button
	getChildren().add(getChildren().size() - 1, placeholder);
	submitBackgroundTask(() -> {
	    final Product product = VgmdbApi.getProductById(id);
	    // Ensure that UI operations occur on the correct thread.
	    Platform.runLater(() -> {
		boolean added = addProduct(product, highlight);
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
	    // Display a "loading" dialog while the search completes
	    Alert loadingDialog = createUnclosableAlert();
	    loadingDialog.initOwner(ancolle.getMainWindow());
	    loadingDialog.initModality(Modality.WINDOW_MODAL);
	    loadingDialog.setTitle("Searching...");
	    loadingDialog.setHeaderText("Search in progress...");
	    loadingDialog.show();

	    String text = result.get();
	    List<ProductPreview> searchResults = VgmdbApi.searchProducts(text);
	    // Finished loading now, so close the dialog and continue
	    loadingDialog.close();

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

    /**
     * Create an {@link Alert} type information dialog that has no buttons and
     * does not respond to close requests. NOTE: This will still respond to the
     * Alt + F4 key combination.
     *
     * @return the alert
     */
    private Alert createUnclosableAlert() {
	// TODO: Prevent Alt + F4 close event - consuming the
	// WINDOW_CLOSING_EVENT doesn't seem to work...
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	DialogPane dialogPane = alert.getDialogPane();
	/*
	 * The INFORMATION dialog contains three children by default: a
	 * GridPane, a Label, and a ButtonBar. The ButtonBar will be removed so
	 * the user cannot click anything to close the dialog, and the Label
	 * will be removed for aesthetic reasons, as the content text is not set.
	 */
	dialogPane.getChildren().removeIf(child -> (child instanceof ButtonBar
		|| child instanceof Label));
	// Set the style to UNDECORATED to remove the close button at the top.
	// alert.initStyle(StageStyle.UNDECORATED);
	// TODO: Setting the style to undecorated seems to prevent the dialog
	// from appearing altogether...
	return alert;
    }

    /**
     * An "adder" node, which when clicked will bring up a dialog to add a new
     * product to the tracker.
     *
     * Not using a ProductNode because this would interfere with the sorting of
     * real product nodes during the insertProduct() method. The ProductNode
     * comparator always places other node types after any ProductNode types, so
     * this node will appear at the end.
     */
    private static class ProductAdderNode extends ItemNode<Object> {

	private ProductAdderNode(ProductView productView) {
	    getStyleClass().add("product-adder-node");
	    getChildren().remove(label2);
	    label1.setText("+");

	    setOnMouseClicked(evt -> {
		if (evt.getButton() == MouseButton.PRIMARY) {
		    productView.doAddProductDialog();
		}
	    });
	}

	@Override
	protected ContextMenu getContextMenu() {
	    return null;
	}
    }

}
