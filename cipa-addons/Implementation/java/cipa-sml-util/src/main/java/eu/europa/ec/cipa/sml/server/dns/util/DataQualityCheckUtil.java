package eu.europa.ec.cipa.sml.server.dns.util;

import eu.europa.ec.cipa.sml.server.dns.impl.DNSClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ZoneTransferException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

/**
 * Created by feriaad on 09/06/2015.
 * <p/>
 * This class can be used to test data consistency between the DNS and the database
 */
public class DataQualityCheckUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityCheckUtil.class);

    private static Properties properties;

    private static String ACCEPTANCE = "ACC";
    private static String PRODUCTION = "PROD";

    private static final String CONFIG_ZONE = "dnsClient.zone";
    private static final String CONFIG_SML_ZONE_NAME = "dnsClient.smlzonename";
    private static final String CONFIG_SERVER = "dnsClient.server";
    private static final String CONFIG_TTL = "dnsClient.ttl";
    private static final String CONFIG_JDBC_DRIVER = "jdbc.driver";
    private static final String CONFIG_JDBC_URL = "jdbc.url";
    private static final String CONFIG_JDBC_USER = "jdbc.user";
    private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

    public static void main(String[] args) throws Throwable {
        if (args == null || args.length != 1) {
            logger.info("syntax : 'java -jar cipa-sml-util [environment]'.  Values allowed for [environment] : ACC, PROD");
        } else {
            if (!ACCEPTANCE.equalsIgnoreCase(args[0]) && !PRODUCTION.equalsIgnoreCase(args[0])) {
                logger.info("syntax : 'java -jar cipa-sml-util [environment]'.  Values allowed for [environment] : ACC, PROD");
            } else {
                properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(args[0].toLowerCase() + ".config.properties"));
                checkDataConsistency();
            }
        }
    }

    private static void checkDataConsistency() throws IOException, ZoneTransferException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        final String sServer = properties.getProperty(CONFIG_SERVER);
        final String sZoneName = properties.getProperty(CONFIG_ZONE);
        final String sSMLZoneName = properties.getProperty(CONFIG_SML_ZONE_NAME);
        final int nTTL = new Integer(properties.getProperty(CONFIG_TTL));
        final DNSClientImpl aDNSClient = new DNSClientImpl(sServer, sZoneName, sSMLZoneName, nTTL);

        // Get all domain records
        final List<Record> aAllRecords = aDNSClient.getAllRecords();

        // Filter the ones for the current SML domain only
        final List<Record> aFilteredRecords = new ArrayList<>();
        {
            for (final Record aRecord : aAllRecords) {
                if (aRecord instanceof ARecord || aRecord instanceof CNAMERecord) {
                    if (aRecord.getName().toString().contains(sZoneName))
                        aFilteredRecords.add(aRecord);
                }
            }
        }

        logger.info("Retrieving entries from the DNS...");

        List<String> participantHashInDNSList = new ArrayList<>();
        List<String> smpInDNSList = new ArrayList<>();

        // check records in the DNS are in the DB. As the participants are encoded in MD5, we can only check the SMP and count the participants
        for (Record rec : aFilteredRecords) {
            String name = rec.getName().toString().toLowerCase();
            if (!name.equalsIgnoreCase(sZoneName + ".")) {
                if (!name.startsWith("b-")) {
                    smpInDNSList.add(rec.getName().toString());
                } else {
                    participantHashInDNSList.add(rec.getName().toString());
                }
            }
        }

        logger.info("There are " + smpInDNSList.size() + " SMP in the DNS");
        logger.info("There are " + participantHashInDNSList.size() + " participants in the DNS");

        Map<String, String> participantHashInDBMap = new HashMap<>();
        List<String> smpInDBList = new ArrayList<>();


        logger.info("Retrieving entries from the database...");

        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        Statement stmt = conn.createStatement();
        String sql = "SELECT SMP_ID FROM SERVICE_METADATA_PUBLISHER";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            smpInDBList.add(rs.getString("SMP_ID") + "." + sSMLZoneName);
        }
        rs.close();
        stmt.close();

        stmt = conn.createStatement();
        sql = "SELECT REC_VALUE, SCHEME FROM RECIPIENT_PART_IDENTIFIER";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String hash = calculateMD5(rs.getString("REC_VALUE"));
            String dnsName = "B-" + hash + "." + rs.getString("SCHEME") + "." + sZoneName + ".";
            participantHashInDBMap.put(rs.getString("REC_VALUE"), dnsName);
        }
        rs.close();
        stmt.close();
        conn.close();

        logger.info("There are " + smpInDBList.size() + " SMP in the database");
        logger.info("There are " + participantHashInDBMap.keySet().size() + " participants in the database");


        int count = 0;
        logger.info("Searching for data inconsistency...");
        for (String smpDNS : smpInDNSList) {
            if (!smpInDBList.contains(smpDNS)) {
                logger.warn("The SMP " + smpDNS + " is in the DNS but is not in the database");
                count++;
            }
        }

        for (String smpDB : smpInDBList) {
            if (!smpInDNSList.contains(smpDB)) {
                logger.warn("The SMP " + smpDB + " is in the database but is not in the DNS");
                count++;
            }
        }


        for (String participant : participantHashInDNSList) {
            if (!participantHashInDBMap.values().contains(participant)) {
                logger.warn("The participant with hash " + participant + " is in the DNS but is not in the database");
                count++;
            }
        }

        for (String participant : participantHashInDBMap.keySet()) {
            if (!participantHashInDNSList.contains(participantHashInDBMap.get(participant))) {
                logger.warn("The participant " + participant + "(dnsName= " + participantHashInDBMap.get(participant) + ") is in the database but is not in the DNS");
                count++;
            }
        }

        if (count == 0) {
            logger.info("No data inconsistency between the DNS and the database. Congratulations!");
        } else {
            logger.info("There was " + count + " difference(s) between the database and the DNS. Please take appropriate actions to solve the problem");
        }
    }

    private static String calculateMD5(String participantId) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(participantId.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
}
