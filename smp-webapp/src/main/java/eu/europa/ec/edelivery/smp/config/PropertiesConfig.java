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

import eu.europa.ec.edelivery.smp.utils.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        @PropertySource(value = "classpath:smp.config.properties", ignoreResourceNotFound = true)
})
public class PropertiesConfig {

    // create own instance because at this time SecurityUtilsServices is not ready to instantiate
    SecurityUtilsServices securityUtilsServices = new SecurityUtilsServices();

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertiesConfig = new PropertySourcesPlaceholderConfigurer();

        Properties prop = getDatabaseProperties();
        propertiesConfig.setProperties(prop);
        propertiesConfig.setLocalOverride(true);
        return propertiesConfig;
    }

    private Properties getDatabaseProperties() {

        Properties fileProperties = getFileProperties();
        // get datasource
        DataSource dataSource = getDatasource(fileProperties);
        EntityManager em = null;
        DatabaseProperties prop = null;
        try {
            em = createEntityManager(dataSource);
            prop = new DatabaseProperties(em);
            if (prop.size() == 0) {
                initializeProperties(em, fileProperties, prop);
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return prop;
    }

    /**
     * Method do the folling tasks
     * // copy SMPProperties
     * // copy and merge keystore
     * // secure password for keystore
     * // -- generate symmetric key
     * // -- encrypt key
     * // -- set password
     *
     * @param em
     * @param fileProperties
     */
    protected void initializeProperties(EntityManager em, Properties fileProperties, Properties initProperties) {
        em.getTransaction().begin();

        initNewValues(em, fileProperties, initProperties);
        for (SMPPropertyEnum val : SMPPropertyEnum.values()) {
            DBConfiguration dbConf = null;
            switch (val) {
                case CONFIGURATION_DIR:
                case KEYSTORE_FILENAME:
                case KEYSTORE_PASSWORD:
                case ENCRYPTION_FILENAME:
                case KEYSTORE_PASSWORD_DECRYPTED:
                    // skip value
                    break;
                default:
                    dbConf = createDBEntry(val.getProperty(), fileProperties.getProperty(val.getProperty(), val.getDefValue()),
                            val.getDesc());
            }
            if (dbConf != null) {
                initProperties.setProperty(dbConf.getProperty(), dbConf.getValue());
                em.persist(dbConf);
            }
        }
        em.getTransaction().commit();
    }

    /**
     * Settings folder is where keystore is located.
     * @param fileProperties
     * @return
     */
    protected File calculateSettingsPath( Properties fileProperties){
        String sigPath = fileProperties.getProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getProperty());
        if (sigPath == null) {
            sigPath = fileProperties.getProperty(SMPPropertyEnum.SML_KEYSTORE_PATH.getProperty());
        }
        File settingsFolder = null;
        if (sigPath != null) {
            settingsFolder = new File(sigPath).getParentFile();
        } else {
            settingsFolder = new File("");
        }
        return settingsFolder;
    }


    /**
     * Method initialize new values for configuration dir, ecryption filename, keystore password, and keystore filename.
     * @param em
     * @param fileProperties
     */
    protected void initNewValues(EntityManager em, Properties fileProperties, Properties initProperties) {

        File settingsFolder = calculateSettingsPath(fileProperties);

        // add configuration path
        storeDBEntry(em, SMPPropertyEnum.CONFIGURATION_DIR, settingsFolder.getPath());
        initProperties.setProperty(SMPPropertyEnum.CONFIGURATION_DIR.getProperty(), settingsFolder.getPath());
        String newKeyPassword = RandomStringUtils.random(8, true, true);
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD_DECRYPTED, newKeyPassword);


        // store encryption filename
        File fEncryption = new File(settingsFolder, SMPPropertyEnum.ENCRYPTION_FILENAME.getDefValue());
        securityUtilsServices.generatePrivateSymmetricKey(fEncryption);
        storeDBEntry(em, SMPPropertyEnum.ENCRYPTION_FILENAME, fEncryption.getName());
        initProperties.setProperty(SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty(), fEncryption.getName());

        // store keystore password  filename
        String encPasswd = securityUtilsServices.encrypt(fEncryption, newKeyPassword);
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD, encPasswd);
        initProperties.setProperty(SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty(), encPasswd);

        //store new keystore
        File keystore = new File(settingsFolder, SMPPropertyEnum.KEYSTORE_FILENAME.getDefValue());
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_FILENAME, keystore.getName());
        initProperties.setProperty(SMPPropertyEnum.KEYSTORE_FILENAME.getProperty(), keystore.getName());


        String sigKeystorePath = fileProperties.getProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getProperty(), null);
        String smlKeystorePath = fileProperties.getProperty(SMPPropertyEnum.SML_KEYSTORE_PATH.getProperty(), null);

        try (FileOutputStream out = new FileOutputStream(keystore)) {
            KeyStore newKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
            // initialize keystore
            newKeystore.load(null, newKeyPassword.toCharArray());
            // merge keys from signature keystore
          if (!StringUtils.isBlank(sigKeystorePath)) {
                String keypasswd = fileProperties.getProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PASSWORD.getProperty());
                try (FileInputStream fis = new FileInputStream(sigKeystorePath)) {
                    KeyStore sourceKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    sourceKeystore.load(fis, keypasswd.toCharArray());
                    securityUtilsServices.mergeKeystore(newKeystore, newKeyPassword, sourceKeystore, keypasswd);
                }
            }

            // merge keys from integration keystore
            if (!StringUtils.isBlank(smlKeystorePath) && !StringUtils.equalsIgnoreCase(smlKeystorePath, sigKeystorePath)) {
                String keypasswd = fileProperties.getProperty(SMPPropertyEnum.SML_KEYSTORE_PASSWORD.getProperty());
                try (FileInputStream fis = new FileInputStream(smlKeystorePath)) {
                    KeyStore sourceKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    sourceKeystore.load(fis, keypasswd.toCharArray());
                    securityUtilsServices.mergeKeystore(newKeystore, newKeyPassword, sourceKeystore, keypasswd);
                }
            }
            newKeystore.store(out, newKeyPassword.toCharArray());
        } catch (IOException e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "IOException occurred while creating keystore", e.getMessage());
        } catch (CertificateException e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "CertificateException occurred while creating keystore", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "NoSuchAlgorithmException occurred while creating keystore", e.getMessage());
        } catch (KeyStoreException e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "KeyStoreException occurred while creating keystore", e.getMessage());
        } catch (Exception e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while creating keystore", e.getMessage());
        }
    }


    protected DBConfiguration createDBEntry(String key, String value, String desc) {
        DBConfiguration dcnew = new DBConfiguration();
        dcnew.setProperty(key);
        dcnew.setDescription(desc);
        dcnew.setValue(value);
        dcnew.setLastUpdatedOn(LocalDateTime.now());
        dcnew.setCreatedOn(LocalDateTime.now());
        return dcnew;
    }

    protected DBConfiguration createDBEntry(SMPPropertyEnum prop, String value) {
        return createDBEntry(prop.getProperty(), value, prop.getDesc());
    }

    protected void storeDBEntry(EntityManager em, SMPPropertyEnum prop, String value) {
        DBConfiguration cnt = createDBEntry(prop.getProperty(), value, prop.getDesc());
        em.persist(cnt);
    }

    /**
     * create datasource to read properties from database
     *
     * @return
     */
    private DataSource getDatasource(Properties connectionProp) {

        DataSource datasource = null;
        String url = connectionProp.getProperty("jdbc.url");
        String jndiDatasourceName = connectionProp.getProperty("datasource.jndi");
        jndiDatasourceName = StringUtils.isBlank(jndiDatasourceName) ? "jdbc/smpDatasource" : jndiDatasourceName;

        if (url != null) {
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(connectionProp.getProperty("jdbc.driver"));
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(connectionProp.getProperty("jdbc.user"));
            driverManagerDataSource.setPassword(connectionProp.getProperty("jdbc.password"));
            datasource = driverManagerDataSource;
        } else {
            JndiObjectFactoryBean dataSource = new JndiObjectFactoryBean();
            dataSource.setJndiName(jndiDatasourceName);
            try {
                dataSource.afterPropertiesSet();
            } catch (IllegalArgumentException | NamingException e) {
                // rethrow
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred while retrieving datasource: " + jndiDatasourceName, e.getMessage());
            }
            datasource = (DataSource) dataSource.getObject();
        }
        return datasource;
    }

    protected Properties getFileProperties() {
        InputStream is = PropertiesConfig.class.getResourceAsStream("/smp.config.properties");
        if (is == null) {
            is = PropertiesConfig.class.getResourceAsStream("/config.properties");
        }
        Properties connectionProp = new Properties();
        try {
            connectionProp.load(is);
        } catch (IOException e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred  while reading properties.", e.getMessage());
        }
        return connectionProp;
    }

    /**
     * Create entity manager just for property updates to handle date columns for different databases.
     *
     * @param dataSource
     * @return
     */
    private EntityManager createEntityManager(DataSource dataSource) {
        Properties prop = new Properties();
        prop.setProperty("hibernate.connection.autocommit", "true");
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lef.setPackagesToScan("eu.europa.ec.edelivery.smp.data.model");
        lef.setJpaProperties(prop);
        lef.afterPropertiesSet();
        EntityManagerFactory enf = lef.getObject();
        return enf.createEntityManager();
    }

    private static class DatabaseProperties extends Properties {

        private static final long serialVersionUID = 1L;

        public DatabaseProperties(EntityManager em) {
            super();
            TypedQuery<DBConfiguration> tq = em.createNamedQuery("DBConfiguration.getAll", DBConfiguration.class);
            List<DBConfiguration> lst = tq.getResultList();
            for (DBConfiguration dc : lst) {
                setProperty(dc.getProperty(), dc.getValue());
            }
        }
    }

}
