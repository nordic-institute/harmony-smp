package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.dynamicdiscovery.core.locator.dns.IDNSLookup;
import eu.europa.ec.dynamicdiscovery.core.locator.dns.impl.DefaultDNSLookup;
import eu.europa.ec.dynamicdiscovery.core.locator.impl.DefaultBDXRLocator;
import eu.europa.ec.dynamicdiscovery.enums.DNSLookupType;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.dynamicdiscovery.model.identifiers.SMPParticipantIdentifier;
import eu.europa.ec.edelivery.smp.data.ui.DNSQueryRO;
import eu.europa.ec.edelivery.smp.data.ui.DNSQueryRequestRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.spi.SmpIdentifierService;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Class provide dynamic discovery tools for UI.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */

@Service
public class UIDynamicDiscoveryTools {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDynamicDiscoveryTools.class);

    private final SmpIdentifierService smpIdentifierService;

    public UIDynamicDiscoveryTools(SmpIdentifierService smpIdentifierService) {
        this.smpIdentifierService = smpIdentifierService;
    }

    /**
     * Metod return DNS queries for given domain and resource identifier
     *
     * @return list of DNS queries
     */
    public List<DNSQueryRO> createDnsQueries(DNSQueryRequestRO dnsQueryRequest) {

        String domainPrivate = StringUtils.trimToEmpty(dnsQueryRequest.getTopDnsDomain());
        ResourceIdentifier identifier = smpIdentifierService.normalizeResourceIdentifier(dnsQueryRequest.getIdentifierValue(),
                dnsQueryRequest.getIdentifierScheme());

        SMPParticipantIdentifier participantIdentifier
                = new SMPParticipantIdentifier(identifier.getValue(), identifier.getScheme());

        // configure ddc client
        DefaultDNSLookup testDNSLookup = new DefaultDNSLookup.Builder()
                .build();
        DefaultBDXRLocator bdxrLocator = new DefaultBDXRLocator.Builder()
                .addTopDnsDomain(domainPrivate)
                .dnsLookup(testDNSLookup).build();

        List<DNSQueryRO> dnsLookupTypes = new ArrayList<>();
        DNSQueryRO cnameResult = createDnsQuery(domainPrivate, participantIdentifier, bdxrLocator, DNSLookupType.CNAME);
        DNSQueryRO naptrResult = createDnsQuery(domainPrivate, participantIdentifier, bdxrLocator, DNSLookupType.NAPTR);

        dnsLookupTypes.add(cnameResult);
        dnsLookupTypes.add(naptrResult);

        return dnsLookupTypes;
    }

    /**
     * Method creates CNAME DNS query and if domain is not empty it tries to resolve it.
     * Method returns list of all resolved domains
     *
     * @param domain             the top dns domain/dns zone
     * @param resourceIdentifier the resource identifier
     * @param bdxrLocator        the bdxr locator
     * @return the DNS query RO object
     */
    private DNSQueryRO createDnsQuery(String domain, SMPParticipantIdentifier resourceIdentifier,
                                      DefaultBDXRLocator bdxrLocator,
                                      DNSLookupType dnsLookupType) {

        String dnsQuery;
        switch (dnsLookupType) {
            case CNAME:
                dnsQuery = bdxrLocator.buildCNameDNSQuery(resourceIdentifier, domain);
                break;
            case NAPTR:
                dnsQuery = bdxrLocator.buildNaptrDNSQuery(resourceIdentifier, domain);
                break;
            default:
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DNS Lookup", "Unknown DNS lookup type: " + dnsLookupType);
        }

        DNSQueryRO dnsQueryRO = new DNSQueryRO(dnsQuery,
                dnsLookupType);

        if (StringUtils.isNotBlank(domain)) {
            switch (dnsLookupType) {
                case CNAME:
                    resolveCNameQuery(dnsQueryRO, resourceIdentifier, bdxrLocator);
                    break;
                case NAPTR:
                    resolveNaptrQuery(dnsQueryRO, resourceIdentifier, bdxrLocator);
                    break;
            }
        }
        return dnsQueryRO;
    }

    /**
     * Method resolve CNAME query and add results to DNS query object or add error message.
     * Method is resolving all CNAMES in chain.
     *
     * @param dnsQuery           the DNS query object to add results or errors
     * @param resourceIdentifier the resource identifier for the query. Used for logging.
     * @param bdxrLocator        the bdxr locator tool to resolve DNS queries
     */
    public void resolveCNameQuery(DNSQueryRO dnsQuery, SMPParticipantIdentifier resourceIdentifier, DefaultBDXRLocator bdxrLocator) {
        IDNSLookup testDNSLookup = bdxrLocator.getDnsLookup();
        try {
            List<Record> result = testDNSLookup.getAllRecordsForType(resourceIdentifier, dnsQuery.getDnsQuery(), DNSLookupType.CNAME);
            // cname is always expected only one
            while (true) {
                if (result != null && !result.isEmpty() && result.get(0).getType() == 5) {
                    CNAMERecord record = (CNAMERecord) result.get(0);
                    dnsQuery.addDnsRecordEntry(
                            record.getName().toString(),
                            DNSLookupType.CNAME,
                            record.toString(),
                            record.getTarget().toString());
                    // follow resolution until CNAMe exists
                    result = testDNSLookup.getAllRecordsForType(resourceIdentifier, record.getTarget().toString(), DNSLookupType.CNAME);
                } else {
                    break;
                }
            }
        } catch (TechnicalException e) {
            LOG.warn("Error during DNS lookup for CNAME record: [{}]", ExceptionUtils.getRootCauseMessage(e));
            dnsQuery.addDnsError(ExceptionUtils.getRootCauseMessage(e));
        }
    }


    /**
     * Method resolve NAPTR query and add results to DNS query object or add error message
     *
     * @param dnsQuery           the DNS query object to add results or errors
     * @param resourceIdentifier the resource identifier for the query. Used for logging.
     * @param bdxrLocator        the bdxr locator tool to resolve DNS queries
     */
    public void resolveNaptrQuery(DNSQueryRO dnsQuery, SMPParticipantIdentifier resourceIdentifier, DefaultBDXRLocator bdxrLocator) {
        IDNSLookup testDNSLookup = bdxrLocator.getDnsLookup();
        try {
            List<Record> result = testDNSLookup.getAllRecordsForType(resourceIdentifier, dnsQuery.getDnsQuery(), DNSLookupType.NAPTR);
            for (Record rec : result) {
                NAPTRRecord record = (NAPTRRecord) rec;
                dnsQuery.addDnsRecordEntry(
                        record.getName().toString(),
                        DNSLookupType.NAPTR,
                        record.rdataToString(),
                        record.getRegexp());
            }
        } catch (TechnicalException e) {
            LOG.warn("Error during DNS lookup for NAPTR record: [{}]", e.getMessage());
            dnsQuery.addDnsError(ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
