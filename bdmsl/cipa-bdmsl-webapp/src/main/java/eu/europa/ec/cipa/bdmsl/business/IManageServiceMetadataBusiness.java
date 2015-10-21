package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageServiceMetadataBusiness {
    ServiceMetadataPublisherBO read(String id) throws
            BusinessException, TechnicalException;

    void validateSMPData(ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException;

    void verifySMPNotExist(String smpId) throws
            BusinessException, TechnicalException;

    void createSMP(ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException;

    void createCurrentCertificate() throws BusinessException, TechnicalException;

    CertificateBO findCertificate(String name) throws
            BusinessException, TechnicalException;

    ServiceMetadataPublisherBO verifySMPExist(String smpId) throws
            BusinessException, TechnicalException;

    void deleteSMP(ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException;

    void validateSMPId(String smpId) throws
            BusinessException, TechnicalException;

    void updateSMP(ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException;

    void checkNoMigrationPlanned(String smpId) throws BusinessException, TechnicalException;
}
