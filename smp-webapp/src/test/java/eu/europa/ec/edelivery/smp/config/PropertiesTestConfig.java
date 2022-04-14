/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;

/**
 * Created by gutowpa on 11/01/2018.
 */
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:config.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = "eu.europa.ec.edelivery.smp")
public class PropertiesTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        // update keystore
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties localProps = new Properties();
        localProps.setProperty("jdbc.driverClassName", "org.h2.Driver");
        localProps.setProperty("jdbc.url", "jdbc:h2:file:./target/myDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_SERVER=TRUE");
        localProps.setProperty("jdbc.user", "smp");
        localProps.setProperty("jdbc.pass", "smp");
        localProps.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        localProps.setProperty("spring.jpa.generate-ddl", "true");
        localProps.setProperty("spring.jpa.properties.hibernate.hbm2ddl.auto", "create");
        localProps.setProperty(SMP_PROPERTY_REFRESH_CRON.getProperty(), SMP_PROPERTY_REFRESH_CRON.getDefValue());
        localProps.setProperty(BLUE_COAT_ENABLED.getProperty(), "true");

        // even thought keystore is generated but secure password generation can be very slow on some server
        // create test password..
        localProps.setProperty(KEYSTORE_PASSWORD.getProperty(), "{DEC}{test123}");
        localProps.setProperty(TRUSTSTORE_PASSWORD.getProperty(), "{DEC}{test123}");

        propertiesConfig.setProperties(localProps);
        propertiesConfig.setLocalOverride(true);

        return propertiesConfig;
    }
}
