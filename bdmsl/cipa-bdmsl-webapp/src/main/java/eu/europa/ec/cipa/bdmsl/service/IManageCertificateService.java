package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.common.bo.PrepareChangeCertificateBO;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageCertificateService {

    void prepareChangeCertificate(PrepareChangeCertificateBO prepareChangeCertificateBO) throws
            BusinessException, TechnicalException;

    void changeCertificates() throws BusinessException, TechnicalException;
}
