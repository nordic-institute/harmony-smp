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

/**
 * Created by gutowpa on 11/01/2018.
 */
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:config.properties", ignoreResourceNotFound = true)
})
public class PropertiesTestConfig {

    private final static String SIGNING_KEYSTORE_PATH = Thread.currentThread().getContextClassLoader().getResource("signature_keys.jks").getFile();

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties localProps = new Properties();
        localProps.setProperty("xmldsig.keystore.classpath", SIGNING_KEYSTORE_PATH);
        propertiesConfig.setProperties(localProps);
        propertiesConfig.setLocalOverride(true);

        return propertiesConfig;
    }
}
