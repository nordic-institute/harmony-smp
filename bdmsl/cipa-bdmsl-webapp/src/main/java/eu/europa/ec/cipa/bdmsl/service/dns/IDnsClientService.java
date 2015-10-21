package eu.europa.ec.cipa.bdmsl.service.dns;

import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.xbill.DNS.Record;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IDnsClientService {
    void createDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    List<Record> getAllRecords(String dnsZone) throws TechnicalException;

    /**
     * Delete the DNS record of a SMP
     *
     * @param smpBO the SMP
     * @throws TechnicalException a technical exception
     */
    void deleteDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    void updateDNSRecordsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    void createDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException;

    void deleteDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException;

    void updateDNSRecordsForParticipant(ParticipantBO participantBO) throws TechnicalException;

    void deleteDNSRecordsForParticipants(List<ParticipantBO> participantBOList) throws TechnicalException;

    void createDNSRecordsForParticipants(List<ParticipantBO> participantBOList) throws TechnicalException;
}
