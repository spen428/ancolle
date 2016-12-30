package ancolle.ui;

import ancolle.AlbumPreview;
import ancolle.Album;
import ancolle.AnColle;
import ancolle.Product;
import ancolle.VGMdbAPI;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * View albums belonging to a Product
 *
 * @author samuel
 */
public class AlbumView extends TilePane {

    private static final int PANE_PADDING_PX = 25;
    private static final double COVER_WIDTH_PX = 100;
    private static final double COVER_PADDING = 10;

    private final AnColle ancolle;
    private Product product;
    private final ConcurrentHashMap<AlbumPreview, Album> fullAlbumMap;

    private final Thread workerThread;
    private final BlockingQueue<Runnable> jobQueue;

    public void cancelQueuedTasks() {
        jobQueue.clear();
    }

    public AlbumView(AnColle ancolle) {
        this(ancolle, null);
    }

    public AlbumView(AnColle ancolle, Product product) {

        this.jobQueue = new LinkedBlockingQueue<>();
        this.workerThread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = jobQueue.take();
                    task.run();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        });
        this.workerThread.setDaemon(true);
        this.workerThread.start();

        this.ancolle = ancolle;
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

        fullAlbumMap.clear();
        List<AlbumPreview> albums = product.albums();
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
        node.setPadding(new Insets(COVER_PADDING));
        node.setMaxWidth(COVER_WIDTH_PX + COVER_PADDING + COVER_PADDING);
        node.setAlignment(Pos.BOTTOM_CENTER);

        final ImageView albumCover = new ImageView();
        albumCover.setSmooth(true);
        albumCover.setPreserveRatio(true);
        albumCover.setFitWidth(COVER_WIDTH_PX);
        albumCover.setFitHeight(COVER_WIDTH_PX);
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

        // Fetch album cover in the background
        jobQueue.add(() -> {
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetching album cover for album #", album.id);
            Album fullAlbum = fullAlbumMap.get(album);
            if (fullAlbum == null) {
                fullAlbum = VGMdbAPI.getAlbumById(album.id);
                if (fullAlbum != null) {
                    fullAlbumMap.put(album, fullAlbum);
                }
            }
            if (fullAlbum != null) {
                Image image = fullAlbum.getImage();
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
