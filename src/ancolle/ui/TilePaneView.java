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

import ancolle.ui.concurrency.AnColleTask;
import javafx.scene.layout.TilePane;

import java.util.logging.Logger;

/**
 * View based on {@link TilePane} on top of which {@link AlbumView} and
 * {@link ProductView} are built.
 *
 * @author lykat
 */
public abstract class TilePaneView extends TilePane {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(TilePaneView.class.getName());

	protected final AnColle ancolle;

	public TilePaneView(AnColle ancolle) {
		this.ancolle = ancolle;
		getStyleClass().add("tile-pane-view");
	}

	public void submitBackgroundTask(AnColleTask task) {
		ancolle.getTaskManager().submitTask(task);
	}

	public void cancelQueuedTasks() {
		ancolle.getTaskManager().cancelTasksFrom(this);
	}

	/**
	 * Clear and reload all item nodes.
	 */
	public abstract void refreshItems();

}
