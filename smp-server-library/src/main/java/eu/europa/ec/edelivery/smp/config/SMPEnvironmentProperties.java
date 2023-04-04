package eu.europa.ec.edelivery.smp.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.config.init.DatabaseConnectionProperties;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED;

/**
 * DomiSMP environment property initialization.
 * The class is "POJO" initialize the properties at startup
 * in the following order:
 *
 * <ol>
 *  <li>Java System properties (System.getProperties()).</li>
 *  <li>Operational system environment variables.</li>
 *  <li>Application properties outside of your packaged jar (smp.config.properties).</li>
 *  <li>Default properties from the SMPEnvPropertyEnum</li>
 * </ol>
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPEnvironmentProperties implements DatabaseConnectionProperties {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPEnvironmentProperties.class);
    private static final String CLASSPATH_PROPERTIES = "/smp.config.properties";

    Properties extInitFileProperties = null;
    Properties extEnvFileProperties = null;

    Properties classPathEnvFileProperties = null;

    ClassLoader classLoader = null;

    private static SMPEnvironmentProperties instance;

    /**
     * Get the instance of the class. The first call of method initialize the instance
     *
     * @return the class instance
     */
    public static SMPEnvironmentProperties getInstance() {
        if (instance == null) {
            instance = createInstance(null);
        }
        return instance;
    }

    public static SMPEnvironmentProperties createInstance(ClassLoader classLoader) {
        return new SMPEnvironmentProperties(classLoader);
    }


    protected SMPEnvironmentProperties(ClassLoader classLoader) {
        this.classLoader = classLoader;
        init();
    }

    /**
     * Initialize the default properties in to the cache for faster lookup of the default values
     */
    private void init() {
        LOG.debug("Initialize DomiSMP environment properties");
        classPathEnvFileProperties = readProperties(CLASSPATH_PROPERTIES, true);
        if (classPathEnvFileProperties != null) {
            LOG.debug("------ Print classPathEnvFileProperties ------");
            classPathEnvFileProperties.entrySet().stream().forEach(e -> LOG.info(e.getKey() + ":" + e.getValue()));
        }

        // get init file property
        String extInitPropFilePath = getEnvPropertyValue(INIT_CONFIGURATION_FILE);

        extInitFileProperties = readProperties(extInitPropFilePath, false);
        if (extInitFileProperties != null) {
            LOG.debug("------ Print classPathEnvFileProperties ------");
            extInitFileProperties.entrySet().stream().forEach(e -> LOG.info(e.getKey() + ":" + e.getValue()));
        }

        String extAppFilePath = getEnvPropertyValue(CONFIGURATION_FILE);
        extEnvFileProperties = readProperties(extAppFilePath, false);
        if (extInitFileProperties != null) {
            LOG.debug("------ Print extInitFileProperties ------");
            extEnvFileProperties.entrySet().stream().forEach(e -> LOG.info(e.getKey() + ":" + e.getValue()));
        }


        // update log configuration
        updateLogConfiguration(getEnvPropertyValue(LOG_FOLDER),
                getEnvPropertyValue(LOG_CONFIGURATION_FILE));
    }

    protected Properties readProperties(String path, boolean inClasspath) {

        if (inClasspath) {
            LOG.info("Read properties from classpath:[{}] with classloader: [{}]", path, classLoader);
            return readProperties(classLoader == null ? SMPEnvironmentProperties.class.getResourceAsStream(path) : classLoader.getResourceAsStream(path));
        }

        Path initFilePath = Paths.get(path);
        if (Files.exists(initFilePath)) {
            try (FileInputStream fos = new FileInputStream(initFilePath.toFile())) {
                return readProperties(fos);
            } catch (IOException e) {
                LOG.error("Can not read the init property file [{}]", initFilePath);
            }
        }
        return null;
    }


    protected Properties readProperties(InputStream isProperties) {
        if (isProperties == null) {
            LOG.info("Null input stream for properties");
            return null;
        }
        try {
            Properties properties = new Properties();
            properties.load(isProperties);
            return updateDeprecatedValues(properties);
        } catch (IOException e) {
            LOG.error("Can not read properties!");

        }
        return null;
    }


    /**
     * Get configuration properties in the following order
     * <ol>
     *  <li>Java System properties (System.getProperties()).</li>
     *  <li>Operational system environment variables.</li>
     *  <li>Application properties outside of your packaged jar (smp.config.properties).</li>
     *  <li>Default properties from the SMPEnvPropertyEnum</li>
     * </ol>
     *
     * @return property value or null
     */
    public String getEnvPropertyValue(SMPEnvPropertyEnum property) {
        return getPropertyValue(property.getProperty(), property.getDefValue());
    }

    public String getApplicationInitPropertyValue(SMPPropertyEnum property) {
        return getPropertyValue(property.getProperty(), property.getDefValue());
    }

    /**
     * Get configuration properties in the following order
     * <ol>
     *  <li>Java System properties (System.getProperties()).</li>
     *  <li>Operational system environment variables.</li>
     *  <li>Application properties outside of your packaged jar (smp.config.properties).</li>
     *  <li>Default properties from the SMPEnvPropertyEnum</li>
     * </ol>
     *
     * @return property value or null
     */
    public String getPropertyValue(String propertyName, String defValue) {
        if (System.getProperties().containsKey(propertyName)) {
            String propVal = System.getProperty(propertyName);
            LOG.debug("Got system property: [{}] with value: [{}].", propertyName, propVal);
            return propVal;
        }

        if (System.getenv().containsKey(propertyName)) {
            String propVal = System.getenv(propertyName);
            LOG.debug("Got OS environment property: [{}] with value: [{}].", propertyName, propVal);
            return propVal;
        }

        if (extInitFileProperties != null && extInitFileProperties.containsKey(propertyName)) {
            String propVal = extInitFileProperties.getProperty(propertyName);
            LOG.debug("Got external init property: [{}] with value: [{}].", propertyName, propVal);
            return propVal;
        }

        if (extEnvFileProperties != null && extEnvFileProperties.containsKey(propertyName)) {
            String propVal = extEnvFileProperties.getProperty(propertyName);
            LOG.debug("Got external configuration property: [{}] with value: [{}].", propertyName, propVal);
            return propVal;
        }
        if (classPathEnvFileProperties != null && classPathEnvFileProperties.containsKey(propertyName)) {
            String propVal = classPathEnvFileProperties.getProperty(propertyName);
            LOG.debug("Got classpath configuration property: [{}] with value: [{}].", propertyName, propVal);
            return propVal;
        }
        // get default value.
        return defValue;
    }

    public static void updateLogConfiguration(String logFileFolder, String logPropertyFile) {
        LOG.debug("Update logging configuration");
        if (StringUtils.isNotBlank(logFileFolder)) {
            LOG.info("Set logging folder [{}].", logFileFolder);
            System.setProperty(LOG_FOLDER.getProperty(), logFileFolder);
        }
        if (StringUtils.isBlank(logPropertyFile)) {
            LOG.info("Log configuration file is not set. Use default logging configuration!");
            return;
        }

        File f = new File(logPropertyFile);
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

    /**
     * Method validates if new value for deprecated value is already set. If not it set the value from deprecated property if exists!
     *
     * @param properties
     * @return
     */
    public static Properties updateDeprecatedValues(Properties properties) {
        if (properties == null) {
            return null;
        }
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

    public Properties getEnvProperties() {
        Properties properties = new Properties();
        Arrays.stream(values()).forEach(prop -> properties.setProperty(prop.getProperty(), StringUtils.getIfEmpty(getEnvPropertyValue(prop), () -> "")));
        return properties;
    }

    @Override
    public String getDatabaseJNDI() {
        return getEnvPropertyValue(DATABASE_JNDI);
    }

    @Override
    public String getJdbcUrl() {
        return getEnvPropertyValue(JDBC_URL);
    }

    @Override
    public String getJdbcDriver() {
        return getEnvPropertyValue(JDBC_DRIVER);
    }

    @Override
    public String getJdbcUsername() {
        return getEnvPropertyValue(JDBC_USER);
    }

    @Override
    public String getJdbcPassword() {
        return getEnvPropertyValue(JDBC_PASSWORD);
    }

    @Override
    public String getDatabaseDialect() {
        return getEnvPropertyValue(HIBERNATE_DIALECT);
    }

    /**
     * For the precaution the mode must be in development mode to enable  create ddl!
     * @return
     */
    @Override
    public boolean updateDatabaseEnabled() {
        return Boolean.parseBoolean(getEnvPropertyValue(SMP_MODE_DEVELOPMENT)) &&
                Boolean.parseBoolean(getEnvPropertyValue(DATABASE_CREATE_DDL));
    }

    @Override
    public boolean isShowSqlEnabled() {
        return Boolean.parseBoolean(getEnvPropertyValue(SMP_MODE_DEVELOPMENT)) &&
                Boolean.parseBoolean(getEnvPropertyValue(DATABASE_SHOW_SQL));
    }




    public boolean isSMPStartupInDevMode(){
        return Boolean.parseBoolean(getEnvPropertyValue(SMP_MODE_DEVELOPMENT));
    }
}
