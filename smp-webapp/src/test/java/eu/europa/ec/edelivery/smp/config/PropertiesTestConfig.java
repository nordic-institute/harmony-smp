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

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by gutowpa on 11/01/2018.
 */
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:config.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
public class PropertiesTestConfig {

    private final static String SIGNING_KEYSTORE_PATH = Thread.currentThread().getContextClassLoader().getResource("service_integration_signatures_single_domain.jks").getFile();

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "keystores");
        Path targetDirectory = Paths.get("target", "keystores");

        FileUtils.copyDirectory(resourceDirectory.toFile(), targetDirectory.toFile());

        String path = targetDirectory.toFile().getAbsolutePath();



        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties localProps = new Properties();
        localProps.setProperty("jdbc.driverClassName", "org.h2.Driver");
        localProps.setProperty("jdbc.url", "jdbc:h2:file:./target/myDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_SERVER=TRUE");
        localProps.setProperty("jdbc.user", "smp");
        localProps.setProperty("jdbc.pass", "smp");
        localProps.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        localProps.setProperty("spring.jpa.generate-ddl", "true");
        localProps.setProperty("spring.jpa.properties.hibernate.hbm2ddl.auto", "create");

        localProps.setProperty("configuration.dir", path);
        localProps.setProperty("encryption.key.filename", "encryptionKey.key");
        localProps.setProperty("smp.keystore.password", "FarFJE2WUfY39SVRTFOqSg==");
        localProps.setProperty("smp.keystore.filename", "smp-keystore_multiple_domains.jks");
        propertiesConfig.setProperties(localProps);
        propertiesConfig.setLocalOverride(true);

        return propertiesConfig;
    }
}
