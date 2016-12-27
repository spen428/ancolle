package ancolle.ui;

import ancolle.Album;
import ancolle.Product;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author samuel
 */
public class ProductView extends GridPane {

    private static final int PADDING = 50;
    private static final int NUM_COLS = 10;
    private static final double ALBUM_PADDING = 10;
    private Product product;

    private final NumberBinding gridSize = Bindings.min(heightProperty(), widthProperty());

    private AtomicInteger taskCount = new AtomicInteger(0);
    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true); // allows app to exit if tasks are running
        return thread;
    });

    public ProductView() {
        this(null);
    }

    public ProductView(Product product) {
        for (int i = 0; i < NUM_COLS; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(95.0 / NUM_COLS);
            getColumnConstraints().add(cc);
        }
        setPadding(new Insets(PADDING));
        setAlignment(Pos.BASELINE_CENTER);
        setProduct(product);
    }

    public void setProduct(Product product) {
        getChildren().clear();
        this.product = product;
        if (product == null) {
            return;
        }

        List<Album> albums = product.albums();
        int x = 0;
        int y = 0;
        for (Album album : albums) {
            ColumnConstraints cc = getColumnConstraints().get(x);
            add(createAlbumView(album, cc), x, y);
            if (++x == NUM_COLS) {
                x = 0;
                ++y;
            }
        }
    }

    private Node createAlbumView(Album album, ColumnConstraints parentColumn) {
        final VBox albumNode = new VBox();
        albumNode.setPadding(new Insets(ALBUM_PADDING));
        final Rectangle albumCover = new Rectangle();
        albumCover.widthProperty().bind(gridSize.divide(NUM_COLS));
        albumCover.heightProperty().bind(albumCover.widthProperty());
        albumCover.setFill(Color.ALICEBLUE);
        albumCover.setStroke(Color.BLACK);
        albumCover.setStrokeWidth(1.0);
        exec.submit(() -> {
            Logger.getLogger(ProductView.class.getName()).log(Level.FINE,
                    "Fetching album cover for album #", album.id);
            Image image = album.getImage();
            ImagePattern imagePattern = new ImagePattern(image);
            albumCover.setFill(imagePattern);
            Logger.getLogger(ProductView.class.getName()).log(Level.FINE,
                    "Fetched album cover for album #", album.id);
        });

        Label title_en_label = new Label("" + album.title_en);
        title_en_label.maxWidthProperty().bind(albumCover.widthProperty());
        albumNode.getChildren().addAll(albumCover, title_en_label);
        return albumNode;
    }

}
