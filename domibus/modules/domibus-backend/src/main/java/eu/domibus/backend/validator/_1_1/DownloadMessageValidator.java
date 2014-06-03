/*
 * 
 */
package eu.domibus.backend.validator._1_1;

import backend.ecodex.org._1_1.Code;
import org.apache.log4j.Logger;
import eu.domibus.backend.db.dao.MessageDAO;
import eu.domibus.backend.db.model.Message;
import eu.domibus.backend.db.model.Payload;
import eu.domibus.backend.service._1_1.exception.DownloadMessageServiceException;
import eu.domibus.backend.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * The Class DownloadMessageValidator.
 */
@Service("DownloadMessageValidator_1_1")
public class DownloadMessageValidator {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(DownloadMessageValidator.class);

    /**
     * The message dao.
     */
    @Autowired
    private MessageDAO messageDAO;

    /**
     * Validate.
     *
     * @param downloadMessageRequest the download message request
     * @throws DownloadMessageServiceException
     *          the download message service exception
     */
    public void validate(final backend.ecodex.org._1_1.DownloadMessageRequest downloadMessageRequest)
            throws DownloadMessageServiceException {
        log.debug("Validating DownloadMessage");

        Message message = null;

        if (StringUtils.isNotEmpty(downloadMessageRequest.getMessageID()) &&
            StringUtils.isNumeric(downloadMessageRequest.getMessageID())) {
            message = messageDAO.findById(Integer.parseInt(downloadMessageRequest.getMessageID()));
        } else {
            message = messageDAO.getFirstNotDownloadedSortedByMessageDate();
        }

        if (message == null || message.getDeleted()) {
            log.error("Error downloading message: message not found");

            final DownloadMessageServiceException downloadMessageServiceException =
                    new DownloadMessageServiceException("Error downloading message: message not found",
                                                        Code.ERROR_DOWNLOAD_001);
            throw downloadMessageServiceException;
        }

        if (message.getDownloaded()) {
            log.error("Error downloading message[ " + message.getIdMessage() + " ]: message already downloaded");

            final DownloadMessageServiceException downloadMessageServiceException =
                    new DownloadMessageServiceException("Error downloading message: message already downloaded",
                                                        Code.ERROR_DOWNLOAD_002);
            throw downloadMessageServiceException;
        }

        final File messageFile =
                new File(message.getDirectory(), eu.domibus.backend.module.Constants.MESSAGING_FILE_NAME);

        if (messageFile == null || !messageFile.exists()) {
            log.error("Error loading message file of message[ " + message.getIdMessage() + " ]");

            final DownloadMessageServiceException downloadMessageServiceException =
                    new DownloadMessageServiceException("Error loading message file", Code.ERROR_DOWNLOAD_003);
            throw downloadMessageServiceException;
        }

        for (final Payload payload : message.getPayloads()) {
            final File file = new File(message.getDirectory(), payload.getFileName());

            if (file == null || !file.exists()) {
                log.error("Error loading file [" + payload.getFileName() + "] of message[ " + message.getIdMessage() +
                          " ]");

                final DownloadMessageServiceException downloadMessageServiceException =
                        new DownloadMessageServiceException(
                                "Error loading file [" + payload.getFileName() + "] of message[ " +
                                message.getIdMessage() + " ]", Code.ERROR_DOWNLOAD_003);
                throw downloadMessageServiceException;
            }
        }
    }
}
