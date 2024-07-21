package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.i18n.SMPLocale;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

@Service
public class SMPLocaleService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPLocaleService.class);

    private final ConfigurationService configurationService;

    public SMPLocaleService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public Path getLocaleFile(SMPLocale locale) {
        File localeFolder = configurationService.getLocaleFolder();
        return new File(localeFolder, locale.getCode() + ".json").toPath().toAbsolutePath();
    }

    public Resource getLocaleResource(SMPLocale locale) {
        return new ClassPathResource("META-INF/resources/ui/assets/i18n/" + locale.getCode() + ".json");
    }

    public void updateLocalesOnDisk() {
        EnumSet.allOf(SMPLocale.class).forEach(this::updateLocaleOnDisk);
    }

    private void updateLocaleOnDisk(SMPLocale locale) {
        Resource resource = getLocaleResource(locale);
        if (resource.exists()) {
            Path localeFile = getLocaleFile(locale);

            try(InputStream inputStream = resource.getInputStream()) {
                Files.deleteIfExists(localeFile);
                Files.copy(inputStream, localeFile);
            } catch (Exception e) {
                LOG.error("An error occurred while updating locale file [{}]", localeFile, e);
            }
        }
    }
}
