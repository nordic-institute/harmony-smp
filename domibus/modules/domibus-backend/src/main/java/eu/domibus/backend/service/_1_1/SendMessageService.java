/*
 * 
 */
package eu.domibus.backend.service._1_1;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.PayloadURLType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import eu.domibus.backend.service._1_1.exception.SendMessageServiceException;
import eu.domibus.backend.service._1_1.helper.SendMessageHelper;
import eu.domibus.backend.util.Converter_1_1;
import eu.domibus.backend.util.IOUtils;
import eu.domibus.backend.validator._1_1.SendMessageValidator;
import eu.domibus.backend.validator._1_1.SendMessageWithReferenceValidator;
import eu.domibus.ebms3.persistent.EbmsPayload;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class SendMessageService.
 */
@Service("SendMessageService_1_1")
public class SendMessageService {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(SendMessageService.class);

    /**
     * The send message helper.
     */
    @Autowired
    @Qualifier("SendMessageHelper_1_1")
    private SendMessageHelper sendMessageHelper;

    /**
     * The send message validator.
     */
    @Autowired
    @Qualifier("SendMessageValidator_1_1")
    private SendMessageValidator sendMessageValidator;

    /**
     * The send message with reference validator.
     */
    @Autowired
    @Qualifier("SendMessageWithReferenceValidator_1_1")
    private SendMessageWithReferenceValidator sendMessageWithReferenceValidator;

    /**
     * Send message.
     *
     * @param messaging   the messaging
     * @param sendRequest the send request
     * @throws SendMessageServiceException the send message service exception
     */
    public backend.ecodex.org._1_1.SendResponse sendMessage(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging,
            final backend.ecodex.org._1_1.SendRequest sendRequest) throws SendMessageServiceException {
        log.debug("Called SendMessageService.sendMessage");

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], "", "SendMessageService",
                                                 "sendMessage",
                                                 eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_INIT_STATUS));

        sendMessageValidator.validate(messaging, sendRequest);

        final MsgInfoSet msgInfoSet =
                Converter_1_1.convertUserMessageToMsgInfoSet(messaging.getMessaging().getUserMessage()[0]);

        final String action =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString();
        final String fromPartyid = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                .getNonEmptyString();

        String fromPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType() != null) {
            log.info(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType()
                                                                                                        .getNonEmptyString());
            fromPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType()
                                                                                                         .getNonEmptyString();
        }

        // String fromPartyidType =
        // messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType().getNonEmptyString();
        final String toPartyid =
                messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getNonEmptyString();
        String toPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType() != null) {
            log.info(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                        .getNonEmptyString());
            toPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                       .getNonEmptyString();
            ;
        }

        // log.info("ToPartyIdType: "+toPartyidType);
        // log.info("FromPartyIdType: "+fromPartyidType);
        // String toPartyidType =
        // messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType().getNonEmptyString();
        final String service =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService().getNonEmptyString();

        final eu.domibus.ebms3.config.PMode pmode = eu.domibus.ebms3.module.Configuration
                .getPModeO(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType);

        msgInfoSet.setPmode(eu.domibus.ebms3.module.Configuration
                                    .getPMode(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType));

        final File tempDir = eu.domibus.backend.util.IOUtils.createTempDir();
        try {
            int counter = 0;
            {

                final PartInfo partInfo =
                        messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

                final String bodyloadFileName = eu.domibus.backend.module.Constants.BODYLOAD_FILE_NAME_FORMAT;

                final EbmsPayload p = new EbmsPayload();

                OutputStream tmpFileOutputStream = null;
                try {
                    tmpFileOutputStream = new FileOutputStream(new File(tempDir, bodyloadFileName));
                    org.apache.commons.io.IOUtils
                            .write(IOUtils.toByteArray(sendRequest.getBodyload().getBase64Binary()),
                                   tmpFileOutputStream);
                } catch (FileNotFoundException fnfe) {
                    throw fnfe;
                } catch (IOException ioe) {
                    throw ioe;
                } finally {
                    if (tmpFileOutputStream != null) {
                        tmpFileOutputStream.close();
                    }
                }

                p.setFile(bodyloadFileName);

                String description = null;
                try {
                    description = messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                            .getDescription().getNonEmptyString();
                } catch (Exception e) {
                }
                p.setDescription(description);
                if (sendRequest.getBodyload().getContentType() != null) {
                    p.setContentType(sendRequest.getBodyload().getContentType().getContentType_type0());
                }

                {
                    // p.setCid(sendRequest.getBodyload().getPayloadId().toString());

                    if (partInfo.getPartProperties() != null && partInfo.getPartProperties().getProperty().length > 0) {
                        for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property property : partInfo
                                .getPartProperties().getProperty()) {
                            p.addPartProperties(property.getName().getNonEmptyString(), property.getNonEmptyString());
                        }
                    }
                }

                msgInfoSet.setBodyPayload(p);

                counter++;
            }

            if (sendRequest.getPayload() != null && sendRequest.getPayload().length > 0) {
                final Set<EbmsPayload> payloads = new HashSet<EbmsPayload>();

                for (final PayloadType payloadType : sendRequest.getPayload()) {
                    final javax.activation.DataHandler dataHandler = payloadType.getBase64Binary();

                    final PartInfo partInfo =
                            messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

                    final EbmsPayload p = new EbmsPayload();

                    final String payloadFileName = MessageFormat
                            .format(eu.domibus.backend.module.Constants.PAYLOAD_FILE_NAME_FORMAT, counter);

                    OutputStream tmpFileOutputStream = null;
                    try {
                        tmpFileOutputStream = new FileOutputStream(new File(tempDir, payloadFileName));
                        org.apache.commons.io.IOUtils.write(IOUtils.toByteArray(dataHandler), tmpFileOutputStream);
                    } catch (FileNotFoundException fnfe) {
                        throw fnfe;
                    } catch (IOException ioe) {
                        throw ioe;
                    } finally {
                        if (tmpFileOutputStream != null) {
                            tmpFileOutputStream.close();
                        }
                    }

                    p.setFile(payloadFileName);

                    String description = null;
                    try {
                        description =
                                messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                                        .getDescription().getNonEmptyString();
                    } catch (Exception e) {
                    }
                    p.setDescription(description);
                    if (payloadType.getContentType() != null) {
                        p.setContentType(payloadType.getContentType().getContentType_type0());
                    }

                    {
                        p.setCid(MessageFormat.format(eu.domibus.backend.module.Constants.CID_MESSAGE_FORMAT,
                                                      new String[]{payloadType.getPayloadId().toString()}));

                        if (partInfo.getPartProperties() != null &&
                            partInfo.getPartProperties().getProperty().length > 0) {
                            for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property property : partInfo
                                    .getPartProperties().getProperty()) {
                                p.addPartProperties(property.getName().getNonEmptyString(),
                                                    property.getNonEmptyString());
                            }
                        }
                    }

                    payloads.add(p);
                    counter++;
                }
                msgInfoSet.getPayloads().setPayloads(payloads);
            }

            final File metadataFile = new File(tempDir, eu.domibus.backend.module.Constants.METADATA_FILE_NAME);
            msgInfoSet.writeToFile(metadataFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error sending message", e);

            log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                    .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], "",
                                                     "SendMessageService", "sendMessage",
                                                     eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_KO_STATUS));

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Error writing data into temporal directory[" + tempDir + "]", e,
                                                    Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }

        final UserMsgToPush userMsgToPush = sendMessageHelper.submitFromFolder(tempDir);

        final String messageId = sendMessageHelper.send(userMsgToPush);

        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            log.error("Error deleting temporal directory[" + tempDir + "]", e);
        }

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], messageId,
                                                 "SendMessageService", "sendMessage",
                                                 eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_OK_STATUS));

        final backend.ecodex.org._1_1.SendResponse sendResponse = new backend.ecodex.org._1_1.SendResponse();
        // changed from setmessageid to addMessageId during 1.6.2 migration
        sendResponse.addMessageID(messageId);

        return sendResponse;
    }

    /**
     * Send message with reference.
     *
     * @param messaging      the messaging
     * @param sendRequestURL the send request url
     * @throws SendMessageServiceException the send message service exception
     */
    public backend.ecodex.org._1_1.SendResponse sendMessageWithReference(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging,
            final backend.ecodex.org._1_1.SendRequestURL sendRequestURL) throws SendMessageServiceException {
        log.debug("Called SendMessageService.sendMessageWithReference");

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], "", "SendMessageService",
                                                 "sendMessageWithReference",
                                                 eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_INIT_STATUS));

        sendMessageWithReferenceValidator.validate(messaging, sendRequestURL);

        final MsgInfoSet msgInfoSet =
                Converter_1_1.convertUserMessageToMsgInfoSet(messaging.getMessaging().getUserMessage()[0]);

        final String action =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString();
        final String fromPartyid = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                .getNonEmptyString();
        final String fromPartyidType = null;
        // String fromPartyidType =
        // messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType().getNonEmptyString();
        final String toPartyid =
                messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getNonEmptyString();
        final String toPartyidType = null;
        // String toPartyidType =
        // messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType().getNonEmptyString();
        final String service =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService().getNonEmptyString();

        final eu.domibus.ebms3.config.PMode pmode = eu.domibus.ebms3.module.Configuration
                .getPModeO(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType);

        msgInfoSet.setPmode(eu.domibus.ebms3.module.Configuration
                                    .getPMode(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType));

        final File tempDir = eu.domibus.backend.util.IOUtils.createTempDir();
        try {
            int counter = 0;
            {
                final String bodyload = sendRequestURL.getBodyload().getString();

                final PartInfo partInfo =
                        messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

                final String bodyloadFileName = eu.domibus.backend.module.Constants.BODYLOAD_FILE_NAME_FORMAT;

                final EbmsPayload p = new EbmsPayload();

                InputStream is = null;

                try {
                    final URL url = new URL(bodyload);
                    is = url.openStream();
                    org.apache.commons.io.IOUtils.copy(is, new FileOutputStream(new File(tempDir, bodyloadFileName)));
                } catch (MalformedURLException e) {
                    log.error("Bodyload url is invalid");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("Bodyload url is invalid", Code.ERROR_SEND_004);
                    throw sendMessageServiceException;
                } catch (IOException e) {
                    log.error("Bodyload url is invalid");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("Bodyload url is invalid", Code.ERROR_SEND_004);
                    throw sendMessageServiceException;
                }

                p.setFile(bodyloadFileName);

                String description = null;
                try {
                    description = messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                            .getDescription().getNonEmptyString();
                } catch (Exception e) {
                }
                p.setDescription(description);

                {
                    // p.setCid(sendRequestURL.getBodyload().getPayloadId().toString());

                    if (partInfo.getPartProperties() != null && partInfo.getPartProperties().getProperty().length > 0) {
                        for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property property : partInfo
                                .getPartProperties().getProperty()) {
                            p.addPartProperties(property.getName().getNonEmptyString(), property.getNonEmptyString());
                        }
                    }
                }

                msgInfoSet.setBodyPayload(p);

                counter++;
            }

            if (sendRequestURL.getPayload() != null && sendRequestURL.getPayload().length > 0) {
                final Set<EbmsPayload> payloads = new HashSet<EbmsPayload>();

                for (final PayloadURLType payloadURLType : sendRequestURL.getPayload()) {
                    final String payload = payloadURLType.getString();

                    final PartInfo partInfo =
                            messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

                    final EbmsPayload p = new EbmsPayload();

                    final String payloadFileName = MessageFormat
                            .format(eu.domibus.backend.module.Constants.PAYLOAD_FILE_NAME_FORMAT, counter);

                    InputStream is = null;

                    try {
                        final URL url = new URL(payload);
                        is = url.openStream();
                        org.apache.commons.io.IOUtils
                                .copy(is, new FileOutputStream(new File(tempDir, payloadFileName)));
                    } catch (MalformedURLException e) {
                        log.error("EbmsPayload url " + counter + " is invalid");

                        final SendMessageServiceException sendMessageServiceException =
                                new SendMessageServiceException("EbmsPayload url " + counter + " is invalid",
                                                                Code.ERROR_SEND_004);
                        throw sendMessageServiceException;
                    } catch (IOException e) {
                        log.error("EbmsPayload url " + counter + " is invalid");

                        final SendMessageServiceException sendMessageServiceException =
                                new SendMessageServiceException("EbmsPayload url " + counter + " is invalid",
                                                                Code.ERROR_SEND_004);
                        throw sendMessageServiceException;
                    }

                    p.setFile(payloadFileName);

                    String description = null;
                    try {
                        description =
                                messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                                        .getDescription().getNonEmptyString();
                    } catch (Exception e) {
                    }
                    p.setDescription(description);

                    {
                        p.setCid(MessageFormat.format(eu.domibus.backend.module.Constants.CID_MESSAGE_FORMAT,
                                                      new String[]{payloadURLType.getPayloadId().toString()}));

                        if (partInfo.getPartProperties() != null &&
                            partInfo.getPartProperties().getProperty().length > 0) {
                            for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property property : partInfo
                                    .getPartProperties().getProperty()) {
                                p.addPartProperties(property.getName().getNonEmptyString(),
                                                    property.getNonEmptyString());
                            }
                        }
                    }

                    payloads.add(p);
                    counter++;
                }
                msgInfoSet.getPayloads().setPayloads(payloads);
            }

            final File metadataFile = new File(tempDir, eu.domibus.backend.module.Constants.METADATA_FILE_NAME);
            msgInfoSet.writeToFile(metadataFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error sending message", e);

            log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                    .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], "",
                                                     "SendMessageService", "sendMessageWithReference",
                                                     eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_KO_STATUS));

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Error writing data into temporal directory[" + tempDir + "]", e,
                                                    Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }

        final UserMsgToPush userMsgToPush = sendMessageHelper.submitFromFolder(tempDir);

        final String messageId = sendMessageHelper.send(userMsgToPush);

        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            log.error("Error deleting temporal directory[" + tempDir + "]", e);
        }

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                .convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0], messageId,
                                                 "SendMessageService", "sendMessageWithReference",
                                                 eu.domibus.logging.persistent.LoggerMessage.MESSAGE_SENT_OK_STATUS));

        final backend.ecodex.org._1_1.SendResponse sendResponse = new backend.ecodex.org._1_1.SendResponse();
        // changed from setmessageid to addMessageId during 1.6.2 migration
        sendResponse.addMessageID(messageId);

        return sendResponse;
    }
}
