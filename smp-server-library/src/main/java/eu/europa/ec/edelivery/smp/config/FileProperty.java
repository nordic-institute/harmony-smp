package eu.europa.ec.edelivery.smp.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

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
    public static final String PROPERTY_DB_DRIVER = "jdbc.driver";
    public static final String PROPERTY_DB_USER = "jdbc.user";
    public static final String PROPERTY_DB_TOKEN = "jdbc.password";
    public static final String PROPERTY_DB_URL = "jdbc.url";
    public static final String PROPERTY_DB_JNDI = "datasource.jndi";
    public static final String PROPERTY_DB_DIALECT = "hibernate.dialect";

    private FileProperty() {
    }

    public static void updateLog4jConfiguration(String logFileFolder, String logPropertyFile, String configurationFolder) {

        if (StringUtils.isNotBlank(logFileFolder)) {
            System.setProperty(PROPERTY_LOG_FOLDER, logFileFolder);
        }

        File f = new File(logPropertyFile);
        if (!f.exists()) {
            LOG.info("Log configuration file: {} not exists.", f.getAbsolutePath());
            f = new File(configurationFolder, logPropertyFile);
            LOG.info("Set log configuration file: {}.", f.getAbsolutePath());

        }
        // if configuration file exist update configuration
        if (f.exists()) {
            setLogConfiguration(f);
        }
    }

    public static void setLogConfiguration(File configurationFile) {
        try (InputStream configStream = new FileInputStream(configurationFile)) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(configStream); // loads logback file
        } catch (IOException | JoranException e) {
            LOG.info("Error occurred while loading LOG configuration.", e);
        }
    }

    public static Properties getFileProperties() {
        LOG.info("Start read file properties from '/smp.config.properties'");
        InputStream is = PropertyInitialization.class.getResourceAsStream("/smp.config.properties");
        if (is == null) {
            LOG.info("File '/smp.config.properties' not found in classpath, read '/config.properties'");
            is = PropertyInitialization.class.getResourceAsStream("/config.properties");
        }
        Properties connectionProp = new Properties();
        try {
            connectionProp.load(is);
        } catch (IOException e) {
            LOG.error("IOException occurred while reading properties", e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred  while reading properties.", e.getMessage());
        }
        return connectionProp;
    }
}
