/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.LocaleUtils;
import eu.europa.ec.smp.spi.exceptions.TranslatedMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
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
        return getMessageTranslation(messageCode, LocaleUtils.DEFAULT_LOCALE);
    }

    public String getMessageTranslation(String messageCode, Map<String, Object> args) {
        return getMessageTranslation(messageCode, args, LocaleUtils.DEFAULT_LOCALE);
    }

    public String getMessageTranslation(String messageCode, String localeCode) {
        return getMessageTranslation(messageCode, new HashMap<>(), localeCode);
    }

    public String getMessageTranslation(String messageCode, Map<String, Object> args, String localeCode) {
        localeCode = LocaleUtils.validateLocale(localeCode);

        Properties uiProperties = smpLanguageResourceService.getErroProperties(localeCode);
        String messageTemplate;
        if (!uiProperties.containsKey(messageCode)) {
            ErrorMessageType msgType = ErrorMessageType.getErrorMessageTypeByCode(messageCode);
            if (msgType == null) {
                LOG.debug("The error message code [{}] is not recognized. Returning the message code as the actual translation.", messageCode);
                return messageCode;
            }
            LOG.debug("The error message code [{}]  missing for local [{}]. Use the default English translation.", messageCode, localeCode);
            messageTemplate = msgType.getTemplate();
        } else {
            messageTemplate = uiProperties.getProperty(messageCode);
        }

        // Check if there is a  message argument that requires translation
        Map<String, Object>  arguments = new HashMap<>(args);
        if (args.containsKey(ErrorMessageArgument.ERROR_MESSAGE_CODE.getArgumentName())) {
            Map<String, Object>  innerArg = new HashMap<>(args);
            Object value =  innerArg.remove(ErrorMessageArgument.ERROR_MESSAGE_CODE.getArgumentName());
            value = getMessageTranslation(value.toString(), innerArg);
            arguments.put(ErrorMessageArgument.ERROR_MESSAGE_CODE.getArgumentName(), value);
        }
        return ErrorMessageType.replacePlaceholder(messageTemplate, arguments);
    }

}
