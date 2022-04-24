package eu.europa.ec.edelivery.smp.cron;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;

/**
 * Class initialize the cron trigger beans
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Configuration
public class CronTriggerConfig {

    public static final String TRIGGER_BEAN_PROPERTY_REFRESH ="SMPCronTriggerPropertyRefresh";

    @Bean(TRIGGER_BEAN_PROPERTY_REFRESH)
    public SMPDynamicCronTrigger gePropertyRefreshCronTrigger(){
        return new SMPDynamicCronTrigger(SMP_PROPERTY_REFRESH_CRON.getDefValue(), SMP_PROPERTY_REFRESH_CRON);
    }
}
