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

    private final long ordinal;
    private final Runnable runnable;
    private final Object source;
    private final Object userData;
    private final int priority;

    @Deprecated
    public AnColleTask(Runnable runnable) {
	this(runnable, Integer.MAX_VALUE, null, null);
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
