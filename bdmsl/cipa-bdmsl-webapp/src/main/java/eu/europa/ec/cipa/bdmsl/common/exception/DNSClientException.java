package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class DNSClientException extends TechnicalException {

    public DNSClientException(String message) {
        super(IErrorCodes.DNS_CLIENT_ERROR, message);
    }

    public DNSClientException(String message, Throwable t) {
        super(IErrorCodes.DNS_CLIENT_ERROR, message, t);
    }
}
