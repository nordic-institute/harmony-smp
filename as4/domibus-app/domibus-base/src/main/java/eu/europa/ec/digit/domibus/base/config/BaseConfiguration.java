package eu.europa.ec.digit.domibus.base.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.ValidationMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.europa.ec.digit.domibus.common.config.domibus.DomibusCommonConfiguration;

@PropertySource({
    "classpath:datasource.properties",
    "classpath:hibernate.properties",
    "classpath:endpoint.properties"
})
@Import ({
	DomibusCommonConfiguration.class
})
@EnableTransactionManagement
public abstract class BaseConfiguration {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    @Autowired
    private Environment environment = null;

    @Autowired
    private DataSourceConfiguration dataConfiguration = null;

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */
    
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(environment.getProperty("hibernate.generate_sql")));
        jpaVendorAdapter.setShowSql(Boolean.valueOf(environment.getProperty("hibernate.show_sql")));
        return jpaVendorAdapter;
    }

    @Bean
    public Properties additionalProperties() {
        Properties properties = new Properties();
        //properties.put("hibernate.hbm2ddl.auto", this.environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", this.environment.getProperty("persistence.dialect"));
        return properties;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(this.getDataConfiguration().dataSource());
        emf.setPackagesToScan(
        	"eu.domibus",
        	"eu.europa.ec.digit.domibus.entity");
        emf.setJpaVendorAdapter(jpaVendorAdapter());
        emf.setJpaProperties(additionalProperties());
        emf.setValidationMode(ValidationMode.AUTO);
        emf.setPersistenceUnitName("domibus");
        return emf;
    }

    @Bean (name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean (name = "persistenceExceptionTranslationPostProcessor")
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataConfiguration().dataSource());
        return jdbcTemplate;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
        	"classpath:i18n/business_messages",
        	"classpath:i18n/generic_messages",
        	"classpath:i18n/parsing_messages",
        	"classpath:i18n/program_messages",
        	"classpath:i18n/validation_messages"
        );
        return messageSource;
    }

    /* ---- Getters and Setters ---- */

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

	public DataSourceConfiguration getDataConfiguration() {
		return dataConfiguration;
	}

	public void setDataConfiguration(DataSourceConfiguration dataConfiguration) {
		this.dataConfiguration = dataConfiguration;
	}
}
