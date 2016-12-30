package ancolle.ui;

import ancolle.items.AlbumPreview;
import ancolle.items.Album;
import ancolle.main.AnColle;
import ancolle.items.Product;
import ancolle.io.VgmdbApi;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

/**
 * View albums belonging to a Product
 *
 * @author samuel
 */
public class AlbumView extends TilePaneView {

    private static final double MAX_TILE_WIDTH_PX = 100;

    private Product product;

    private final ConcurrentHashMap<AlbumPreview, Album> fullAlbumMap;

    public AlbumView(AnColle ancolle) {
        this(ancolle, null);
    }

    public AlbumView(AnColle ancolle, Product product) {
        super(ancolle);
        this.fullAlbumMap = new ConcurrentHashMap<>();
        setPadding(new Insets(PANE_PADDING_PX));
        setAlignment(Pos.BASELINE_CENTER);
        setProduct(product);
    }

    private void updateProduct() {
        getChildren().clear();
        if (product == null) {
            return;
        }

        fullAlbumMap.clear(); // Clear album cache
        List<AlbumPreview> albums = product.getAlbums();
        albums.forEach((album) -> {
            getChildren().add(createAlbumView(album));
        });
    }

    public void setProduct(Product product) {
        if (this.product == product) {
            return;
        }
        this.product = product;
        updateProduct();
    }

    public Product getProduct() {
        return this.product;
    }

    private Node createAlbumView(AlbumPreview album) {
        final VBox node = new VBox();
        node.setPadding(new Insets(TILE_PADDING_PX));
        node.setMaxWidth(MAX_TILE_WIDTH_PX + (2 * TILE_PADDING_PX));
        node.setAlignment(Pos.BOTTOM_CENTER);

        final ImageView albumCover = new ImageView();
        albumCover.setSmooth(true);
        albumCover.setPreserveRatio(true);
        albumCover.setFitWidth(MAX_TILE_WIDTH_PX);
        albumCover.setFitHeight(MAX_TILE_WIDTH_PX);
        node.getChildren().add(albumCover);

        Label label1 = new Label(album.title_en);
        label1.maxWidthProperty().bind(node.widthProperty());
        label1.setAlignment(Pos.BOTTOM_CENTER);
        node.getChildren().add(label1);

        String dateString = "";
        if (album.date != null) {
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
        }
        Label label2 = new Label(dateString);
        label2.maxWidthProperty().bind(node.widthProperty());
        label2.setAlignment(Pos.BOTTOM_CENTER);
        node.getChildren().add(label2);

        // Context menu
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("Yo"));

        // Mouse/key handlers
        node.setOnMouseEntered(evt -> {
            node.setStyle("-fx-background-color: #9ec1ff;");
        });
        node.setOnMouseExited(evt -> {
            node.setStyle("-fx-background-color: none;");
            contextMenu.hide();
        });
        node.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() > 1) {
                // If full album details are loaded, open in a new tab, else ignore
                Album fullAlbum = fullAlbumMap.get(album);
                if (fullAlbum != null) {
                    AlbumDetailsView adv = new AlbumDetailsView(fullAlbum);
                    Tab tab = ancolle.newTab(fullAlbum.title_ja, adv);
                    ancolle.setSelectedTab(tab);
                }
            }
        });

        // Fetch album cover in the background
        submitBackgroundTask(() -> {
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetching album cover for album #", album.id);
            Album fullAlbum = fullAlbumMap.get(album);
            if (fullAlbum == null) {
                fullAlbum = VgmdbApi.getAlbumById(album.id);
                if (fullAlbum != null) {
                    fullAlbumMap.put(album, fullAlbum);
                }
            }
            if (fullAlbum != null) {
                Image image = fullAlbum.getPicture();
                Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                        "Fetched album cover for album #", album.id);
                Platform.runLater(() -> {
                    albumCover.setImage(image);
                });
            } else {
                Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                        "Failed to fetch full album details for album #", album.id);
            }
        });
        return node;
    }

}
