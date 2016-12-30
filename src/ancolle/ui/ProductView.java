package ancolle.ui;

import ancolle.main.AnColle;
import ancolle.items.Product;
import ancolle.items.ProductPreview;
import ancolle.io.VgmdbApi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 * View products
 *
 * @author samuel
 */
public class ProductView extends TilePaneView {

    private static final double MIN_TILE_WIDTH_PX = 50;
    private static final double MAX_TILE_WIDTH_PX = 250;

    private final ConcurrentLinkedQueue<Product> products;

    public ProductView(AnColle ancolle) {
        this(ancolle, null);
    }

    public ProductView(AnColle ancolle, Collection<Product> products) {
        super(ancolle);
        this.products = new ConcurrentLinkedQueue<>();

        // Button for adding new products to track
        getChildren().add(createProductAdderNode());

        if (products != null) {
            products.forEach((product) -> {
                addProduct(product);
            });
        }

        setPadding(new Insets(PANE_PADDING_PX));
        setAlignment(Pos.BASELINE_CENTER);
    }

    public void addProduct(Product product) {
        addProduct(product, -1);
    }

    public void addProduct(Product product, int idx) {
        this.products.add(product);
        ProductNode node = createProductNode(product.title_en, product.title_ja);
        Label label1 = (Label) node.getChildren().get(1);

        // Styling
        if (product.type.equals("Franchise")) {
            label1.setStyle("-fx-font-weight: bold;");
        }
        node.setOnMouseClicked(evt -> {
            ancolle.view(product);
        });

        // Get product logo
        submitBackgroundTask(() -> {
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetching product cover for product #", product.id);
            final Image image = product.getPicture();
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetched product cover for product #", product.id);
            Platform.runLater(() -> {
                node.imageView.setImage(image);
            });
        });
        if (idx != -1) {
            getChildren().add(idx, node);
        } else {
            // Insert before "Add" button
            idx = getChildren().size() - 1;
            getChildren().add(idx, node);
        }
    }

    private ProductNode createProductAdderNode() {
        ProductNode node = createProductNode("+", "");
        node.getChildren().remove(node.label2);
        node.label1.setAlignment(Pos.CENTER);
        node.label1.setFont(new Font("Arial", 40));

        node.setOnMouseClicked(evt -> {
            doAddProductDialog();
        });

        return node;
    }

    private ProductNode createProductNode(String label1text, String label2text) {
        double minWidth = MIN_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
        double maxWidth = MAX_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
        ProductNode node = new ProductNode(minWidth, maxWidth);
        node.label1.setText(label1text);
        node.label2.setText(label2text);
        return node;
    }

    public void addProductById(int id) {
        final Node placeholder = createProductNode("Product #" + id, "Loading...");
        // Insert before "Add"  button
        getChildren().add(getChildren().size() - 1, placeholder);
        submitBackgroundTask(() -> {
            final Product product = VgmdbApi.getProductById(id);
            // Ensure that UI operations occur on the correct thread.
            Platform.runLater(() -> {
                int idx = getChildren().indexOf(placeholder);
                addProduct(product, idx);
                getChildren().remove(placeholder);
            });
        });
    }

    public void doAddProductDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Track a new product");
        dialog.setContentText("Enter a product name:");
        ProductPreview chosenProduct = null;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String text = result.get();
            List<ProductPreview> searchResults = VgmdbApi.searchProducts(text);
            if (searchResults.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No search results");
                alert.setContentText("No products could be found that "
                        + "matched the search terms: \"" + text + "\"");
                alert.showAndWait();
            } else {
                // Build list of choices
                int defaultChoice = 0;
                List<String> choices = new ArrayList<>();
                for (int idx = 0; idx < searchResults.size(); idx++) {
                    ProductPreview p = searchResults.get(idx);
                    String str = String.format("#%d (%s) %s | %s", p.id,
                            p.type, p.title_en, p.title_ja);
                    choices.add(str);

                    // Use first "Franchise" as default choice
                    if (defaultChoice == 0 && p.type.equals("Franchise")) {
                        defaultChoice = idx;
                    }
                }

                // Show choice dialog
                ChoiceDialog<String> resultsChooser = new ChoiceDialog(choices.get(defaultChoice), choices);
                Optional<String> choice = resultsChooser.showAndWait();
                chosenProduct = searchResults.get(defaultChoice);
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

}
