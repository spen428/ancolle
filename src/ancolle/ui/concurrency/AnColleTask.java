package ancolle.ui.concurrency;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lykat
 */
public class AnColleTask implements Runnable, Comparable<AnColleTask> {

	/**
	 * Counter for preserving FIFO order when {@link AnColleTask#compareTo}
	 * returns 0.
	 */
	static final AtomicLong counter = new AtomicLong();

	final long ordinal;
	final Runnable runnable;
	final Object source;
	final Object userData;
	final int priority;

	/**
	 * Instantiate a new {@link AnColleTask} with maximum priority, a
	 * {@code null} source and a {@code null} user data.
	 *
	 * @param runnable the task's {@link Runnable}
	 * @deprecated Use full constructor to specify priority value.
	 */
	@Deprecated
	public AnColleTask(Runnable runnable) {
		this(runnable, Integer.MAX_VALUE, null, null);
	}

	/**
	 * Copy constructor
	 *
	 * @param task the {@link AnColleTask} to shallow copy
	 */
	public AnColleTask(AnColleTask task) {
		this(task.runnable, task.priority, task.source, task.userData);
	}

	public AnColleTask(Runnable runnable, int priority, Object source,
	                   Object userData) {
		this.ordinal = counter.getAndIncrement();
		this.runnable = runnable;
		this.priority = priority;
		this.source = source;
		this.userData = userData;
	}

	@Override
	public void run() {
		runnable.run();
	}

	@Override
	public int compareTo(AnColleTask o) {
		int c = Integer.compare(priority, o.priority);
		if (c != 0) {
			return c;
		}
		return Long.compare(ordinal, o.ordinal);
	}

}
