package eu.europa.ec.cipa.smp.server.services;

import eu.europa.ec.cipa.smp.server.security.CertificateDetails;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface IBlueCoatCertificateService {

    boolean isBlueCoatClientCertificateValid(final CertificateDetails certificate);

    void validateBlueCoatClientCertificate(final CertificateDetails certificate) throws Exception;
}
