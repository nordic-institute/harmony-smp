/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.submission;

import eu.domibus.common.MessageStatus;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.submission.handler.MessageRetriever;
import eu.domibus.submission.handler.MessageSubmitter;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by kochc01 on 22.06.2015.
 */
public abstract class AbstractBackendConnector<U, T> implements BackendConnector<U, T> {

    private final String name;
    
    @Autowired
    protected MessageRetriever<Submission> messageRetriever;
    @Autowired
    protected MessageSubmitter<Submission> messageSubmitter;

    public AbstractBackendConnector(String name) {
        this.name = name;
    }
    
    public abstract MessageSubmissionTransformer<U> getMessageSubmissionTransformer();

    public abstract MessageRetrievalTransformer<T> getMessageRetrievalTransformer();

    @Override
    public String submit(final U message) throws ValidationException, TransformationException {
        return this.messageSubmitter.submit(this.getMessageSubmissionTransformer().transformToSubmission(message));
    }

    /**
     * provides the message with the corresponding messageId
     *
     * @param messageId the messageId of the message to retrieve
     * @return the message object with the given messageId
     * @throws eu.domibus.submission.validation.exception.ValidationException
     */
    @Override
    public T downloadMessage(final String messageId, final T target) throws ValidationException {
        return this.getMessageRetrievalTransformer().transformFromSubmission(this.messageRetriever.downloadMessage(messageId), target);
    }

    /**
     * provides a list of messageIds which have not been downloaded yet
     *
     * @return a list of messages that have not been downloaded yet
     */
    @Override
    public Collection<String> listPendingMessages() {
        return this.messageRetriever.listPendingMessages();
    }

    /**
     * Returns message status {@link eu.domibus.common.MessageStatus} for message with messageid
     *
     * @param messageId id of the message the status is requested for
     * @return the message status {@link eu.domibus.common.MessageStatus}
     */
    @Override
    public MessageStatus getMessageStatus(final String messageId) {
        return this.messageRetriever.getMessageStatus(messageId);
    }

    /**
     * Returns List {@link java.util.List} of error logs {@link eu.domibus.common.model.logging.ErrorLogEntry} for message with messageid
     *
     * @param messageId id of the message the errors are requested for
     * @return the list of error log entries {@link java.util.List<eu.domibus.common.model.logging.ErrorLogEntry>}
     */
    @Override
    public List<ErrorLogEntry> getErrorsForMessage(final String messageId) {
        return this.messageRetriever.getErrorsForMessage(messageId);
    }

    @Override
    public Future<Boolean> messageNotification(final MessageMetadata metadata) {
        if (!this.isResponsible(metadata)) {
            return null;
        }
        final Future<Boolean> messageDelivered;
        messageDelivered = this.deliverMessage(metadata);
        return messageDelivered;
    }

    public abstract boolean isResponsible(MessageMetadata metadata);

    public abstract Future<Boolean> deliverMessage(MessageMetadata metadata);

    public String getName() {
        return name;
    }
}
