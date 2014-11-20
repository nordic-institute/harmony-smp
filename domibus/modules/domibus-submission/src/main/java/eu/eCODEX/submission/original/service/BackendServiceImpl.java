package eu.eCODEX.submission.original.service;

import backend.ecodex.org._1_1.*;
import eu.eCODEX.submission.handler.MessageRetriever;
import eu.eCODEX.submission.handler.MessageSubmitter;
import eu.eCODEX.submission.original.generated._1_1.BackendServiceSkeleton;
import eu.eCODEX.submission.original.generated._1_1.exception.DownloadMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.ListPendingMessagesFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageWithReferenceFault;
import eu.eCODEX.submission.validation.exception.ValidationException;
import eu.eCODEX.transport.dto.BackendMessageIn;
import eu.eCODEX.transport.dto.BackendMessageOut;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

import java.util.Collection;


public class BackendServiceImpl extends BackendServiceSkeleton {

    private static final Logger LOG = Logger.getLogger(BackendServiceImpl.class);

    private MessageRetriever<BackendMessageOut> messageRetriever;
    private MessageSubmitter<BackendMessageIn> messageSubmitter;


    @Override
    public SendResponse sendMessageWithReference(final MessagingE messagingRequest, final SendRequestURL sendRequestURL)
            throws SendMessageWithReferenceFault {
        return super.sendMessageWithReference(messagingRequest, sendRequestURL);
    }

    @Override
    public SendResponse sendMessage(final MessagingE messagingRequest, final SendRequest sendRequest)
            throws SendMessageFault {


        try {
            final String messageId = this.messageSubmitter.submit(new BackendMessageIn(messagingRequest, sendRequest));
            final SendResponse sendResponse = new SendResponse();
            sendResponse.addMessageID(messageId);

            return sendResponse;
        } catch (RuntimeException e) {
            BackendServiceImpl.LOG.error("Error while processing message", e);
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new SendMessageFault("Error while processing message, please contact the server administrator");
        } catch (ValidationException e) {
            LOG.error(e);
            throw new SendMessageFault("Error while processing message, please contact the server administrator");
        }

    }

    @Override
    public ListPendingMessagesResponse listPendingMessages(final ListPendingMessagesRequest listPendingMessagesRequest)
            throws ListPendingMessagesFault {

        final Collection<String> ids = this.messageRetriever.listPendingMessages();
        final String[] messageIds = ids.toArray(new String[ids.size()]);
        final ListPendingMessagesResponse response = new ListPendingMessagesResponse();
        response.setMessageID(messageIds);
        return response;
    }

    @Override
    public MessagingE downloadMessage(final DownloadMessageResponse downloadMessageResponse,
                                      final DownloadMessageRequest downloadMessageRequest) throws DownloadMessageFault {
        // TODO: validate request
        // TODO: order of receivedMessages is not considered
        BackendMessageOut receivedMsg = null;

        final String msgId = downloadMessageRequest.getMessageID();

        try {
            if ((msgId == null) || msgId.isEmpty()) {
                receivedMsg = this.messageRetriever.downloadNextMessage(new BackendMessageOut(downloadMessageResponse));
            } else {
                receivedMsg =
                        this.messageRetriever.downloadMessage(msgId, new BackendMessageOut(downloadMessageResponse));
            }
        } catch (ValidationException e) {
            LOG.error(e);
            throw new DownloadMessageFault("Error while processing message, please contact the server administrator");
        }

        return receivedMsg.getMessagingE();
    }

    /**
     * Setter for messageRetreiver, in order to be able to inject the messageRetriever bean.
     *
     * @param messageRetriever
     */
    public void setMessageRetriever(MessageRetriever<BackendMessageOut> messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    /**
     * Setter for messageSubmitter, in order to be able to inject the messageSubmitter bean.
     *
     * @param messageSubmitter
     */
    public void setMessageSubmitter(MessageSubmitter<BackendMessageIn> messageSubmitter) {
        this.messageSubmitter = messageSubmitter;
    }
}
