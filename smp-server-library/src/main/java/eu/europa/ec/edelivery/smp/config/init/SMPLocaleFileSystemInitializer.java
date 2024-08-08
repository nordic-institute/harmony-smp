package eu.europa.ec.edelivery.smp.config.init;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.SMPLocaleService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Updates locale files on the disk with locales provided by DomiSMP. Any existing locales on the disk matching the
 * ones provided in DomiSMP are removed in the process of being replaced.
 *
 * @since 5.1
 * @author Sebastian-Ion TINCU
 * @see eu.europa.ec.edelivery.smp.i18n.SMPLocale
 */
@Component
public class SMPLocaleFileSystemInitializer {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPLocaleFileSystemInitializer.class);

    private final ConfigurationService configurationService;

    private final SMPLocaleService smpLocaleService;

    public SMPLocaleFileSystemInitializer(ConfigurationService configurationService, SMPLocaleService smpLocaleService) {
        this.configurationService = configurationService;
        this.smpLocaleService = smpLocaleService;
    }

    @PostConstruct
    public void init() {
        LOG.info("Initializing SMP locale filesystem");
        smpLocaleService.updateLocalesOnDisk();
    }
}
