package eu.domibus.ebms3.workers;

import org.apache.log4j.Logger;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Workers")
public class WorkerPool extends FileWatcher implements Serializable {
    private static final long serialVersionUID = -5593318201928374737L;

    private static final Logger log = Logger.getLogger(WorkerPool.class);

    @ElementList(entry = "Worker", inline = true, required = false)
    protected List<Worker> workers = new ArrayList<Worker>();

    public List<Worker> getWorkers() {
        return this.workers;
    }

    public void setWorkers(final List<Worker> workers) {
        this.workers = workers;
    }

    public void addWorker(final String name, final String workerClass, final boolean activate) {
        if ((name == null) || (workerClass == null)) {
            return;
        }
        Worker w = this.getWorker(name);
        if (w == null) {
            w = new Worker();
            w.setName(name);
            w.setWorkerClass(workerClass);
            w.setActivate(activate);
            this.workers.add(w);
            if (activate) {
                w.start();
            }
        } else {
            w.setWorkerClass(workerClass);
            final boolean act = w.isActivate();
            w.setActivate(activate);
            if (!act && activate) {
                w.start();
            }
        }
    }

    public Worker getWorker(final String name) {
        if ((name == null) || "".equals(name.trim())) {
            return null;
        }
        for (final Worker w : this.workers) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        return null;
    }

    public static WorkerPool load(final File sourceFile) {
        if ((sourceFile == null) || !sourceFile.exists()) {
            WorkerPool.log.debug("Could not load file " + sourceFile +
                                 "  because it is either null or does not exists");
            return null;
        }
        WorkerPool pool = null;
        try {
            final Serializer serializer = new Persister();
            pool = serializer.read(WorkerPool.class, sourceFile);
            pool.setSource(sourceFile);
            //log.debug("loaded workers from file " + sourceFile);
        } catch (Exception ex) {
            WorkerPool.log.error("Error while loading WorkerPool", ex);
        }
        return pool;
    }

    public void start() {
        if ((this.workers != null) && !this.workers.isEmpty()) {
            for (final Worker w : this.workers) {
                if (w.isActivate()) {
                    w.start();
                    WorkerPool.log.debug("started worker " + w.getName());
                }
            }
        }
    }

    public void reload(final File source) {
        final WorkerPool pool = WorkerPool.load(source);
        if (pool == null) {
            return;
        }
        for (final Worker w : pool.getWorkers()) {
            final Worker worker = this.getWorker(w.getName());
            if (worker != null) {
                worker.update(w);
            } else {
                this.workers.add(w);
                if (w.isActivate()) {
                    w.start();
                }
            }
        }
    }

    public void onChange(final File s) {
        //log.debug("workers file " + s + "  has changed. Reloading it...");
        this.reload(s);
    }
}