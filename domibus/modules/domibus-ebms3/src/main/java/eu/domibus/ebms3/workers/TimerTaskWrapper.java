package eu.domibus.ebms3.workers;

import java.util.TimerTask;

/**
 * @author Hamid Ben Malek
 */
public class TimerTaskWrapper extends TimerTask {
    protected final Runnable task;

    public TimerTaskWrapper(final Runnable action) {
        this.task = action;
    }

    public void run() {
        if (task != null) {
            task.run();
        }
    }
}