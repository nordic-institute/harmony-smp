/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
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
