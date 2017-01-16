package eu.europa.ec.cipa.smp.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by migueti on 13/01/2017.
 */
public class BadRequestException extends RuntimeException {
    private ErrorResponse.BusinessCode businessCode;

    private static final Logger s_aLogger = LoggerFactory.getLogger (NotFoundException.class);

    public BadRequestException(ErrorResponse.BusinessCode businessCode, String sMsg) {
        super(sMsg);
        this.businessCode = businessCode;

        // Always log!
        s_aLogger.warn (sMsg);
    }

    ErrorResponse.BusinessCode getBusinessCode() {
        return businessCode;
    }
}
