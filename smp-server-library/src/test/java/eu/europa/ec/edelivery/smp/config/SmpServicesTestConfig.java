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


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by gutowpa on 21/09/2017.
 */
@Configuration
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp.services",
        "eu.europa.ec.edelivery.smp.data.dao",
        "eu.europa.ec.edelivery.smp.sml",
        "eu.europa.ec.edelivery.smp.conversion"})
public class SmpServicesTestConfig {

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.user}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.url}")
    private String url;

    @Bean
    public DataSource smpDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(driver);
        driverManagerDataSource.setUrl(url);
        driverManagerDataSource.setUsername(username);
        driverManagerDataSource.setPassword(password);

        return driverManagerDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean smpEntityManagerFactory() {
        Properties prop = new Properties();
        prop.setProperty("org.hibernate.envers.store_data_at_delete", "true"); // add this cause of constraints
        // test database
        prop.setProperty("hibernate.dialect","org.hibernate.dialect.OracleDialect");

        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(smpDataSource());
        lef.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lef.setJpaProperties(prop);
        lef.setPackagesToScan("eu.europa.ec.edelivery.smp.data.model");
        return lef;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

}
