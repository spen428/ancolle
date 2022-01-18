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

import ancolle.ui.StatusBar;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Executes background tasks
 *
 * @author lykat
 */
public class AnColleTaskManager {

	private static final Logger LOG = Logger.getLogger(AnColleTaskManager.class.getName());

	private final ThreadPoolExecutor threadPoolExecutor;
	private final PriorityBlockingQueue<Runnable> taskPriorityQueue;
	private final AtomicInteger taskCount;
	private final StatusBar statusBar;

	/**
	 * Instantiate a new {@link AnColleTaskManager}
	 *
	 * @param statusBar the {@link StatusBar} to use to display background task
	 *                  progress
	 */
	public AnColleTaskManager(StatusBar statusBar) {
		this.statusBar = statusBar;
		this.taskCount = new AtomicInteger();
		this.taskPriorityQueue = new PriorityBlockingQueue<>();
		this.threadPoolExecutor = new ThreadPoolExecutor(8, 16,
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
	}

	public void cancelTasks() {
		taskPriorityQueue.clear();
	}

	/**
	 * Cancel all tasks whose {@link AnColleTask#source} field matches the given
	 * object reference. This will not match {@code null} pointers. This will
	 * fail if the task(s) have already been taken off the queue to be executed.
	 *
	 * @param source the object reference
	 * @return true if at least one task was cancelled
	 */
	public boolean cancelTasksFrom(Object source) {
		return taskPriorityQueue.removeIf(task -> {
			if (source != null && task instanceof AnColleTask) {
				AnColleTask at = (AnColleTask) task;
				return at.source != null && at.source.equals(source);
			}
			return false;
		});
	}

	/**
	 * Cancel all tasks whose {@link AnColleTask#userData} field matches the
	 * given object reference. This will not match {@code null} pointers. This
	 * will fail if the task(s) have already been taken off the queue to be
	 * executed.
	 *
	 * @param userData the object reference
	 * @return true if at least one task was cancelled
	 */
	public boolean cancelTasksWith(Object userData) {
		return taskPriorityQueue.removeIf(task -> {
			if (userData != null && task instanceof AnColleTask) {
				AnColleTask at = (AnColleTask) task;
				return at.userData != null && at.userData.equals(userData);
			}
			return false;
		});
	}

	/**
	 * Decrements the task counter when an {@link AnColleTask} finishes
	 * execution.
	 */
	private class AnColleTaskCallbackWrapper extends AnColleTask {

		AnColleTaskCallbackWrapper(AnColleTask task) {
			super(task);
		}

		@Override
		public void run() {
			Platform.runLater(() -> {
				final int running = taskCount.incrementAndGet();
				final int queued = taskPriorityQueue.size();
				statusBar.statusLabel.setText(String.format("%d background "
						+ "tasks running, %d queued.", running, queued));
			});
			super.run();
			Platform.runLater(() -> {
				final int running = taskCount.decrementAndGet();
				final int queued = taskPriorityQueue.size();
				statusBar.statusLabel.setText(String.format("%d background "
						+ "tasks running, %d queued.", running, queued));
				if (running == 0 && queued == 0) {
					statusBar.statusLabel.setText("Ready.");
				}
			});
		}

	}

}
