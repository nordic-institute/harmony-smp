package eu.eCODEX.transport.dto;

import backend.ecodex.org._1_1.SendRequest;
import eu.eCODEX.submission.original.service.BackendServiceImpl;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;


/**
 * Data transfer object (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the backend and
 * holodeck. This is used for the webservice-implementation {@link BackendServiceImpl}
 */

public class BackendMessageIn {

    private final MessagingE messagingEnvelope;
    private final SendRequest sendRequest;

    /**
     * Creates the DTO
     *
     * @param messagingEnvelope the envelope as provided by the webservice request
     * @param sendRequest       the sendrequest as provided by the webservice
     */
    public BackendMessageIn(final MessagingE messagingEnvelope, final SendRequest sendRequest) {
        this.messagingEnvelope = messagingEnvelope;
        this.sendRequest = sendRequest;
    }

    public MessagingE getMessagingEnvelope() {
        return this.messagingEnvelope;
    }

    public SendRequest getSendRequest() {
        return this.sendRequest;
    }
}
