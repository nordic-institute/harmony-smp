package eu.europa.ec.edelivery.smp.test;

import eu.europa.ec.edelivery.smp.config.SMPDatabaseConfig;
import eu.europa.ec.edelivery.smp.config.SMPWebAppConfig;
import eu.europa.ec.edelivery.smp.config.WSSecurityConfigurerAdapter;
import eu.europa.ec.edelivery.smp.config.properties.SMPSecurityPropertyUpdateListener;
import eu.europa.ec.edelivery.smp.cron.CronTriggerConfig;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Configuration
@Import({
        PropertiesTestConfig.class,
        SMPWebAppConfig.class,
        SMPDatabaseConfig.class,
        WSSecurityConfigurerAdapter.class,
        SMPSecurityPropertyUpdateListener.class,
        CronTriggerConfig.class,
        ConfigurationDao.class})
public class SmpTestWebAppConfig {
}
