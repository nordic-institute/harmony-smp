package eu.europa.ec.cipa.sml.server.dns;

import eu.europa.ec.cipa.sml.server.dns.helper.HashUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xbill.DNS.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.*;
import java.util.*;

/**
 * Created by rodrfla on 30/08/2016.
 * <p>
 * This class can be used to test data consistency between the DNS and the database
 */
public class CreateMissingDataIntoDNS {

    private static final Logger logger = LoggerFactory.getLogger(CreateMissingDataIntoDNS.class);

    private static Properties properties;

    private static String ACCEPTANCE = "ACC";
    private static String PRODUCTION = "PROD";

    private static String BDMSL = "BDMSL";
    private static String SML = "SML";

    private static String component = SML;

    private static final String CONFIG_ZONE = "dnsClient.zone";
    private static final String CONFIG_SML_ZONE_NAME = "dnsClient.smlzonename";
    private static final String CONFIG_SERVER = "dnsClient.server";
    private static final String CONFIG_JDBC_DRIVER = "jdbc.driver.origin";
    private static final String CONFIG_JDBC_URL = "jdbc.connection.origin";
    private static final String CONFIG_JDBC_USER = "jdbc.user";
    private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

    public static void main(String[] args) throws Throwable {
        Security.addProvider(new BouncyCastleProvider());
        properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("acc.config.properties"));
        component = BDMSL;
        run();
    }

    private static void run() throws Exception {
        Map<String, Object> dnsEntries = getDNSEntries();
        List<String> smpInDNSList = (List<String>) dnsEntries.get("smpInDNSList");
        List<String> participantHashInDNSList = (List<String>) dnsEntries.get("participantHashInDNSList");

        createSMPs(smpInDNSList);
        createParticipants(participantHashInDNSList);
    }

    private static Map<String, Object> getDNSEntries() throws IOException, ZoneTransferException {

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

        for (Record rec : aFilteredRecords) {
            String name = rec.getName().toString().toLowerCase();
            if (!name.equalsIgnoreCase(sZoneName + ".")) {
                if (rec instanceof CNAMERecord || rec instanceof ARecord) {
                    if (name.startsWith("b-")) {
                        participantHashInDNSList.add(rec.getName().toString());
                    } else if (name.contains(".publisher.")) {
                        smpInDNSList.add(rec.getName().toString());
                    } else {
                        // log warning -> only 4 records will normally end there like "edelivery.tech.ec.europa.eu.  1800    IN      A       147.67.2.54"
                        logger.warn("Exceptional condition: " + rec);
                    }
                } else if (rec instanceof NAPTRRecord) {
                    participantHashInDNSList.add(rec.getName().toString());
                }
            }
        }
        dnsEntries.put("smpInDNSList", smpInDNSList);
        dnsEntries.put("participantHashInDNSList", participantHashInDNSList);
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
        return participantsHashInDB;
    }

    private static List<Smp> getSMPsFromDB() throws
            IOException, ZoneTransferException, ClassNotFoundException, SQLException {
        logger.info("Retrieving SMPs from the database...");
        final String sSMLZoneName = properties.getProperty(CONFIG_SML_ZONE_NAME);
        List<Smp> smpInDBList = new ArrayList<>();
        Class.forName(properties.getProperty(CONFIG_JDBC_DRIVER));
        Connection conn = DriverManager.getConnection(properties.getProperty(CONFIG_JDBC_URL), properties.getProperty(CONFIG_JDBC_USER), properties.getProperty(CONFIG_JDBC_PASSWORD));
        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);
        conn.setReadOnly(true);
        String sql = "SELECT SMP_ID,LOGICAL_ADDRESS,PHYSICAL_ADDRESS FROM SERVICE_METADATA_PUBLISHER";

        if (BDMSL.equalsIgnoreCase(component)) {
            sql = "SELECT SMP_ID,ENDPOINT_PHYSICAL_ADDRESS,ENDPOINT_LOGICAL_ADDRESS FROM BDMSL_SMP";
        }

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Smp smp = new Smp();
            smp.setSmpId(rs.getString("SMP_ID"));
            smp.setSmpCode(rs.getString("SMP_ID") + "." + sSMLZoneName);
            if (BDMSL.equalsIgnoreCase(component)) {
                smp.setPhysicalAddress(rs.getString("ENDPOINT_PHYSICAL_ADDRESS"));
                smp.setLogicalAddress(rs.getString("ENDPOINT_LOGICAL_ADDRESS"));
            } else {
                smp.setPhysicalAddress(rs.getString("PHYSICAL_ADDRESS"));
                smp.setLogicalAddress(rs.getString("LOGICAL_ADDRESS"));
            }
            smpInDBList.add(smp);
        }
        rs.close();
        stmt.close();
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

    private static void createSMPs(List<String> smpInDNSList) throws Exception {
        List<Smp> smpInDBList = getSMPsFromDB();
        List<Record> recordsToCreate = new ArrayList<>();
        for (Smp smpDB : smpInDBList) {
            if (!smpInDNSList.contains(smpDB.getSmpCode())) {
                recordsToCreate.add(createCNAME(smpDB));
            }
        }


        if (!recordsToCreate.isEmpty()) {
            getDnsClient().addRecords(recordsToCreate);
        }
    }

    private static void createParticipants(List<String> participantHashInDNSList) throws Exception {
        List<Participant> participantsHashInDB = getParticipantsFromDB();
        List<Record> recordsToCreate = new ArrayList<>();
        boolean hasMd5 = false;
        boolean hasSHA256 = false;

        for (Participant participant : participantsHashInDB) {
            for (String participantId : participantHashInDNSList) {
                if (participant.getMd5Code().equals(participantId)) {
                    hasMd5 = true;
                }
                if (participant.getSha256Code().equals(participantId)) {
                    hasSHA256 = true;
                }
            }

            if (!hasMd5) {
                recordsToCreate.add(createCNAME(participant.getParticipantId(), participant.getScheme(), participant.getSmpId()));
            }
            if (!hasSHA256) {
                recordsToCreate.add(createNAPTR(participant.getParticipantId(), participant.getScheme(), participant.getSmpId()));
            }

            hasMd5 = false;
            hasSHA256 = false;
        }



        List<Record> smallRecordsToCreate = new ArrayList<>();
        int i = 0;
        for (Record record : recordsToCreate) {
            smallRecordsToCreate.add(record);
            i++;
            if (i == 100) {
                i = 0;
                getDnsClient().addRecords(smallRecordsToCreate);
                smallRecordsToCreate.clear();
            }
        }
        if (!smallRecordsToCreate.isEmpty()) {
            getDnsClient().addRecords(smallRecordsToCreate);
        }
    }

    private static Record createCNAME(Smp smp) throws Exception {
        final String dnsZoneName = addDomainExtraCharacter(properties.getProperty(CONFIG_ZONE));
        final Name publisherHost = createPublisherDNSNameObject(smp.getSmpId(), dnsZoneName);
        String endPoint;
        try {
            endPoint = new URL(smp.getLogicalAddress()).getHost();
        } catch (MalformedURLException e) {
            throw new Exception("Logical address is malformed: " + e.getMessage(), e);
        }
        Record record = null;
        byte[] ipAddressBytes = Address.toByteArray(endPoint, Address.IPv4);
        if (ipAddressBytes != null) {
            //IPV4
            final InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getByAddress(ipAddressBytes);
            } catch (UnknownHostException exc) {
                throw new Exception("Invalid IPv4 address : " + endPoint + "'", exc);
            }
            record = new ARecord(publisherHost, DClass.IN, 60, inetAddress);
        } else {
            try {
                record = new CNAMERecord(publisherHost, DClass.IN, 60, new Name(endPoint + "."));
            } catch (TextParseException exc) {
                throw new Exception("Failed to build DNS Name from '" + endPoint + "'", exc);
            }
        }

        logger.info("RECORD CREATED " + record);

        return record;
    }


    private static Record createCNAME(String participantId, String scheme, String smpId) throws Exception {
        final String dnsZoneName = addDomainExtraCharacter(properties.getProperty(CONFIG_ZONE));
        final Name publisherHost = createPublisherDNSNameObject(smpId, dnsZoneName);
        final Name participantCnameHost = createParticipantDNSNameObjectSML("B-", participantId, scheme, dnsZoneName);
        CNAMERecord cnameRecord = new CNAMERecord(participantCnameHost, DClass.IN, 60, publisherHost);

        logger.info("RECORD CREATED " + cnameRecord);
        return cnameRecord;
    }

    private static Record createNAPTR(String participantId, String scheme, String smpId) throws Exception {
        final String dnsZoneName = addDomainExtraCharacter(properties.getProperty(CONFIG_ZONE));
        final Name publisherHost = createPublisherDNSNameObject(smpId, dnsZoneName);
        final Name participantNaptrHost = createParticipantDNSNameObjectBDXL(participantId, scheme, dnsZoneName);
        NAPTRRecord newNaptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, 60, 100, 10, "U", "Meta:SMP", "!^.*$!http://" + removeDomainExtraCharacter(publisherHost.toString()) + "!", Name.fromString("."));

        logger.info("RECORD CREATED " + newNaptrRecord);
        return newNaptrRecord;
    }

    private static String removeDomainExtraCharacter(String domain) {
        if (!StringUtils.isEmpty(domain) && domain.substring(domain.length() - 1).equals(".")) {
            return domain.substring(0, domain.length() - 1);
        }
        return domain;
    }


    private static Name createPublisherDNSNameObject(final String smpId, final String dnsZoneName) throws Exception {
        String smpDnsName = null;

        smpDnsName = smpId + ".publisher." + dnsZoneName;
        return Name.fromString(smpDnsName);
    }

    private static Name createParticipantDNSNameObjectBDXL(String participantId, String scheme, String dnsZoneName) throws UnsupportedEncodingException, NoSuchAlgorithmException, TextParseException {
        String smpDnsName = participantId + ", " + scheme + ", " + dnsZoneName;
        if ("*".equals(participantId)) {
            smpDnsName = "*." + scheme + "." + dnsZoneName;
        } else {
            String hashedParticipantID = HashUtil.getSHA256HashBase32(participantId.toLowerCase(Locale.US));
            smpDnsName = hashedParticipantID + "." + scheme + "." + dnsZoneName;
        }
        return Name.fromString(smpDnsName);
    }

    private static Name createParticipantDNSNameObjectSML(String prefix, String participantId, String scheme, String dnsZoneName) throws Exception {
        String smpDnsName = null;
        if ("*".equals(participantId)) {
            smpDnsName = "*." + scheme + "." + dnsZoneName;
        } else {
            smpDnsName = prefix + HashUtil.getMD5Hash(participantId.toLowerCase(Locale.US)) + "." + scheme + "." + dnsZoneName;
        }
        return Name.fromString(smpDnsName);
    }

    public static class Smp {
        private String logicalAddress;
        private String physicalAddress;
        private String smpId;
        private String smpCode;

        public String getSmpCode() {
            return smpCode;
        }

        public void setSmpCode(String smpCode) {
            this.smpCode = smpCode;
        }

        public String getLogicalAddress() {
            return logicalAddress;
        }

        public void setLogicalAddress(String logicalAddress) {
            this.logicalAddress = logicalAddress;
        }

        public String getPhysicalAddress() {
            return physicalAddress;
        }

        public void setPhysicalAddress(String physicalAddress) {
            this.physicalAddress = physicalAddress;
        }

        public String getSmpId() {
            return smpId;
        }

        public void setSmpId(String smpId) {
            this.smpId = smpId;
        }
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
