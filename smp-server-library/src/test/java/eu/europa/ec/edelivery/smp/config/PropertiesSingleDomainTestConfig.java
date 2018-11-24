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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.testutil.LocalPropertiesTestUtil.buildLocalProperties;

/**
 * Created by gutowpa on 11/01/2018.
 */
@Configuration
@PropertySource("classpath:config.properties")
public class PropertiesSingleDomainTestConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer setLocalProperties() {

        Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores");
        String path = resourceDirectory.toFile().getAbsolutePath();

        return buildLocalProperties(new String[][]{
                {"configuration.dir", path},
                {"encryption.key.filename","encryptionKey.key"},
                {"smp.keystore.password", "FarFJE2WUfY39SVRTFOqSg=="},
                {"smp.keystore.filename", "smp-keystore.jks"},
        });
    }
}
