/*
 * 
 */
package eu.domibus.backend.service;

import org.apache.log4j.Logger;
import eu.domibus.backend.db.dao.MessageDAO;
import eu.domibus.backend.db.model.Message;
import eu.domibus.backend.module.Constants;
import eu.domibus.backend.service._1_1.exception.JobServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * The Class JobService.
 */
@Service
public class JobService {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(JobService.class);

    /**
     * The message dao.
     */
    @Autowired
    private MessageDAO messageDAO;

    /**
     * Delete old messages.
     *
     * @throws JobServiceException the job service exception
     */
    @Transactional
    public void deleteOldMessages() throws JobServiceException {
        log.debug("Starting SendMessageService.deleteOldMessages");

        final int messagesTimeLiveInDays = Constants.getMessagesTimeLive();

        final List<Message> messagesToDelete = messageDAO.findNotDeleted(messagesTimeLiveInDays);

        for (final Message message : messagesToDelete) {
            if (deleteMessage(message)) {
                message.setDeleted(true);

                messageDAO.save(message);
            }
        }

        log.debug("Finished SendMessageService.deleteOldMessages");
    }

    public boolean deleteMessage(final Message message) {
        final File directory = new File(message.getDirectory());

        final boolean deleted = eu.domibus.backend.util.IOUtils.removeDirectory(directory);

        if (deleted || (directory != null && !directory.exists())) {
            log.debug("Deleted Message[" + message.getIdMessage() + "]");

            return true;
        } else {
            log.error("Directory of Message[" + message.getIdMessage() + "] cannot be deleted");

            return true;
        }
    }
}
