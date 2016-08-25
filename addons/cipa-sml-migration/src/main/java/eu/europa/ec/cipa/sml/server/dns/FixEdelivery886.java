package eu.europa.ec.cipa.sml.server.dns;

import eu.europa.ec.cipa.sml.server.dns.helper.UtilHelper;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.xbill.DNS.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by rodrfla on 23/08/2016
 */
public class FixEdelivery886 {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FixEdelivery886.class);
    private final static int DEFAULT_TTL_SECS = 60;
    private static IDNSClient client = null;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException, ZoneTransferException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        migrate();
    }

    private static void migrate() throws SQLException, ClassNotFoundException, IOException, ZoneTransferException, NoSuchAlgorithmException {
        String SQL = "SELECT PARTICIPANT_ID,SCHEME,FK_SMP_ID FROM BDMSL_PARTICIPANT_IDENTIFIER";
        Connection connOrigin = UtilHelper.getConnection();
        PreparedStatement pstmt = connOrigin.prepareStatement(SQL);
        ResultSet resultSet = pstmt.executeQuery();
        List<Record> records = getDnsClient().getAllRecords();
        int result = 0;
        int participants = 0;
        while (resultSet.next()) {
            participants++;
            String participantId = resultSet.getString("PARTICIPANT_ID");
            String scheme = resultSet.getString("SCHEME");
            String smpId = resultSet.getString("FK_SMP_ID");

            for (Record record : records) {
                if (record instanceof NAPTRRecord) {
                    if (record.getName().toString().startsWith("B-" + HashUtil.getSHA224Hash(participantId.toLowerCase(Locale.US)))) {
                        handle(record, participantId, smpId, scheme);
                        result++;
                    }
                }
            }
        }
        connOrigin.close();
        logger.info(result + " NAPTRRecord(s) migrated.");
        logger.info(participants + " Participant(s) found.");
    }

    private static IDNSClient getDnsClient() {
        if (client == null) {
            client = DNSClientFactory.getSimpleInstace();
        }
        return client;
    }

    private static void handle(Record oldRecord, String participantId, String smpId, String scheme) throws IOException, NoSuchAlgorithmException {
        final Name participantNaptrHost = createParticipantDNSNameObjectBDXL(participantId, scheme, UtilHelper.addDomainExtraCharacter());
        final Name publisherHost = createPublisherDNSNameObject(smpId, UtilHelper.addDomainExtraCharacter(), "publisher");
        logger.info("DELETING NAPTRRecord: " + oldRecord);
        getDnsClient().deleteList(Arrays.asList(new Record[]{oldRecord}));

        NAPTRRecord newNaptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", "Meta:SMP", "!^.*$!http://" + UtilHelper.removeDomainExtraCharacter(publisherHost.toString()) + "!", Name.fromString("."));
        logger.info("CREATING  NAPTRRecord: " + newNaptrRecord);
        getDnsClient().addRecords(Arrays.asList(new Record[]{newNaptrRecord}));
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

    private static Name createPublisherDNSNameObject(final String smpId, final String dnsZoneName, String dnsPublisherPrefix) throws TextParseException {
        String principalSmpDnsName = smpId + "." + dnsPublisherPrefix + "." + dnsZoneName;
        return Name.fromString(principalSmpDnsName);
    }
}

