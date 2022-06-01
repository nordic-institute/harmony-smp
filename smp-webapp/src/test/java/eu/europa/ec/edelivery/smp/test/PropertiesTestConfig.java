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

package eu.europa.ec.edelivery.smp.test;

import eu.europa.ec.edelivery.smp.config.FileProperty;
import eu.europa.ec.edelivery.smp.config.PropertiesConfig;
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
@ComponentScan(basePackages = "eu.europa.ec.edelivery.smp",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = PropertiesConfig.class)})
public class PropertiesTestConfig {

    public static final String DATABASE_URL="jdbc:h2:file:./target/myDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_SERVER=TRUE";
    public static final String DATABASE_USERNAME="smp";
    public static final String DATABASE_PASS="smp";
    public static final String DATABASE_DRIVER="org.h2.Driver";
    public static final String DATABASE_DIALECT="org.hibernate.dialect.H2Dialect";


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        // update keystore
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties localProps = new Properties();
        localProps.setProperty(FileProperty.PROPERTY_DB_DIALECT, DATABASE_DIALECT);
        localProps.setProperty(FileProperty.PROPERTY_DB_DRIVER, DATABASE_DRIVER);
        localProps.setProperty(FileProperty.PROPERTY_DB_URL, DATABASE_URL);
        localProps.setProperty(FileProperty.PROPERTY_DB_USER,DATABASE_USERNAME);
        localProps.setProperty(FileProperty.PROPERTY_DB_TOKEN, DATABASE_PASS);
        // create database objects if not exists for the test
        localProps.setProperty("spring.jpa.generate-ddl", "true");
        localProps.setProperty("spring.jpa.properties.hibernate.hbm2ddl.auto", "create");

        localProps.setProperty("configuration.dir", "./target/");

        localProps.setProperty(SMP_PROPERTY_REFRESH_CRON.getProperty(), SMP_PROPERTY_REFRESH_CRON.getDefValue());
        // even thought keystore is generated but secure password generation can be very slow on some server
        // create test password..
        localProps.setProperty(KEYSTORE_PASSWORD.getProperty(), "{DEC}{test123}");
        localProps.setProperty(TRUSTSTORE_PASSWORD.getProperty(), "{DEC}{test123}");
        localProps.setProperty(PARTC_SCH_MANDATORY.getProperty(),"false");

        propertiesConfig.setProperties(localProps);
        propertiesConfig.setLocalOverride(true);

        return propertiesConfig;
    }
}
