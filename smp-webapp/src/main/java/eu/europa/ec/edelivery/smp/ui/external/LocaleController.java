package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.i18n.SMPLocale;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.SMPLocaleService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_LOCALE;

/**
 * Provides support for returning locale files required by the ngx-translation Angular library.
 *
 * @author Sebastian-Ion TINCU
 * @since 5.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_LOCALE)
public class LocaleController {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(LocaleController.class);
    private static final String DEFAULT_LOCALE_RESOURCE = "/META-INF/resources/ui/assets/i18n/en.json";
    private final SMPLocaleService smpLocaleService;

    public LocaleController(SMPLocaleService smpLocaleService) {
        this.smpLocaleService = smpLocaleService;
    }

    @GetMapping(value = "/{code}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resource getLocale(@PathVariable("code") String code) {
        Path langResourcePath = smpLocaleService.getLocaleFile(SMPLocale.fromCodeDefaultingToEnglish(code));
        if (langResourcePath.toFile().exists()) {
            LOG.debug("Returning locale file [{}]", langResourcePath.toAbsolutePath());
            return new FileSystemResource(langResourcePath);
        } else {
            LOG.warn("Locale file [{}] does not exist. Return default translation!", langResourcePath.toAbsolutePath());

            ClassPathResource defResource = new ClassPathResource(DEFAULT_LOCALE_RESOURCE);
            if (defResource.exists()) {
                return defResource;
            } else {
                LOG.error("Default locale file [{}] does not exist in classpath!", DEFAULT_LOCALE_RESOURCE);
                return null;
            }
        }
    }
}
