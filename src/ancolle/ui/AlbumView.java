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
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

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
            getChildren().add(createAlbumNode(album));
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

    private AlbumNode createAlbumNode(AlbumPreview album) {
        double maxWidth = MAX_TILE_WIDTH_PX + (2 * TILE_PADDING_PX);
        AlbumNode node = new AlbumNode(maxWidth);
        node.label1.setText(album.title_en);

        // Get date and set date label
        String dateString = "";
        if (album.date != null) {
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(album.date);
        }
        node.label2.setText(dateString);

        // Mouse listener
        node.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() > 1) {
                // If full album details are loaded, open details in a new tab
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
                final Image image = fullAlbum.getPicture();
                Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                        "Fetched album cover for album #", album.id);
                Platform.runLater(() -> {
                    node.albumCover.setImage(image);
                });
            } else {
                Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                        "Failed to fetch full album details for album #",
                        album.id);
            }
        });
        return node;
    }

}
