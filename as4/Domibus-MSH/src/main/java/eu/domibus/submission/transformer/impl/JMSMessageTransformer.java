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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.domibus.submission.transformer.impl;


import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import eu.domibus.submission.Submission;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;


/**
 * This class is responsible for transformations from {@link javax.jms.MapMessage} to {@link eu.domibus.submission.Submission} and vice versa
 *
 * @author Padraig
 */

@Service
public class JMSMessageTransformer
        implements MessageRetrievalTransformer<MapMessage>, MessageSubmissionTransformer<MapMessage> {
    public static final String SUBMISSION_JMS_MAPMESSAGE_ACTION = "Action";
    public static final String SUBMISSION_JMS_MAPMESSAGE_SERVICE = "Service";
    public static final String SUBMISSION_JMS_MAPMESSAGE_SERVICE_TYPE = "serviceType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID = "ConversationID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF = "AgreementRef";
    public static final String SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID = "refToMessageId";

    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID = "fromPartyID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE = "fromPartyType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE = "fromRole";

    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID = "toPartyID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE = "toPartyType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_ROLE = "toRole";

    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER = "originalSender";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT = "finalRecipient";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT = "endPointAddress";

    public static final String SUBMISSION_JMS_MAPMESSAGE_PROTOCOL = "protocol";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TOTAL_NUMBER_OF_PAYLOADS = "totalNumberOfPayloads";
    public static final String PAYLOAD_FILE_NAME_FORMAT = "payload_{0}.bin";
    public static final String BODYLOAD_FILE_NAME_FORMAT = "bodyload.bin";
    public static final String MESSAGING_FILE_NAME = "messaging.xml";
    public static final String METADATA_ARTIFACT_NAME = "metadata.xml";
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_PREFIX = "payload-";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT = JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_PREFIX + "{0}";
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_SUFFIX = "-description";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT = JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_SUFFIX;
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_SUFFIX = "-MimeType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT = JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_SUFFIX;
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_SUFFIX = "-MimeContentID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT = JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_SUFFIX;
    private static final Log LOG = LogFactory.getLog(JMSMessageTransformer.class);


    /**
     * Transforms {@link eu.domibus.submission.Submission} to {@link javax.jms.MapMessage}
     *
     * @param submission the message to be transformed     *
     * @return result of the transformation as {@link javax.jms.MapMessage}
     */
    @Override
    public MapMessage transformFromSubmission(final Submission submission, final MapMessage messageOut) {


        try {
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_ACTION, submission.getAction());
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_SERVICE, submission.getService());
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID, submission.getConversationId());

            for (final Submission.Party fromParty : submission.getFromParties()) {
                messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID, fromParty.getPartyId());
                messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE, fromParty.getPartyIdType());
            }

            for (final Submission.Party toParty : submission.getToParties()) {
                messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID, toParty.getPartyId());
                messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE, toParty.getPartyIdType());
            }

            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE, submission.getFromRole());
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_ROLE, submission.getToRole());

            for (final Map.Entry p : submission.getMessageProperties().entrySet()) {
                if (p.getKey().equals(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER)) {
                    messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER, p.getValue().toString());
                }

                if (p.getKey().equals(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT)) {
                    messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT, p.getValue().toString());
                }

                if (p.getKey().equals(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT)) {
                    messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT, p.getValue().toString());
                }
            }

            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROTOCOL, "AS4");
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF, submission.getAgreementRef());
            messageOut.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID, submission.getRefToMessageId());

            int counter = 2;

            for (final Submission.Payload p : submission.getPayloads()) {

                if (p.isInBody()) {
                    messageOut.setBytes(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, 1), p.getPayloadData());
                    messageOut.setStringProperty(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, 1), p.getPayloadProperties().getProperty(Property.MIME_TYPE));
                    messageOut.setStringProperty(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, 1), p.getContentId());
                    if (p.getDescription() != null) {
                        messageOut.setStringProperty(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, 1), p.getDescription());
                    }
                } else {

                    final String payContID = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, counter));
                    final String payDescrip = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, counter));
                    final String propPayload = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, counter));
                    final String payMimeTypeProp = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, counter));
                    messageOut.setBytes(propPayload, p.getPayloadData());
                    messageOut.setStringProperty(payMimeTypeProp, p.getPayloadProperties().getProperty(Property.MIME_TYPE));
                    messageOut.setStringProperty(payContID, p.getContentId());

                    if (p.getDescription() != null) {
                        messageOut.setStringProperty(payDescrip, p.getDescription());
                    }
                    counter++;
                }
            }
            messageOut.setInt(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TOTAL_NUMBER_OF_PAYLOADS, submission.getPayloads().size());
        } catch (final JMSException ex) {
            JMSMessageTransformer.LOG.error("Error while filling the MapMessage", ex);
        }

        return messageOut;
    }

    /**
     * Transforms {@link javax.jms.MapMessage} to {@link eu.domibus.submission.Submission}
     *
     * @param messageIn the message ({@link javax.jms.MapMessage}) to be tranformed
     * @return the result of the transformation as {@link eu.domibus.submission.Submission}
     */
    @Override
    public Submission transformToSubmission(final MapMessage messageIn) {

        final Submission target = new Submission();

        try {

            target.setAction(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_ACTION));
            target.setService(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_SERVICE));
            target.setServiceType(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_SERVICE_TYPE));
            target.setConversationId(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID));
            final String fromPartyID = messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID);
            final String fromPartyType = messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE);
            target.addFromParty(fromPartyID, fromPartyType);
            target.setFromRole(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE));
            final String toPartyID = messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID);
            final String toPartyType = messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE);
            target.addToParty(toPartyID, toPartyType);
            target.setToRole(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TO_ROLE));
            target.addMessageProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER, messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER));
            target.addMessageProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT, messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT));
            target.setRefToMessageId(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID));
            target.setAgreementRef(messageIn.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF));
            final int numPayloads = messageIn.getIntProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_TOTAL_NUMBER_OF_PAYLOADS);


            for (int i = 1; i <= numPayloads; i++) {
                final String propPayload = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT, i));

                final String bodyloadFileName = JMSMessageTransformer.BODYLOAD_FILE_NAME_FORMAT;

                final String contentId;
                final String mimeType;
                String description = null;
                final byte[] payloadData;
                payloadData = messageIn.getBytes(propPayload);
                final String payMimeTypeProp = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT, i));
                mimeType = messageIn.getStringProperty(payMimeTypeProp);
                final String payDescrip = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT, i));

                if (messageIn.getStringProperty(payDescrip) != null) {
                    description = messageIn.getStringProperty(payDescrip);
                }

                final String payContID = String.valueOf(MessageFormat.format(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT, i));

                contentId = messageIn.getStringProperty(payContID);

                final Properties partProperties = new Properties();
                if (mimeType != null && !mimeType.trim().equals("")) {
                    partProperties.setProperty(Property.MIME_TYPE, mimeType);
                }

                target.addPayload(contentId, payloadData, partProperties, i == 1, description, null);
            }


        } catch (final JMSException ex) {
            JMSMessageTransformer.LOG.error("Error while getting properties from MapMessage", ex);
            throw new RuntimeException(ex);
        }

        return target;

    }


}
