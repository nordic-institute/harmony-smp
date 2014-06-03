/*
 * 
 */
package eu.domibus.backend.validator._1_1;

import org.apache.log4j.Logger;
import eu.domibus.backend.service._1_1.exception.DownloadMessageServiceException;
import org.springframework.stereotype.Service;

/**
 * The Class ListPendingMessagesValidator.
 */
@Service("ListPendingMessagesValidator_1_1")
public class ListPendingMessagesValidator {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(ListPendingMessagesValidator.class);

    /**
     * Validate.
     *
     * @param listPendingMessagesRequest the list pending messages request
     * @throws DownloadMessageServiceException
     *          the download message service exception
     */
    public void validate(final backend.ecodex.org._1_1.ListPendingMessagesRequest listPendingMessagesRequest)
            throws DownloadMessageServiceException {
        log.debug("Validating ListPendingMessages");
    }
}
