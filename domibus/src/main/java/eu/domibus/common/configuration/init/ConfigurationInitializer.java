/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.configuration.init;


import eu.domibus.common.configuration.model.Configuration;
import eu.domibus.common.dao.ConfigurationDAO;
import eu.domibus.common.exception.ConfigurationException;
import eu.domibus.ebms3.common.dao.PModeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;


public class ConfigurationInitializer {

    private static final Log LOG = LogFactory.getLog(ConfigurationInitializer.class);

    private File configFile;
    private JAXBContext jaxbContext;
    @Autowired
    private ConfigurationDAO configurationDAO;
    private PModeProvider pModeProvider;

    public void setJaxbContext(final JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public void setConfigFile(final File configFile) {
        this.configFile = configFile;
    }


    public void init() {

        if (this.configurationDAO.configurationExists()) {
            ConfigurationInitializer.LOG.warn("Configuration already present. The configuration file is ignored.");

        } else
            try {
                final Configuration configuration = (Configuration) this.jaxbContext.createUnmarshaller().unmarshal(this.configFile);
                this.configurationDAO.create(configuration);
                this.pModeProvider.init();
            } catch (final JAXBException e) {
                throw new ConfigurationException(e);
            }
        this.pModeProvider.init();

    }


    public void setpModeProvider(final PModeProvider pModeProvider) {
        this.pModeProvider = pModeProvider;
    }
}
