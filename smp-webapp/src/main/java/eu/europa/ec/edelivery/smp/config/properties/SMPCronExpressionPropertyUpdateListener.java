package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;

@Component
public class SMPCronExpressionPropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPCronExpressionPropertyUpdateListener.class);

    final List<SMPDynamicCronTrigger> smpDynamicCronTriggerList;

    public SMPCronExpressionPropertyUpdateListener(Optional<List<SMPDynamicCronTrigger>> cronTriggerList) {
        this.smpDynamicCronTriggerList = cronTriggerList.orElse(Collections.emptyList());
    }


    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        if (smpDynamicCronTriggerList.isEmpty()) {
            LOG.warn("No cron trigger bean is configured!");
            return;
        }
        // update cron expressions!
        smpDynamicCronTriggerList.forEach(trigger -> {
                    if (properties.containsKey(trigger.getCronExpressionProperty())) {
                        trigger.updateCronExpression(
                                (String) properties.get(trigger.getCronExpressionProperty()));
                    }
                }
        );
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return Collections.singletonList(SMP_PROPERTY_REFRESH_CRON);
    }
}