/*
 * 
 */
package eu.domibus.backend.service._1_1;

import backend.ecodex.org._1_1.*;
import org.apache.axis2.databinding.types.Token;
import org.apache.log4j.Logger;
import eu.domibus.backend.db.dao.MessageDAO;
import eu.domibus.backend.db.model.Message;
import eu.domibus.backend.db.model.Payload;
import eu.domibus.backend.service.JobService;
import eu.domibus.backend.service._1_1.exception.DownloadMessageServiceException;
import eu.domibus.backend.util.StringUtils;
import eu.domibus.backend.validator._1_1.DownloadMessageValidator;
import eu.domibus.backend.validator._1_1.ListPendingMessagesValidator;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3.www._2005._05.xmlmime.ContentType_type0;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.List;

/**
 * The Class DownloadMessageService.
 */
@Service("DownloadMessageService_1_1")
public class DownloadMessageService {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(DownloadMessageService.class);

    /**
     * The message dao.
     */
    @Autowired
    private MessageDAO messageDAO;

    /**
     * The download message validator.
     */
    @Autowired
    @Qualifier("DownloadMessageValidator_1_1")
    private DownloadMessageValidator downloadMessageValidator;

    /**
     * The list pending messages validator.
     */
    @Autowired
    @Qualifier("ListPendingMessagesValidator_1_1")
    private ListPendingMessagesValidator listPendingMessagesValidator;

    /**
     * The Delete job service.
     */
    @Autowired
    private JobService deleteJobService;

    /**
     * List pending messages.
     *
     * @param listPendingMessagesRequest the list pending messages request
     * @return the backend.ecodex.org. list pending messages response
     * @throws DownloadMessageServiceException
     *          the download message service exception
     */
    public ListPendingMessagesResponse listPendingMessages(final ListPendingMessagesRequest listPendingMessagesRequest)
            throws DownloadMessageServiceException {
        log.debug("Called SendMessageService.listPendingMessages");

        listPendingMessagesValidator.validate(listPendingMessagesRequest);

        final List<Message> messages = messageDAO.findNotDownloadedSortedByMessageDate();

        final ListPendingMessagesResponse listPendingMessagesResponse =
                eu.domibus.backend.util.Converter_1_1.convertMessageListToListPendingMessagesResponse(messages);

        return listPendingMessagesResponse;
    }

    /**
     * Download message.
     *
     * @param downloadMessageResponse the download message response
     * @param downloadMessageRequest  the download message request
     * @return the org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704. messaging e
     * @throws DownloadMessageServiceException
     *          the download message service exception
     */
    @Transactional
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE downloadMessage(
            final DownloadMessageResponse downloadMessageResponse, final DownloadMessageRequest downloadMessageRequest)
            throws DownloadMessageServiceException {
        log.debug("Called SendMessageService.downloadMessage");

        downloadMessageValidator.validate(downloadMessageRequest);

        Message message = null;

        if (StringUtils.isNotEmpty(downloadMessageRequest.getMessageID()) &&
            StringUtils.isNumeric(downloadMessageRequest.getMessageID())) {
            message = messageDAO.findById(Integer.parseInt(downloadMessageRequest.getMessageID()));
        } else {
            message = messageDAO.getFirstNotDownloadedSortedByMessageDate();
        }

        final File messageFile =
                new File(message.getDirectory(), eu.domibus.backend.module.Constants.MESSAGING_FILE_NAME);

        final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messagingResponse =
                eu.domibus.backend.util.Converter_1_1.convertFileToMessagingE(messageFile);

        //Remove cid: of the href
        for (final PartInfo partInfo : messagingResponse.getMessaging().getUserMessage()[0].getPayloadInfo()
                                                                                           .getPartInfo()) {
            if (partInfo.getHref() != null && partInfo.getHref().toString().toLowerCase().startsWith("cid:")) {
                partInfo.getHref().setValue(partInfo.getHref().toString().substring("cid:".length()));
            }
        }

        for (final Payload payload : message.getPayloads()) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final File file = new File(message.getDirectory(), payload.getFileName());

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);

                eu.domibus.backend.util.IOUtils.copy(fileInputStream, byteArrayOutputStream);
            } catch (FileNotFoundException e) {
                log.error("Error loading file[" + file.getAbsolutePath() + "] of message[ " + message.getIdMessage() +
                          " ]");

                final DownloadMessageServiceException downloadMessageServiceException =
                        new DownloadMessageServiceException("Error loading file[" + file.getAbsolutePath() + "]",
                                                            Code.ERROR_DOWNLOAD_003);
                throw downloadMessageServiceException;
            } catch (IOException e) {
                log.error("Error loading file[" + file.getAbsolutePath() + "] of message[ " + message.getIdMessage() +
                          " ]");

                final DownloadMessageServiceException downloadMessageServiceException =
                        new DownloadMessageServiceException("Error loading file[" + file.getAbsolutePath() + "]",
                                                            Code.ERROR_DOWNLOAD_003);
                throw downloadMessageServiceException;
            } finally {
                if (fileInputStream != null) {
                    eu.domibus.backend.util.IOUtils.closeQuietly(fileInputStream);
                }
            }

            final DataHandler repositoryItem = new DataHandler(
                    new ByteArrayDataSource(byteArrayOutputStream.toByteArray(),
                                            eu.domibus.backend.module.Constants.BINARY_MIME_TYPE));

            final PayloadType payloadType = new PayloadType();

            if (StringUtils.isNotEmpty(payload.getContentType())) {
                final org.w3.www._2005._05.xmlmime.ContentType_type0 contentType_type0 = new ContentType_type0();
                contentType_type0.setContentType_type0(payload.getContentType());

                payloadType.setContentType(contentType_type0);
            }

            if (payload.getBodyload()) {
                payloadType.setBase64Binary(repositoryItem);

                if (StringUtils.isNotEmpty(payload.getPayloadId())) {
                    payloadType.setPayloadId(new Token(payload.getPayloadId()));
                }

                downloadMessageResponse.setBodyload(payloadType);
            } else {
                payloadType.setBase64Binary(repositoryItem);

                if (StringUtils.isNotEmpty(payload.getPayloadId())) {
                    payloadType.setPayloadId(new Token(payload.getPayloadId()));
                }

                downloadMessageResponse.addPayload(payloadType);
            }
        }

        //Set downloaded flag
        {
            if (deleteJobService.deleteMessage(message)) {
                message.setDeleted(true);
            }

            message.setDownloaded(true);

            messageDAO.save(message);
        }

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1
                                                                                     .convertUserMessageToMessageInfo(
                                                                                             messagingResponse
                                                                                                     .getMessaging()
                                                                                                     .getUserMessage()[0],
                                                                                             messagingResponse
                                                                                                     .getMessaging()
                                                                                                     .getUserMessage()[0]
                                                                                                     .getMessageInfo()
                                                                                                     .getMessageId()
                                                                                                     .getNonEmptyString(),
                                                                                             "DownloadMessageService",
                                                                                             "downloadMessage",
                                                                                             eu.domibus.logging.persistent.LoggerMessage.MESSAGE_DOWNLOADED_STATUS));

        return messagingResponse;
    }

    @Transactional
    public void deleteMessage(final DownloadMessageRequest downloadMessageRequest) {
        Message message = null;
        if (StringUtils.isNotEmpty(downloadMessageRequest.getMessageID()) &&
            StringUtils.isNumeric(downloadMessageRequest.getMessageID())) {
            message = messageDAO.findById(Integer.parseInt(downloadMessageRequest.getMessageID()));
        } else {
            message = messageDAO.getFirstNotDownloadedSortedByMessageDate();
        }

        if (message != null) {
            message.setDownloaded(true);

            messageDAO.save(message);
        }
    }
}
