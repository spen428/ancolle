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
