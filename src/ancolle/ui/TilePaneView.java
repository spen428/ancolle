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

import ancolle.main.AnColle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.TilePane;

/**
 * View based on {@link TilePane} on top of which {@link AlbumView} and
 * {@link ProductView} are built.
 *
 * @author samuel
 */
public class TilePaneView extends TilePane {

    public static final double PANE_PADDING_PX = 25;
    public static final double TILE_PADDING_PX = 10;
    private static final Logger LOG = Logger.getLogger(TilePaneView.class.getName());

    protected final AnColle ancolle;
    private final BlockingQueue<Runnable> jobQueue;
    private final Thread workerThread;

    public TilePaneView(AnColle ancolle) {
	this.ancolle = ancolle;
	this.jobQueue = new LinkedBlockingQueue<>();
	this.workerThread = new Thread(() -> {
	    while (true) {
		try {
		    Runnable task = jobQueue.take();
		    task.run();
		} catch (InterruptedException ex) {
		    Logger.getLogger(ProductView.class.getName())
			    .log(Level.SEVERE, null, ex);
		    break;
		}
	    }
	});
	this.workerThread.setDaemon(true);
	this.workerThread.start();
    }

    /**
     * Add a {@link Runnable} to the queue of background tasks.
     *
     * @param task the task
     */
    public void submitBackgroundTask(Runnable task) {
	this.jobQueue.add(task);
    }

    public void cancelQueuedTasks() {
	jobQueue.clear();
    }

}
