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

import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mail data model for mail content. The class is used to create mail messages from templates and message
 * translations. It contains common properties like current date time, SMP instance name, etc.
 */
public class MailDataModel {
    private static final String DEFAULT_LANGUAGE = "en";
    public enum CommonProperties {
        CURRENT_DATETIME,
        SMP_INSTANCE_NAME,
    }
    private final String language;
    private final AlertTypeEnum alertType;
    Map<String, String> model = new HashMap<>();

    public MailDataModel(String language, AlertTypeEnum alertType, Map<String, Object> model) {
        this.language = language;
        this.alertType = alertType;
        model.forEach((key, value) -> this.model.put(key, valueToString(value)));
    }

    public MailDataModel(String language, final DBAlert alert) {
        this.language = language;
        this.alertType = alert.getAlertType();
        alert.getProperties().forEach((key, prop) ->  this.model.put(key,valueToString(prop.getValue())));
    }

    private String valueToString(Object value) {
        if (value instanceof OffsetDateTime) {
            OffsetDateTime odt = (OffsetDateTime) value;
            return DateTimeUtils.formatOffsetDateTimeWithLocal(odt, getLanguage(), null);
        }
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }
    /**
     * Get language of the mail. If not set, default language "en" is used.
     * @return language of the mail or "en" if not set
     */
    public String getLanguage() {
        return StringUtils.isBlank(language)?DEFAULT_LANGUAGE:language;
    }

    public AlertTypeEnum getMailType() {
        return alertType;
    }

    public Map<String, String> getModel() {
        return model;
    }
}
