package seatiger.util;

import eu.eCODEX.testing.infrastructure.DbToolInterface;
import eu.eCODEX.testing.infrastructure.DerbyConfiguration;
import eu.eCODEX.testing.infrastructure.impl.DbToolInterfaceImpl;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import static seatiger.util.Configuration.*;

public class Derby {
    private static final Logger LOG = Logger.getLogger(Derby.class);
    private Type type;
    private DerbyConfiguration dc;
    private String defaultConfigFile = System.getProperty(DOMIBUS_TESTING_CONFIG_FILE);
    private DbToolInterface db = new DbToolInterfaceImpl();
    private String cleanScriptSending = System.getProperty(DATABASE_SENDING_CLEAN_SCRIPT);
    private String initialScriptSending = System.getProperty(DATABASE_SENDING_INITIAL_SCRIPT);
    private Connection con;
    private String cleanScriptReceiving = System.getProperty(DATABASE_RECEIVING_CLEAN_SCRIPT);
    private String initialScriptReceiving = System.getProperty(DATABASE_RECEIVING_INITIAL_SCRIPT);

    public Derby(Type type) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(defaultConfigFile));
        } catch (IOException e) {
            LOG.error(e);
        }
        this.type = type;
        DerbyConfiguration dc = new DerbyConfiguration();
        String url = null;
        switch (type) {
            case SENDING:
                url = (props.getProperty(DOMIBUS_PERSISTENCE_URL));
                dc.setUserName(props.getProperty(DOMIBUS_PERSISTENCE_USER));
                dc.setPassword(props.getProperty(DOMIBUS_PERSISTENCE_PASSWORD));
                break;
            case RECEIVING:
                url = (props.getProperty(DOMIBUS_PERSISTENCE_TESTING_URL));
                dc.setUserName(props.getProperty(DOMIBUS_PERSISTENCE_USER));
                dc.setPassword(props.getProperty(DOMIBUS_PERSISTENCE_PASSWORD));
                break;
        }
        parseURL(url, dc);
        this.dc = dc;
    }


    public Derby(DerbyConfiguration conf) {
        this.dc = conf;
    }

    // This method only works for urls formated like jdbc:derby://localhost:1529/domibus2;create=true
    private void parseURL(String url, DerbyConfiguration dc) {
        url = url.replaceFirst("jdbc:derby://", "");
        String[] split = url.split(":");
        dc.setHostName(split[0]);
        split = split[1].split("/");
        dc.setPort(split[0].substring(0));
        split = split[1].split(";");
        dc.setDatabaseName(split[0]);
        if (split.length == 2 && "create=true".equals(split[1].toLowerCase())) {
            dc.setCreate(true);
        }


    }

    public Connection start() {
        this.con = db.startDerby(dc);
        runInitialScript();
        return con;
    }

    private void runInitialScript() {

        db.executeScript(con, new File(this.type == Type.SENDING ? initialScriptSending : initialScriptReceiving));
    }

    public void runCleanScript() {
        db.executeScript(con, new File(this.type == Type.SENDING ? cleanScriptSending : cleanScriptReceiving));
    }

    public void stop() {
        db.stopDerby(dc);
    }

    public enum Type {
        SENDING, RECEIVING;
    }

}
