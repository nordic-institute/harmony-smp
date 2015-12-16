package eu.europa.ec.digit.domibus.endpoint.handler;

import org.springframework.util.ErrorHandler;

import eu.europa.ec.digit.domibus.common.log.Logger;

public class JMSErrorHandler implements ErrorHandler {

    /* ---- Constants ---- */
    public final Logger log = new  Logger(JMSErrorHandler.class);

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    @Override
    public void handleError(Throwable throwable) {
        log.error("JMS Error: ", throwable);
    }

    /* ---- Getters and Setters ---- */

}
