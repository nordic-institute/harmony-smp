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

package eu.domibus.ebms3.common;

import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.ebms3.common.dao.PModeProvider;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;


/**
 * TODO: add class description
 */
@DisallowConcurrentExecution
public class RetentionWorker extends QuartzJobBean {


    private static final Log LOG = LogFactory.getLog(RetentionWorker.class);
    @Autowired
    private PModeProvider pModeProvider;

    @Autowired
    private MessageLogDao messageLogDao;

    @Autowired
    private MessagingDao messagingDao;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RetentionWorker.LOG.debug("RetentionWorker executed");
        deleteExpiredMessages();

    }

    private void deleteExpiredMessages() {
        List<String> mpcs = pModeProvider.getMpcList();
        for (String mpc : mpcs) {
            int messageRetentionDownladed = pModeProvider.getRetentionDownloadedByMpcName(mpc);
            if (messageRetentionDownladed > 0) { // if -1 the messages will be kept indefinetely and if 0 it already has been deleted
                List<String> messageIds = messageLogDao.getDownloadedUserMessagesOlderThan(DateUtils.addMinutes(new Date(), messageRetentionDownladed * -1), mpc);
                for (String messageId : messageIds) {
                    messagingDao.delete(messageId);
                }
            }
            int messageRetentionUndownladed = pModeProvider.getRetentionUndownloadedByMpcName(mpc);
            if (messageRetentionUndownladed > -1) { // if -1 the messages will be kept indefinetely and if 0, although it makes no sense, is legal
                List<String> messageIds = messageLogDao.getUndownloadedUserMessagesOlderThan(DateUtils.addMinutes(new Date(), messageRetentionUndownladed * -1), mpc);
                for (String messageId : messageIds) {
                    messagingDao.delete(messageId);
                }
            }
        }

    }


}
