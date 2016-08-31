package eu.europa.ec.cipa.sml.server.dns.util;

import eu.europa.ec.cipa.sml.server.dns.HashUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.*;
import java.util.*;

/**
 * Created by feriaad on 09/06/2015.
 * <p/>
 * This class can be used to test data consistency between the DNS and the database
 */
public class DataQualityChecSHA224Util {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityChecSHA224Util.class);

    private static Properties properties;

    private static String ACCEPTANCE = "ACC";
    private static String PRODUCTION = "PROD";

    private static String BDMSL = "BDMSL";
    private static String SML = "SML";

    private static String component = SML;

    private static final String CONFIG_ZONE = "dnsClient.zone";
    private static final String CONFIG_SML_ZONE_NAME = "dnsClient.smlzonename";
    private static final String CONFIG_SERVER = "dnsClient.server";
    private static final String CONFIG_JDBC_DRIVER = "jdbc.driver";
    private static final String CONFIG_JDBC_URL = "jdbc.url";
    private static final String CONFIG_JDBC_USER = "jdbc.user";
    private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

    public static void main(String[] args) throws Throwable {
        Security.addProvider(new BouncyCastleProvider());
        if (args == null || args.length != 2) {
            logger.info("syntax : 'java -jar cipa-sml-util [environment] [component]'.  Values allowed for [environment] : ACC, PROD. Values allowed for [component] : SML, BDMSL");
        } else {
            if (!ACCEPTANCE.equalsIgnoreCase(args[0]) && !PRODUCTION.equalsIgnoreCase(args[0]) && !BDMSL.equalsIgnoreCase(args[1]) && !SML.equalsIgnoreCase(args[1])) {
                logger.info("syntax : 'java -jar cipa-sml-util [environment] [component]'.  Values allowed for [environment] : ACC, PROD. Values allowed for [component] : SML, BDMSL");
            } else {
                properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(args[0].toLowerCase() + ".config.properties"));
                component = args[1].toUpperCase();
                checkDataConsistency();
            }
        }
    }

    private static void checkDataConsistency() throws IOException, ZoneTransferException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        final String sServer = properties.getProperty(CONFIG_SERVER);
        final String sZoneName = properties.getProperty(CONFIG_ZONE);
        final String sSMLZoneName = properties.getProperty(CONFIG_SML_ZONE_NAME);

        // Get all domain records
        final List<Record> aAllRecords = getAllRecords(sZoneName, sServer);
        List<String> participantHashMd5InDNSList = new ArrayList<>();
        List<String> participantHashSha224InDNSList = new ArrayList<>();

        // Filter the ones for the current SML domain only
        final List<Record> aFilteredRecords = new ArrayList<>();
        {
            for (final Record aRecord : aAllRecords) {
                if (aRecord instanceof ARecord || aRecord instanceof CNAMERecord || aRecord instanceof NAPTRRecord) {
                    if (aRecord.getName().toString().contains(sZoneName))
                        aFilteredRecords.add(aRecord);
                }
            }
        }

        logger.info("Retrieving entries from the DNS...");

        List<String> smpInDNSList = new ArrayList<>();

        // check records in the DNS are in the DB. As the participants are encoded in MD5, we can only check the SMP and count the participants
        for (Record rec : aFilteredRecords) {
            String name = rec.getName().toString().toLowerCase();
            if (!name.equalsIgnoreCase(sZoneName + ".")) {
                if (!name.startsWith("b-")) {
                    smpInDNSList.add(rec.getName().toString());
                } else {
                    if (rec instanceof ARecord || rec instanceof CNAMERecord) {
                        participantHashMd5InDNSList.add(rec.getName().toString());
                    } else if (rec instanceof NAPTRRecord) {
                        participantHashSha224InDNSList.add(rec.getName().toString());
                    }
                }
            }
        }

        logger.info("There are " + smpInDNSList.size() + " SMP in the DNS");
        logger.info("There are " + participantHashMd5InDNSList.size() + " CNAME records for participants in the DNS");
        if (BDMSL.equalsIgnoreCase(component)) {
            logger.info("There are " + participantHashSha224InDNSList.size() + " NAPTR records for participants in the DNS");
        }

        Map<String, String> participantHashMd5InDBMap = new HashMap<>();
        Map<String, String> participantHashSha224InDBMap = new HashMap<>();
        List<String> smpInDBList = new ArrayList<>();


        logger.info("Retrieving entries from the database...");

        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        Statement stmt = conn.createStatement();
        String sql = "SELECT SMP_ID FROM SERVICE_METADATA_PUBLISHER";

        if (BDMSL.equalsIgnoreCase(component)) {
            sql = "SELECT SMP_ID FROM BDMSL_SMP";
        }

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            smpInDBList.add(rs.getString("SMP_ID") + "." + sSMLZoneName);
        }
        rs.close();
        stmt.close();

        stmt = conn.createStatement();
        String participantColumn = "REC_VALUE";
        sql = "SELECT " + participantColumn + ", SCHEME FROM RECIPIENT_PART_IDENTIFIER";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            // MD5
            String participantId = rs.getString(participantColumn);
            String hashMD5 = HashUtil.getMD5Hash(participantId);
            String scheme = rs.getString("SCHEME");
            String dnsName = "B-" + hashMD5 + "." + scheme + "." + sZoneName + ".";
            participantHashMd5InDBMap.put(participantId, dnsName);

            if (BDMSL.equalsIgnoreCase(component)) {
                // SHA224
                String hashSha224 = HashUtil.getSHA224Hash(participantId);
                dnsName = "B-" + hashSha224 + "." + scheme + "." + sZoneName + ".";
                participantHashSha224InDBMap.put(participantId, dnsName);
            }
        }
        rs.close();
        stmt.close();
        conn.close();

        logger.info("There are " + smpInDBList.size() + " SMP in the database");
        int participantCount = participantHashMd5InDBMap.keySet().size();
        logger.info("There are " + participantCount + " participants in the database");


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


        count += checkRecords(participantHashMd5InDNSList, participantHashMd5InDBMap);

        if (BDMSL.equalsIgnoreCase(component)) {
            count += checkRecords(participantHashSha224InDNSList, participantHashSha224InDBMap);
        }

        if (count == 0) {
            logger.info("No data inconsistency between the DNS and the database. Congratulations!");
        } else {
            logger.info("There was " + count + " difference(s) between the database and the DNS. Please take appropriate actions to solve the problem");
        }
    }

    public static List<Record> getAllRecords(String dnsZone, String dnsServer) throws IOException, ZoneTransferException {
        // do zone transfer to get complete list..
        String dnsZoneName = dnsZone;
        // we need the full qualified dns-name
        if (!dnsZoneName.endsWith(".")) {
            dnsZoneName += '.';
        }
        final ZoneTransferIn xfr;
        List<Record> records;
        xfr = ZoneTransferIn.newAXFR(Name.fromString(dnsZoneName), dnsServer, null);
        records = xfr.run();

        return records;
    }

    private static int checkRecords(List<String> participantHashInDNSList, Map<String, String> participantHashInDBMap) {
        int count = 0;
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
        return count;
    }
}
