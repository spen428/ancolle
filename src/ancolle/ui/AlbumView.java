package ancolle.ui;

import ancolle.Album;
import ancolle.AnColle;
import ancolle.Product;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

    private static final int PANE_PADDING = 25;
    private final double COVER_WIDTH_PX = 100;
    private double COVER_PADDING = 10;

    private final AtomicInteger taskCount = new AtomicInteger(0);
    private final BlockingQueue<Runnable> jobQueue = new SynchronousQueue<>();
    private final ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            jobQueue,
            runnable
            -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    }
    );

    private final AnColle ancolle;
    private Product product;

    public AlbumView(AnColle ancolle) {
        this(ancolle, null);
    }

    public AlbumView(AnColle ancolle, Product product) {
        this.ancolle = ancolle;
        setPadding(new Insets(PANE_PADDING));
        setAlignment(Pos.BASELINE_CENTER);
        setProduct(product);
    }

    private void updateProduct() {
        getChildren().clear();
        if (product == null) {
            return;
        }

        List<Album> albums = product.albums();
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

    private Node createAlbumView(Album album) {
        final VBox albumNode = new VBox();
        albumNode.setPadding(new Insets(COVER_PADDING));
        albumNode.setMaxWidth(COVER_WIDTH_PX + COVER_PADDING + COVER_PADDING);
        albumNode.setAlignment(Pos.BOTTOM_CENTER);

        final ImageView albumCover = new ImageView();
        albumCover.setSmooth(true);
        albumCover.setPreserveRatio(true);
        albumCover.setFitWidth(COVER_WIDTH_PX);
        albumCover.setFitHeight(COVER_WIDTH_PX);
        albumNode.getChildren().add(albumCover);

        Label label1 = new Label("" + album.title_en);
        label1.maxWidthProperty().bind(albumNode.widthProperty());
        label1.setAlignment(Pos.BOTTOM_CENTER);
        albumNode.getChildren().add(label1);

        Label label2 = new Label("" + album.date.toString());
        label2.maxWidthProperty().bind(albumNode.widthProperty());
        label2.setAlignment(Pos.BOTTOM_CENTER);
        albumNode.getChildren().add(label2);

        // Mouse/key handlers
        albumNode.setOnMouseEntered(evt -> {
            albumNode.setStyle("-fx-background-color: #9ec1ff;");
        });
        albumNode.setOnMouseExited(evt -> {
            albumNode.setStyle("-fx-background-color: none;");
        });

        // Fetch album cover in the background
        executor.submit(() -> {
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetching album cover for album #", album.id);
            Image image = album.getImage();
            albumCover.setImage(image);
            Logger.getLogger(AlbumView.class.getName()).log(Level.FINE,
                    "Fetched album cover for album #", album.id);
        });
        return albumNode;
    }

    public void cancelQueuedTasks() {
        jobQueue.clear();
    }
    
}
