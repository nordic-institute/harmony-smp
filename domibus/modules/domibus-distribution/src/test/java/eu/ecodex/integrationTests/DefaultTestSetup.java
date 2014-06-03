package eu.ecodex.integrationTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import seatiger.util.Container;
import seatiger.util.Derby;


public class DefaultTestSetup {
    private static Derby derby_sending;
    private static Derby derby_receiving;
    private static Container tomcat_sending;
    private static Container tomcat_receiving;

    @BeforeClass
    public static void startServers() {
        DefaultTestSetup.derby_sending = new Derby(Derby.Type.SENDING);
        DefaultTestSetup.derby_receiving = new Derby(Derby.Type.RECEIVING);
        DefaultTestSetup.tomcat_sending = new Container(Container.Type.SENDING);
        DefaultTestSetup.tomcat_receiving = new Container(Container.Type.RECEIVING);
        DefaultTestSetup.derby_sending.start();
        DefaultTestSetup.derby_receiving.start();
        DefaultTestSetup.tomcat_sending.start();
        DefaultTestSetup.tomcat_receiving.start();
    }

    @AfterClass
    public static void stopServers() {
        DefaultTestSetup.tomcat_receiving.stop();
        DefaultTestSetup.tomcat_sending.stop();
        DefaultTestSetup.derby_receiving.stop();
        DefaultTestSetup.derby_sending.stop();
    }

    public static Derby getDerby_sending() {
        return DefaultTestSetup.derby_sending;
    }

    public static Derby getDerby_receiving() {
        return DefaultTestSetup.derby_receiving;
    }

    public static Container getTomcat_sending() {
        return DefaultTestSetup.tomcat_sending;
    }

    public static Container getTomcat_receiving() {
        return DefaultTestSetup.tomcat_receiving;
    }
}
