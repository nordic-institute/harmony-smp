/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Properties;

/**
 * Created by gutowpa on 21/09/2017.
 */

@Configuration
@ImportResource("classpath:spring-test-context.xml")
@ComponentScan(basePackages = {
        "eu.europa.ec"})
@TestPropertySource(properties = {
        "identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes=case-sensitive-participant-1|case-sensitive-participant-2",
        "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes=case-sensitive-doc-1|case-sensitive-doc-2"
})
@PropertySource(value = "classpath:config.properties")
public class SmpServicesTestConfig {


}