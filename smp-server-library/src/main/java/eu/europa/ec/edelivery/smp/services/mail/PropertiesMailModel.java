/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.data.model.DBAlert;

import java.util.Properties;

public class PropertiesMailModel implements MailModel<Properties> {

    private final Properties model = new Properties();

    private final String templatePath;

    private final String subject;


    public PropertiesMailModel(final String templatePath, final String subject) {
        this.templatePath = templatePath;
        this.subject = subject;
    }

    public PropertiesMailModel(final DBAlert alert) {
        this.templatePath = alert.getAlertType().getTemplate();
        this.subject = alert.getMailSubject();
        alert.getProperties().forEach((key, prop) -> setProperty(key, prop.getValue())
        );
    }

    @Override
    public Properties getModel() {
        return model;
    }


    public void setProperty(String key, String value) {
        model.setProperty(key, value);
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public String getSubject() {
        return subject;
    }
}
