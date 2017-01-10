package eu.europa.ec.cipa.sml.server.dns.util;

import eu.europa.ec.cipa.sml.server.dns.DNSClientFactory;
import eu.europa.ec.cipa.sml.server.dns.HashUtil;
import eu.europa.ec.cipa.sml.server.dns.IDNSClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xbill.DNS.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.*;
import java.util.*;

/**
 * Created by rodrfla on 30/08/2016.
 * <p>
 * This class can be used to test data consistency between the DNS and the database
 */
public class DataQualityCheckSHA256Util {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityCheckSHA256Util.class);

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
            logger.info("syntax : 'java -jar cipa-sml-util.jar [environment] [component]'.  Values allowed for [environment] : ACC, PROD. Values allowed for [component] : SML, BDMSL");
        } else {
            if (!ACCEPTANCE.equalsIgnoreCase(args[0]) && !PRODUCTION.equalsIgnoreCase(args[0]) && !BDMSL.equalsIgnoreCase(args[1]) && !SML.equalsIgnoreCase(args[1])) {
                logger.info("syntax : 'java -jar cipa-sml-util.jar [environment] [component]'.  Values allowed for [environment] : ACC, PROD. Values allowed for [component] : SML, BDMSL");
            } else {
                properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(args[0].toLowerCase() + ".config.properties"));
                component = args[1].toUpperCase();
                checkDataConsistency();
            }
        }
    }

    private static void checkDataConsistency() throws Exception {
        logger.info("Checking Data Consistency");

        Map<String, Object> dnsEntries = getDNSEntries();
        List<String> smpInDNSList = (List<String>) dnsEntries.get("smpInDNSList");
        int cnameaRecordsCount = (Integer) dnsEntries.get("cnameaRecordsCount");
        int naptrsCount = (Integer) dnsEntries.get("naptrsCount");
        List<String> participantHashInDNSList = (List<String>) dnsEntries.get("participantHashInDNSList");

        logger.info("There are " + smpInDNSList.size() + " SMP in the DNS");
        logger.info("There are " + cnameaRecordsCount + " CNAME records for participants in the DNS");
        if (BDMSL.equalsIgnoreCase(component)) {
            logger.info("There are " + naptrsCount + " NAPTR records for participants in the DNS");
        }

        logger.info("Searching for data inconsistency...");
        int count = 0;
        count += checkSMPs(smpInDNSList);
        count += checkParticipants(participantHashInDNSList);

        logger.info("...");

        if (count == 0) {
            logger.info("No data inconsistency between the DNS and the database. Congratulations!");
        } else {
            logger.info("There was " + count + " difference(s) between the database and the DNS. Please take appropriate actions to solve the problem");
        }
    }

    private static Map<String, Object> getDNSEntries() throws IOException, ZoneTransferException {
        logger.info("Retrieving entries from the DNS...");

        Map<String, Object> dnsEntries = new HashMap<>();

        final String sServer = properties.getProperty(CONFIG_SERVER);
        final String sZoneName = properties.getProperty(CONFIG_ZONE);

        // Get all domain records
        final List<Record> aAllRecords = getAllRecords(sZoneName, sServer);
        List<String> participantHashInDNSList = new ArrayList<>();

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

        List<String> smpInDNSList = new ArrayList<>();
        // check records in the DNS are in the DB. As the participants are encoded in MD5, we can only check the SMP and count the participants
        int naptrsCount = 0;
        int cnameaRecordsCount = 0;
        for (Record rec : aFilteredRecords) {
            String name = rec.getName().toString().toLowerCase();
            if (!name.equalsIgnoreCase(sZoneName + ".")) {
                if (rec instanceof CNAMERecord || rec instanceof ARecord) {
                    if (name.startsWith("b-")) {
                        ++cnameaRecordsCount;
                        participantHashInDNSList.add(rec.getName().toString());
                    } else if (name.contains(".publisher.")) {
                        smpInDNSList.add(rec.getName().toString());
                    } else {
                        // log warning -> only 4 records will normally end there like "edelivery.tech.ec.europa.eu.  1800    IN      A       147.67.2.54"
                        logger.warn("Exceptional condition: " + rec);
                    }
                } else if (rec instanceof NAPTRRecord) {
                    ++naptrsCount;
                    participantHashInDNSList.add(rec.getName().toString());
                }
            }
        }
        dnsEntries.put("smpInDNSList", smpInDNSList);
        dnsEntries.put("naptrsCount", naptrsCount);
        dnsEntries.put("participantHashInDNSList", participantHashInDNSList);
        dnsEntries.put("cnameaRecordsCount", cnameaRecordsCount);
        return dnsEntries;
    }

    private static String addDomainExtraCharacter(String domain) throws IOException {
        if (!domain.substring(domain.length() - 1).equals(".")) {
            return domain + ".";
        }
        return domain;
    }

    private static List<Participant> getParticipantsFromDB() throws
            IOException, ZoneTransferException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        logger.info("Retrieving Participants from the database...");
        final String sZoneName = properties.getProperty(CONFIG_ZONE);
        List<Participant> participantsHashInDB = new ArrayList<>();
        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        conn.setAutoCommit(false);
        conn.setReadOnly(true);
        Statement stmt = conn.createStatement();
        String sql = "SELECT SMP_ID FROM SERVICE_METADATA_PUBLISHER";
        String participantColumn = "REC_VALUE";
        sql = "SELECT " + participantColumn + ", SCHEME,SMP_ID FROM RECIPIENT_PART_IDENTIFIER";

        if (BDMSL.equalsIgnoreCase(component)) {
            participantColumn = "PARTICIPANT_ID";
            sql = "SELECT " + participantColumn + ", SCHEME,FK_SMP_ID FROM BDMSL_PARTICIPANT_IDENTIFIER";
        }

        ResultSet rs = stmt.executeQuery(sql);
        int participantsCount = 0;
        while (rs.next()) {
            ++participantsCount;
            // MD5
            Participant participant = new Participant();
            String participantId = rs.getString(participantColumn);
            String hashMD5 = HashUtil.getMD5Hash(participantId.toLowerCase(Locale.US));
            String scheme = rs.getString("SCHEME");
            String dnsName = "B-" + hashMD5 + "." + scheme + "." + sZoneName;
            String smpId = "";

            if (BDMSL.equalsIgnoreCase(component)) {
                smpId = rs.getString("FK_SMP_ID");
            } else {
                smpId = rs.getString("SMP_ID");
            }
            dnsName = addDomainExtraCharacter(dnsName);
            participant.setMd5Code(dnsName);
            participant.setScheme(scheme);
            participant.setParticipantId(participantId);
            participant.setSmpId(smpId);

            if (BDMSL.equalsIgnoreCase(component)) {
                // SHA256
                String hashSha256 = HashUtil.getSHA256HashBase32(participantId.toLowerCase(Locale.US));
                dnsName = hashSha256 + "." + scheme + "." + sZoneName;
                dnsName = addDomainExtraCharacter(dnsName);
                participant.setSha256Code(dnsName);
            }
            participantsHashInDB.add(participant);

        }
        rs.close();
        stmt.close();
        conn.close();

        logger.info("There are " + participantsCount + " participants in the database");
        return participantsHashInDB;
    }

    private static List<String> getSMPsFromDB() throws
            IOException, ZoneTransferException, ClassNotFoundException, SQLException {
        logger.info("Retrieving SMPs from the database...");
        final String sSMLZoneName = properties.getProperty(CONFIG_SML_ZONE_NAME);
        List<String> smpInDBList = new ArrayList<>();
        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);
        conn.setReadOnly(true);
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
        logger.info("There are " + smpInDBList.size() + " SMP in the database");
        return smpInDBList;
    }

    private static IDNSClient getDnsClient() {
        return DNSClientFactory.getSimpleInstace();
    }


    private static List<Record> getAllRecords(String dnsZone, String dnsServer) throws
            IOException, ZoneTransferException {
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

    private static int checkSMPs(List<String> smpInDNSList) throws ClassNotFoundException, SQLException, ZoneTransferException, IOException {
        List<String> smpInDBList = getSMPsFromDB();
        int count = 0;
        logger.info(" --- SMP is in the DNS but is not in the Database ---");
        for (String smpDNS : smpInDNSList) {
            if (!smpInDBList.contains(smpDNS)) {
                logger.warn(" >>> The SMP " + smpDNS + " is in the DNS but is not in the database");
                count++;
            }
        }

        for (String smpDB : smpInDBList) {
            if (!smpInDNSList.contains(smpDB)) {
                logger.warn(" >>> The SMP " + smpDB + " is in the database but is not in the DNS");
                count++;
            }
        }
        return count;
    }

    private static int checkParticipants(List<String> participantHashInDNSList) throws Exception {
        int count = 0;
        List<Participant> participantsHashInDB = getParticipantsFromDB();
        List<Record> recordsToCreate = new ArrayList<>();
        logger.info(" --- SMP Participants(CNAME, A, NAPTR) - Data is in the DNS but is not in the Database ---");
        boolean hasMd5 = false;
        boolean hasSHA256 = false;
        for (String participantId : participantHashInDNSList) {
            for (Participant participant : participantsHashInDB) {
                if (participant.getMd5Code().equals(participantId)) {
                    hasMd5 = true;
                }
                if (participant.getSha256Code().equals(participantId)) {
                    hasSHA256 = true;
                }
            }

            if (!hasMd5 && participantId.contains("B-") || !hasSHA256 && !participantId.contains("B-")) {
                if (!hasMd5 && participantId.contains("B-")) {
                    logger.warn(" >>> The participant with hash MD5 " + participantId + " is in the DNS but is not in the database");
                    count++;
                }
                if (!hasSHA256 && !participantId.contains("B-")) {
                    logger.warn(" >>> The participant with hash SHA256 " + participantId + " is in the DNS but is not in the database");
                    count++;
                }
                //   count++;
            }
            hasMd5 = false;
            hasSHA256 = false;
        }

        hasMd5 = false;
        hasSHA256 = false;
        logger.info(" --- SMP Participants(CNAME, A, NAPTR) - Data is in the database but is not in the DNS ---");
        for (Participant participant : participantsHashInDB) {
            for (String participantId : participantHashInDNSList) {
                if (participant.getMd5Code().equals(participantId)) {
                    hasMd5 = true;
                }
                if (participant.getSha256Code().equals(participantId)) {
                    hasSHA256 = true;
                }
            }

            if (!hasMd5 || !hasSHA256) {
                if (!hasMd5) {
                    logger.warn(" >>> The participant MD5 " + participant.getParticipantId() + "(dnsName= " + participant.getMd5Code() + ") is in the database but is not in the DNS");
                    count++;
                }
                if (!hasSHA256) {
                    logger.warn(" >>> The participant SHA256 " + participant.getParticipantId() + "(dnsName= " + participant.getSha256Code() + ") is in the database but is not in the DNS");
                    count++;
                }
            }
            hasMd5 = false;
            hasSHA256 = false;
        }
        return count;
    }

    public static class Participant {
        private String participantId;
        private String scheme;
        private String smpId;
        private String md5Code;
        private String sha256Code;

        public Participant() {

        }

        public String getSha256Code() {
            return sha256Code;
        }

        public void setSha256Code(String sha256Code) {
            this.sha256Code = sha256Code;
        }

        public String getMd5Code() {
            return md5Code;
        }

        public void setMd5Code(String md5Code) {
            this.md5Code = md5Code;
        }

        public String getParticipantId() {
            return participantId;
        }

        public void setParticipantId(String participantId) {
            this.participantId = participantId;
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getSmpId() {
            return smpId;
        }

        public void setSmpId(String smpId) {
            this.smpId = smpId;
        }
    }
}