package eu.europa.ec.cipa.bdmsl.service.dns.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.common.exception.DNSClientException;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDomainDAO;
import eu.europa.ec.cipa.bdmsl.security.CertificateDetails;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsClientService;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import eu.europa.ec.cipa.bdmsl.service.dns.ISIG0KeyProviderService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import eu.europa.ec.cipa.common.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xbill.DNS.*;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by feriaad on 24/06/2015.
 */
@Service
public class DnsClientServiceImpl implements IDnsClientService {

    private static final int MAX_RETRY = 5;

    @Autowired
    private ILoggingService loggingService;

    @Autowired
    private ICertificateDomainDAO certificateDomainDAO;

    @Autowired
    private IDnsMessageSenderService dnsMessageSenderService;

    @Autowired
    private ISIG0KeyProviderService sig0KeyProviderService;

    private final int DEFAULT_TTL_SECS = 60;

    private final String DNS_CNAME_PARTICIPANT_PREFIX = "B-";

    private final String DNS_NAPTR_PARTICIPANT_PREFIX = "B-";

    @Value("${dnsClient.server}")
    private String dnsServer;

    @Value("${dnsClient.publisherPrefix}")
    private String dnsPublisherPrefix;

    @Value("${dnsClient.SIG0Enabled}")
    private String sig0Enabled;

    @Value("${dnsClient.enabled}")
    private String dnsEnabled;

    @Override
    public List<Record> getAllRecords(String dnsZone) throws TechnicalException {
        // do zone transfer to get complete list..
        String dnsZoneName = dnsZone;
        // we need the full qualified dns-name
        if (!dnsZoneName.endsWith(".")) {
            dnsZoneName += '.';
        }
        final ZoneTransferIn xfr;
        List<Record> records;
        try {
            xfr = ZoneTransferIn.newAXFR(Name.fromString(dnsZoneName), dnsServer, null);
            records = xfr.run();
        } catch (Exception e) {
            throw new DNSClientException("Impossible to retrieve all records for zone " + dnsZone, e);
        }

        return records;
    }

    @Override
    public void createDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        String endPoint;
        try {
            endPoint = new URL(smpBO.getLogicalAddress()).getHost();
        } catch (MalformedURLException e) {
            throw new BadRequestException("Logical address is malformed: " + e.getMessage(), e);
        }
        String dnsZoneName = getDnsZoneName();
        final Update dnsUpdate = getUpdateObject(dnsZoneName);

        // Delete old host - if exists!
        final Name publisherHost = createPublisherDNSNameObject(smpBO.getSmpId(), dnsZoneName);
        dnsUpdate.delete(publisherHost);

        byte[] ipAddressBytes = Address.toByteArray(endPoint, Address.IPv4);

        Name replacement = null;
        try {
            replacement = Name.fromString(".");
        } catch (TextParseException exc) {
            throw new DNSClientException("Failed to build DNS Name from '" + replacement + "'", exc);
        }
        CNAMERecord cnameRecord = null;
        ARecord aRecord = null;
        if (ipAddressBytes != null) {
            loggingService.debug(" - IPV4");
            final InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getByAddress(ipAddressBytes);
            } catch (UnknownHostException exc) {
                throw new DNSClientException("Invalid IPv4 address : " + endPoint + "'", exc);
            }
            loggingService.debug("Creating ARecord in the DNS for SMP " + publisherHost);
            aRecord = new ARecord(publisherHost, DClass.IN, DEFAULT_TTL_SECS, inetAddress);
            dnsUpdate.add(aRecord);
        } else {
            try {
                loggingService.debug("Creating CNAMERecord in the DNS for SMP " + publisherHost);
                cnameRecord = new CNAMERecord(publisherHost, DClass.IN, DEFAULT_TTL_SECS, new Name(endPoint + "."));
                dnsUpdate.add(cnameRecord);
            } catch (TextParseException exc) {
                throw new DNSClientException("Failed to build DNS Name from '" + endPoint + "'", exc);
            }
        }

        sendAndValidateMessage(dnsUpdate);

        if (cnameRecord != null) {
            loggingService.businessLog(LogEvents.BUS_CNAME_RECORD_FOR_SMP_CREATED, smpBO.getSmpId(), cnameRecord.toString());
        }
        if (aRecord != null) {
            loggingService.businessLog(LogEvents.BUS_A_RECORD_FOR_SMP_CREATED, smpBO.getSmpId(), aRecord.toString());
        }
    }

    @Override
    public void createDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException {
        this.createDNSRecordsForParticipants(Arrays.asList(new ParticipantBO[]{participantBO}));
    }

    @Override
    public void updateDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        this.createDNSRecordsForSMP(smpBO);
    }

    @Override
    public void updateDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException {
        this.createDNSRecordsForParticipant(participantBO);
    }

    @Override
    public void deleteDNSRecordsForParticipants(List<ParticipantBO> participantBOList) throws TechnicalException {
        String dnsZoneName = getDnsZoneName();
        final Update dnsUpdate = getUpdateObject(dnsZoneName);
        for (ParticipantBO participantBO : participantBOList) {
            final Name participantCnameHost = createParticipantDNSNameObjectSML(DNS_CNAME_PARTICIPANT_PREFIX, participantBO.getParticipantId(), participantBO.getScheme(), dnsZoneName);
            dnsUpdate.delete(participantCnameHost);
            final Name participantNaptrHost = createParticipantDNSNameObjectBDXL(DNS_NAPTR_PARTICIPANT_PREFIX, participantBO.getParticipantId(), participantBO.getScheme(), dnsZoneName);
            dnsUpdate.delete(participantNaptrHost);
        }
        sendAndValidateMessage(dnsUpdate);
    }

    @Override
    public void createDNSRecordsForParticipants(List<ParticipantBO> participantBOList) throws TechnicalException {
        String dnsZoneName = getDnsZoneName();
        final Update dnsUpdate = getUpdateObject(dnsZoneName);
        for (ParticipantBO participantBO : participantBOList) {
            // Delete old host - if exists!
            final Name participantCnameHost = createParticipantDNSNameObjectSML(DNS_CNAME_PARTICIPANT_PREFIX, participantBO.getParticipantId(), participantBO.getScheme(), dnsZoneName);
            dnsUpdate.delete(participantCnameHost);

            final Name participantNaptrHost = createParticipantDNSNameObjectBDXL(DNS_NAPTR_PARTICIPANT_PREFIX, participantBO.getParticipantId(), participantBO.getScheme(), dnsZoneName);
            dnsUpdate.delete(participantNaptrHost);

            final Name publisherHost = createPublisherDNSNameObject(participantBO.getSmpId(), dnsZoneName);
            CNAMERecord cnameRecord;
            NAPTRRecord naptrRecord;
            try {
                cnameRecord = new CNAMERecord(participantCnameHost, DClass.IN, DEFAULT_TTL_SECS, publisherHost);
                naptrRecord = new NAPTRRecord(participantNaptrHost, DClass.IN, DEFAULT_TTL_SECS, 100, 10, "U", participantBO.getType(), "!^.$!http://" + publisherHost + "!", Name.fromString("."));

                loggingService.debug("Creating CNAMERecord in the DNS for SMP " + participantCnameHost);
                dnsUpdate.add(cnameRecord);
                loggingService.debug("Creating NAPTRRecord in the DNS for SMP " + participantNaptrHost);
                dnsUpdate.add(naptrRecord);
            } catch (final TextParseException exc) {
                throw new DNSClientException("Failed to create DNS records", exc);
            }

            if (cnameRecord != null) {
                loggingService.businessLog(LogEvents.BUS_CNAME_RECORD_FOR_PARTICIPANT_CREATED, participantBO.getParticipantId(), cnameRecord.toString());
            }
            if (naptrRecord != null) {
                loggingService.businessLog(LogEvents.BUS_NAPTR_RECORD_FOR_PARTICIPANT_CREATED, participantBO.getParticipantId(), naptrRecord.toString());
            }
        }
        sendAndValidateMessage(dnsUpdate);
    }

    @Override
    public void deleteDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException {
        this.deleteDNSRecordsForParticipants(Arrays.asList(new ParticipantBO[]{participantBO}));
    }


    @Override
    public void deleteDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        String dnsZoneName = getDnsZoneName();
        final Update dnsUpdate = getUpdateObject(dnsZoneName);
        final Name publisherHost = createPublisherDNSNameObject(smpBO.getSmpId(), dnsZoneName);
        dnsUpdate.delete(publisherHost);
        sendAndValidateMessage(dnsUpdate);
    }

    private Update getUpdateObject(String dnsZoneName) throws DNSClientException {
        final Update dnsUpdate;
        try {
            dnsUpdate = new Update(Name.fromString(dnsZoneName));
        } catch (TextParseException exc) {
            throw new DNSClientException("Failed to build DNS Name from '" + dnsZoneName + "'", exc);
        }
        return dnsUpdate;
    }

    private String getDnsZoneName() throws TechnicalException {
        // retrieve the DNS zone name
        CertificateDomainBO certificateDomainBO = certificateDomainDAO.findDomain(((CertificateDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getRootCertificateDN());
        String dnsZoneName = certificateDomainBO.getDomain();
        // we need the full qualified dns-name
        if (!dnsZoneName.endsWith(".")) {
            dnsZoneName += '.';
        }
        return dnsZoneName;
    }

    private Name createPublisherDNSNameObject(final String smpId, final String dnsZoneName) throws TechnicalException {
        String smpDnsName = null;
        try {
            smpDnsName = smpId + "." + dnsPublisherPrefix + "." + dnsZoneName;
            return Name.fromString(smpDnsName);
        } catch (final TextParseException exc) {
            throw new DNSClientException("Failed to build DNS Name from '" + smpDnsName + "'", exc);
        }
    }

    private Name createParticipantDNSNameObjectSML(String prefix, String participantId, String scheme, String dnsZoneName) throws TechnicalException {
        String smpDnsName = null;
        try {
            if ("*".equals(participantId)) {
                smpDnsName = "*." + scheme + "." + dnsZoneName;
            } else {
                smpDnsName = prefix + HashUtil.getMD5Hash(participantId) + "." + scheme + "." + dnsZoneName;
            }
            return Name.fromString(smpDnsName);
        } catch (final TextParseException | NoSuchAlgorithmException | UnsupportedEncodingException exc) {
            throw new DNSClientException("Failed to build DNS Name from '" + smpDnsName + "'", exc);
        }
    }


    private Name createParticipantDNSNameObjectBDXL(String prefix, String participantId, String scheme, String dnsZoneName) throws TechnicalException {
        String smpDnsName = null;
        try {
            if ("*".equals(participantId)) {
                smpDnsName = "*." + scheme + "." + dnsZoneName;
            } else {
                smpDnsName = prefix + HashUtil.getSHA224Hash(participantId) + "." + scheme + "." + dnsZoneName;
            }
            return Name.fromString(smpDnsName);
        } catch (final TextParseException | NoSuchAlgorithmException | UnsupportedEncodingException exc) {
            throw new DNSClientException("Failed to build DNS Name from '" + smpDnsName + "'", exc);
        }
    }

    /**
     * Send a message to the DNS server and validate it. If it fails and an exception is caught, a
     * retry mechanism has been put in place. After X failures, an exception is thrown.
     *
     * @param dnsUpdate
     */
    private void sendAndValidateMessage(Update dnsUpdate) throws TechnicalException {
        sendAndValidateMessage(dnsUpdate, 0, true, null, null);
    }


    private void sendAndValidateMessage(Update dnsUpdate, int retry, boolean sign, Exception lastException, Message lastResponse) throws TechnicalException {
        if (retry > 0) {
            loggingService.info("Retrying for the " + retry + " time");
        }
        if (retry < MAX_RETRY) {
            Message response = null;
            try {
                response = sendMessageToDnsServer(dnsUpdate, sign);
                validateDNSResponse(response);
            } catch (Exception exc) {
                loggingService.warn("An error occurred while sending message to the DNS. Retrying...");
                sendAndValidateMessage(dnsUpdate, retry + 1, false, exc, response);
            }
        } else {
            loggingService.info("Error response message from the DNS: " + lastResponse);
            throw new DNSClientException("ERROR: There was an error. Impossible to update the DNS server after " + retry + " retries.", lastException);
        }
    }

    /**
     * Common method for validating Responses from DNS
     *
     * @param response
     */
    private void validateDNSResponse(final Message response) throws TechnicalException {
        final String retCode = Rcode.string(response.getRcode());
        loggingService.debug("validateDNSResponse '" + retCode + "'");

        if (response.getRcode() != Rcode.NOERROR) {
            // Error - not handling special cases yet
            throw new DNSClientException("Error performing DNS request : " + retCode);
        }
    }

    private Message sendMessageToDnsServer(Message m, boolean sign) throws Exception {
        boolean SIG0Enabled = Boolean.parseBoolean(sig0Enabled);
        loggingService.debug("Starting signature of the DNS call");
        long init = System.currentTimeMillis();
        if (SIG0Enabled && sign) {
            // To avoid any problem with the validity start date for the time of signature,
            // we start the validity a few minutes back
            int validityMinutesBack = 2;
            CustomSIG0.signMessage(m, sig0KeyProviderService.getSIG0Record(), sig0KeyProviderService.getPrivateSIG0Key(), null, validityMinutesBack);
        }
        loggingService.debug("DNS call signature took " + (System.currentTimeMillis() - init) + " ms");
        init = System.currentTimeMillis();
        loggingService.debug("Sending update to DNS ");
        Message resp = dnsMessageSenderService.sendMessage(m);
        loggingService.debug("DNS Call took " + (System.currentTimeMillis() - init) + " ms");
        return resp;
    }
}
