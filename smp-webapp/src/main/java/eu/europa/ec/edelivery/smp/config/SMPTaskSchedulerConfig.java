package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ComponentScan(
        basePackages = "eu.europa.ec.edelivery.smp")
public class SMPTaskSchedulerConfig {

    ConfigurationDao configurationDao;

    @Autowired
    public SMPTaskSchedulerConfig(ConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

    @Scheduled(cron = "${smp.property.refresh.cronJobExpression:0 48 */1 * * *}")
    public void refreshProperties() {
        configurationDao.refreshProperties();
    }
}
