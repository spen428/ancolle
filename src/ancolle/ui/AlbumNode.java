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

import static ancolle.ui.TilePaneView.TILE_PADDING_PX;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author samuel
 */
// TODO: Almost identical to ProductNode
public class AlbumNode extends VBox {

    public static final Background COLOR_NOT_COLLECTED = new Background(new BackgroundFill(null, null, null));
    public static final Background COLOR_COLLECTED = new Background(new BackgroundFill(Color.FORESTGREEN, null, null));
    public static final Background COLOR_HOVERING = new Background(new BackgroundFill(Color.AQUAMARINE, null, null));

    public final ImageView albumCover;
    public final Label label1;
    public final Label label2;

    public boolean collected = false;

    public AlbumNode(double maxWidth) {
        super();

        setPadding(new Insets(TILE_PADDING_PX));
        setMaxWidth(maxWidth);
        setAlignment(Pos.BOTTOM_CENTER);

        albumCover = new ImageView();
        albumCover.setSmooth(true);
        albumCover.setPreserveRatio(true);
        albumCover.setFitWidth(maxWidth);
        albumCover.setFitHeight(maxWidth);
        getChildren().add(albumCover);

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
            setBackground(COLOR_HOVERING);
        });
        setOnMouseExited(evt -> {
            setBackground(collected ? COLOR_COLLECTED : COLOR_NOT_COLLECTED);
        });
    }

}
