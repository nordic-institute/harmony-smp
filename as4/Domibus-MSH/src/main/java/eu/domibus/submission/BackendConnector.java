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
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * TODO: add class description
 */
public interface BackendConnector<U, T> {

    public abstract MessageSubmissionTransformer<U> getMessageSubmissionTransformer();

    public abstract MessageRetrievalTransformer<T> getMessageRetrievalTransformer();


    public String submit(final U message) throws ValidationException, TransformationException;

    public T downloadMessage(final String messageId, final T target) throws ValidationException;


    public Collection<String> listPendingMessages();

    public MessageStatus getMessageStatus(final String messageId);

    public List<ErrorLogEntry> getErrorsForMessage(final String messageId);

    public Future<Boolean> messageNotification(final MessageMetadata metadata);

    public abstract boolean isResponsible(MessageMetadata metadata);

    public abstract Future<Boolean> deliverMessage(MessageMetadata metadata);

    public String getName();
}
