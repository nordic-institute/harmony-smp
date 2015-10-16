package eu.eCODEX.submission.handler.impl;

import eu.domibus.common.persistent.TempStoreDAO;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.submit.EbMessage;
import eu.eCODEX.submission.handler.MessageRetriever;
import eu.eCODEX.submission.handler.MessageSubmitter;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatus;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatusDAO;
import eu.eCODEX.submission.transformer.MessageRetrievalTransformer;
import eu.eCODEX.submission.transformer.MessageSubmissionTransformer;
import eu.eCODEX.submission.validation.exception.ValidationException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @param <T>
 * @author Christian Koch
 *         This Class handles the interaction between the backend (JMS/Webservice) and the database.
 */

public class DbMessageHandler<T, U> implements MessageSubmitter<T>, MessageRetriever<U> {

    private MessageSubmissionTransformer<T> messageSubmissionTransformer;
    private MessageRetrievalTransformer<U> messageRetrievalTransformer;

    private final UserMsgToPushDAO userMsgToPushDAO = new UserMsgToPushDAO();
    private final ReceivedUserMsgDAO rumd = new ReceivedUserMsgDAO();
    private final ReceivedUserMsgStatusDAO rumsd = new ReceivedUserMsgStatusDAO();
    private final TempStoreDAO tsd = new TempStoreDAO();

    public DbMessageHandler() {
    }

    /**
     * @param submissionTransformer a {@link MessageSubmissionTransformer} which handles the mapping of the incomming message to an {@link eu.domibus.ebms3.submit.EbMessage}
     * @param retrievalTransformer  a {@link MessageRetrievalTransformer} which handles the mapping of the outgoing message to an {@link eu.domibus.ebms3.submit.EbMessage}
     */
    public DbMessageHandler(final MessageSubmissionTransformer<T> submissionTransformer,
                            final MessageRetrievalTransformer<U> retrievalTransformer) {
        this.messageSubmissionTransformer = submissionTransformer;
        this.messageRetrievalTransformer = retrievalTransformer;

    }

    @Override
    public U downloadMessage(final String messageId, final U data) throws ValidationException {
        final List<ReceivedUserMsg> msg = this.rumd.findByMessageId(messageId);

        U res = this.messageRetrievalTransformer.transformFromEbMessage(msg.get(0), data);

        ReceivedUserMsgStatus receivedUserMsgStatus = this.rumsd.findByReceivedUserMsgId(msg.get(0).getId());
        receivedUserMsgStatus.setDownloaded(new Date());
        this.rumsd.update(receivedUserMsgStatus);

        return res;
    }

    @Override
    public Collection<String> listPendingMessages() {
        return this.rumsd.listPendingMessageIds(50);
    }

    @Override
    public U downloadNextMessage(final U data) throws ValidationException {
        final ReceivedUserMsg msg = this.rumd.findNextUndownloaded();
        final U res = this.messageRetrievalTransformer.transformFromEbMessage(msg, data);

        ReceivedUserMsgStatus receivedUserMsgStatus = this.rumsd.findByReceivedUserMsgId(msg.getId());
        receivedUserMsgStatus.setDownloaded(new Date());
        this.rumsd.update(receivedUserMsgStatus);

        return res;
    }

    @Override
    public String submit(final T messageData) throws ValidationException {
        
        final EbMessage message = this.messageSubmissionTransformer.transformToEbMessage(messageData);

        if (message instanceof UserMsgToPull) {
            return this.savePullMsgToDb((UserMsgToPull) message);
        }

        if (message instanceof UserMsgToPush) {
            return this.savePushMsgToDb((UserMsgToPush) message);
        }

        throw new IllegalArgumentException(
                "Expecting UserMsgToPush or UserMsgToPull got " + message.getClass().getName());
    }

    /**
     * Persists the {@link UserMsgToPush} into the DB
     *
     * @param message the {@link UserMsgToPush}
     * @return the messageId of the persisted {@link UserMsgToPush}
     */
    private String savePushMsgToDb(final UserMsgToPush message) {
        //        this.tsd.persistAll(message.getAttachmentData());
        this.userMsgToPushDAO.persist(message);
        return message.getMessageId();
    }

    /**
     * Persists the {@link UserMsgToPull} into the DB
     *
     * @param message the {@link UserMsgToPull}
     * @return the messageId of the persisted {@link UserMsgToPull}
     * @throws UnsupportedOperationException as there is no support in holodeck for push messages this feature is not implemented yet
     */
    private String savePullMsgToDb(@SuppressWarnings("UnusedParameters") final UserMsgToPull message) {
        throw new UnsupportedOperationException("Pull currently not supported");
    }

    public void setMessageSubmissionTransformer(MessageSubmissionTransformer<T> messageSubmissionTransformer) {
        this.messageSubmissionTransformer = messageSubmissionTransformer;
    }

    public void setMessageRetrievalTransformer(MessageRetrievalTransformer<U> messageRetrievalTransformer) {
        this.messageRetrievalTransformer = messageRetrievalTransformer;
    }
}
