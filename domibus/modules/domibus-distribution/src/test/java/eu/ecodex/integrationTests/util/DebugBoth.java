package eu.ecodex.integrationTests.util;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import seatiger.util.Container;
import seatiger.util.Derby;

import java.io.IOException;

@SuppressWarnings({"StaticVariableMayNotBeInitialized", "StaticVariableUsedBeforeInitialization"})
public class DebugBoth {
    private static Derby derby_sending;
    private static Derby derby_receiving;
    private static Container tomcat_sending;
    private static Container tomcat_receiving;

    @BeforeClass
    public static void startServers() {
        DebugBoth.derby_sending = new Derby(Derby.Type.SENDING);
        DebugBoth.derby_receiving = new Derby(Derby.Type.RECEIVING);
        DebugBoth.tomcat_sending = new Container(Container.Type.SENDING);
        DebugBoth.tomcat_receiving = new Container(Container.Type.RECEIVING);
        DebugBoth.derby_sending.start();
        DebugBoth.derby_receiving.start();
        DebugBoth.tomcat_sending.setJvmArgs(
                "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Xnoagent -Djava.compiler=NONE -Duser.language=en")
                 .start();
        DebugBoth.tomcat_receiving.setJvmArgs(
                "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9000 -Xnoagent -Djava.compiler=NONE -Duser.language=en")
                 .start();
    }


    @Test
    public void printMessage() throws IOException {
        System.out.println("Debug mode startet, this process has to be terminated externally");
        System.in.read();
    }

    @AfterClass
    public static void stopServers() {
        DebugBoth.tomcat_receiving.setJvmArgs("-Duser.language=en").stop();
        DebugBoth.tomcat_sending.setJvmArgs("-Duser.language=en").stop();
        DebugBoth.derby_receiving.stop();
        DebugBoth.derby_sending.stop();
    }

}
