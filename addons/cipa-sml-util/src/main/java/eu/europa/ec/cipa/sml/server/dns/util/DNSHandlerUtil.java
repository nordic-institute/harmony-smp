package eu.europa.ec.cipa.sml.server.dns.util;

import eu.europa.ec.cipa.sml.server.dns.DNSClientFactory;
import eu.europa.ec.cipa.sml.server.dns.IDNSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rodrfla on 30/08/2016.
 */
public class DNSHandlerUtil {

    private static IDNSClient client = null;
    private static final Logger logger = LoggerFactory.getLogger(DNSHandlerUtil.class);

    public static void main(String[] args) throws IOException, ZoneTransferException, DNSSEC.DNSSECException {
        //deleteRecords(getRecordChildrenBySMPName("TestHash1.publisher.acc.edelivery.tech.ec.europa.eu"));
        //deleteRecord(getRecordByName("B-03952de4b9adae9fcff6858eaa2342b1.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        //exportRecords();
    }

    private static IDNSClient getDnsClient() {
        if (client == null) {
            client = DNSClientFactory.getSimpleInstace();
        }
        return client;
    }

    private static String stringPattern(Object... values) {
        String record = new String();
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                record += (i < (values.length - 1)) ? values[i] + "," : values[i];
            }
            record += "\n";
        }
        return record;
    }

    private static void exportRecords() throws IOException, ZoneTransferException, DNSSEC.DNSSECException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("EXPORTING RECORDS... \n");
        for (Record record : getDnsClient().getAllRecords()) {
            switch (record.getType()) {
                case Type.CNAME:
                    CNAMERecord cname = (CNAMERecord) record;
                    stringBuilder.append(stringPattern(cname.getAlias(), cname.getName(), cname.getType(), cname.getTarget(), cname.getTTL(), cname.getAdditionalName(), cname.getDClass(), cname.getRRsetType(), cname.getClass()));
                    break;
                case Type.A:
                    ARecord aname = (ARecord) record;
                    stringBuilder.append(stringPattern(aname.getName(), aname.getType(), aname.getTTL(), aname.getAdditionalName(),
                            aname.getDClass(), aname.getRRsetType(), aname.getClass(), aname.getAddress(), aname.rdataToString()));
                    break;
                case Type.NAPTR:
                    NAPTRRecord naptrRecord = (NAPTRRecord) record;
                    stringBuilder.append(stringPattern(naptrRecord.getName(), naptrRecord.getType(), naptrRecord.getTTL(), naptrRecord.getAdditionalName(),
                            naptrRecord.getDClass(), naptrRecord.getRRsetType(), naptrRecord.getClass(), naptrRecord.getFlags(), naptrRecord.rdataToString(), naptrRecord.getOrder()
                            , naptrRecord.getPreference(), naptrRecord.getRegexp(), naptrRecord.getReplacement(), naptrRecord.getService()));
                    break;
                case Type.SOA:
                    SOARecord soaRecord = (SOARecord) record;
                    stringBuilder.append(stringPattern(soaRecord.getAdmin(), soaRecord.getExpire(), soaRecord.getSerial(), soaRecord.getHost(), soaRecord.getMinimum(), soaRecord.getRefresh(), soaRecord.getRetry(), soaRecord.getName(), soaRecord.getType(), soaRecord.getTTL(), soaRecord.getAdditionalName(),
                            soaRecord.getDClass(), soaRecord.getRRsetType(), soaRecord.getClass(), soaRecord.rdataToString()));
                    break;
                case Type.NS:
                    NSRecord nsRecord = (NSRecord) record;
                    stringBuilder.append(stringPattern(nsRecord.getTarget(), nsRecord.getName(), nsRecord.getType(), nsRecord.getTTL(), nsRecord.getAdditionalName(),
                            nsRecord.getDClass(), nsRecord.getRRsetType(), nsRecord.getClass(), nsRecord.rdataToString()));
                    break;
                case Type.KEY:
                    KEYRecord keyRecord = (KEYRecord) record;
                    stringBuilder.append(stringPattern(keyRecord.getFlags(), keyRecord.getAlgorithm(), keyRecord.getDClass(), keyRecord.getFootprint(), keyRecord.getKey(), keyRecord.getProtocol(), keyRecord.getPublicKey(), keyRecord.getName(), keyRecord.getType(), keyRecord.getTTL(), keyRecord.getAdditionalName(),
                            keyRecord.getDClass(), keyRecord.getRRsetType(), keyRecord.getClass(), keyRecord.rdataToString()));
                    break;
                default:
                    stringBuilder.append(stringPattern("RECORD_TYPE_NOT_DEFINED", record.getDClass(), record.getName(), record.getType(), record.getTTL(), record.getAdditionalName(),
                            record.getDClass(), record.getRRsetType(), record.getClass(), record.rdataToString()));
                    break;
            }
        }
        logger.info(stringBuilder.toString());
    }

    private static void deleteRecord(Record record) {
        if (record != null) {
            deleteRecords(Arrays.asList(new Record[]{record}));
        }
    }

    private static void deleteRecords(List<Record> records) {
        if (!records.isEmpty()) {
            getDnsClient().deleteList(records);
        }
    }

    private static void addRecords(List<Record> records) {
        getDnsClient().addRecords(records);
    }

    private static void addRecord(Record record) {
        addRecords(Arrays.asList(new Record[]{record}));
    }

    private static Record getRecordByName(String recordName) {
        try {
            for (Record record : getDnsClient().getAllRecords()) {

                if (record instanceof CNAMERecord) {
                    System.out.println(((CNAMERecord) record).getAlias());
                    System.out.println(((CNAMERecord) record));
                }
                if (record.getName().toString().equals(recordName)) {
                    return record;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ZoneTransferException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Record> getRecordChildrenBySMPName(String recordName) {
        List<Record> records = new ArrayList<>();
        try {
            for (Record record : getDnsClient().getAllRecords()) {
                if (record instanceof CNAMERecord) {
                    if (((CNAMERecord) record).getAlias().toString().contains(recordName)) {
                        records.add(record);
                    }
                }

                if (record instanceof NAPTRRecord) {
                    if (((NAPTRRecord) record).getRegexp().toString().contains(recordName)) {
                        records.add(record);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ZoneTransferException e) {
            e.printStackTrace();
        }
        return records;
    }
}