package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
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
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;

/**
 * Service providing operations for managing locales (e.g. updating locales on the disk).
 *
 * @since 5.1
 * @author Sebastian-Ion TINCU
 */
@Service
public class SMPLocaleService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPLocaleService.class);

    private final ConfigurationService configurationService;

    public SMPLocaleService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public Path getLocaleFile(SMPLocale locale) {
        File localeFolder = configurationService.getLocaleFolder();
        if (!localeFolder.exists() && !localeFolder.mkdirs()) {
            LOG.error("Failed to create locale folder [{}]", localeFolder);
            return null;
        }
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
            if (localeFile == null) {
                LOG.warn("Can not generate 'locale/language' file [{}]! Check if local folder defined in property [{}] " +
                        "has writing permissions and the folder exists", localeFile, SMPEnvPropertyEnum.LOCALE_FOLDER.getProperty());
                return;
            }
            if (Files.exists(localeFile)) {
                LOG.warn("Language file [{}] already exists, and it will be replaced with up-to-date translations", localeFile);
            }
            try(InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, localeFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                LOG.error("An error occurred while updating locale file [{}]", localeFile, e);
            }
        }
    }
}
