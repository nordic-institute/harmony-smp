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
package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SMPLanguageResourceService;
import eu.europa.ec.edelivery.smp.utils.StringNamedSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

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
@Service
public class MailTemplateService {
    private static final String MAIL_TEMPLATE = "/mail-messages/mail-template.htm";
    private static final String MAIL_TEMPLATE_CHARSET = "UTF-8";
    private static final String MAIL_HEADER = "MAIL_HEADER";
    private static final String MAIL_FOOTER = "MAIL_FOOTER";
    private static final String MAIL_TITLE = "MAIL_TITLE";
    private static final String MAIL_CONTENT = "MAIL_CONTENT";

    SMPLanguageResourceService smpLanguageResourceService;

    public MailTemplateService(SMPLanguageResourceService smpLanguageResourceService) {
        this.smpLanguageResourceService = smpLanguageResourceService;
    }


    public String getMailHtmlContent(MailDataModel model) {
        InputStream templateIS = MailTemplateService.class.getResourceAsStream(MAIL_TEMPLATE);
        try {
            Map<String, String> modelData = new HashMap<>();
            modelData.put(MAIL_HEADER, getMailHeader(model));
            modelData.put(MAIL_FOOTER, getMailFooter(model));
            modelData.put(MAIL_TITLE, getMailTitle(model));
            modelData.put(MAIL_CONTENT, getMailBody(model));
            return StringNamedSubstitutor.resolve(templateIS, modelData, MAIL_TEMPLATE_CHARSET);
        } catch (IOException e) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error reading mail template", ExceptionUtils.getRootCauseMessage(e));
        }
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
        Properties translations = smpLanguageResourceService.getMailProperties(model.getLanguage());
        String dataTemplate = translations.getProperty(key);
        return StringUtils.isBlank(dataTemplate) ? dataTemplate : StringNamedSubstitutor.resolve(dataTemplate, model.getModel());
    }
}
