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

	protected final ApplicationRoot ancolle;

	public TilePaneView(ApplicationRoot ancolle) {
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
