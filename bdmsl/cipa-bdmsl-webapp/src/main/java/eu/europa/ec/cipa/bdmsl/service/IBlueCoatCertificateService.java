package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.security.CertificateDetails;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface IBlueCoatCertificateService {
    /**
     * Validate a certificate sent by the BlueCoat server in the HTTP header
     *
     * @param certificate the certificate sent by the BlueCoat server in the HTTP header
     * @return true if the certificate is valid, false otherwise
     * @throws TechnicalException a technical exception
     * @throws BusinessException  a business exception
     */
    boolean isBlueCoatClientCertificateValid(final CertificateDetails certificate) throws TechnicalException, BusinessException;
}
