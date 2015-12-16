package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.common.exception.DomibusValidationException;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

public interface MessageValidationService {

    /**
     * Validates the {@link MessageBO} and guarantees that all mandatory
     * elements are present in the message.
     *
     * @param messageObject internal message object
     * @throws DomibusValidationException for an invalid message
     */
    public void validate(final MessageBO message);
}
