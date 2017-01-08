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
package ancolle.ui.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * @author lykat
 */
public class AnColleTaskManager {

    private static final Logger LOG = Logger.getLogger(AnColleTaskManager.class.getName());

    private final ThreadPoolExecutor threadPoolExecutor;
    private final PriorityBlockingQueue<Runnable> taskPriorityQueue;
    private final AtomicInteger taskCount;

    public AnColleTaskManager() {
	this.taskCount = new AtomicInteger();
	this.taskPriorityQueue = new PriorityBlockingQueue<>();
	this.threadPoolExecutor = new ThreadPoolExecutor(4, 16,
		Integer.MAX_VALUE, TimeUnit.DAYS, taskPriorityQueue);
	threadPoolExecutor.setThreadFactory((Runnable r) -> {
	    Thread t = Executors.defaultThreadFactory().newThread(r);
	    t.setDaemon(true);
	    return t;
	});
    }

    /**
     * Submit a task to the thread pool and increment the task counter.
     *
     * @param task the task
     */
    public void submitTask(AnColleTask task) {
	threadPoolExecutor.execute(new AnColleTaskCallbackWrapper(task));
	Platform.runLater(() -> {
	    final int count = taskCount.incrementAndGet();
	    LOG.log(Level.FINER, "{0} tasks running", count);
	});
    }

    public void cancelTasks() {
	taskPriorityQueue.clear();
    }

    /**
     * Decrements the task counter when an {@link AnColleTask} finishes
     * execution.
     */
    private class AnColleTaskCallbackWrapper implements Runnable,
	    Comparable<AnColleTaskCallbackWrapper> {

	protected final AnColleTask task;

	AnColleTaskCallbackWrapper(AnColleTask task) {
	    this.task = task;
	}

	@Override
	public void run() {
	    task.run();
	    Platform.runLater(() -> {
		final int count = taskCount.decrementAndGet();
		LOG.log(Level.FINER, "{0} tasks running", count);
	    });
	}

	@Override
	public int compareTo(AnColleTaskCallbackWrapper o) {
	    return task.compareTo(o.task);
	}

    }

}
