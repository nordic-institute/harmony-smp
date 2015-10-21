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

package eu.domibus.submission.handler;

import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;

/**
 * Implementations of this interface handle the submission of messages from the
 * backend to holodeck.
 *
 * @param <T> Data transfer object
 *            (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the
 *            backend and holodeck
 * @author Christian Koch
 */
public interface MessageSubmitter<T> {

    /**
     * Submits a message to holodeck to be processed.
     *
     * @param messageData the message to be processed
     * @return the messageId of the submitted message
     * @throws ValidationException
     */
    String submit(T messageData) throws ValidationException, TransformationException;
}
