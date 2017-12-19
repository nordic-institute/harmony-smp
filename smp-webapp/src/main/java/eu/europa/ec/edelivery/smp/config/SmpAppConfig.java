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

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by gutowpa on 12/07/2017.
 */

@Configuration
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp.validation",
        "eu.europa.ec.edelivery.smp.services",
        "eu.europa.ec.edelivery.smp.data.dao",
        "eu.europa.ec.cipa.smp.server.hook",
        "eu.europa.ec.edelivery.smp.conversion",
        "eu.europa.ec.cipa.smp.server.util"})
@Import({PropertiesConfig.class, DatabaseConfig.class})
public class SmpAppConfig {

}
