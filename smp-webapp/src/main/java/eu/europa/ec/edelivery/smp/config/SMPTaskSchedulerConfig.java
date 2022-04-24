package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_PROPERTY_REFRESH;

@Configuration
@EnableScheduling
public class SMPTaskSchedulerConfig implements SchedulingConfigurer {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPTaskSchedulerConfig.class);

    final ConfigurationDao configurationDao;
    final SMPDynamicCronTrigger refreshPropertiesTrigger;

    @Autowired
    public SMPTaskSchedulerConfig(
            ConfigurationDao configurationDao,
            @Qualifier(TRIGGER_BEAN_PROPERTY_REFRESH) SMPDynamicCronTrigger refreshPropertiesTrigger
    ) {
        this.configurationDao = configurationDao;
        this.refreshPropertiesTrigger = refreshPropertiesTrigger;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        LOG.debug("Configure cron tasks");
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> {
                    configurationDao.refreshProperties();
                },
                refreshPropertiesTrigger
        );
    }
}
