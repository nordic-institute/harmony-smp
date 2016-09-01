package eu.europa.ec.cipa.sml.server.dns;

import eu.europa.ec.cipa.sml.server.dns.helper.UtilHelper;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
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
public class FixEdelivery907 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FixEdelivery907.class);

    private final static int DEFAULT_TTL_SECS = 60;

    private static IDNSClient client = null;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException, ZoneTransferException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        migrate();
    }

    private static void migrate() throws SQLException, ClassNotFoundException, IOException, ZoneTransferException, NoSuchAlgorithmException {
        List<Record> records = getDnsClient().getAllRecords();
        int result = 0;

        for (Record record : records) {
            if (record instanceof NAPTRRecord) {
                if (((NAPTRRecord) record).getRegexp().contains("acc.edelivery.tech.ec.europa.eu.!")) {
                    handle(record);
                    result++;
                }
            }
        }

        logger.info(result + " NAPTRRecord(s) migrated.");
    }

    private static IDNSClient getDnsClient() {
        if (client == null) {
            client = DNSClientFactory.getSimpleInstace();
        }
        return client;
    }

    private static void handle(Record oldRecord) throws IOException, NoSuchAlgorithmException {
        final Name participantNaptrHost = Name.fromString(oldRecord.getName().toString());
        final String publisherHost = ((NAPTRRecord) oldRecord).getRegexp();
        logger.info("DELETING NAPTRRecord: " + oldRecord);
        getDnsClient().deleteList(Arrays.asList(new Record[]{oldRecord}));
        NAPTRRecord newNaptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", "Meta:SMP", UtilHelper.removeDomainExtraCharacter(publisherHost), Name.fromString("."));
        logger.info("CREATING  NAPTRRecord: " + newNaptrRecord);
        getDnsClient().addRecords(Arrays.asList(new Record[]{newNaptrRecord}));
    }

    private static Name createPublisherDNSNameObject(final String smpId, final String dnsZoneName, String dnsPublisherPrefix) throws TextParseException {
        String principalSmpDnsName = smpId + "." + dnsPublisherPrefix + "." + dnsZoneName;
        return Name.fromString(principalSmpDnsName);
    }
}