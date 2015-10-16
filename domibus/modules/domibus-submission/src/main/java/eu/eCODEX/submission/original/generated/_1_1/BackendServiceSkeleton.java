/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1;

import backend.ecodex.org._1_1.*;
import eu.eCODEX.submission.original.generated._1_1.exception.DownloadMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.ListPendingMessagesFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageWithReferenceFault;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

/**
 * The Class BackendServiceSkeleton.
 */
public class BackendServiceSkeleton {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(BackendServiceSkeleton.class);


    /**
     * Send message with reference.
     *
     * @param messagingRequest the messaging request
     * @param sendRequestURL   the send request url
     * @throws SendMessageWithReferenceFault the send message with reference fault
     */
    public SendResponse sendMessageWithReference(final MessagingE messagingRequest, final SendRequestURL sendRequestURL)
            throws SendMessageWithReferenceFault {
        throw new UnsupportedOperationException("sendMessageWithReference");
    }

    /**
     * Send message.
     *
     * @param messagingRequest the messaging request
     * @param sendRequest      the send request
     * @throws SendMessageFault the send message fault
     */
    public SendResponse sendMessage(final MessagingE messagingRequest, final SendRequest sendRequest)
            throws SendMessageFault {

        throw new UnsupportedOperationException("sendMessage");
    }

    /**
     * List pending messages.
     *
     * @param listPendingMessagesRequest the list pending messages request
     * @return the backend.ecodex.org._1_1. list pending messages response
     * @throws ListPendingMessagesFault the list pending messages fault
     */
    public ListPendingMessagesResponse listPendingMessages(final ListPendingMessagesRequest listPendingMessagesRequest)
            throws ListPendingMessagesFault {
        throw new UnsupportedOperationException("listPendingMessages");
    }

    /**
     * Download message.
     *
     * @param downloadMessageResponse the download message response
     * @param downloadMessageRequest  the download message request
     * @return the org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704. messaging e
     * @throws DownloadMessageFault the download message fault
     */
    public MessagingE downloadMessage(final DownloadMessageResponse downloadMessageResponse,
                                      final DownloadMessageRequest downloadMessageRequest) throws DownloadMessageFault {
        throw new UnsupportedOperationException("downloadMessage");
    }

    /**
     * Create partner.
     *
     */
    public CreatePartnershipResponse createPartnership(final CreatePartnershipResponse createPartnershipResponse,
                                      final CreatePartnershipRequest createPartnershipRequest) throws CreatePartnershipFault {
        throw new UnsupportedOperationException("createPartnership");
    }
}
