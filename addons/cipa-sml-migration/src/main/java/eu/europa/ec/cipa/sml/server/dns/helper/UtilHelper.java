package eu.europa.ec.cipa.sml.server.dns.helper;

import eu.europa.ec.cipa.sml.server.dns.FixEdelivery907;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by rodrfla on 24/08/2016.
 */
public class UtilHelper {

    private static Properties properties;
    private static final String ENVIRONMENT = "acc.config.properties";

    static {
        try {
            properties = load(ENVIRONMENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String addDomainExtraCharacter() throws IOException {
        String domain = UtilHelper.getProperty("domain");
        if (!domain.substring(domain.length() - 1).equals(".")) {
            return domain + ".";
        }
        return domain;
    }

    public static String removeDomainExtraCharacter(String domain) {
        return domain.replace(".eu.!", ".eu!");
    }

    public static String getProperty(String key) throws IOException {
        return properties.getProperty(key);
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        Connection conn = null;
        Class.forName(properties.getProperty("jdbc.driver.origin"));
        conn = DriverManager.getConnection(properties.getProperty("jdbc.connection.origin"));
        conn.setReadOnly(true);

        return conn;
    }

    private static Properties load(String configFile) throws IOException {
        Properties result = null;
        try {
            InputStream stream = UtilHelper.class.getResourceAsStream(configFile);
            Properties props = new Properties();
            props.load(stream);
            stream.close();
            result = props;
        } catch (Exception exc) {
            try {
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(configFile);
                Properties props = new Properties();
                props.load(stream);
                stream.close();
                result = props;
            } catch (Exception exc1) {
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
                Properties props = new Properties();
                props.load(stream);
                stream.close();
                result = props;
            }
        }

        return result;
    }
}
