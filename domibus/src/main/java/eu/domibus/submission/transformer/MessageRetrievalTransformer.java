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

package eu.domibus.submission.transformer;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.submission.Submission;
import eu.domibus.submission.validation.exception.ValidationException;

/**
 * Implementations of this interface transsform a message of type
 * {@link Messaging} to an object of type {@literal <T>}
 *
 * @param <U> Data transfer object
 *            (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the
 *            backend and holodeck
 * @author Christian Koch
 * @author Stefan Müller
 * @since 3.0
 */
public interface MessageRetrievalTransformer<U> {

    /**
     * transforms the Messaging to the typed object
     *
     * @param submission the {@link eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging} to be transformed
     * @return the transformed message
     * @throws eu.domibus.submission.validation.exception.ValidationException
     */
    U transformFromSubmission(Submission submission, U target) throws ValidationException;
}
