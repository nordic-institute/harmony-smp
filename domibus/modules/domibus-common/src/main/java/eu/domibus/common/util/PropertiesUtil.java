package eu.domibus.common.util;

import org.apache.log4j.Logger;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {

    private static final Logger logger = Logger.getLogger(PropertiesUtil.class);

    private static Properties properties = null;

    public static final String CONFIG_CLASS_PATH = "domibus.properties";

    public static final String ALT_CONFIG_CLASS_PATH = "domibus.default.properties";

    /**
     * If properties haven't been loaded yet, loads it from context passed as parameter or default file location in case context==null.
     *
     * @return the loaded properties or null if there was any problem loading the properties file.
     */
    public static Properties getProperties() {
        if (properties == null) {
            try {
                logger.debug("Loading properties from " + CONFIG_CLASS_PATH + "...");
                properties = load(CONFIG_CLASS_PATH);
                if (properties == null) {
                    logger.warn("No configuration file " + CONFIG_CLASS_PATH + " found");
                    logger.debug("Loading properties from " + CONFIG_CLASS_PATH + "...");
                    properties = load(ALT_CONFIG_CLASS_PATH);

                }
                if (properties == null) {
                    logger.error("No configuration file found. Default values will be used. Be warned that the application may not behave correctly if you don't configure the " + CONFIG_CLASS_PATH + " and put it in the classpath");
                }
            } catch (Exception e) {
                properties = null;
            }
        }

        return properties;

    }

    private static Properties load(String configFile) {
        Properties result = null;
        try {
            InputStream stream = PropertiesUtil.class.getResourceAsStream(configFile);
            result = new Properties();
            result.load(stream);
            stream.close();
        } catch (Exception exc) {
            try {
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(configFile);
                result = new Properties();
                result.load(stream);
                stream.close();
            } catch (Exception exc1) {
                try {
                    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
                    result = new Properties();
                    result.load(stream);
                    stream.close();
                } catch (Exception exc2) {
                    logger.debug("Couldn't load properties: " + configFile);
                }
            }
        }

        if (result != null) {
            logger.debug("Properties loaded from " + configFile);
        }

        return result;
    }
}
