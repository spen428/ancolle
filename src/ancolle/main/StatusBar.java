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
package ancolle.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

/**
 * @author samuel
 */
public class StatusBar extends HBox {

    private static final double HEIGHT_PX = 22;

    public final Label statusLabel;

    public StatusBar() {
	super();
	setMinHeight(HEIGHT_PX);
	setMaxHeight(HEIGHT_PX);
	setAlignment(Pos.CENTER_LEFT);

	statusLabel = new Label("Ready.");
	statusLabel.setPadding(new Insets(2, 20, 2, 20));
	statusLabel.setAlignment(Pos.CENTER);
	getChildren().add(statusLabel);
    }

}
