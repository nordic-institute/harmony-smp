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

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

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
        @PropertySource(value = "classpath:config.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:smp.config.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
public class PropertiesConfig extends PropertyInitialization {


    SMPLogger LOG = SMPLoggerFactory.getLogger(PropertiesConfig.class);

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties prop = getDatabaseProperties();
        // log application properties
        logBuildProperties();
        propertiesConfig.setProperties(prop);
        propertiesConfig.setLocalOverride(true);
        LOG.debug("Properties are initialized");

        return propertiesConfig;
    }
}
