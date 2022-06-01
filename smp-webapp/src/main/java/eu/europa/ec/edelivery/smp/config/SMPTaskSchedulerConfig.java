package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.CredentialValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_CREDENTIAL_ALERTS;
import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_PROPERTY_REFRESH;

@Configuration
@EnableScheduling
public class SMPTaskSchedulerConfig implements SchedulingConfigurer {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPTaskSchedulerConfig.class);

    final ConfigurationDao configurationDao;
    final CredentialValidatorService credentialValidatorService;
    final SMPDynamicCronTrigger refreshPropertiesTrigger;
    final SMPDynamicCronTrigger credentialsAlertTrigger;

    ScheduledTaskRegistrar taskRegistrar;

    @Autowired
    public SMPTaskSchedulerConfig(
            ConfigurationDao configurationDao,
            CredentialValidatorService credentialValidatorService,
            @Qualifier(TRIGGER_BEAN_PROPERTY_REFRESH) SMPDynamicCronTrigger refreshPropertiesTrigger,
            @Qualifier(TRIGGER_BEAN_CREDENTIAL_ALERTS) SMPDynamicCronTrigger credentialsAlertTrigger
    ) {
        this.configurationDao = configurationDao;
        this.credentialValidatorService = credentialValidatorService;
        this.refreshPropertiesTrigger = refreshPropertiesTrigger;
        this.credentialsAlertTrigger = credentialsAlertTrigger;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
        LOG.info("Configure cron tasks");
        this.taskRegistrar.setScheduler(taskExecutor());
        LOG.debug("Configure cron task for property refresh");
        this.taskRegistrar.addTriggerTask(
                () -> {
                    configurationDao.refreshProperties();
                },
                refreshPropertiesTrigger
        );

        LOG.debug("Configure cron task for alerts: credentials validation");
        this.taskRegistrar.addTriggerTask(
                () -> {
                    credentialValidatorService.validateCredentials();
                },
                credentialsAlertTrigger
        );
    }

    public void updateCronTasks() { //call it when you want to change chron
        synchronized (SMPTaskSchedulerConfig.class) {
            List<CronTask> crons = this.taskRegistrar.getCronTaskList();
            taskRegistrar.destroy(); //important, cleanups current scheduled tasks
            taskRegistrar.afterPropertiesSet(); //rebuild
        }
    }
}
