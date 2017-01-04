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

import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author lykat
 */
public class StatusBar extends HBox {

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(StatusBar.class.getName());

    private static final double HEIGHT_PX = 22;

    public static final String CLASS_STATUS_BAR_LABEL = "status-label";
    public static final String CLASS_STATUS_BAR = "status-bar";

    public final Label statusLabel;

    public StatusBar() {
	super();
	getStyleClass().add(CLASS_STATUS_BAR);
	setMinHeight(HEIGHT_PX);
	setMaxHeight(HEIGHT_PX);
	setAlignment(Pos.CENTER_LEFT);

	statusLabel = new Label("Ready.");
	statusLabel.setPadding(new Insets(2, 20, 2, 20));
	statusLabel.setAlignment(Pos.CENTER);
	statusLabel.getStyleClass().add(CLASS_STATUS_BAR_LABEL);
	getChildren().add(statusLabel);
    }

}
