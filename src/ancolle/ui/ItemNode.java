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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author samuel
 * @param <T> the type of {@link Item} that this node represents
 */
public abstract class ItemNode<T> extends VBox {

    public static final double ITEM_NODE_PADDING_PX = 10;
    public static final Background COLOR_HOVERING = new Background(new BackgroundFill(Color.AQUAMARINE, null, null));

    public final ImageView imageView;
    public final Label label1;
    public final Label label2;

    /**
     * The {@link Item} represented by this node.
     */
    private T item;

    public ItemNode() {
	setPadding(new Insets(ITEM_NODE_PADDING_PX));
	setAlignment(Pos.BOTTOM_CENTER);

	imageView = new ImageView();
	imageView.setSmooth(true);
	imageView.setPreserveRatio(true);
	imageView.fitWidthProperty().bind(maxWidthProperty());
	imageView.fitHeightProperty().bind(maxHeightProperty());
	getChildren().add(imageView);

	label1 = new Label();
	label1.maxWidthProperty().bind(widthProperty());
	label1.setAlignment(Pos.BOTTOM_CENTER);
	getChildren().add(label1);

	label2 = new Label();
	label2.maxWidthProperty().bind(widthProperty());
	label2.setAlignment(Pos.BOTTOM_CENTER);
	getChildren().add(label2);

	// Mouse hover highlighting
	setOnMouseEntered(evt -> {
	    setBackground(COLOR_HOVERING);
	    ContextMenu cm = getContextMenu();
	    if (cm != null) {
		cm.hide();
	    }
	});
	setOnMouseExited(evt -> {
	    setBackground(Background.EMPTY);
	});
    }

    abstract protected ContextMenu getContextMenu();

    /**
     * Open the context menu, anchored to this {@link ItemNode} and offset
     * according to the location of the mouse click that triggered this event
     *
     * @param evt the {@link MouseEvent} that triggered the opening of the
     * context menu
     */
    protected void showContextMenu(MouseEvent evt) {
	double dx = evt.getX();
	double dy = evt.getY();
	getContextMenu().show(this, Side.TOP, dx, dy);
    }

    /**
     * @return the item
     */
    protected T getItem() {
	return item;
    }

    /**
     * @param item the item to set
     */
    protected void setItem(T item) {
	this.item = item;
    }

}
