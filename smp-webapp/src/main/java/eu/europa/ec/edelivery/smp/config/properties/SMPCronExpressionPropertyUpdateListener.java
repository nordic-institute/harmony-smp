/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.SMPTaskSchedulerConfig;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.Strings;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.util.*;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;

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
            if (newCronExpression == null) {
                LOG.debug("New cron expression for property: [{}] is not set!, skip re-setting the cron!", trigger.getCronExpressionProperty());
                continue;
            }

            if (Strings.CI.equals(trigger.getExpression(), newCronExpression.toString())) {
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
                SMP_ALERT_CREDENTIALS_CRON,
                SMP_ALERT_SYSTEM_CERTIFICATES_CRON);
    }
}
