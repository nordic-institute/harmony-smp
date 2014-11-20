package eu.domibus.ebms3.workers;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hamid Ben Malek
 */
public abstract class FileWatcher extends TimerTask {
    protected long timeStamp;
    protected File source;

    public FileWatcher() {
    }

    public FileWatcher(final File file) {
        this.source = file;
        this.timeStamp = file.lastModified();
    }

    public File getSource() {
        return this.source;
    }

    public void setSource(final File source) {
        this.source = source;
    }

    public void watch(final long period) {
        final Timer timer = new Timer();
        timer.schedule(this, new Date(), period);
    }

    public final void run() {
        final long timeStamp = this.source.lastModified();

        if (this.timeStamp != timeStamp) {
            this.timeStamp = timeStamp;
            this.onChange(this.source);
        }
    }

    protected abstract void onChange(File file);
}