package eu.ecodex.integrationTests.util;

import org.junit.Test;
import seatiger.util.Derby;

import java.io.IOException;

import static seatiger.util.Configuration.DATABASE_SENDING_INITIAL_SCRIPT;
import static seatiger.util.Configuration.DOMIBUS_TESTING_CONFIG_FILE;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 05.02.14
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class StartDerby {
    @Test
    public void startDerbySending() {
        System.setProperty(DOMIBUS_TESTING_CONFIG_FILE,
                           "C:\\development\\git-repos\\domibus\\domibus\\domibus-config\\domibus.properties");
        System.setProperty(DATABASE_SENDING_INITIAL_SCRIPT,
                           "C:\\development\\git-repos\\domibus\\domibus\\modules\\domibus-distribution\\target\\sql\\hibernate3\\domibus_derby_integration_testing.sql");
        new Derby(Derby.Type.SENDING).start();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
