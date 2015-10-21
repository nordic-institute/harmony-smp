package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface ISmpDAO {
    /**
     * Retrieves the Service Metadata Publisher record for the service metadata.
     * @param serviceMetadataPublisherID the unique ID
     *           of the Service Metadata Publisher for which the record is required
     * @return ServiceMetadataPublisherBO the service metadata publisher
     */

    ServiceMetadataPublisherBO findSMP(String serviceMetadataPublisherID) throws TechnicalException;

    void createSMP(ServiceMetadataPublisherBO smpBO, String certificateId) throws TechnicalException;

    void deleteSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    void updateSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    void changeCertificateForSMP(Long id, Long newCertificateId) throws TechnicalException;
}
