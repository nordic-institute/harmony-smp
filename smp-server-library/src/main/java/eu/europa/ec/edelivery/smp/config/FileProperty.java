package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

public class FileProperty {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(FileProperty.class);

    public static final String PROPERTY_LOG_FOLDER = "log.folder";
    public static final String PROPERTY_LOG_PROPERTIES = "log.configuration.file";

    public static final String PROPERTY_ALLOWED_ENC_SLASHES = "encodedSlashesAllowedInUrl";

    public static final String PROPERTY_DB_DRIVER = "jdbc.driver";
    public static final String PROPERTY_DB_USER= "jdbc.user";
    public static final String PROPERTY_DB_PASSWORD = "jdbc.password";
    public static final String PROPERTY_DB_URL = "jdbc.url";
    public static final String PROPERTY_DB_JNDI = "datasource.jndi";
    public static final String PROPERTY_DB_DIALECT = "hibernate.dialect";

    public static void updateLog4jConfiguration(String logFileFolder, String logPropertyFile, String configurationFolder) {
        Properties props = new Properties();
        try {
            InputStream configStream = null;
            if (!StringUtils.isBlank(logPropertyFile)) {
                File f = new File(logPropertyFile);
                if (!f.exists()) {
                    LOG.info("Log configuration file:  "+f.getAbsolutePath() + " not exists.");
                    f = new File(configurationFolder, logPropertyFile);
                    LOG.info("Set log configuration file: "+f.getAbsolutePath() + ".");
                }

                if (f.exists()) {
                    LOG.info("Set log configuration: "+f.getAbsolutePath() + ".");
                    configStream = new FileInputStream(f);
                }
            }
            // if null use default properties
            if (configStream == null) {
                LOG.info("Set default log configuration.");
                configStream = FileProperty.class.getResourceAsStream("/smp-log4j.properties");
            }
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("Error occurred while loading default LOG configuration.");
        }
        // set
        if (!StringUtils.isBlank(logFileFolder)) {
            LOG.info("Set log4j.appender.MainLogFile.File : "+logFileFolder + "/edelivery-smp.log");
            props.setProperty("log4j.appender.MainLogFile.File", logFileFolder + "/edelivery-smp.log");
        }
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    public static Properties getFileProperties() {
        LOG.info( "Start read file properties from '/smp.config.properties'");
        InputStream is = PropertyInitialization.class.getResourceAsStream("/smp.config.properties");
        if (is == null) {
            LOG.info( "File '/smp.config.properties' not found in classpath, read '/config.properties'");
            is = PropertyInitialization.class.getResourceAsStream("/config.properties");
        }
        Properties connectionProp = new Properties();
        try {
            connectionProp.load(is);
        } catch (IOException e) {
            LOG.error( "IOException occurred while reading properties", e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred  while reading properties.", e.getMessage());
        }
        return connectionProp;
    }
}
