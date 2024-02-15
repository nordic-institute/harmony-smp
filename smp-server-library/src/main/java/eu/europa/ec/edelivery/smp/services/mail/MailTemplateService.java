/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.utils.StringNamedSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Mail template for mail content. The class is used to create mail messages from templates and message
 * translations.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component
public class MailTemplateService {
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String MAIL_TEMPLATE = "/mail-messages/mail-template.htm";

    private static final String MAIL_HEADER = "MAIL_HEADER";
    private static final String MAIL_FOOTER = "MAIL_FOOTER";
    private static final String MAIL_TITLE = "MAIL_TITLE";
    private static final String MAIL_CONTENT = "MAIL_CONTENT";


    public String getMailHtmlContent(MailDataModel model) {
        InputStream templateIS = MailTemplateService.class.getResourceAsStream(MAIL_TEMPLATE);
        try {
            Map<String, Object> modelData = new HashMap<>();
            modelData.put(MAIL_HEADER, getMailHeader(model));
            modelData.put(MAIL_FOOTER, getMailFooter(model));
            modelData.put(MAIL_TITLE, getMailTitle(model));
            modelData.put(MAIL_CONTENT, getMailBody(model));
            return StringNamedSubstitutor.resolve(templateIS, modelData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMailHeader(MailDataModel model) {
        return getMailData(model, "mail.header");
    }

    public String getMailFooter(MailDataModel model) {
        return getMailData(model, "mail.footer");
    }

    public String getMailTitle(MailDataModel model) {
        return getMailData(model, "mail." + model.getMailType().getTemplate() + ".title");
    }

    public String getMailBody(MailDataModel model) {
        return getMailData(model, "mail." + model.getMailType().getTemplate() + ".content");
    }

    public String getMailData(MailDataModel model, String key) {
        Properties translations = getMessageTranslations(model.getLanguage());
        String dataTemplate = translations.getProperty(key);
        return StringUtils.isBlank(dataTemplate) ? dataTemplate : StringNamedSubstitutor.resolve(dataTemplate, model.getModel());
    }

    public Properties getMessageTranslations(String language) {

        Properties translations = loadTranslations(language);
        if (translations != null) {
            return translations;
        }
        return loadTranslations(DEFAULT_LANGUAGE);
    }

    @Cacheable("mail-templates-translations")
    public Properties loadTranslations(String language) {
        String langResource = "/mail-messages/mail-messages_" + language + ".properties";
        InputStream isLanguage = MailTemplateService.class.getResourceAsStream(langResource);
        if (isLanguage != null) {
            try {
                Properties translations = new Properties();
                translations.load(isLanguage);
                return translations;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
