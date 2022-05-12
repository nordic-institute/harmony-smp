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

import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

/**
 * Created by Flavio Santos
 * Class read properties from configuration file if exists. Than it use datasource (default by JNDI
 * if not defined in property file jdbc/smpDatasource) to read application properties. Because this class is
 * invoked before datasource is initialized by default - it creates it's own database connection.
 * Also it uses hibernate to handle dates  for Configuration table.
 */
public class PropertyInitialization {

    SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyInitialization.class);
    // if SMP is initialized without keystore - a demo keystore with test certificate is created
    private static final String TEST_CERT_ISSUER_DN = "CN=rootCNTest,OU=B4,O=DIGIT,L=Brussels,ST=BE,C=BE";
    private static final String TEST_CERT_SUBJECT_DN = "CN=SMP_TEST-PRE-SET-EXAMPLE, OU=eDelivery, O=DIGITAL, C=BE";
    private static final String TEST_CERT_ISSUER_ALIAS = "issuer";
    private static final String TEST_CERT_CERT_ALIAS = "sample_key";

    protected Properties getDatabaseProperties(Properties fileProperties) {
        String dialect = fileProperties.getProperty(FileProperty.PROPERTY_DB_DIALECT);
        if (StringUtils.isBlank(dialect)) {
            LOG.warn("The application property: {} is not set!. Database might not initialize!", FileProperty.PROPERTY_DB_DIALECT);
        }
        // get datasource
        DataSource dataSource = getDatasource(fileProperties);
        EntityManager em = null;
        DatabaseProperties prop;
        boolean devMode = Boolean.parseBoolean(fileProperties.getProperty(FileProperty.PROPERTY_SMP_MODE_DEVELOPMENT, "false"));
        if (devMode) {
            LOG.warn("***********************************************************************");
            LOG.warn("WARNING: The SMP is started in DEVELOPMENT mode!");
            LOG.warn("***********************************************************************");
        }
        try {
            em = createEntityManager(dataSource, dialect);
            prop = new DatabaseProperties(em);
            if (prop.size() == 0) {
                initializeProperties(em, fileProperties, prop, devMode);
            } else {
                validateProperties(em, fileProperties, prop, devMode);
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        prop.setProperty(FileProperty.PROPERTY_SMP_MODE_DEVELOPMENT, Boolean.toString(devMode));
        return prop;
    }

    /**
     * Method do the next tasks
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
    protected void initializeProperties(EntityManager em, Properties fileProperties, Properties initProperties, boolean testMode) {
        em.getTransaction().begin();
        LOG.warn("Database configuration table is empty! Initialize new values!");
        File encFile = initNewValues(em, fileProperties, initProperties, testMode);

        for (SMPPropertyEnum val : SMPPropertyEnum.values()) {
            DBConfiguration dbConf = null;
            switch (val) {
                case CONFIGURATION_DIR:
                case TRUSTSTORE_FILENAME:
                case TRUSTSTORE_PASSWORD:
                case KEYSTORE_FILENAME:
                case TRUSTSTORE_PASSWORD_DECRYPTED:
                case KEYSTORE_PASSWORD:
                case ENCRYPTION_FILENAME:
                case KEYSTORE_PASSWORD_DECRYPTED:
                    // skip values because they are aready created in initNewValues method
                    break;
                default:
                    // insert only non deprecated values
                    if (val.getDesc() == null || !val.getDesc().trim().equalsIgnoreCase("deprecated")) {
                        String value = fileProperties.getProperty(val.getProperty(), val.getDefValue());
                        if (val.isEncrypted()) {
                            value = SecurityUtils.encryptWrappedToken(encFile, value);
                        }
                        dbConf = createDBEntry(val.getProperty(), value, val.getDesc());
                    }
            }
            if (dbConf != null) {
                initProperties.setProperty(dbConf.getProperty(), dbConf.getValue());

                em.persist(dbConf);
            }
        }
        em.getTransaction().commit();
    }

    public void initTruststore(String absolutePath, File fEncryption, EntityManager em, Properties properties, Properties fileProperties, boolean testMode) {
        LOG.info("Start generating new truststore.");
        String encTrustEncToken;
        if (fileProperties.containsKey(SMPPropertyEnum.TRUSTSTORE_PASSWORD.getProperty())) {
            LOG.info("get token from  properties");
            encTrustEncToken = SecurityUtils.encryptWrappedToken(fEncryption,
                    fileProperties.getProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD.getProperty()));
        } else {
            // generate new token
            LOG.info("generate  token");
            String trustToken = SecurityUtils.generateAuthenticationToken(testMode);
            storeDBEntry(em, SMPPropertyEnum.TRUSTSTORE_PASSWORD_DECRYPTED, trustToken);
            encTrustEncToken = SecurityUtils.encrypt(fEncryption, trustToken);
        }
        LOG.info("Store truststore security token to database");
        // store token to database
        storeDBEntry(em, SMPPropertyEnum.TRUSTSTORE_PASSWORD, encTrustEncToken);
        properties.setProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD.getProperty(), encTrustEncToken);

        LOG.info("Decode security token");
        String trustToken = SecurityUtils.decrypt(fEncryption, encTrustEncToken);
        LOG.info("Get keystore");
        File truststore;
        if (fileProperties.containsKey(SMPPropertyEnum.TRUSTSTORE_FILENAME.getProperty())) {
            LOG.info("Get  truststore value from property file");
            truststore = new File(absolutePath, fileProperties.getProperty(
                    SMPPropertyEnum.TRUSTSTORE_FILENAME.getProperty()));

        } else {
            LOG.info("Generate  truststore file ");
            truststore = getNewFile(absolutePath, "smp-truststore.jks");
        }
        LOG.info("Generate new truststore to file [{}]!", truststore.getAbsolutePath());
        // store file to database 
        storeDBEntry(em, SMPPropertyEnum.TRUSTSTORE_FILENAME, truststore.getName());
        properties.setProperty(SMPPropertyEnum.TRUSTSTORE_FILENAME.getProperty(), truststore.getName());

        // if truststore does not exist create a new file
        if (!truststore.exists()) {

            LOG.info("Generate new truststore file {}.", truststore.getAbsolutePath());
            try (FileOutputStream out = new FileOutputStream(truststore)) {
                KeyStore newTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                // init the truststore
                newTrustStore.load(null, trustToken.toCharArray());
                newTrustStore.store(out, trustToken.toCharArray());
            } catch (Exception e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while creating truststore", ExceptionUtils.getRootCauseMessage(e));
            }
        }
    }

    public void initAndMergeKeystore(String absolutePath, File fEncryption, EntityManager em, Properties initProperties,
                                     Properties fileProperties, boolean testMode) {
        LOG.info("Start generating new keystore.");
        // store keystore password  filename
        String newKeyPassword = SecurityUtils.generateAuthenticationToken(testMode);
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD_DECRYPTED, newKeyPassword);
        String encPasswd = SecurityUtils.encrypt(fEncryption, newKeyPassword);
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD, encPasswd);
        initProperties.setProperty(SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty(), encPasswd);

        //store new keystore
        File keystore = getNewFile(absolutePath, SMPPropertyEnum.KEYSTORE_FILENAME.getDefValue());
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_FILENAME, keystore.getName());
        initProperties.setProperty(SMPPropertyEnum.KEYSTORE_FILENAME.getProperty(), keystore.getName());

        try (FileOutputStream out = new FileOutputStream(keystore)) {
            KeyStore newKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
            // initialize keystore
            newKeystore.load(null, newKeyPassword.toCharArray());
            // check if keystore is empty then generate cert for user
            if (newKeystore.size() == 0) {
                X509CertificateUtils.createAndStoreCertificateWithChain(
                        new String[]{TEST_CERT_ISSUER_DN, TEST_CERT_SUBJECT_DN},
                        new String[]{TEST_CERT_ISSUER_ALIAS, TEST_CERT_CERT_ALIAS},
                        newKeystore, newKeyPassword);
            }
            newKeystore.store(out, newKeyPassword.toCharArray());
        } catch (Exception e) {
            throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while creating keystore", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public File initEncryptionKey(String absolutePath, EntityManager em, Properties initProperties, Properties fileProperties) {
        LOG.info("Calculate encryption key [{}]. This could take some time!", absolutePath);
        File fEncryption;
        if (fileProperties.containsKey(ENCRYPTION_FILENAME.getProperty())) {
            fEncryption = new File(absolutePath, fileProperties.getProperty(ENCRYPTION_FILENAME.getProperty()));

        } else {
            fEncryption = getNewFile(absolutePath, ENCRYPTION_FILENAME.getDefValue());
        }
        // if file is not existing yet - as is the case in getNewFile create file
        if (!fEncryption.exists()) {
            SecurityUtils.generatePrivateSymmetricKey(fEncryption);
        }

        SecurityUtils.generatePrivateSymmetricKey(fEncryption);
        LOG.info("Encryption key generated.");
        storeDBEntry(em, ENCRYPTION_FILENAME, fEncryption.getName());
        initProperties.setProperty(ENCRYPTION_FILENAME.getProperty(), fEncryption.getName());
        return fEncryption;
    }

    /**
     * Method initialize new values for configuration dir, encryption filename, keystore password, and keystore filename.
     *
     * @param em
     * @param fileProperties
     */
    protected File initNewValues(EntityManager em, Properties fileProperties, Properties initProperties, boolean testMode) {
        String absolutePath;
        if (fileProperties.containsKey(CONFIGURATION_DIR.getProperty())) {
            absolutePath = fileProperties.getProperty(CONFIGURATION_DIR.getProperty());
        } else {
            // set absolute path
            absolutePath = Paths.get(CONFIGURATION_DIR.getDefValue()).toFile().getAbsolutePath();
            LOG.warn("The property [{}] Initialize SMP configuration files to folder [{}]!", CONFIGURATION_DIR.getProperty(), absolutePath);
        }

        File confFolder = new File(absolutePath);
        if (!confFolder.exists()) {
            LOG.warn("Configuration folder [{}] not exists. Folder will be created!", confFolder.getAbsolutePath());
            confFolder.mkdirs();
        }
        // add configuration path
        storeDBEntry(em, CONFIGURATION_DIR, absolutePath);
        initProperties.setProperty(CONFIGURATION_DIR.getProperty(), absolutePath);

        // init encryption filename
        File fEncryption = initEncryptionKey(absolutePath, em, initProperties, fileProperties);

        // init truststore
        initTruststore(absolutePath, fEncryption, em, initProperties, fileProperties, testMode);
        initAndMergeKeystore(absolutePath, fEncryption, em, initProperties, fileProperties, testMode);

        return fEncryption;
    }

    public static File getNewFile(String folder, String fileName) {
        File file = new File(folder, fileName);
        if (file.exists()) {
            int index = 0;
            File f = null;
            // search for new file
            while ((f = new File(folder, fileName + "." + (++index))).exists()) {

            }
            try {
                Files.move(file.toPath(), f.toPath());
            } catch (IOException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while renaming file:" + fileName, e.getMessage());
            }
        }
        return file;

    }

    /**
     * Method do the next tasks
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
    protected void validateProperties(EntityManager em, Properties fileProperties, Properties databaseProperties, boolean devMode) {
        em.getTransaction().begin();

        if (!databaseProperties.containsKey(CONFIGURATION_DIR.getProperty())) {
            String folder = (new File("./")).getAbsolutePath();
            LOG.warn("Missing property: {} set new walue: {}", CONFIGURATION_DIR.getProperty(), folder);
            storeDBEntry(em, CONFIGURATION_DIR, folder);
            databaseProperties.setProperty(CONFIGURATION_DIR.getProperty(), folder);
        }

        String configurationDir = databaseProperties.getProperty(CONFIGURATION_DIR.getProperty());
        File fEncryption = null;
        if (!databaseProperties.containsKey(ENCRYPTION_FILENAME.getProperty())) {
            fEncryption = initEncryptionKey(configurationDir, em, databaseProperties, fileProperties);
        } else {
            String encryptionFilename = databaseProperties.getProperty(ENCRYPTION_FILENAME.getProperty());
            fEncryption = new File(configurationDir + File.separator + encryptionFilename);
        }
        if (!fEncryption.exists()) {
            LOG.error("Encryption key file '{}' does not exists. Remove configuration and restart the server!", fEncryption.getAbsolutePath());
            throw new SMPRuntimeException(INTERNAL_ERROR, "Encryption file '{}' from the configuration does not exist!", fEncryption.getAbsolutePath());
        }


        if (!databaseProperties.containsKey(KEYSTORE_FILENAME.getProperty())) {
            throw new SMPRuntimeException(INTERNAL_ERROR, "Keystore file does not exists.!");
        }

        // init this one because it is new!
        if (!databaseProperties.containsKey(TRUSTSTORE_FILENAME.getProperty())) {
            initTruststore(configurationDir, fEncryption, em, databaseProperties, fileProperties, devMode);
        }
        em.getTransaction().commit();
    }


    protected DBConfiguration createDBEntry(String key, String value, String desc) {
        DBConfiguration dcnew = new DBConfiguration();
        dcnew.setProperty(key);
        dcnew.setDescription(desc);
        dcnew.setValue(value);
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
    protected DataSource getDatasource(Properties connectionProp) {
        LOG.info("Start database properties");
        DataSource datasource;
        String url = connectionProp.getProperty(FileProperty.PROPERTY_DB_URL);
        String jndiDatasourceName = connectionProp.getProperty(FileProperty.PROPERTY_DB_JNDI);
        jndiDatasourceName = StringUtils.isBlank(jndiDatasourceName) ? "jdbc/smpDatasource" : jndiDatasourceName;

        if (!StringUtils.isBlank(url)) {
            LOG.info("Connect to {}.", url);
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(connectionProp.getProperty("jdbc.driver"));
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(connectionProp.getProperty("jdbc.user"));
            driverManagerDataSource.setPassword(connectionProp.getProperty("jdbc.password"));
            datasource = driverManagerDataSource;
        } else {
            LOG.info("Use JNDI {} to connect to database.", jndiDatasourceName);
            JndiObjectFactoryBean dataSource = new JndiObjectFactoryBean();
            dataSource.setJndiName(jndiDatasourceName);
            try {
                dataSource.afterPropertiesSet();
            } catch (IllegalArgumentException | NamingException e) {
                // rethrow
                LOG.error("Error occurred while retriving datasource whith JNDI {}. Is datasource configured in server!", jndiDatasourceName);
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Error occurred while retrieving datasource: " + jndiDatasourceName, e.getMessage());
            }
            datasource = (DataSource) dataSource.getObject();
        }
        return datasource;
    }


    /**
     * Create entity manager just for property updates to handle date columns for different databases.
     *
     * @param dataSource
     * @return
     */
    private EntityManager createEntityManager(DataSource dataSource, String databaseDialect) {
        LOG.info("Init entity manager with dialect: {}", databaseDialect);
        Properties prop = new Properties();
        prop.setProperty("hibernate.connection.autocommit", "true");
        if (!StringUtils.isBlank(databaseDialect)) {
            prop.setProperty("hibernate.dialect", databaseDialect);
        }
        prop.setProperty("org.hibernate.envers.store_data_at_delete", "true");
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        lef.setPackagesToScan("eu.europa.ec.edelivery.smp.data.model");
        lef.setJpaProperties(prop);
        lef.afterPropertiesSet();
        EntityManagerFactory enf = lef.getObject();
        return enf.createEntityManager();
    }
}
