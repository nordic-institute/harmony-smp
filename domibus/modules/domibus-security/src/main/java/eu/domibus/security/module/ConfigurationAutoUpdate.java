package eu.domibus.security.module;

import java.util.TimerTask;

public class ConfigurationAutoUpdate extends TimerTask {

    @Override
    public void run() {
        Configuration.loadSecurityConfigFileIfModified();
    }

}
