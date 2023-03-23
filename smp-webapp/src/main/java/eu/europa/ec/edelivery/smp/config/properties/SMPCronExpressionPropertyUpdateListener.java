package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.SMPTaskSchedulerConfig;
import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.util.*;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;

/**
 * Property change listener for cron expression. Component updates crone version for the trigger with matching
 * Cron Expression Property
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPCronExpressionPropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPCronExpressionPropertyUpdateListener.class);

    final List<SMPDynamicCronTrigger> smpDynamicCronTriggerList;

    SMPTaskSchedulerConfig taskSchedulerConfig;

    public SMPCronExpressionPropertyUpdateListener(Optional<List<SMPDynamicCronTrigger>> cronTriggerList,
                                                   SMPTaskSchedulerConfig taskSchedulerConfig
    ) {
        this.smpDynamicCronTriggerList = cronTriggerList.orElse(Collections.emptyList());
        this.taskSchedulerConfig = taskSchedulerConfig;
    }


    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        if (smpDynamicCronTriggerList.isEmpty()) {
            LOG.warn("No cron trigger bean is configured!");
            return;
        }
        // update cron expressions!
        boolean cronExpressionChanged = false;
        for (SMPDynamicCronTrigger trigger : smpDynamicCronTriggerList) {
            // check if updated properties contains value for the cron trigger
            if (!properties.containsKey(trigger.getCronExpressionProperty())) {
                LOG.debug("Update cron properties does not contain change for cron [{}]", trigger.getCronExpressionProperty());
                continue;
            }
            // check if cron was changed
            CronExpression newCronExpression = (CronExpression) properties.get(trigger.getCronExpressionProperty());
            if (newCronExpression ==null) {
                LOG.debug("New cron expression for property: [{}] is not set!, skip re-setting the cron!", trigger.getCronExpressionProperty());
                continue;
            }

            if (StringUtils.equalsIgnoreCase(trigger.getExpression(), newCronExpression.toString())) {
                LOG.debug("Cron expression did not changed for cron: [{}], skip re-setting the cron!", trigger.getCronExpressionProperty());
                continue;
            }
            LOG.info("Change expression from [{}] to [{}] for property: [{}]!",
                    trigger.getExpression(),
                    newCronExpression.toString(),
                    trigger.getCronExpressionProperty());

            trigger.updateCronExpression((CronExpression)
                    properties.get(trigger.getCronExpressionProperty()));
            cronExpressionChanged = true;
        }

        if (cronExpressionChanged) {
            LOG.debug("One of monitored cron expression changed! Reset the cron task configuration!");
            taskSchedulerConfig.updateCronTasks();
        }
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return Arrays.asList(SMP_PROPERTY_REFRESH_CRON,
                SMP_ALERT_CREDENTIALS_CRON);
    }
}
