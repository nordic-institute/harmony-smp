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

package eu.domibus.submission.transformer.impl;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.submission.Submission;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kochc01 on 22.06.2015.
 */


@org.springframework.stereotype.Service
public class SymmetricAS4Transformer implements MessageSubmissionTransformer<Messaging>, MessageRetrievalTransformer<UserMessage> {
    @Autowired
    private SubmissionAS4Transformer submissionAS4Transformer;


    @Override
    public UserMessage transformFromSubmission(final Submission submission, final UserMessage target) throws ValidationException {
        return this.submissionAS4Transformer.transformFromSubmission(submission);
    }

    @Override
    public Submission transformToSubmission(final Messaging messageData) throws ValidationException, TransformationException {
        return this.submissionAS4Transformer.transformFromMessaging(messageData.getUserMessage());
    }
}
