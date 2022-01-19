package ancolle.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A container for an {@link ImageView}. The fit width and fit height of the
 * contained {@link ImageView} will be bound to the container's max width and
 * max height properties, which can then be styled using the style sheet.
 *
 * @author lykat
 */
public class ImageViewContainer extends VBox {

	public final ImageView imageView;

	public ImageViewContainer() {
		getStyleClass().add("image-view-container");
		this.imageView = new ImageView();
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(maxWidthProperty());
		imageView.fitHeightProperty().bind(maxHeightProperty());
		getChildren().add(imageView);
		VBox.setVgrow(imageView, Priority.ALWAYS);
	}

	/**
	 * Calls the {@link ImageView#setImage} method of the contained
	 * {@link ImageView}
	 *
	 * @param image the value to set
	 */
	public void setImage(Image image) {
		imageView.setImage(image);
	}

}
