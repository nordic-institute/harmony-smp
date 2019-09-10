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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

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
public class PropertyInitialization {

    private static final String PROP_BUILD_NAME="smp.artifact.name";
    private static final String PROP_BUILD_VERSION="smp.artifact.version";
    private static final String PROP_BUILD_TIME="smp.artifact.build.time";

    private static final String VALID_PW_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>?,./";
    private static final int DEFAULT_PASSWORD_LENGTH = 16;

    SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyInitialization.class);


    public void logBuildProperties(){
        InputStream is = PropertyInitialization.class.getResourceAsStream("/application.properties");
        if(is!=null){
            Properties applProp = new Properties();
            try {
                applProp.load(is);
                LOG.info("*****************************************************************************************");
                LOG.info("Start application: name: {}, version: {}, build time: {}.",applProp.getProperty(PROP_BUILD_NAME)
                        ,applProp.getProperty(PROP_BUILD_VERSION)
                        ,applProp.getProperty(PROP_BUILD_TIME));
                LOG.info("*****************************************************************************************");
            } catch (IOException e) {
                LOG.error( "Error occurred  while reading application properties. Is file /application.properties included in war!", e);
            }
        } else {
            LOG.error( "Not found application build properties: /application.properties!");
        }
    }

    public Properties getFileProperties(){
        return FileProperty.getFileProperties();
    }

    protected Properties getDatabaseProperties(Properties fileProperties) {

        // get datasource
        DataSource dataSource = getDatasource(fileProperties);
        EntityManager em = null;
        DatabaseProperties prop = null;
        try {
            em = createEntityManager(dataSource);
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


    protected Properties getDatabaseProperties() {
        Properties fileProperties = FileProperty.getFileProperties();
        return getDatabaseProperties(fileProperties);
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
        LOG.warn( "Database configuration table is empty! Initialize new values!");
        initNewValues(em, fileProperties, initProperties);
        for (SMPPropertyEnum val : SMPPropertyEnum.values()) {
            DBConfiguration dbConf = null;
            switch (val) {
                case CONFIGURATION_DIR:
                case KEYSTORE_FILENAME:
                case KEYSTORE_PASSWORD:
                case ENCRYPTION_FILENAME:
                case KEYSTORE_PASSWORD_DECRYPTED:
                    // skip values because they are aready created in initNewValues method
                    break;
                default:
                    // insert only non deprecated values
                    if (val.getDesc() ==null || !val.getDesc().trim().equalsIgnoreCase("deprecated")) {
                        dbConf = createDBEntry(val.getProperty(), fileProperties.getProperty(val.getProperty(), val.getDefValue()),
                                val.getDesc());
                    }
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
     * Method initialize new values for configuration dir, encryption filename, keystore password, and keystore filename.
     * @param em
     * @param fileProperties
     */
    protected void initNewValues(EntityManager em, Properties fileProperties, Properties initProperties) {

        File settingsFolder = calculateSettingsPath(fileProperties);
        // set absolute path
        String absolutePath = settingsFolder.getAbsolutePath();
        LOG.info( "Generate new keystore to folder: " + absolutePath);

        // add configuration path
        storeDBEntry(em, CONFIGURATION_DIR, absolutePath);
        initProperties.setProperty(CONFIGURATION_DIR.getProperty(), absolutePath);
        String newKeyPassword = null;
        try {
            newKeyPassword = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 0, VALID_PW_CHARS.length(),
                    false, false,
                    VALID_PW_CHARS.toCharArray(), SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            String msg = "Error occurred while generation test password: No strong random algorithm. Error:"
                    + ExceptionUtils.getRootCauseMessage(e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, msg, e.getMessage());
        }


        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD_DECRYPTED, newKeyPassword);

        // store encryption filename
        File fEncryption =getNewFile(absolutePath, SMPPropertyEnum.ENCRYPTION_FILENAME.getDefValue());

        LOG.info( "Generate new encryption key: " + fEncryption.getName()+ " settings folder: " + absolutePath);
        SecurityUtils.generatePrivateSymmetricKey(fEncryption);
        storeDBEntry(em, SMPPropertyEnum.ENCRYPTION_FILENAME, fEncryption.getName());
        initProperties.setProperty(SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty(), fEncryption.getName());

        // store keystore password  filename
        String encPasswd = SecurityUtils.encrypt(fEncryption, newKeyPassword);
        storeDBEntry(em, SMPPropertyEnum.KEYSTORE_PASSWORD, encPasswd);
        initProperties.setProperty(SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty(), encPasswd);

        //store new keystore
        File keystore = getNewFile(absolutePath, SMPPropertyEnum.KEYSTORE_FILENAME.getDefValue());
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
              LOG.info( "Import keys from keystore for signature: " + sigKeystorePath);
                String keypasswd = fileProperties.getProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PASSWORD.getProperty());
                try (FileInputStream fis = new FileInputStream(sigKeystorePath)) {
                    KeyStore sourceKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    sourceKeystore.load(fis, keypasswd.toCharArray());
                    SecurityUtils.mergeKeystore(newKeystore, newKeyPassword, sourceKeystore, keypasswd);
                    // if there is only one certificate - update null signature aliases
                    if (sourceKeystore.size()==1) {
                        String alias = sourceKeystore.aliases().nextElement();
                        updateAlias(em, "DBDomain.updateNullSignAlias",alias );
                        if (StringUtils.equalsIgnoreCase(smlKeystorePath, sigKeystorePath)){
                            updateAlias(em, "DBDomain.updateNullSMLAlias",alias );
                        }
                    }
                }
            }

            // merge keys from integration keystore
            if (!StringUtils.isBlank(smlKeystorePath) && !StringUtils.equalsIgnoreCase(smlKeystorePath, sigKeystorePath)) {
                LOG.info( "Import keys from keystore for sml integration: " + smlKeystorePath);
                String keypasswd = fileProperties.getProperty(SMPPropertyEnum.SML_KEYSTORE_PASSWORD.getProperty());
                try (FileInputStream fis = new FileInputStream(smlKeystorePath)) {
                    KeyStore sourceKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    sourceKeystore.load(fis, keypasswd.toCharArray());

                    SecurityUtils.mergeKeystore(newKeystore, newKeyPassword, sourceKeystore, keypasswd);
                    // if there is only one cetificate - update null signature aliases
                    if (sourceKeystore.size()==1) {
                        updateAlias(em, "DBDomain.updateNullSMLAlias",sourceKeystore.aliases().nextElement() );
                    }
                }
            }
            // check if keystore is empty then generate cert for user
            if (newKeystore.size()==0) {
                X509CertificateUtils.createAndAddTextCertificate("CN=SMP_TEST-"+ UUID.randomUUID().toString()+", OU=eDelivery, O=DIGITAL, C=BE", newKeystore, newKeyPassword);
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

    public File getNewFile(String folder, String fileName){
        File file = new File(folder, fileName);
        if (file.exists()){
            int index = 0;
            File f= null;
            // search for new file
            while((f= new File(folder, fileName+"."+ (++index))).exists()){

            }
            try {
                Files.move(file.toPath(), f.toPath());
            } catch (IOException e) {
                throw new SMPRuntimeException(INTERNAL_ERROR, e, "Exception occurred while renaming file:" + fileName , e.getMessage());
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

        if (!databaseProperties.containsKey(CONFIGURATION_DIR.getProperty())){
            String folder = (new File("./")).getAbsolutePath();
            LOG.warn("Missing property: {} set new walue: {}", CONFIGURATION_DIR.getProperty(), folder );
            storeDBEntry(em,CONFIGURATION_DIR, folder);
            databaseProperties.setProperty(CONFIGURATION_DIR.getProperty(), folder);
        }


        String configurationDir = databaseProperties.getProperty(CONFIGURATION_DIR.getProperty());
        String encryptionFilename = databaseProperties.getProperty(ENCRYPTION_FILENAME.getProperty());
        String keystoreFilePath = databaseProperties.getProperty(KEYSTORE_FILENAME.getProperty());
        String keystorePassword = databaseProperties.getProperty(KEYSTORE_PASSWORD.getProperty());
        String keystorePasswordDec = databaseProperties.getProperty(KEYSTORE_PASSWORD_DECRYPTED.getProperty());

        File file = new File(configurationDir + File.separator + encryptionFilename);
        File keystoreFile = new File(configurationDir + File.separator + keystoreFilePath);
        if (!file.exists()) {
            LOG.error("Encryption key file '{}' does not exists!", file.getAbsolutePath());
            return;
        }

        //
        // if there is only one certificate - and there is empty alias
        //initializeProperties();

        em.getTransaction().commit();
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

    protected void updateAlias(EntityManager em,String namedQuery, String alias) {
        Query query = em.createNamedQuery(namedQuery);
        query.setParameter("alias", alias);
        query.executeUpdate();
    }

    /**
     * create datasource to read properties from database
     *
     * @return
     */
    private DataSource getDatasource(Properties connectionProp) {
        LOG.info( "Start database properties");
        DataSource datasource = null;
        String url = connectionProp.getProperty("jdbc.url");
        String jndiDatasourceName = connectionProp.getProperty("datasource.jndi");
        jndiDatasourceName = StringUtils.isBlank(jndiDatasourceName) ? "jdbc/smpDatasource" : jndiDatasourceName;

        if (!StringUtils.isBlank(url)) {
            LOG.info( "Connect to {}.", url);
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(connectionProp.getProperty("jdbc.driver"));
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(connectionProp.getProperty("jdbc.user"));
            driverManagerDataSource.setPassword(connectionProp.getProperty("jdbc.password"));
            datasource = driverManagerDataSource;
        } else {
            LOG.info( "Use JNDI {} to connect to database.", jndiDatasourceName);
            JndiObjectFactoryBean dataSource = new JndiObjectFactoryBean();
            dataSource.setJndiName(jndiDatasourceName);
            try {
                dataSource.afterPropertiesSet();
            } catch (IllegalArgumentException | NamingException e) {
                // rethrow
                LOG.error( "Error occurred while retriving datasource whith JNDI {}. Is datasource configured in server!", jndiDatasourceName);
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

}
