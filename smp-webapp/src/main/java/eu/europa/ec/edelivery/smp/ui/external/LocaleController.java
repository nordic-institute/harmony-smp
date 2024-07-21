package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.i18n.SMPLocale;
import eu.europa.ec.edelivery.smp.services.SMPLocaleService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_LOCALE;

/**
 * @since
 * @author Sebastian-Ion TINCU
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_LOCALE)
public class LocaleController {

    private final SMPLocaleService smpLocaleService;

    public LocaleController(SMPLocaleService smpLocaleService) {
        this.smpLocaleService = smpLocaleService;
    }

    @GetMapping(value = "/{code}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FileSystemResource getLocale(@PathVariable("code") String code) {
        return new FileSystemResource(smpLocaleService.getLocaleFile(SMPLocale.fromCodeDefaultingToEnglish(code)));
    }
}
