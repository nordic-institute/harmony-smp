package eu.europa.ec.edelivery.smp.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
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

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

public class FileProperty {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(FileProperty.class);

    // the property file is set in the root fo the resources
    public static final String PROPERTY_FILE = "/smp.config.properties";
    // legacy configuration file
    public static final String PROPERTY_FILE_BACKUP = "/config.properties";

    public static final String PROPERTY_LOG_FOLDER = "log.folder";
    public static final String PROPERTY_LOG_PROPERTIES = "log.configuration.file";
    public static final String PROPERTY_DB_DRIVER = "jdbc.driver";
    public static final String PROPERTY_DB_USER = "jdbc.user";
    public static final String PROPERTY_DB_TOKEN = "jdbc.password";
    public static final String PROPERTY_DB_URL = "jdbc.url";
    public static final String PROPERTY_DB_JNDI = "datasource.jndi";
    public static final String PROPERTY_DB_DIALECT = "hibernate.dialect";
    public static final String PROPERTY_LIB_FOLDER = "libraries.folder";
    public static final String PROPERTY_SMP_MODE_DEVELOPMENT = "smp.mode.development";

    protected FileProperty() {
    }

    public static void updateLogConfiguration(String logFileFolder, String logPropertyFile, String configurationFolder) {

        if (StringUtils.isNotBlank(logFileFolder)) {
            System.setProperty(PROPERTY_LOG_FOLDER, logFileFolder);
        }
        if (StringUtils.isBlank(logPropertyFile)) {
            LOG.info("Log configuration file is not set.");
            return;
        }

        File f = new File(logPropertyFile);
        if (!f.exists()) {
            LOG.info("Log configuration file: [{}] not exists.", f.getAbsolutePath());
            f = new File(configurationFolder, logPropertyFile);
            LOG.info("Try with the configuration file path: [{}].", f.getAbsolutePath());
        }
        // if configuration file exist update configuration
        if (f.exists()) {
            setLogConfiguration(f);
        } else {
            LOG.info("File path: [{}] does not exists.", f.getAbsolutePath());
        }
    }

    public static void setLogConfiguration(File configurationFile) {
        LOG.info("Set log configuration properties from the file: [{}]", configurationFile.getAbsolutePath());
        try (InputStream configStream = new FileInputStream(configurationFile)) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(configStream); // loads logback file
        } catch (IOException | JoranException e) {
            LOG.info("Error occurred while loading LOG configuration.", e);
        }
    }

    public static Properties getFileProperties() {
        return getFileProperties(PROPERTY_FILE);
    }

    public static Properties getFileProperties(String filename) {
        LOG.info("Start read file properties from [{}]", filename);
        InputStream is = PropertyInitialization.class.getResourceAsStream(filename);
        if (is == null) {
            LOG.info("File '[{}]' not found in classpath, read [{}].", filename, PROPERTY_FILE_BACKUP);
            is = PropertyInitialization.class.getResourceAsStream(PROPERTY_FILE_BACKUP);
        }
        Properties connectionProp = new Properties();
        try {
            connectionProp.load(is);
        } catch (IOException e) {
            LOG.error("IOException occurred while reading properties", e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred  while reading properties.", e.getMessage());
        }
        // update deprecated values and return properties:
        return updateDeprecatedValues(connectionProp);
    }

    /**
     * Method validates if new value for deprecated value is already set. If not it set the value from deprecated property if exists!
     *
     * @param properties
     * @return
     */
    public static Properties updateDeprecatedValues(Properties properties) {

        updateDeprecatedProperty(properties, EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, CLIENT_CERT_HEADER_ENABLED_DEPRECATED);

        return properties;
    }

    public static Properties updateDeprecatedProperty(Properties properties, SMPPropertyEnum newProperty, SMPPropertyEnum deprecatedProperty) {
        if (!properties.containsKey(newProperty.getProperty())
                && properties.containsKey(deprecatedProperty.getProperty())) {

            properties.setProperty(newProperty.getProperty(),
                    properties.getProperty(deprecatedProperty.getProperty()));
        }
        return properties;
    }


}
