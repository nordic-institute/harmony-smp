package eu.europa.ec.cipa;

import de.mendelson.comm.as2.database.DBDriverManager;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Agent;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.security.BCCryptoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    public static final String USER = "user";
    public static final String PASSWORD = "password";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
       // Security.addProvider(new BouncyCastleProvider());

        // register preferences
        registerPreferences();

        // start AS2 server
        startAS2Server();
    }

    private void registerPreferences() {
        Properties properties = PropertiesUtil.getProperties();
        PreferencesAS2 preferencesAS2 = new PreferencesAS2();
        for (Object propKey : properties.keySet()) {
            preferencesAS2.put((String) propKey, (String) properties.get(propKey));
        }
    }

    private void startAS2Server() {
        //start server
        try {
            //register the database drivers for the VM
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            //initialize the security provider
            BCCryptoHelper helper = new BCCryptoHelper();
            helper.initialize();
            AS2Server as2Server = new AS2Server(false, true);
            new AS2Agent(as2Server);
        } catch (Throwable exc) {
            logger.error(exc.getMessage(), exc);
            AS2Server.deleteLockFile();
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            DBDriverManager.shutdownConnectionPool();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            Connection configConnection = DBDriverManager.getConnectionWithoutErrorHandling(DBDriverManager.DB_CONFIG, "localhost");
            Connection runtimeConnection = DBDriverManager.getConnection(DBDriverManager.DB_RUNTIME, "localhost");
            configConnection.createStatement().execute("SHUTDOWN");
            configConnection.close();
            logger.info("DB server: config DB shutdown complete.");
            runtimeConnection.createStatement().execute("SHUTDOWN");
            runtimeConnection.close();
            logger.info("DB server: runtime DB shutdown complete.");
        } catch (Exception e) {
            logger.error("DB server shutdown: " + e.getMessage(), e);
        } finally {
            AS2Server.deleteLockFile();
        }
    }
}