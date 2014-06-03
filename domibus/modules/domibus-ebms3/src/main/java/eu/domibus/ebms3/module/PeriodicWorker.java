package eu.domibus.ebms3.module;

import org.apache.log4j.Logger;

/**
 * @author Hamid Ben Malek
 */
public abstract class PeriodicWorker implements Runnable {

    private static final Logger log = Logger.getLogger(PeriodicWorker.class);

    protected static final int ONE_SECOND = 1000;
    protected static final int DEFAULT_INTERVAL = 40;
    protected int timeInterval = 40;
    protected Thread currentThread = null;
    protected boolean running = true;

    public PeriodicWorker() {
    }

    public PeriodicWorker(final String timeInSeconds) {
        setTimeInterval(timeInSeconds);
    }

    public PeriodicWorker(final int time) {
        timeInterval = time;
    }

    protected void setTimeInterval(final String timeInMinutes) {
        try {
            timeInterval = Integer.parseInt(timeInMinutes);
        } catch (Exception e) {
            timeInterval = DEFAULT_INTERVAL;
        }
    }

    public void start() {
        try {
            if (currentThread == null) {
                currentThread = new Thread(this);
            }
            running = true;
            currentThread.start();
        } catch (Exception e) {
            log.error("Error occured during thread execution", e);
        }
    }

    public void stop() {
        running = false;
    }

    public void terminate() {
        stop();
        currentThread = null;
    }

    public void run() {
        final Thread thisThread = Thread.currentThread();
        try {
            while (thisThread == currentThread && currentThread != null && running) {
                task();
                Thread.sleep(ONE_SECOND * (timeInterval));
            }
        } catch (Exception e) {
            log.error("Error occured in PeriodicWorker", e);
            run();
        }
    }

    protected abstract void task();
}