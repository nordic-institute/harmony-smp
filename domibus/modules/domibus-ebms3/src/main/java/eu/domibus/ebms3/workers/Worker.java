package eu.domibus.ebms3.workers;

import eu.domibus.common.util.ClassUtil;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Worker")
public class Worker implements Runnable {
    private static final Logger log = Logger.getLogger(Worker.class);

    @Attribute
    protected String name;

    @Attribute(required = false)
    protected boolean activate;

    @Attribute(required = false)
    protected long interval = 30000;

    @Attribute(required = false)
    protected int total = -1;

    @Attribute(required = false)
    protected int groupSize;

    @Attribute(required = false)
    protected long pause = this.interval;

    @Attribute(required = false)
    protected String workerClass;

    @ElementMap(entry = "parameter", key = "name", attribute = true, inline = true, required = false)
    protected Map<String, String> parameters;

    protected int counter;
    protected Timer timer;
    //protected Task task = null;
    protected Runnable task;

    protected String state = Worker.VIRGIN;
    protected static final String VIRGIN = "virgin";
    protected static final String RUNNING = "running";
    protected static final String CANCELLED = "cancelled";
    protected TimerTask timerTask;

    public void start() {
        this.start(true);
    }

    public void start(final boolean cycle) {
        if (this.state.equals(Worker.RUNNING) || !this.activate) {
            return;
        }
        this.getTask();
        try {
            if (this.timer == null) {
                this.timer = new Timer();
            }
            if (cycle && (this.total > 0) && (this.counter >= this.total)) {
                this.counter = 0;
            }
            this.timer.schedule(this.getTimerTask(), this.interval, this.interval);
            this.state = Worker.RUNNING;
        } catch (Exception e) {
            Worker.log.error("Error occured during thread execution", e);
        }
    }

    /**
     * Subclasses should override this method
     */
    public void task() {
    }

    public final void run() {
        if (!this.activate) {
            return;
        }
        if ((this.total == 0) || ((this.total > 0) && (this.counter >= this.total))) {
            this.terminate();
            return;
        }
        try {
            if ((this.counter > 0) && (this.groupSize > 0) &&
                ((this.counter % this.groupSize) == 0) && (this.pause > 0) &&
                ((this.pause - this.interval) > 0)) {
                Thread.sleep(this.pause - this.interval);
            }
        } catch (Exception e) {
            Worker.log.error("Error occured during thread execution", e);
        }
        this.counter++;

        try {
            if (this.getTask() != null) {
                this.task.run();
            } else {
                this.task();
            }
        } catch (Exception ex) {
            Worker.log.error("Error occured in ControllerPeriodicWorker", ex);
            this.terminate();
            this.start(false);
        }
    }

    public void terminate() {
        if (!this.state.equals(Worker.RUNNING) || this.state.equals(Worker.VIRGIN)) {
            return;
        }
        this.timerTask.cancel();
        this.timer.purge();
        this.state = Worker.CANCELLED;
    }

    public void update(final Worker worker) {
        if (worker == null) {
            return;
        }
        this.interval = worker.getInterval();
        this.total = worker.getTotal();
        this.groupSize = worker.getGroupSize();
        this.pause = worker.getPause();
        this.workerClass = worker.getWorkerClass();
        this.parameters = worker.getParameters();
        if (this.activate && !worker.isActivate()) {
            this.activate = worker.isActivate();
            this.terminate();
        } else if (!this.activate && worker.isActivate()) {
            this.activate = worker.isActivate();
            this.start();
        } else {
            this.activate = worker.isActivate();
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isActivate() {
        return this.activate;
    }

    public void setActivate(final boolean activate) {
        this.activate = activate;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(final long interval) {
        this.interval = interval;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public int getGroupSize() {
        return this.groupSize;
    }

    public void setGroupSize(final int groupSize) {
        this.groupSize = groupSize;
    }

    public long getPause() {
        return this.pause;
    }

    public void setPause(final long pause) {
        this.pause = pause;
    }

    public String getWorkerClass() {
        return this.workerClass;
    }

    public void setWorkerClass(final String workerClass) {
        this.workerClass = workerClass;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Runnable getTask() {
        if (this.task != null) {
            return this.task;
        }
        if ((this.workerClass == null) || "".equals(this.workerClass.trim())) {
            return null;
        }
        try {
            this.task = (Runnable) ClassUtil.createInstance(this.workerClass);
            if (this.task instanceof Task) {
                ((Task) this.task).setParameters(this.parameters);
            }
        } catch (Exception ex) {
            Worker.log.debug(ex.getMessage());
        }
        return this.task;
    }

    public TimerTask getTimerTask() {
        if ((this.timerTask != null) && !this.state.equals(Worker.CANCELLED)) {
            return this.timerTask;
        }
        this.timerTask = new TimerTaskWrapper(this);
        return this.timerTask;
    }
}