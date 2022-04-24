package eu.europa.ec.edelivery.smp.cron;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/**
 * Cron trigger with option to reset cron expression to new value
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPDynamicCronTrigger implements Trigger {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPDynamicCronTrigger.class);
    final SMPPropertyEnum cronExpressionProperty;
    Date nextExecutionDate;
    CronTrigger cronTrigger;


    public SMPDynamicCronTrigger(String expression, SMPPropertyEnum cronExpressionProperty) {
        cronTrigger = new CronTrigger(expression);
        this.cronExpressionProperty = cronExpressionProperty;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if (cronTrigger == null) {
            LOG.debug("Cron is disabled.");
            return null;
        }
        nextExecutionDate = cronTrigger.nextExecutionTime(triggerContext);
        return nextExecutionDate;
    }

    public void updateCronExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            LOG.debug("Disable cron trigger for property: [{}]. ", expression);
            cronTrigger = null;
            nextExecutionDate = null;
            return;
        }
        cronTrigger = new CronTrigger(expression);
        LOG.debug("Set new cron expression: [{}] for property: [{}]. ", expression,
                cronExpressionProperty.getProperty());
        if (nextExecutionDate != null) {
            LOG.debug("The new cron expression will be used after the current planned execution [{}].",
                    DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(nextExecutionDate));
        }
    }

    /**
     * Return next scheduled execution date
     *
     * @return next scheduled execution date;
     */
    public Date getNextExecutionDate() {
        return nextExecutionDate;
    }

    /**
     * Method returns the property which sets the cron expression
     *
     * @return property name
     */
    public SMPPropertyEnum getCronExpressionProperty() {
        return cronExpressionProperty;
    }
}
