package eu.europa.ec.edelivery.smp.config.init;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.DatabaseProperties;
import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum.SECURITY_FOLDER;
import static eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum.SMP_MODE_DEVELOPMENT;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPConfigurationInitializer implements SMPKeystoreConfBuilder.PropertySerializer {

    public static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPConfigurationInitializer.class);

    private static final String TEST_CERT_ISSUER_DN = "CN=rootCNTest,OU=B4,O=DIGIT,L=Brussels,ST=BE,C=BE";
    private static final String TEST_CERT_SUBJECT_DN = "CN=SMP_TEST-PRE-SET-EXAMPLE, OU=eDelivery, O=DIGITAL, C=BE";
    private static final String TEST_CERT_ISSUER_ALIAS = "issuer";
    private static final String TEST_CERT_CERT_ALIAS = "sample_key";


    final DatabaseProperties properties;
    final EntityManager entityManager;
    final SMPEnvironmentProperties environmentProperties;

    public SMPConfigurationInitializer(EntityManager entityManager, SMPEnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
        this.entityManager = entityManager;
        this.properties = new DatabaseProperties(entityManager);
    }

    /**
     * Get property value. First try with Database properties if not found use environmentProperties lookup.
     *
     * @param property
     * @return
     */
    public String getApplicationInitPropertyValue(final SMPPropertyEnum property) {
        return properties.getProperty(property.getProperty(),
                environmentProperties.getApplicationInitPropertyValue(property));
    }

    public DatabaseProperties getProperties() {
        return properties;
    }

    /**
     * stores the property to the database. If the property value is already set in the properties, the update is skipped!
     *
     * @param property property name
     * @param value    property value
     */
    @Override
    public void storeProperty(final SMPPropertyEnum property, final String value) {
        String internalValue = StringUtils.trimToEmpty(value);

        LOG.debug("Store property [{}], value [{}]", property.getProperty(), PropertyUtils.getMaskedData(property.getProperty(), internalValue));
        if (properties.containsKey(property.getProperty())
                && StringUtils.equals(properties.getProperty(property.getProperty()), internalValue)) {
            LOG.debug("Property [{}] has already the same value! Skip database update!", property.getProperty());
            return;
        }

        properties.setProperty(property.getProperty(), internalValue);
        DBConfiguration cnt = createDBEntry(property.getProperty(), internalValue, property.getDesc());
        entityManager.merge(cnt);
    }

    /**
     * Create database entry for storing the  SMP configuration to the database table.
     *
     * @param key   property name
     * @param value property value
     * @param desc  property description
     * @return DBConfiguration entity
     */
    protected DBConfiguration createDBEntry(String key, String value, String desc) {
        DBConfiguration configuration = new DBConfiguration();
        configuration.setProperty(key);
        configuration.setDescription(desc);
        configuration.setValue(value);
        configuration.setCreatedOn(OffsetDateTime.now());
        configuration.setLastUpdatedOn(configuration.getCreatedOn());
        return configuration;
    }

    public DatabaseProperties getDatabaseProperties() {
        boolean devMode = Boolean.parseBoolean(environmentProperties.getEnvPropertyValue(SMP_MODE_DEVELOPMENT));
        if (devMode) {
            LOG.warn("***********************************************************************");
            LOG.warn("WARNING: The SMP is started in DEVELOPMENT mode!");
            LOG.warn("***********************************************************************");
        }
        // get datasource
        return initializeProperties(devMode);
    }

    /**
     * Initialize DomiSMP database configuration properties. The method validates the properties and add new properties
     * if they are missing.
     *
     * @param devMode
     */
    protected DatabaseProperties initializeProperties(boolean devMode) {
        LOG.warn("Database configuration table is empty! Initialize new values!");
        SecurityUtils.Secret secret = initSecurityValues(devMode);
        // iterate over configuration values and set the properties
        for (SMPPropertyEnum val : SMPPropertyEnum.values()) {
            switch (val) {
                case ENCRYPTION_FILENAME:
                case TRUSTSTORE_FILENAME:
                case TRUSTSTORE_TYPE:
                case TRUSTSTORE_PASSWORD:
                case TRUSTSTORE_PASSWORD_DECRYPTED:
                case KEYSTORE_FILENAME:
                case KEYSTORE_TYPE:
                case KEYSTORE_PASSWORD:
                case KEYSTORE_PASSWORD_DECRYPTED:
                    // skip values because they are already created in initNewValues method
                    break;
                default:
                    // insert only non deprecated values
                    if (val.getDesc() == null || !val.getDesc().trim().equalsIgnoreCase("deprecated")) {
                        String resultValue = getApplicationInitPropertyValue(val);
                        if (val.isEncrypted()) {
                            resultValue = SecurityUtils.encryptWrappedToken(secret, resultValue);
                        }
                        storeProperty(val, resultValue);
                    }
            }
        }
        return properties;
    }

    /**
     * Method generates the encryption key if it does not exist and stores the property to the databse
     *
     * @param securityDir is the folder to store the encryption key
     * @param devMode     set to false for using stronger encryption algorithms. Dev mode can use semi-random algorithmes
     *                    which are faster but less secure.
     * @return the secret
     */
    public SecurityUtils.Secret initEncryptionKey(File securityDir, boolean devMode) {
        File fEncryption = new File(securityDir, getApplicationInitPropertyValue(ENCRYPTION_FILENAME));
        // if file is not existing yet - as is the case in getNewFile create file
        if (!fEncryption.exists()) {
            LOG.info("Start generating encryption key. This can take a while.");
            SecurityUtils.generatePrivateSymmetricKey(fEncryption, devMode);
            LOG.info("Encryption key generated.");
        } else {
            LOG.info("Use existing encryption key! [{}].", fEncryption.getAbsolutePath());
        }
        // try to parse encryption key
        SecurityUtils.Secret secret = SecurityUtils.readSecret(fEncryption);
        storeProperty(ENCRYPTION_FILENAME, fEncryption.getName());
        return secret;
    }

    /**
     * Method initialize new values for configuration dir, encryption filename, keystore password, and keystore filename.
     *
     * @param devMode
     */
    protected SecurityUtils.Secret initSecurityValues(boolean devMode) {
        // set absolute path
        String configPath = environmentProperties.getEnvPropertyValue(SECURITY_FOLDER);
        File confFolder = Paths.get(configPath).toAbsolutePath().toFile();
        LOG.info("Set configuration folder to: [{}] (absolute path: [{}])", configPath, confFolder.getAbsolutePath());
        if (!confFolder.exists()) {
            LOG.warn("Configuration folder [{}] does not exist. Folder will be created!", confFolder.getAbsolutePath());
            confFolder.mkdirs();
        }
        // init encryption filename
        SecurityUtils.Secret secret = initEncryptionKey(confFolder, devMode);

        // init truststore
        SMPKeystoreConfBuilder.create()
                .propertySecurityToken(TRUSTSTORE_PASSWORD)
                .propertyTruststoreDecToken(TRUSTSTORE_PASSWORD_DECRYPTED)
                .propertyType(TRUSTSTORE_TYPE)
                .propertyFilename(TRUSTSTORE_FILENAME)
                .outputFolder(confFolder)
                .testMode(devMode)
                .secret(secret)
                .initPropertyService(this)
                .build();

        // init keystore
        SMPKeystoreConfBuilder.create()
                .propertySecurityToken(KEYSTORE_PASSWORD)
                .propertyTruststoreDecToken(KEYSTORE_PASSWORD_DECRYPTED)
                .propertyType(KEYSTORE_TYPE)
                .propertyFilename(KEYSTORE_FILENAME)
                .outputFolder(confFolder)
                .subjectChain(TEST_CERT_ISSUER_DN, TEST_CERT_SUBJECT_DN)
                .aliasList(TEST_CERT_ISSUER_ALIAS, TEST_CERT_CERT_ALIAS)
                .testMode(devMode)
                .secret(secret)
                .initPropertyService(this)
                .build();

        return secret;
    }
}
