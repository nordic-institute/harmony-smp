package eu.europa.ec.digit.domibus.endpoint.handler.domibus;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import eu.domibus.common.exception.EbMS3Exception;
import eu.europa.ec.digit.domibus.common.aggregate.components.FaultDetail;
import eu.europa.ec.digit.domibus.common.aggregate.components.ObjectFactory;
import eu.europa.ec.digit.domibus.common.exception.DomibusException;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.FaultResponse;

@Component
public class DomibusBasicExceptionHandler {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */
	
    @Autowired
    private MessageSource messageSource = null;

    /* ---- Business Methods ---- */
    
    /**
     * Handles attachment exception
     *
     * @param exception exception
     */
    public void handleException(Exception exception) throws FaultResponse {
        FaultDetail detail = (new ObjectFactory()).createFaultDetail();
        if (exception instanceof DomibusException) {
            DomibusException de = (DomibusException) exception;
            detail.setDescription("DOMIBUS");
            detail.setResponseCode(de.getMessageKey());
            String description = buildErrorMessage(de);
            String message = messageSource.getMessage(
                    de.getMessageKey(),
                    de.getMessageParameters(),
                    de.getLocale()) + description;
            detail.setMessage(message);
        } else {
            detail.setDescription("SYSTEM");
            detail.setMessage(exception.getMessage());

        }
        throw new FaultResponse("Error Occured on the DomibusBasicEndpoint", detail);
    }

    private String buildErrorMessage(Throwable e) {
        final int MAX_CAUSES = 10;
        String message = e.getMessage() != null ? e.getMessage() : "";

        Throwable cause = e.getCause();
        int numberOfCauses = 0;
        while (cause != null && numberOfCauses < MAX_CAUSES) {
            String str;
            if (cause instanceof EbMS3Exception) {
                str = ((EbMS3Exception) cause).getErrorDetail() != null ? ((EbMS3Exception) cause).getErrorDetail() : "";
            } else {
                str = cause.getMessage() != null ? cause.getMessage() : "";
            }
            message += ". " + str;
            numberOfCauses++;
            cause = cause.getCause();
        }

        return message;
    }
    
    /* ---- Getters and Setters ---- */

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
