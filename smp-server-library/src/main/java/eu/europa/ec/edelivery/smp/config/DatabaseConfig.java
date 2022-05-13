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

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

/**
 * Created by Flavio Santos
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"eu.europa.ec.edelivery.smp.data.dao"})
public class DatabaseConfig {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DatabaseConfig.class);


    @Value("${" + FileProperty.PROPERTY_DB_DRIVER + ":}")
    protected String driver;

    @Value("${" + FileProperty.PROPERTY_DB_USER + ":}")
    protected String username;

    @Value("${" + FileProperty.PROPERTY_DB_TOKEN + ":}")
    protected String password;

    @Value("${" + FileProperty.PROPERTY_DB_URL + ":}")
    protected String url;
    // set default jdbc
    @Value("${" + FileProperty.PROPERTY_DB_JNDI + ":jdbc/smpDatasource}")
    protected String jndiDatasourceName;

    @Value("${" + FileProperty.PROPERTY_DB_DIALECT + ":}")
    protected String hibernateDialect;


    @Bean(name = "dataSource")
    public DataSource getDataSource() {

        DataSource dataSource;
        if (!StringUtils.isBlank(url)) {
            LOG.info("create datasource with URL: " + url);
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(driver);
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(username);
            driverManagerDataSource.setPassword(password);
            dataSource = driverManagerDataSource;
        } else {
            LOG.info("Retrieve datasource with JNDI: " + jndiDatasourceName);
            JndiObjectFactoryBean jndiDataSource = new JndiObjectFactoryBean();
            jndiDataSource.setJndiName(jndiDatasourceName);
            try {
                jndiDataSource.afterPropertiesSet();
            } catch (IllegalArgumentException | NamingException e) {
                // rethrow
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "while retrieving datasource: " + jndiDatasourceName, e.getMessage());
            }
            dataSource = (DataSource) jndiDataSource.getObject();
        }
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean smpEntityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        Properties prop = new Properties();
        prop.setProperty("org.hibernate.envers.store_data_at_delete", "true");
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("eu.europa.ec.edelivery.smp.data.model");
        lef.setJpaProperties(prop);
        return lef;
    }

    @Bean
    public PlatformTransactionManager smpTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        if (!StringUtils.isBlank(hibernateDialect)) {
            hibernateJpaVendorAdapter.setDatabasePlatform(hibernateDialect);
        }
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }
}
