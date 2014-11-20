package eu.domibus.ebms3.workers;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hamid Ben Malek
 */
public abstract class DirWatcher extends TimerTask implements FileFilter {
    private String path;
    protected File[] filesArray;
    protected final Map<File, Long> dir = new HashMap<File, Long>();

    public DirWatcher() {
    }

    public DirWatcher(final String path) {
        this.setPath(path);
    }

    public void setPath(final String path) {
        this.path = path;
        final File dirToWatch = new File(path);
        this.filesArray = dirToWatch.listFiles(this);
        if (this.filesArray == null) {
            throw new RuntimeException("directory to watch:" + dirToWatch.getAbsolutePath() + "not found");
        }

        // transfer to the hashmap be used a reference and keep the
        // lastModfied value
        for (final File aFilesArray : this.filesArray) {
            this.dir.put(aFilesArray, aFilesArray.lastModified());
        }
    }

    public final void run() {
        final File dirToWatch = new File(this.path);
        this.filesArray = dirToWatch.listFiles(this);
        if (this.filesArray == null) {
            throw new RuntimeException("directory to watch:" + dirToWatch.getAbsolutePath() + "not found");
        }

        // scan the files and check for modification/addition
        for (final File aFilesArray : this.filesArray) {
            final Long current = this.dir.get(aFilesArray);
            if (current == null) {
                // new file
                this.dir.put(aFilesArray, aFilesArray.lastModified());
                this.onChange(aFilesArray, "add");
            } else if (current != aFilesArray.lastModified()) {
                // modified file
                this.dir.put(aFilesArray, aFilesArray.lastModified());
                this.onChange(aFilesArray, "modify");
            }
        }
    }

    public void watch(final long period) {
        final Timer timer = new Timer();
        timer.schedule(this, period, period);
    }

    protected abstract void onChange(File file, String action);

    public abstract boolean accept(File file);
}