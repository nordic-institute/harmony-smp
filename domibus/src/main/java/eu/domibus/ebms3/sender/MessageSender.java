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

package eu.domibus.ebms3.sender;

import eu.domibus.common.MSHRole;
import eu.domibus.common.MessageStatus;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.dao.ErrorLogDao;
import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.common.exception.ConfigurationException;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.common.model.logging.MessageLogEntry;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.ebms3.common.dao.PModeProvider;
import eu.domibus.submission.AbstractBackendConnector;
import eu.domibus.submission.BackendConnector;
import eu.domibus.submission.MessageMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;

//import eu.domibus.ebms3.pmode.PModeFinder;
//import eu.domibus.ebms3.pmode.model.PMode;

/**
 * This class is responsible for the handling of outgoing messages.
 *
 * @author Christian Koch
 * @author Stefan Müller
 * @since 3.0
 */

@Service
public class MessageSender {
    private static final Log LOG = LogFactory.getLog(MessageSender.class);

    private final String UNRECOVERABLE_ERROR_RETRY = "domibus.dispatch.ebms.error.unrecoverable.retry";

    @Autowired
    private ErrorLogDao errorLogDao;

    @Autowired
    private MessagingDao messagingDao;

    @Autowired
    private MessageLogDao messageLogDao;

    @Autowired
    private PModeProvider pModeProvider;

    @Autowired
    private MSHDispatcher mshDispatcher;

    @Autowired
    private EbMS3MessageBuilder messageBuilder;

    @Autowired
    private ReliabilityChecker reliabilityChecker;

    @Autowired
    private EbmsErrorChecker ebmsErrorChecker;


    @Resource(name = "backends")
    private List<AbstractBackendConnector> backends;

    /**
     * This method starts the send process for all unsent outgoing messages
     *
     * @return the amount of messages that have been sent
     */
    public int sendAllUserMessages() {

        final List<String> messagesToSend = this.messageLogDao.findUnsentMessages();
        //To avoid huge memory usage we load each message to send seperately from the db
        int sent = 0;
        for (final String messageId : messagesToSend) {
            try {
                this.sendUserMessage(messageId);
            } catch (ConfigurationException e) {
                MessageSender.LOG.error("Could not process message with id: " + messageId, e);
                continue;
            }
            ++sent;
        }
        return sent;
    }

    /**
     * This method gets called from {@link MessageSender#sendAllUserMessages()}
     * for all unsent messages
     *
     * @param messageId id of the message to send
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void sendUserMessage(final String messageId) {
        boolean reliabilityCheckSuccessful = false;
        EbmsErrorChecker.CheckResult errorCheckResult = null;

        LegConfiguration legConfiguration = null;
        String pModeKey = null;
        if (MessageSender.LOG.isTraceEnabled()) {
            MessageSender.LOG.trace("Transaction active: " + TransactionSynchronizationManager.getCurrentTransactionName());
        }
        final UserMessage userMessage = this.messagingDao.findUserMessageByMessageId(messageId);
        try {
            pModeKey = this.pModeProvider.findPModeKeyForUserMesssage(userMessage);
            legConfiguration = this.pModeProvider.getLegConfiguration(pModeKey);

            MessageSender.LOG.debug("PMode found : " + pModeKey);
            SOAPMessage soapMessage = this.messageBuilder.buildSOAPMessage(userMessage, legConfiguration);
            SOAPMessage response = this.mshDispatcher.dispatch(soapMessage, pModeKey);
            errorCheckResult = this.ebmsErrorChecker.check(soapMessage, response, pModeKey);
            if (EbmsErrorChecker.CheckResult.ERROR.equals(errorCheckResult)) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0004, "Problem occured during marshalling", messageId, null, MSHRole.SENDING);
            }
            reliabilityCheckSuccessful = this.reliabilityChecker.check(soapMessage, response, pModeKey);
        } catch (SOAPFaultException f) {
            if (f.getCause() instanceof Fault && f.getCause().getCause() instanceof EbMS3Exception) {
                this.handleEbms3Exception((EbMS3Exception) f.getCause().getCause(), messageId, legConfiguration);
            }

        } catch (EbMS3Exception e) {
            this.handleEbms3Exception(e, messageId, legConfiguration);
        } finally {
            if (reliabilityCheckSuccessful) {

                switch (errorCheckResult) {
                    case OK:
                        this.messageLogDao.setMessageAsSent(messageId);
                        break;
                    case WARNING:
                        this.messageLogDao.setMessageAsSentWithWarnings(messageId);
                        break;
                    case ERROR:
                        assert false;
                        break;
                    default:
                        assert false;
                }
            } else {
                this.updateRetryLogging(messageId, legConfiguration);
            }

        }
    }

    /**
     * This method is responsible for the ebMS3 error handling (creation of errorlogs and marking message as sent)
     *
     * @param exceptionToHandle the exception {@link eu.domibus.common.exception.EbMS3Exception} that needs to be handled
     * @param messageId         id of the message the exception belongs to
     */
    private void handleEbms3Exception(EbMS3Exception exceptionToHandle, String messageId, LegConfiguration legConfiguration) {
        exceptionToHandle.setRefToMessageId(messageId);
        if (!exceptionToHandle.isRecoverable() && !Boolean.parseBoolean(System.getProperty(UNRECOVERABLE_ERROR_RETRY))) {
            messageLogDao.setMessageAsSent(messageId);
        }

        exceptionToHandle.setMshRole(MSHRole.SENDING);
        MessageSender.LOG.error(exceptionToHandle);
        this.errorLogDao.create(new ErrorLogEntry(exceptionToHandle));

        MessageMetadata metadata = new MessageMetadata(messageId, legConfiguration.getService(), legConfiguration.getAction(), MessageMetadata.Type.ERROR);
        this.notifyBackends(metadata);
    }

    @Async
    private void notifyBackends(MessageMetadata metadata) {


        for (BackendConnector backend : backends) {
            if (backend.isResponsible(metadata)) {
                backend.messageNotification(metadata);
                break;
            }


        }
    }

    /**
     * This method is responsible for the handling of retries for a given message
     *
     * @param messageId        id of the message that needs to be retried
     * @param legConfiguration processing information for the message
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void updateRetryLogging(String messageId, LegConfiguration legConfiguration) {
        MessageLogEntry messageLogEntry = this.messageLogDao.findByMessageId(messageId, MSHRole.SENDING);
        if (messageLogEntry.getSendAttempts() < messageLogEntry.getSendAttemptsMax() && messageLogEntry.getNextAttempt().getTime() < System.currentTimeMillis()) {
            messageLogEntry.setSendAttempts(messageLogEntry.getSendAttempts() + 1);
            if (legConfiguration.getReceptionAwareness() != null) {
                messageLogEntry.setNextAttempt(legConfiguration.getReceptionAwareness().getStrategy().getAlgorithm().compute(messageLogEntry.getNextAttempt(), messageLogEntry.getSendAttemptsMax(), legConfiguration.getReceptionAwareness().getRetryTimeout()));
            }

        } else { // mark message as ultimately failed and deleted if max retries reached
            messageLogEntry.setMessageStatus(MessageStatus.FAILED);
        }

        this.messageLogDao.update(messageLogEntry);
    }


}
