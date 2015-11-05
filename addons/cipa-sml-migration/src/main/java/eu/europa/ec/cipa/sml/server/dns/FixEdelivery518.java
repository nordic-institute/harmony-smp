package eu.europa.ec.cipa.sml.server.dns;

import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by feriaad on 04/11/2015.
 */
public class FixEdelivery518 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SMLToBDMSL.class);

    private final static int DEFAULT_TTL_SECS = 60;

    private static IDNSClient client = null;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException, ZoneTransferException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        try {
            List<Record> recs = getDnsClient().getAllRecords();
            System.out.println("Number of records on the list: " + recs.size());
            for (Record rec : recs) {
                if (rec.getType() == Type.NAPTR) {
                    String participantHash = rec.getName().toString();
                    String smp = "";
                    Pattern pattern = Pattern.compile(".*!http://(.*)!.*");
                    Matcher matcher = pattern.matcher(rec.rdataToString());
                    while (matcher.find()) {
                        smp = matcher.group(1);
                    }
                    createNaptrRecordsForParticipants(participantHash, smp);
                }
            }

        } catch (final Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private static IDNSClient getDnsClient() {
        if (client == null) {
            client = DNSClientFactory.getSimpleInstace();
        }
        return client;
    }

    private static void createNaptrRecordsForParticipants(String participantHash, String smpDnsName) throws TextParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        final Name participantNaptrHost = Name.fromString(participantHash);
        final Name publisherHost = Name.fromString(smpDnsName);
        Record oldNaptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", "Meta:SMP", "!^.$!http://" + publisherHost + "!", Name.fromString("."));
        logger.info("delete  " + oldNaptrRecord);
        getDnsClient().deleteList(Arrays.asList(new Record[]{oldNaptrRecord}));
        NAPTRRecord newNaptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", "Meta:SMP", "!^.*$!http://" + publisherHost + "!", Name.fromString("."));
        logger.info("create  " + newNaptrRecord);
        getDnsClient().addRecords(Arrays.asList(new Record[]{newNaptrRecord}));
    }
}
