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
