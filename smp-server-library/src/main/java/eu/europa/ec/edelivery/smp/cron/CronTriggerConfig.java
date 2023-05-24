package eu.europa.ec.edelivery.smp.cron;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;

/**
 * Class initialize the cron trigger beans
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Configuration
public class CronTriggerConfig {

    public static final String TRIGGER_BEAN_PROPERTY_REFRESH = "SMPCronTriggerPropertyRefresh";
    public static final String TRIGGER_BEAN_CREDENTIAL_ALERTS = "SMPCronTriggerCredentialsAlerts";


    @Bean(TRIGGER_BEAN_PROPERTY_REFRESH)
    public SMPDynamicCronTrigger getPropertyRefreshCronTrigger() {
        return new SMPDynamicCronTrigger(SMP_PROPERTY_REFRESH_CRON.getDefValue(), SMP_PROPERTY_REFRESH_CRON);
    }

    @Bean(TRIGGER_BEAN_CREDENTIAL_ALERTS)
    public SMPDynamicCronTrigger getCredentialAlertsCronTrigger() {
        return new SMPDynamicCronTrigger(SMP_ALERT_CREDENTIALS_CRON.getDefValue(), SMP_ALERT_CREDENTIALS_CRON);
    }
}
