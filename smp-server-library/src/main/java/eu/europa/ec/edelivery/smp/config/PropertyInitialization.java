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
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.utils.X509CertificateUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
    // application properties contains build data and are set at build time.
    private static final String FILE_APPLICATION_PROPERTIES = "/application.properties";

    private static final String PROP_BUILD_NAME = "smp.artifact.name";
    private static final String PROP_BUILD_VERSION = "smp.artifact.version";
    private static final String PROP_BUILD_TIME = "smp.artifact.build.time";


    SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyInitialization.class);


    public void logBuildProperties() {
        InputStream is = PropertyInitialization.class.getResourceAsStream(FILE_APPLICATION_PROPERTIES);
        if (is != null) {
            Properties applProp = new Properties();
            try {
                applProp.load(is);
                LOG.info("*****************************************************************************************");
                LOG.info("Start application: name: {}, version: {}, build time: {}.", applProp.getProperty(PROP_BUILD_NAME)
                        , applProp.getProperty(PROP_BUILD_VERSION)
                        , applProp.getProperty(PROP_BUILD_TIME));
                LOG.info("*****************************************************************************************");
            } catch (IOException e) {
                LOG.error("Error occurred  while reading application properties. Is file " + FILE_APPLICATION_PROPERTIES + " included in war!", e);
            }
        } else {
            LOG.error("Not found application build properties: {}!", FILE_APPLICATION_PROPERTIES);
        }
    }

    protected Properties getDatabaseProperties(Properties fileProperties) {
        String dialect = fileProperties.getProperty(FileProperty.PROPERTY_DB_DIALECT);
        if (StringUtils.isBlank(dialect)) {
            LOG.warn("Attribute: {} is empty. Database might not initialize!", FileProperty.PROPERTY_DB_DIALECT);
        }
        // get datasource
        DataSource dataSource = getDatasource(fileProperties);
        EntityManager em = null;
        DatabaseProperties prop = null;
        try {
            em = createEntityManager(dataSource, dialect);
            prop = new DatabaseProperties(em);
            if (prop.size() == 0) {
                initializeProperties(em, fileProperties, prop);
            } else {
                validateProperties(em, fileProperties, prop);
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
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
    protected void initializeProperties(EntityManager em, Properties fileProperties, Properties initProperties) {
        em.getTransaction().begin();
        LOG.warn("Database configuration table is empty! Initialize new values!");
        File encFile = initNewValues(em, fileProperties, initProperties);

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

    public void initTruststore(String absolutePath, File fEncryption, EntityManager em, Properties properties, Properties fileProperties) {
        LOG.info("Start generating new truststore.");

        String encTrustEncToken;

        if (fileProperties.containsKey(SMPPropertyEnum.TRUSTSTORE_PASSWORD.getProperty())) {
            LOG.info("get token from  properties");
            encTrustEncToken = SecurityUtils.encryptWrappedToken(fEncryption,
                    fileProperties.getProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD.getProperty()));
        } else {
            // generate new token
            LOG.info("generate  token");
            String trustToken = SecurityUtils.generateAuthenticationToken();
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
            } catch (IOException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "IOException occurred while creating truststore", e.getMessage());
            } catch (CertificateException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "CertificateException occurred while creating truststore", e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "NoSuchAlgorithmException occurred while creating truststore", e.getMessage());
            } catch (KeyStoreException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "KeyStoreException occurred while creating truststore", e.getMessage());
            } catch (Exception e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while creating truststore", e.getMessage());
            }
        }
    }

    public void initAndMergeKeystore(String absolutePath, File fEncryption, EntityManager em, Properties initProperties,
                                     Properties fileProperties) {

        // store keystore password  filename
        String newKeyPassword = SecurityUtils.generateAuthenticationToken();
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
                X509CertificateUtils.createAndAddTextCertificate("CN=SMP_TEST-PRE-SET-EXAMPLE, OU=eDelivery, O=DIGITAL, C=BE", newKeystore, newKeyPassword);
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

    public File initEncryptionKey(String absolutePath, EntityManager em, Properties initProperties, Properties fileProperties) {
        File fEncryption;
        if (fileProperties.containsKey(ENCRYPTION_FILENAME.getProperty())) {
            fEncryption = new File(absolutePath, fileProperties.getProperty(ENCRYPTION_FILENAME.getProperty()));

        } else {
            fEncryption = getNewFile(absolutePath, SMPPropertyEnum.ENCRYPTION_FILENAME.getDefValue());
        }
        ;
        // if file is not existing yet - as is the case in getNewFile create file
        if (!fEncryption.exists()) {
            SecurityUtils.generatePrivateSymmetricKey(fEncryption);
        }

        SecurityUtils.generatePrivateSymmetricKey(fEncryption);
        storeDBEntry(em, SMPPropertyEnum.ENCRYPTION_FILENAME, fEncryption.getName());
        initProperties.setProperty(SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty(), fEncryption.getName());
        return fEncryption;
    }

    /**
     * Method initialize new values for configuration dir, encryption filename, keystore password, and keystore filename.
     *
     * @param em
     * @param fileProperties
     */
    protected File initNewValues(EntityManager em, Properties fileProperties, Properties initProperties) {
        String absolutePath;
        if (fileProperties.containsKey(CONFIGURATION_DIR.getProperty())) {
            absolutePath = fileProperties.getProperty(CONFIGURATION_DIR.getProperty());
        } else {
            File settingsFolder = new File("/");
            // set absolute path
            absolutePath = new File("/").getAbsolutePath();
        }

        File confFolder = new File(absolutePath);
        if (!confFolder.exists()) {
            LOG.warn("Configuration folder {} not exists. Folder will be created!", confFolder.getAbsolutePath());
            confFolder.mkdirs();
        }

        LOG.info("Generate new keystore to folder: " + absolutePath);
        // add configuration path
        storeDBEntry(em, CONFIGURATION_DIR, absolutePath);
        initProperties.setProperty(CONFIGURATION_DIR.getProperty(), absolutePath);

        // init encryption filename

        File fEncryption = initEncryptionKey(absolutePath, em, initProperties, fileProperties);

        // init truststore
        initTruststore(absolutePath, fEncryption, em, initProperties, fileProperties);
        initAndMergeKeystore(absolutePath, fEncryption, em, initProperties, fileProperties);

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
    protected void validateProperties(EntityManager em, Properties fileProperties, Properties databaseProperties) {
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
            initTruststore(configurationDir, fEncryption, em, databaseProperties, fileProperties);
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
