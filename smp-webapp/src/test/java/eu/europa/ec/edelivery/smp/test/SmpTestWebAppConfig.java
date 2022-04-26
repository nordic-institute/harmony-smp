package eu.europa.ec.edelivery.smp.test;

import eu.europa.ec.edelivery.smp.config.DatabaseConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.WSSecurityConfigurerAdapter;
import eu.europa.ec.edelivery.smp.config.properties.SMPSecurityPropertyUpdateListener;
import eu.europa.ec.edelivery.smp.cron.CronTriggerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Configuration
@Import({
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        DatabaseConfig.class,
        WSSecurityConfigurerAdapter.class,
        SMPSecurityPropertyUpdateListener.class,
        CronTriggerConfig.class})
public class SmpTestWebAppConfig {
}
