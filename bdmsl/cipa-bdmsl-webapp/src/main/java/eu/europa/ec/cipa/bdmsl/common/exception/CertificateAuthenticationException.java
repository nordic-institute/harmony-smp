package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class CertificateAuthenticationException extends TechnicalException {

    public CertificateAuthenticationException(String message) {
        super(IErrorCodes.CERTIFICATE_AUTHENTICATION_ERROR, message);
    }

    public CertificateAuthenticationException(String message, Throwable t) {
        super(IErrorCodes.CERTIFICATE_AUTHENTICATION_ERROR, message, t);
    }
}
