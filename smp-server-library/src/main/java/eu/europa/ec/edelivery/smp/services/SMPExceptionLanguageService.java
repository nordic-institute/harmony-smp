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

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.spi.exceptions.TranslatedMessage;
import org.apache.commons.lang3.RegExUtils;
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

    public static final String PREFIX_MESSAGE_VALUE_TRANSLATION = "TRANSLATION_REQUIRED_";

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

        for (String placeholder : args.keySet()) {
            Object value = args.get(placeholder);
            if (value instanceof String && ((String) value).startsWith(PREFIX_MESSAGE_VALUE_TRANSLATION)) {
                value = getMessageTranslation(((String) value).replaceFirst(PREFIX_MESSAGE_VALUE_TRANSLATION, ""), args);
            }
            property = RegExUtils.replaceAll(property, "\\{\\{" + placeholder + "\\}\\}", value.toString());
        }
        return property;
    }

}
