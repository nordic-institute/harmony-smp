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
/**
 * @Author Christian Koch
 * @Since 3.0
 */

import eu.domibus.common.MSHRole;
import eu.domibus.common.MessageStatus;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.configuration.model.Mpc;
import eu.domibus.common.configuration.model.Party;
import eu.domibus.common.dao.ErrorLogDao;
import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.MessageType;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.common.model.logging.MessageLogEntry;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.common.validators.PayloadProfileValidator;
import eu.domibus.common.validators.PropertyProfileValidator;
import eu.domibus.ebms3.common.CompressionService;
import eu.domibus.ebms3.common.MessageIdGenerator;
import eu.domibus.ebms3.common.NotificationStatus;
import eu.domibus.ebms3.common.dao.PModeProvider;
import eu.domibus.submission.Submission;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.transformer.impl.SubmissionAS4Transformer;
import eu.domibus.submission.validation.exception.ValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseMessageHandler implements MessageSubmitter<Submission>, MessageRetriever<Submission> {
    private static final Log LOG = LogFactory.getLog(DatabaseMessageHandler.class);
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory ebMS3Of = new eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory();
    @Autowired
    CompressionService compressionService;
    @Autowired
    private SubmissionAS4Transformer transformer;
    @Autowired
    private MessagingDao messagingDao;
    @Autowired
    private MessageLogDao messageLogDao;
    @Autowired
    private ErrorLogDao errorLogDao;
    @Autowired
    private PModeProvider pModeProvider;
    @Autowired
    private MessageIdGenerator messageIdGenerator;
    @Autowired
    private PayloadProfileValidator payloadProfileValidator;
    @Autowired
    private PropertyProfileValidator propertyProfileValidator;

    @Override
    @Transactional
    public Submission downloadMessage(final String messageId) throws ValidationException {
        DatabaseMessageHandler.LOG.info("looking for message with id: " + messageId);
        MessageLogEntry messageLogEntry;
        UserMessage userMessage;
        try {
            userMessage = this.messagingDao.findUserMessageByMessageId(messageId);
            messageLogEntry = this.messageLogDao.findByMessageId(messageId, MSHRole.RECEIVING);
        } catch (final NoResultException e) {
            DatabaseMessageHandler.LOG.warn("Message with id" + messageId + " was not found");
            throw new ValidationException("Message with id" + messageId + " was not found", e);
        }

        messageLogEntry.setDeleted(new Date());
        this.messageLogDao.update(messageLogEntry);
        if (0 == pModeProvider.getRetentionDownloadedByMpcName(messageLogEntry.getMpc())) {
            messagingDao.delete(messageId);
        }
        ;
        return this.transformer.transformFromMessaging(userMessage);
    }


    @Override
    public Collection<String> listPendingMessages() {
        return this.messageLogDao.findUndownloadedUserMessages();
    }


    @Override
    public MessageStatus getMessageStatus(final String messageId) {
        return this.messageLogDao.getMessageStatus(messageId);
    }

    @Override
    public List<ErrorLogEntry> getErrorsForMessage(final String messageId) {
        return this.errorLogDao.getErrorsForMessage(messageId);
    }


    @Override
    @Transactional
    public String submit(final Submission messageData) throws ValidationException, TransformationException {
        final UserMessage m = this.transformer.transformFromSubmission(messageData);
        final MessageInfo messageInfo = m.getMessageInfo();
        if (messageInfo == null) {
            m.setMessageInfo(this.objectFactory.createMessageInfo());
        }
        if (m.getMessageInfo().getMessageId() == null || m.getMessageInfo().getMessageId().trim().isEmpty()) {
            m.getMessageInfo().setMessageId(messageIdGenerator.generateMessageId());
        }
        final String pmodeKey;
        final Messaging message = this.ebMS3Of.createMessaging();
        message.setUserMessage(m);

        try {
            pmodeKey = this.pModeProvider.findPModeKeyForUserMesssage(m);

            Party to = pModeProvider.getReceiverParty(pmodeKey);
            LegConfiguration legConfiguration = this.pModeProvider.getLegConfiguration(pmodeKey);
            Map<Party, Mpc> mpcMap = legConfiguration.getPartyMpcMap();
            String mpc = Mpc.DEFAULT_MPC;
            if (legConfiguration.getDefaultMpc() != null) {
                mpc = legConfiguration.getDefaultMpc().getQualifiedName();
            }
            if (mpcMap != null && mpcMap.containsKey(to)) {
                mpc = mpcMap.get(to).getQualifiedName();
            }
            m.setMpc(mpc);
            this.payloadProfileValidator.validate(message, pmodeKey);
            this.propertyProfileValidator.validate(message, pmodeKey);
        } catch (final EbMS3Exception e) {
            //FIXME: More meaningful error responses for the backend webservice
            throw new ValidationException(e);
        }

        final LegConfiguration legConfiguration = this.pModeProvider.getLegConfiguration(pmodeKey);

        int sendAttemptsMax = 1;

        if (legConfiguration.getReceptionAwareness() != null) {
            sendAttemptsMax = legConfiguration.getReceptionAwareness().getRetryCount();
        }

        try {
            final boolean compressed = this.compressionService.handleCompression(m, legConfiguration);
            DatabaseMessageHandler.LOG.debug("Compression for message with id: " + m.getMessageInfo().getMessageId() + " applied: " + compressed);
        } catch (final EbMS3Exception e) {
            this.errorLogDao.create(new ErrorLogEntry(e));
            throw new ValidationException(e);
        }

        //We do not create MessageIds for SignalMessages, as those should never be submitted via the backend
        this.messagingDao.create(message);
        final MessageLogEntry messageLogEntry = new MessageLogEntry();
        messageLogEntry.setMessageId(m.getMessageInfo().getMessageId());
        messageLogEntry.setMshRole(MSHRole.SENDING);
        messageLogEntry.setReceived(new Date());
        messageLogEntry.setMessageType(MessageType.USER_MESSAGE);
        messageLogEntry.setSendAttempts(0);
        messageLogEntry.setSendAttemptsMax(sendAttemptsMax);
        messageLogEntry.setNextAttempt(messageLogEntry.getReceived());
        messageLogEntry.setNotificationStatus(legConfiguration.getErrorHandling().isBusinessErrorNotifyProducer() ? NotificationStatus.REQUIRED : NotificationStatus.NOT_REQUIRED);
        messageLogEntry.setMessageStatus(MessageStatus.IN_PROGRESS);
        messageLogEntry.setMpc(message.getUserMessage().getMpc());
        this.messageLogDao.create(messageLogEntry);
        return m.getMessageInfo().getMessageId();
    }

}
