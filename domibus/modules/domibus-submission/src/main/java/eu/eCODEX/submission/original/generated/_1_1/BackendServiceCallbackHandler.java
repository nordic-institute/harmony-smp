/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1;

import backend.ecodex.org._1_1.CreatePartnershipResponse;
import backend.ecodex.org._1_1.DownloadMessageResponse;
import backend.ecodex.org._1_1.ListPendingMessagesResponse;

/**
 * The Class BackendServiceCallbackHandler.
 */
public abstract class BackendServiceCallbackHandler {

    /**
     * The client data.
     */
    protected Object clientData;

    /**
     * Instantiates a new backend service callback handler.
     *
     * @param clientData the client data
     */
    public BackendServiceCallbackHandler(final Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Instantiates a new backend service callback handler.
     */
    public BackendServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Gets the client data.
     *
     * @return the client data
     */
    public Object getClientData() {
        return this.clientData;
    }

    /**
     * Receive resultsend message with reference.
     */
    public void receiveResultsendMessageWithReference() {
    }

    /**
     * Receive errorsend message with reference.
     *
     * @param e the e
     */
    public void receiveErrorsendMessageWithReference(final Exception e) {
    }

    /**
     * Receive resultsend message.
     */
    public void receiveResultsendMessage() {
    }

    /**
     * Receive errorsend message.
     *
     * @param e the e
     */
    public void receiveErrorsendMessage(final Exception e) {
    }

    /**
     * Receive resultlist pending messages.
     *
     * @param result the result
     */
    public void receiveResultlistPendingMessages(final ListPendingMessagesResponse result) {
    }

    /**
     * Receive errorlist pending messages.
     *
     * @param e the e
     */
    public void receiveErrorlistPendingMessages(final Exception e) {
    }

    /**
     * Receive resultdownload message.
     *
     * @param result the result
     */
    public void receiveResultdownloadMessage(final DownloadMessageResponse result) {
    }

    /**
     * Receive receiveResultcreatePartnership message.
     *
     * @param result the result
     */
    public void receiveResultcreatePartnership(final CreatePartnershipResponse result) {
    }

    /**
     * Receive errordownload message.
     *
     * @param e the e
     */
    public void receiveErrordownloadMessage(final Exception e) {
    }

    /**
     * Receive errorcreatePartnership message.
     *
     * @param e the e
     */
    public void receiveErrorcreatePartnership(final Exception e) {
    }
}
