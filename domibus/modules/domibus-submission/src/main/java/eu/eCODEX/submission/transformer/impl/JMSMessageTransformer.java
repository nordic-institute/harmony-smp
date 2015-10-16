/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.eCODEX.submission.transformer.impl;

import eu.domibus.backend.util.IOUtils;
import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.eCODEX.submission.transformer.MessageRetrievalTransformer;
import eu.eCODEX.submission.transformer.MessageSubmissionTransformer;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static eu.eCODEX.submission.Constants.*;

/**
 * This class is responsible for transformations from {@link javax.jms.MapMessage} to {@link eu.domibus.ebms3.submit.EbMessage} and vice versa
 *
 * @author Padraig
 */
public class JMSMessageTransformer
        implements MessageRetrievalTransformer<MapMessage>, MessageSubmissionTransformer<MapMessage> {

    private static final Logger LOG = Logger.getLogger(JMSMessageTransformer.class);

    private Validator<EbMessage> preRetrievalValidator;
    private Validator<MapMessage> preSubmissionValidator;
    private Validator<EbMessage> postSubmissionValidator;
    private Validator<MapMessage> postRetrievalValidator;

    /**
     * Transforms {@link eu.domibus.ebms3.submit.EbMessage} to {@link javax.jms.MapMessage}
     *
     * @param message    the message to be transformed
     * @param messageOut the {@link javax.jms.MapMessage} object the message is tranformed to
     * @return result of the transformation as {@link javax.jms.MapMessage}
     */
    @Override
    public MapMessage transformFromEbMessage(final EbMessage message, final MapMessage messageOut) {
        final ReceivedUserMsg msg = (ReceivedUserMsg) message;

        try {
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_ACTION, msg.getAction());
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_SERVICE, msg.getService());
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID, msg.getMsgInfo().getConversationId());

            for (Party fromParty : msg.getMsgInfo().getFromParties()) {
                messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID, fromParty.getPartyId());
                messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE, fromParty.getType());
            }

            for (Party toParty : msg.getMsgInfo().getToParties()) {
                messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID, toParty.getPartyId());
                messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE, toParty.getType());
            }

            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE, msg.getMsgInfo().getFromRole());
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_ROLE, msg.getMsgInfo().getToRole());

            for (Property p : msg.getMsgInfo().getMessageProperties()) {
                if (p.getName().equals(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER)) {
                    messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER, p.getValue());
                }

                if (p.getName().equals(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT)) {
                    messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT, p.getValue());
                }

                if (p.getName().equals(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT)) {
                    messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT, p.getValue());
                }
            }

            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROTOCOL, "AS4");
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF, msg.getMsgInfo().getAgreementRef());
            messageOut.setStringProperty(SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID, msg.getMsgInfo().getRefToMessageId());

            int counter = 2;

            for (final PartInfo p : ((ReceivedUserMsg) message).getMsgInfo().getParts()) {

                if (p.isBody()) {
                    messageOut.setBytes(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, 1), p.getPayloadData());
                    messageOut.setStringProperty(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, 1), p.getMimeType());
                    messageOut.setStringProperty(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, 1), p.getCid());
                    if (p.getDescription() != null) {
                        messageOut.setStringProperty(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, 1), p.getDescription());
                    }
                } else {

                    StringBuilder payCId = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, counter)));

                    StringBuilder payDesc = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, counter)));

                    String payContID = payCId.toString();

                    String payDescrip = payDesc.toString();

                    StringBuilder payMimeT = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, counter)));

                    StringBuilder pay = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, counter)));

                    String propPayload = pay.toString();

                    String payMimeTypeProp = payMimeT.toString();

                    messageOut.setBytes(propPayload, p.getPayloadData());
                    messageOut.setStringProperty(payMimeTypeProp, p.getMimeType());
                    messageOut.setStringProperty(payContID, p.getCid());

                    if (p.getDescription() != null) {
                        messageOut.setStringProperty(payDescrip, p.getDescription());
                    }

                    counter++;
                }

            }
        } catch (JMSException ex) {
            LOG.error("Error while filling the MapMessage", ex);
        }

        return messageOut;
    }

    /**
     * Transforms {@link javax.jms.MapMessage} to {@link eu.domibus.ebms3.submit.EbMessage}
     *
     * @param messageIn the message ({@link javax.jms.MapMessage}) to be tranformed
     * @return the result of the transformation as {@link eu.domibus.ebms3.submit.EbMessage}
     * @throws ValidationException
     */
    @Override
    public EbMessage transformToEbMessage(final MapMessage messageIn) throws ValidationException {

        final MsgInfoSet msgInfoSet = new MsgInfoSet();

        final String tempGroup = IOUtils.createTempGroup();
        final Collection<TempStore> attachmentData = new HashSet<TempStore>();

        try {

            final String action = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_ACTION);
            final String service = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_SERVICE);
            final String conversationID = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID);
            final String fromPartyID = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID);
            final String fromPartyType = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE);
            final String fromRole = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE);
            final String toPartyID = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID);
            final String toPartyType = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE);
            final String toRole = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_TO_ROLE);
            final String originalSender = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER);
            final String finalRecipient = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT);
            final String serviceType = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_SERVICE_TYPE);
            final String protocol = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROTOCOL);
            final String refToMessageId = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID);
            final String agreementRef = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF);
            final String endPointAddress = messageIn.getStringProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT);

            Party fromParty = new Party();
            fromParty.setPartyId(fromPartyID);
            fromParty.setType(fromPartyType);

            msgInfoSet.setPmode(
                    Configuration.getPMode(action, service, fromPartyID, fromPartyType, toPartyID, toPartyType));


            msgInfoSet.setConversationId(conversationID);
            msgInfoSet.setAgreementRef(agreementRef);
            msgInfoSet.setRefToMessageId(refToMessageId);

            Producer producer = new Producer();
            producer.setRole(fromRole);

            final Set<Party> parties = new HashSet<Party>();

            parties.add(fromParty);

            producer.setParties(parties);

            msgInfoSet.setProducer(producer);

            msgInfoSet.setLegNumber(1);

            final Properties properties = new Properties();

            properties.addProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT, endPointAddress);
            properties.addProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER, originalSender);
            properties.addProperty(SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT, finalRecipient);

            msgInfoSet.setProperties(properties);

            final int numPayloads = messageIn.getIntProperty(SUBMISSION_JMS_MAPMESSAGE_TOTAL_NUMBER_OF_PAYLOADS);
            final Set<EbmsPayload> payloads = new HashSet<EbmsPayload>();


            for (int i = 1; i < numPayloads + 1; i++) {

                StringBuilder pay = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, i)));

                String propPayload = pay.toString();

                final String bodyloadFileName = BODYLOAD_FILE_NAME_FORMAT;
                if (i == 1) {

                    final EbmsPayload p = new EbmsPayload();
                    TempStore ts = new TempStore();
                    ts.setGroup(tempGroup);
                    ts.setArtifact(bodyloadFileName);

                    ts.setBytes(messageIn.getBytes(propPayload));

                    attachmentData.add(ts);

                    p.setQualifiedFileName(tempGroup + "/" + bodyloadFileName);
                    StringBuilder payMimeT = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, i)));

                    String payMimeTypeProp = payMimeT.toString();

                    p.setContentType(messageIn.getStringProperty(payMimeTypeProp));


                    StringBuilder payDesc = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, i)));

                    String payDescrip = payDesc.toString();

                    String description = null;
                    if (messageIn.getStringProperty(payDescrip) != null) {
                        description = messageIn.getStringProperty(payDescrip);
                    }

                    StringBuilder payCId = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, i)));

                    String payContID = payCId.toString();

                    p.setDescription(description);
                    p.setCid(
                            MessageFormat.format(CID_MESSAGE_FORMAT, messageIn.getStringProperty(payContID)));

                    msgInfoSet.setBodyPayload(p);

                } else {
                    final EbmsPayload p = new EbmsPayload();

                    final String payloadFileName = MessageFormat.format(PAYLOAD_FILE_NAME_FORMAT, i);

                    TempStore ts = new TempStore();
                    ts.setGroup(tempGroup);
                    ts.setArtifact(payloadFileName);

                    ts.setBytes(messageIn.getBytes(propPayload));

                    attachmentData.add(ts);

                    p.setQualifiedFileName(tempGroup + "/" + bodyloadFileName);
                    StringBuilder payMimeT = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, i)));

                    String payMimeTypeProp = payMimeT.toString();
                    p.setContentType(messageIn.getStringProperty(payMimeTypeProp));

                    StringBuilder payDesc = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, i)));

                    String payDescrip = payDesc.toString();

                    String description = null;

                    if (messageIn.getStringProperty(payDescrip) != null) {
                        description = messageIn.getStringProperty(payDescrip);
                    }

                    StringBuilder payCId = new StringBuilder(String.valueOf(MessageFormat.format(SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, i)));

                    String payContID = payCId.toString();
                    p.setDescription(description);
                    p.setCid(
                            MessageFormat.format(CID_MESSAGE_FORMAT, messageIn.getStringProperty(payContID)));

                    payloads.add(p);
                }
            }

            if (!payloads.isEmpty()) {
                msgInfoSet.getPayloads().setPayloads(payloads);
            }

        } catch (JMSException ex) {
            LOG.error("Error while getting properties from MapMessage", ex);
            throw new RuntimeException(ex);
        }

        try {

            final UserMsgToPush usrMsgToPush = new UserMsgToPush(tempGroup, msgInfoSet, attachmentData);

            postSubmissionValidator.validate(usrMsgToPush);

            return usrMsgToPush;

        } catch (ValidationException ex) {
            LOG.error("Error during validation of message", ex);

            return null;
        }

    }

    /**
     * Setter for postRetrievalValidator, in order to be able to inject the postRetrievalValidator bean.
     *
     * @param validator
     */
    @Override
    public void setPostRetrievalValidator(Validator<MapMessage> validator) {
        this.postRetrievalValidator = validator;
    }

    /**
     * Setter for preRetrievalValidator, in order to be able to inject the preRetrievalValidator bean.
     *
     * @param validator
     */
    @Override
    public void setPreRetrievalValidator(Validator<EbMessage> validator) {
        this.preRetrievalValidator = validator;
    }

    /**
     * Setter for postSubmissionValidator, in order to be able to inject the postSubmissionValidator bean.
     *
     * @param validator
     */
    @Override
    public void setPostSubmissionValidator(Validator<EbMessage> validator) {
        this.postSubmissionValidator = validator;
    }

    /**
     * Setter for preSubmissionValidator, in order to be able to inject the preSubmissionValidator bean.
     *
     * @param validator
     */
    @Override
    public void setPreSubmissionValidator(Validator<MapMessage> validator) {
        this.preSubmissionValidator = validator;
    }

}
