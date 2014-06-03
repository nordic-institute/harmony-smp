package eu.domibus.ebms3.workers.impl;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.config.PModePool;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.workers.DirWatcher;
import eu.domibus.ebms3.workers.Task;

import java.io.File;
import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
public class PModesWatcher extends DirWatcher implements Task {
    // private Log log = LogFactory.getLog(PModesWatcher.class);
    private final Logger log = Logger.getLogger(PModesWatcher.class);

    protected File pmodesDir = null;

    public PModesWatcher() {
        setPath(Configuration.getPModesDir());
    }

    protected void onChange(final File file, final String action) {
        if (file == null || action == null) {
            return;
        }
        final PModePool pool = PModePool.load(file);
        if (pool != null) {
            Configuration.addPModePool(pool);
        }
    }

    public boolean accept(final File file) {
        return !file.isDirectory() && file.getName().endsWith(".xml");
    }

    public void setParameters(final Map<String, String> parameters) {
        setPath(Configuration.getPModesDir());


        log.debug("Loading PModes...");
        for (final File file : filesArray) {
            onChange(file, "New");
        }
        log.debug("All PModes have been loaded.");
    }
}