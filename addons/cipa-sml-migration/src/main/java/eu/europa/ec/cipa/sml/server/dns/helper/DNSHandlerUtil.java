package eu.europa.ec.cipa.sml.server.dns.helper;

import eu.europa.ec.cipa.sml.server.dns.DNSClientFactory;
import eu.europa.ec.cipa.sml.server.dns.IDNSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rodrfla on 30/08/2016.
 */
public class DNSHandlerUtil {

    private static IDNSClient client = null;
    private static final Logger logger = LoggerFactory.getLogger(DNSHandlerUtil.class);

    public static void main(String[] args) throws IOException, ZoneTransferException, DNSSEC.DNSSECException, NoSuchAlgorithmException {
        exportRecords();
       /* deleteRecord(getRecordByName("05ab0010659a5e5eb7582142edc5256925918681500cfe8e093d5206.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("45420262e80335418ff5d046651eb693e3b1fc8d0bda4720ade8bc2c.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("2YO72LIMIKWPSK62UHI47YOVXVZDL57ZBFHQCPJ6NN3MAJUKFW3A.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("SGKALKBYWZ7JO4AVKN6YUJV4SL3P267TYLORQXI5FLTYZIXFTEAQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu. "));
        deleteRecord(getRecordByName("YPL72BNZQEVX6WRKB4SQQB3VHLV5IBXKIAR5KS43QX2PSG3ECSHQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("RBWAL3UIAMX4EGSMAUC6AMW6SYA4D4RUZVF45UEOQ7VGGCBB3DXA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("PR3HIOS3PNNJPZAMLIJQBO2YSQCBAQINUXBK4AZPP3QEIBYG7WBA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("MD7NSFWUSJJVSQUPBSIA74SCSVWCC6GVMI76D6VHGOHNKEK3HHGQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("MBAMCESO7IJI5BIF6J2B3Y6GVGW4FUK7LCAIO24EEMQRR7TYYBVA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("KG6FVQEXVQ35FHEE2HWB4HCKY33AKRPETU46N245OLLB2RMQLIRA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu. "));
        deleteRecord(getRecordByName("GM2N3V3MXZ3BRU6QDKZQ47AW5FG4FAZFSRTCQW7UUVH5L4HKRAHQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("G4YOYGF2ECSHCVEWUGDAFO3542DM3UKMNUIFDX2SQ273KD65C3HQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("FWVIE456WYAA7NLSM7ZIOSVRZ6C7ARGQDGMT2W5DDPUC5W3OMQSA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("fd31ad97d1e10c22d83462e767f6aac70b55e84b43dcbf48d9bcc0b1.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("F62U6ZTIA2664RVHGVGJ5Y525VCCVM7BLR4TBJO2NR7CYOJGX22Q.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("ETYERQ4LQEAOJQ5VLZSG77ROQ6DNZFO6G74JLEHDYBVNFJTFXYMQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("b376026a3b6fcf937781dce9a9e8240db16ff2eb551645936c555a9d.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("B-YPL72BNZQEVX6WRKB4SQQB3VHLV5IBXKIAR5KS43QX2PSG3ECSHQ.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("B-KG6FVQEXVQ35FHEE2HWB4HCKY33AKRPETU46N245OLLB2RMQLIRA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));
        deleteRecord(getRecordByName("B-KG6FVQEXVQ35FHEE2HWB4HCKY33AKRPETU46N245OLLB2RMQLIRA.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu."));*/
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

    private static Record getRecordByName(String recordName) {
        try {
            for (Record record : getDnsClient().getAllRecords()) {
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