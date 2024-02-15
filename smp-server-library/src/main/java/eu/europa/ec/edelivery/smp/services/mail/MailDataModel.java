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

import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class MailDataModel {
    public enum CommonProperties {
        CURRENT_DATETIME,
        SMP_INSTANCE_NAME,
    }
    private final String language;
    private final AlertTypeEnum alertType;
    Map<String, Object> model = new HashMap<>();


    public MailDataModel(String language, AlertTypeEnum alertType, Map<String, Object> model) {
        this.language = language;
        this.alertType = alertType;
        this.model.putAll(model);
    }

    public MailDataModel(String language, final DBAlert alert) {
        this.language = language;
        this.alertType = alert.getAlertType();
        alert.getProperties().forEach((key, prop) ->  this.model.put(key, prop.getValue()));
    }

    public String getLanguage() {
        return language;
    }

    public AlertTypeEnum getMailType() {
        return alertType;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
