package ancolle.ui;

import static ancolle.ui.TilePaneView.TILE_PADDING_PX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * @author samuel
 */
public class ProductNode extends VBox {

    public final ImageView imageView;
    public final Label label1;
    public final Label label2;

    public ProductNode(double minWidth, double maxWidth) {
        super();
        setPadding(new Insets(TILE_PADDING_PX));
        setMinWidth(minWidth);
        setMaxWidth(maxWidth);
        setAlignment(Pos.BOTTOM_CENTER);

        imageView = new ImageView();
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(maxWidth);
        // imageView.setFitHeight(MAX_TILE_WIDTH_PX);
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
            setStyle("-fx-background-color: #9ec1ff;");
        });
        setOnMouseExited(evt -> {
            setStyle("-fx-background-color: none;");
        });
    }

}
