package eu.europa.ec.cipa.sml.server.dns;

import com.mysql.jdbc.JDBC4PreparedStatement;
import eu.europa.ec.cipa.sml.server.dns.helper.HashUtil;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by feriaad on 20/10/2015.
 */
public class SMLToBDMSL {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SMLToBDMSL.class);

    private final static int DEFAULT_TTL_SECS = 60;

    private static final int MAX_RETRY = 5;

    private static Properties properties;

    private static Map<String, Integer> certificateIdMap = new HashMap<>();

    private static IDNSClient client = null;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException, ZoneTransferException, NoSuchAlgorithmException {
        Connection connOrigin = null;
        Connection connDestination = null;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        try {
            properties = loadProperties(args);

            Class.forName(properties.getProperty("jdbc.driver.origin"));
            Class.forName(properties.getProperty("jdbc.driver.destination"));
            connOrigin = DriverManager.getConnection(properties.getProperty("jdbc.connection.origin"));
            connOrigin.setReadOnly(true);
            connDestination = DriverManager.getConnection(properties.getProperty("jdbc.connection.destination"));
            connDestination.setAutoCommit(false);

            logger.info("clean up BDMSL tables");
            cleanup(connDestination);

            logger.info("Update certificate domain");
            updateBDMSLCertificateDomain(connDestination);

            logger.info("Select certificates from SML");
            List<String> certificates = selectSMLCertificates(connOrigin);
            logger.info(certificates.size() + " certificates to migrate");

            logger.info("Update certificates to BDMSL");
            updateBDMSLCertificates(connDestination, certificates);

            logger.info("Select SMP from SML");
            List<SMP> smpList = selectSMP(connOrigin);
            logger.info(smpList.size() + " SMP to migrate");

            logger.info("Update SMP to BDMSL");
            updateBDMSLSmp(connDestination, smpList);

            logger.info("Select Participants from SML");
            List<Participant> participantList = selectParticipants(connOrigin);
            logger.info(participantList.size() + " participants to migrate");

            logger.info("Update Participants to BDMSL");
            updateBDMSLParticipant(connDestination, participantList);

            logger.info("Select Migration data from SML");
            List<MigrationRecord> migrationRecordList = selectMigrationRecords(connOrigin);
            logger.info(migrationRecordList.size() + " MigrationRecord to migrate");

            logger.info("Update Migration data to BDMSL");
            updateBDMSLMigrationRecord(connDestination, connOrigin, migrationRecordList);

            logger.info("Commit migration data in destination database");
            connDestination.commit();

            logger.info("Update U-NAPTR DNS records");
            updateNAPTRRecords(connOrigin);

            printDNS();

        } finally {
            if (connDestination != null) {
                connDestination.close();
            }
            if (connOrigin != null) {
                connOrigin.close();
            }
        }
    }

    private static void updateNAPTRRecords(Connection connOrigin) throws IOException, ZoneTransferException, SQLException, NoSuchAlgorithmException {
        logger.info("Delete all NAPTR records");
        //deleteAllNaptrRecords();

        logger.info("get all participants from database");
        List<Participant> participants = selectParticipants(connOrigin);

        logger.info("Create NAPTR records for all participants");
        createNaptrRecordsForParticipants(participants);
    }

    private static void createNaptrRecordsForParticipants(List<Participant> participants) throws TextParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String dnsZoneName = properties.getProperty("domain");
        List<Record> updateList = new ArrayList<>();
        int i = 0;
        for (Participant participant : participants) {
            // Delete old host - if exists!
            final Name participantNaptrHost = createParticipantDNSNameObjectBDXL(participant.getRecValue(), participant.getScheme(), dnsZoneName + ".");

            final Name publisherHost = createPublisherDNSNameObject(participant.getSmpId(), dnsZoneName);
            NAPTRRecord naptrRecord;
            naptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", "Meta:SMP", "!^.*$!http://" + publisherHost + "!", Name.fromString("."));
            logger.debug("Creating NAPTRRecord in the DNS for SMP " + participantNaptrHost);
            updateList.add(naptrRecord);

            if (naptrRecord != null) {
                logger.info("Adding NAPTR record " + naptrRecord.toString());
            }

            i++;
            if (i == 400) {
                i = 0;
                //addRecordsWithRetry(updateList);
                //updateList.clear();
            }
        }
        if (!updateList.isEmpty()) {
            //addRecordsWithRetry(updateList);
        }
    }

    private static void addRecordsWithRetry(List<Record> recordList) {
        addRecordsWithRetry(recordList, 0);
    }

    private static void printDNS() throws IOException, ZoneTransferException {
        List<Record> recs = getDnsClient().getAllRecords();
        for (Record rec : recs) {
            logger.info(rec.toString());
        }
    }

    private static void addRecordsWithRetry(List<Record> recordList, int retry) {
        if (retry > 0) {
            logger.error("Retrying for the " + retry + " time");
        }
        if (retry < MAX_RETRY) {
            try {
                getDnsClient().addRecords(recordList);
            } catch (Throwable exc) {
                logger.warn("An error occurred while adding records to the DNS.", exc);
                addRecordsWithRetry(recordList, retry + 1);
            }
        } else {
            logger.error("FATAL: There was a fatal error. Impossible to add the records to the DNS after " + retry + " tentatives");
            System.exit(-1);
        }
    }

    private static Name createPublisherDNSNameObject(final String smpId, final String dnsZoneName) throws TextParseException {
        String smpDnsName = null;
        smpDnsName = smpId + "." + "publisher" + "." + dnsZoneName;
        return Name.fromString(smpDnsName);
    }

    private static Name createParticipantDNSNameObjectBDXL(String participantId, String scheme, String dnsZoneName) throws UnsupportedEncodingException, NoSuchAlgorithmException, TextParseException {
        String smpDnsName = null;
        if ("*".equals(participantId)) {
            smpDnsName = "*." + scheme + "." + dnsZoneName;
        } else {
            smpDnsName = HashUtil.getSHA256HashBase32(participantId.toLowerCase(Locale.US)) + "." + scheme + "." + dnsZoneName;
        }
        return Name.fromString(smpDnsName);
    }


    private static IDNSClient getDnsClient() {
        if (client == null) {
            client = DNSClientFactory.getSimpleInstace();
        }
        return client;
    }

    private static void deleteAllNaptrRecords() throws IOException, ZoneTransferException {
        List<Record> recs = getDnsClient().getAllRecords();
        System.out.println("Number of records on the list: " + recs.size());
        List<Record> out = new ArrayList<>();
        int i = 0;
        for (Record rec : recs) {
            if (rec.getType() == Type.NAPTR) {
                out.add(rec);
                i++;
                if (i == 400) {
                    deleteRecordsWithRetry(out);
                    out.clear();
                    i = 0;
                }
            }
        }
        if (!out.isEmpty()) {
            deleteRecordsWithRetry(out);
        }
    }

    private static void deleteRecordsWithRetry(List<Record> recordList) {
        deleteRecordsWithRetry(recordList, 0);
    }

    private static void deleteRecordsWithRetry(List<Record> recordList, int retry) {
        if (retry > 0) {
            logger.error("Retrying for the " + retry + " time");
        }
        if (retry < MAX_RETRY) {
            try {
                getDnsClient().deleteList(recordList);
            } catch (Throwable exc) {
                logger.warn("An error occurred while deleting records from the DNS.", exc);
                deleteRecordsWithRetry(recordList, retry + 1);
            }
        } else {
            logger.error("FATAL: There was a fatal error. Impossible to delete the records from the DNS after " + retry + " tentatives");
            System.exit(-1);
        }
    }

    private static void cleanup(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM bdmsl_allowed_wildcard");
        stmt.executeUpdate("DELETE FROM bdmsl_migrate");
        stmt.executeUpdate("DELETE FROM bdmsl_participant_identifier");
        stmt.executeUpdate("DELETE FROM bdmsl_smp");
        stmt.executeUpdate("DELETE FROM bdmsl_certificate");
        stmt.executeUpdate("DELETE FROM bdmsl_certificate_domain where root_certificate_alias like '%'");
        conn.commit();
    }

    private static void updateBDMSLMigrationRecord(Connection connDestination, Connection connOrigin, List<MigrationRecord> migrationRecordList) throws SQLException {
        String SQL = "INSERT INTO bdmsl_migrate(scheme, participant_id, migration_key, migrated, old_smp_id, new_smp_id, created_on, last_updated_on) values (?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = connDestination.prepareStatement(SQL);
        for (MigrationRecord migrationRecord : migrationRecordList) {
            pstmt.setString(1, migrationRecord.getScheme());
            pstmt.setString(2, migrationRecord.getRecValue());
            pstmt.setString(3, migrationRecord.getMigrationCode());
            pstmt.setBoolean(4, false);
            String currentSmpId = getSmpId(connOrigin, migrationRecord);
            pstmt.setString(5, "OLD_" + currentSmpId);
            pstmt.setString(6, currentSmpId);
            pstmt.setTimestamp(7, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            pstmt.setTimestamp(8, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            // pstmt.executeUpdate();
            pstmt.addBatch();
        }
        int[] count = pstmt.executeBatch();
        connDestination.commit();
    }

    private static String getSmpId(Connection conn, MigrationRecord migrationRecord) throws SQLException {
        String SQL = "SELECT SMP_ID FROM RECIPIENT_PART_IDENTIFIER WHERE SCHEME = ? and REC_VALUE = ?";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, migrationRecord.getScheme());
        pstmt.setString(2, migrationRecord.getRecValue());
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            return resultSet.getString("SMP_ID");
        }
        return null;
    }

    private static List<MigrationRecord> selectMigrationRecords(Connection conn) throws SQLException {
        String SQL = "SELECT REC_VALUE, MIGRATION_CODE, SCHEME FROM MIGRATE";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        ResultSet resultSet = pstmt.executeQuery();
        List<MigrationRecord> migrationRecordList = new ArrayList<>();
        while (resultSet.next()) {
            MigrationRecord migrationRecord = new MigrationRecord();
            migrationRecord.setMigrationCode(resultSet.getString("MIGRATION_CODE"));
            migrationRecord.setRecValue(resultSet.getString("REC_VALUE"));
            migrationRecord.setScheme(resultSet.getString("SCHEME"));
            migrationRecordList.add(migrationRecord);
        }
        return migrationRecordList;
    }

    private static void updateBDMSLParticipant(Connection conn, List<Participant> participantList) throws SQLException {
        String SQL = "INSERT INTO bdmsl_participant_identifier(participant_id, scheme, fk_smp_id, created_on, last_updated_on) values (?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        for (Participant participant : participantList) {
            pstmt.setString(1, participant.getRecValue());
            pstmt.setString(2, participant.getScheme());
            pstmt.setString(3, participant.getSmpId());
            pstmt.setTimestamp(4, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            pstmt.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            // pstmt.executeUpdate();
            pstmt.addBatch();
        }
        int[] count = pstmt.executeBatch();
        conn.commit();
    }

    private static List<Participant> selectParticipants(Connection conn) throws SQLException {
        String SQL = "SELECT REC_VALUE, SCHEME, SMP_ID FROM RECIPIENT_PART_IDENTIFIER";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        ResultSet resultSet = pstmt.executeQuery();
        List<Participant> participantList = new ArrayList<>();
        while (resultSet.next()) {
            Participant participant = new Participant();
            participant.setSmpId(resultSet.getString("SMP_ID"));
            participant.setRecValue(resultSet.getString("REC_VALUE"));
            participant.setScheme(resultSet.getString("SCHEME"));
            participantList.add(participant);
        }
        return participantList;
    }

    private static void updateBDMSLSmp(Connection conn, List<SMP> smpList) throws SQLException {
        String SQL = "INSERT INTO bdmsl_smp(smp_id, fk_certificate_id, endpoint_physical_address, endpoint_logical_address, created_on, last_updated_on) values (?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        for (SMP smp : smpList) {
            pstmt.setString(1, smp.getSmpId());
            pstmt.setInt(2, certificateIdMap.get(smp.getUsername()));
            pstmt.setString(3, smp.getPhysicalAddress());
            pstmt.setString(4, smp.getLogicalAddress());
            pstmt.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            pstmt.setTimestamp(6, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            // pstmt.executeUpdate();
            pstmt.addBatch();
        }
        int[] count = pstmt.executeBatch();
        conn.commit();
    }

    private static List<SMP> selectSMP(Connection conn) throws SQLException {
        String SQL = "SELECT SMP_ID, LOGICAL_ADDRESS, PHYSICAL_ADDRESS, USERNAME FROM SERVICE_METADATA_PUBLISHER";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        ResultSet resultSet = pstmt.executeQuery();
        List<SMP> smpList = new ArrayList<>();
        while (resultSet.next()) {
            SMP smp = new SMP();
            smp.setSmpId(resultSet.getString("SMP_ID"));
            smp.setLogicalAddress(resultSet.getString("LOGICAL_ADDRESS"));
            smp.setPhysicalAddress(resultSet.getString("PHYSICAL_ADDRESS"));
            smp.setUsername(resultSet.getString("USERNAME"));
            smpList.add(smp);
        }
        return smpList;
    }

    private static void updateBDMSLCertificates(Connection conn, List<String> certificates) throws SQLException, ParseException {
        int i = 1;
        String SQL = "INSERT INTO bdmsl_certificate(id, certificate_id, valid_from, valid_until, created_on, last_updated_on) values (?,?,?,?,?,?)";
        for (String certificate : certificates) {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setLong(1, i);
            pstmt.setString(2, certificate);
            pstmt.setTimestamp(3, new Timestamp(0));
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            pstmt.setTimestamp(4, new Timestamp(df.parse("31/12/2999 23:59:59").getTime()));
            pstmt.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            pstmt.setTimestamp(6, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            pstmt.executeUpdate();
            certificateIdMap.put(certificate, i);
            i++;
        }
    }

    private static List<String> selectSMLCertificates(Connection conn) throws SQLException {
        String SQL = "SELECT SML_USERNAME FROM SML_USER";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        ResultSet resultSet = pstmt.executeQuery();
        List<String> smlCertificates = new ArrayList<>();
        while (resultSet.next()) {
            String certificate = resultSet.getString("SML_USERNAME");
            smlCertificates.add(certificate);
        }
        return smlCertificates;
    }

    private static void updateBDMSLCertificateDomain(Connection conn) throws SQLException {
        String SQL = "INSERT INTO bdmsl_certificate_domain(root_certificate_alias, domain, crl_url, created_on, last_updated_on) values (?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, properties.getProperty("root_certificate_alias"));
        pstmt.setString(2, properties.getProperty("domain"));
        pstmt.setString(3, properties.getProperty("crl_url"));
        pstmt.setTimestamp(4, new Timestamp(Calendar.getInstance().getTimeInMillis()));
        pstmt.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
        pstmt.executeUpdate();
    }

    private static Properties loadProperties(String[] args) throws IOException {
        String environment = "DEV";
        if (args != null && args.length == 1 && "PROD".equalsIgnoreCase(args[0])) {
            environment = "PROD";
        } else if (args != null && args.length == 1 && "ACC".equalsIgnoreCase(args[0])) {
            environment = "ACC";
        }
        Properties properties = load(environment.toLowerCase() + ".config.properties");
        return properties;
    }

    private static Properties load(String configFile) throws IOException {
        Properties result = null;
        try {
            InputStream stream = SMLToBDMSL.class.getResourceAsStream(configFile);
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
