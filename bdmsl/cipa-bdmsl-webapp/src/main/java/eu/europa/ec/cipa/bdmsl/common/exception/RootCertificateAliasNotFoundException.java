package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class RootCertificateAliasNotFoundException extends TechnicalException {

    public RootCertificateAliasNotFoundException(String message) {
        super(IErrorCodes.ROOT_CERTIFICATE_ALIAS_NOT_FOUND_ERROR, message);
    }


    public RootCertificateAliasNotFoundException(String message, Throwable t) {
        super(IErrorCodes.ROOT_CERTIFICATE_ALIAS_NOT_FOUND_ERROR, message, t);
    }
}
