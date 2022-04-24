package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class AlertService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AlertService.class);
    final AlertDao alertDao;


    public AlertService(AlertDao alertDao) {
        this.alertDao = alertDao;
    }

    public void alertUserAccountSuspended(){
        // create alert
        DBAlert alert = new DBAlert();
        alert.setProcessed(false);
        alert.setReportingTime(LocalDateTime.now());
        alert.setAlertType(AlertTypeEnum.USER_ACCOUNT_SUSPENDED);
        alert.setAlertLevel(AlertLevelEnum.MEDIUM);
        alertDao.persistFlushDetach(alert);
    }
}
