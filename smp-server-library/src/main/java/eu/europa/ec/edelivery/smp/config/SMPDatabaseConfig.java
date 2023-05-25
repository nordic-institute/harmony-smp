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

import eu.europa.ec.edelivery.smp.config.init.DatabaseConnectionBeanCreator;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * The configuration class instantiates the database spring beans.
 *
 * @author Flavio Santos
 * @author Joze Rihtarsic
 * @since 3.0
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"eu.europa.ec.edelivery.smp.data.dao"})
public class SMPDatabaseConfig {
    static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPDatabaseConfig.class);
    final DatabaseConnectionBeanCreator databaseConnectionBeanCreator;

    public SMPDatabaseConfig() {
        databaseConnectionBeanCreator = new DatabaseConnectionBeanCreator(SMPEnvironmentProperties.getInstance());
    }

    @Primary
    @Bean(name = "smpDataSource")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSource getDataSource() {
        LOG.info("Create DomiSMP datasource");
        return databaseConnectionBeanCreator.getDataSource();
    }

    @Primary
    @Bean(name = "smpEntityManagerFactory")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public LocalContainerEntityManagerFactoryBean smpEntityManagerFactory(@Qualifier("smpDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LOG.info("Create DomiSMP EntityManagerFactory");
        return databaseConnectionBeanCreator.smpEntityManagerFactory(dataSource, jpaVendorAdapter);
    }

    @Primary
    @Bean(name = "transactionManager")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PlatformTransactionManager smpTransactionManager(@Qualifier("smpEntityManagerFactory") EntityManagerFactory emf) {
        LOG.info("Create DomiSMP TransactionManager");
        return databaseConnectionBeanCreator.getSmpTransactionManager(emf);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return databaseConnectionBeanCreator.getJpaVendorAdapter();
    }
}
