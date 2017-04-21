package eu.europa.ec.cipa.sml.server;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by rodrfla on 21/04/2017.
 */
public class FixEdelivery1990 {
    private static Properties properties;
    private static final String CONFIG_JDBC_DRIVER = "jdbc.driver";
    private static final String CONFIG_JDBC_URL = "jdbc.url";
    private static final String CONFIG_JDBC_USER = "jdbc.user";
    private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

    static {
        try {
            properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        generateSQLScript();
    }

    private static Connection getDBConnection() throws Exception {
        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        conn.setAutoCommit(false);
        conn.setReadOnly(true);
        return conn;
    }

    private static List<SerialNumber> getCertificates() throws Exception {
        Connection conn = getDBConnection();
        Statement stmt = conn.createStatement();
        String sql = "SELECT certificate_id FROM bdmsl_certificate WHERE certificate_id != 'unsecure-http-client' AND certificate_id != 'debug-insecure-client-http-only'";

        ResultSet rs = stmt.executeQuery(sql);
        List<SerialNumber> serialNumbers = new ArrayList<>();
        while (rs.next()) {
            String currentCertificateId = rs.getString("CERTIFICATE_ID");
            String currentSerialNumber = extractSerialNumberFromCertificateId(currentCertificateId);
            if (currentSerialNumber == null) {
                throw new Exception("Serial Number is null for Certificate Id" + currentCertificateId);
            }
            String newSerialNumber = convertStringtoPaddedString(currentSerialNumber);

            serialNumbers.add(new SerialNumber(currentCertificateId, currentCertificateId.replace(currentSerialNumber, newSerialNumber)));
        }
        return serialNumbers;
    }

    private static String extractSerialNumberFromCertificateId(String certificateId) throws Exception {
        String[] groups = certificateId.split(",");

        for (String group : Arrays.asList(groups)) {
            if (group.toUpperCase().startsWith("C=")) {
                return group.substring(5, group.length());
            }
        }
        return null;
    }

    private static String convertStringtoPaddedString(String str) throws Exception {
        String serialNumber = StringUtils.leftPad(str, 32, "0");
        if (serialNumber.length() > 32) {
            //throw new Exception("Serial number " + serialNumber + " is bigger than 32 chars - " + str);
            System.out.println("Serial number " + serialNumber + " is bigger than 32 chars - " + str);
        }
        return serialNumber;
    }

    private static void generateSQLScript() throws Exception {
        List<SerialNumber> serialNumbers = getCertificates();
        StringBuilder sqlScript = new StringBuilder();

        for (SerialNumber serialNumber : serialNumbers) {
            sqlScript.append("UPDATE bdmsl_certificate SET certificate_id = '" + serialNumber.getNewCertificateId() + "' WHERE certificate_id = '" + serialNumber.getCurrentCertificateId() + "'; \n");
        }
        sqlScript.append("commit;");

        System.out.println("######################### SQL SCRIPT GENERATED #########################");
        System.out.println(sqlScript.toString());
    }

    static class SerialNumber {
        private String currentCertificateId;
        private String newCertificateId;

        public SerialNumber(String currentCertificateId, String newCertificateId) {
            this.currentCertificateId = currentCertificateId;
            this.newCertificateId = newCertificateId;
        }

        public String getCurrentCertificateId() {
            return currentCertificateId;
        }

        public String getNewCertificateId() {
            return newCertificateId;
        }

        @Override
        public String toString() {
            return "SerialNumber{" +
                    "currentCertificateId='" + currentCertificateId + '\'' +
                    ", newCertificateId='" + newCertificateId + '\'' +
                    '}';
        }
    }
}
