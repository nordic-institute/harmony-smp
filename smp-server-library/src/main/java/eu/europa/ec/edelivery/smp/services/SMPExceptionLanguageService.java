package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.spi.exceptions.TranslatedMessage;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SMPExceptionLanguageService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPExceptionLanguageService.class);

    private final SMPLanguageResourceService smpLanguageResourceService;

    public SMPExceptionLanguageService(SMPLanguageResourceService smpLanguageResourceService) {
        this.smpLanguageResourceService = smpLanguageResourceService;
    }

    public Throwable getTranslated(Throwable e) {
        ExceptionUtils.getThrowableList(e).stream()
                .filter(t -> t instanceof TranslatedMessage)
                .forEach(t -> ((TranslatedMessage) t).setDefaultTranslatedMessage(getMessageTranslation((TranslatedMessage) t)));
        return e;
    }

    public String getMessageTranslation(TranslatedMessage message) {
        return getMessageTranslation(message.getMessageCode(), message.getMessageArgs());
    }

    public String getMessageTranslation(String messageCode) {
        return getMessageTranslation(messageCode, new HashMap<>());
    }

    public String getMessageTranslation(String messageCode, Map<String, Object> args) {
        Properties uiProperties = smpLanguageResourceService.getUiProperties(SMPLanguageResourceService.LANGUAGE_DEFAULT);

        if (!uiProperties.containsKey(messageCode)) {
            LOG.debug("The [{}] message code is missing the default English translation so returning the message code as the actual translation.", messageCode);
            return messageCode;
        }
        String property = uiProperties.getProperty(messageCode);
        args.keySet().forEach(placeholder -> RegExUtils.replaceAll(property, "{{" + placeholder + "}}", args.get(placeholder).toString()));
        return property;
    }

}
