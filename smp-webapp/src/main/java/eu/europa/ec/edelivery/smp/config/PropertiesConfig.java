/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.config;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

/**
 * Created by Flavio Santos
 * Class read properties from configuration file if exists. Than it use datasource (default by JNDI
 * if not defined in property file jdbc/smpDatasource) to read application properties. Because this class is
 * invoked before datasource is initialiyzed by default - it creates it's own database connection.
 * Also it uses hibernate to handle dates  for Configuration table.
 *
 */
@Configuration
@ComponentScan(basePackages = {
        "eu.europa.ec"})
@PropertySources({
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
public class PropertiesConfig {

    private static PropertyInitialization PROP_INIT_TOOLS = new PropertyInitialization();
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(PropertiesConfig.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties fileProperties = FileProperty.getFileProperties();
        Properties prop = PROP_INIT_TOOLS.getDatabaseProperties(fileProperties);
        // set default value
        if (!prop.containsKey(SMP_PROPERTY_REFRESH_CRON.getProperty())){
            prop.setProperty(SMP_PROPERTY_REFRESH_CRON.getProperty(), SMP_PROPERTY_REFRESH_CRON.getDefValue());
        }
        // add properties from database - add override from the database properties
        fileProperties.putAll(prop);

        // log application properties
        PROP_INIT_TOOLS.logBuildProperties();
        // update log configuration
        FileProperty.updateLog4jConfiguration(fileProperties.getProperty(FileProperty.PROPERTY_LOG_FOLDER),
                fileProperties.getProperty(FileProperty.PROPERTY_LOG_PROPERTIES),prop.getProperty(CONFIGURATION_DIR.getProperty())
                );

        propertiesConfig.setProperties(fileProperties);
        propertiesConfig.setLocalOverride(true);
        LOG.debug("Properties are initialized");


        return propertiesConfig;
    }
}
