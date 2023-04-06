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

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.DatabaseProperties;
import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.config.init.SMPConfigurationInitializer;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.TypedQuery;
import java.io.File;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.CONFIGURATION_ERROR;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Repository(value = "configurationDao")
public class ConfigurationDao extends BaseDao<DBConfiguration> implements InitializingBean {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationDao.class);
    boolean isRefreshProcess = false;
    final Properties cachedProperties = new Properties();
    Map<String, Object> cachedPropertyValues = new HashMap();
    OffsetDateTime lastUpdate = null;
    OffsetDateTime initiateDate = null;


    boolean serverRestartNeeded = false;

    protected final SMPEnvironmentProperties environmentProperties = SMPEnvironmentProperties.getInstance();
    protected final ApplicationContext applicationContext;
    protected PlatformTransactionManager txManager;

    public ConfigurationDao(ApplicationContext applicationContext, PlatformTransactionManager txManager) {
        this.applicationContext = applicationContext;
        this.txManager = txManager;
    }

    /**
     * Validate and initialize database configurations
     */
    public void afterPropertiesSet() {
        LOG.debug("Reload DomiSMP properties");
        // Transaction might not be yet initialized (@Transactional on this method does not help :) ).
        // Wrap the method to TransactionTemplate to make possible database property initialization
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(status -> {
            LOG.info("Start initial (re)load of the DomiSMP properties with transaction status object [{}]",  status);
            reloadPropertiesFromDatabasePrivate();
            return null;
        });
    }

    /**
     * Searches for a configuration entity by its  key and returns it if found. Returns an empty {@code Optional} if missing.
     *
     * @param property The property key of the user entity to find
     * @return an optional user entity
     */

    public Optional<DBConfiguration> getConfigurationEntityFromDatabase(SMPPropertyEnum property) {
        return findConfigurationProperty(property.getProperty());
    }

    public Optional<DBConfiguration> findConfigurationProperty(String key) {
        DBConfiguration dbConfiguration = memEManager.find(DBConfiguration.class, key);
        return Optional.ofNullable(dbConfiguration);
    }

    @Transactional
    public DBConfiguration setPropertyToDatabase(String key, String value) {
        Optional<SMPPropertyEnum> optionalSMPPropertyEnum = SMPPropertyEnum.getByProperty(key);
        if (!optionalSMPPropertyEnum.isPresent()) {
            LOG.warn("Property: [{}] is not SMP property and it is ignored!", key);
            return null;
        }
        return setPropertyToDatabase(optionalSMPPropertyEnum.get(), value, null);
    }

    @Transactional
    public DBConfiguration setPropertyToDatabase(SMPPropertyEnum key, String value, String description) {
        File rootFolder = getSecurityFolder();
        if (!PropertyUtils.isValidProperty(key, value, rootFolder)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, key.getPropertyType().getErrorMessage(key.getProperty()));
        }

        Optional<DBConfiguration> result = getConfigurationEntityFromDatabase(key);
        DBConfiguration configurationEntity;
        if (!result.isPresent()) {
            configurationEntity = new DBConfiguration();
            configurationEntity.setProperty(key.getProperty());
            configurationEntity.setValue(prepareValue(key, value));
            configurationEntity.setDescription(StringUtils.isBlank(description) ? key.getDesc() : description);
            memEManager.persist(configurationEntity);

        } else {
            configurationEntity = result.get();
            configurationEntity.setValue(prepareValue(key, value));
            // set default  for null value
            if (description != null) {
                configurationEntity.setDescription(description);
            }
            configurationEntity = memEManager.merge(configurationEntity);
        }

        if (key.isRestartNeeded()) {
            // set flag that server restart is needed
            LOG.warn("Property [{}] changed and server restart is needed!", key.getProperty());
            serverRestartNeeded = true;
        }
        return configurationEntity;
    }

    private String prepareValue(SMPPropertyEnum prop, String value) {

        if (Objects.equals(prop.getPropertyType(), SMPPropertyTypeEnum.BOOLEAN)) {
            return value.toLowerCase();
        }

        if (prop.isEncrypted() && !StringUtils.isBlank(value)) {
            return encryptString(prop, value);
        }
        return value;
    }

    @Transactional
    public Optional<DBConfiguration> deletePropertyFromDatabase(SMPPropertyEnum key) {
        Optional<DBConfiguration> result = getConfigurationEntityFromDatabase(key);
        if (!result.isPresent()) {
            return Optional.empty();
        }
        memEManager.remove(result.get());
        return result;
    }

    @Transactional
    public String getCachedProperty(SMPPropertyEnum key) {
        return getCachedProperty(key.getProperty(), key.getDefValue());
    }

    @Transactional
    public String getCachedProperty(String property, String defValue) {
        if (lastUpdate == null) {
            // init properties
            refreshProperties();
        }
        return cachedProperties.getProperty(property, defValue);
    }

    @Transactional
    public <T extends Object> T getCachedPropertyValue(SMPPropertyEnum key) {
        if (lastUpdate == null) {
            // init properties
            refreshProperties();
        }
        return (T) cachedPropertyValues.get(key.getProperty());
    }

    @Transactional
    public void refreshAndUpdateProperties() {
        // get update
        OffsetDateTime lastUpdateFromDB = getLastUpdate();
        if (lastUpdate == null || lastUpdateFromDB == null || lastUpdateFromDB.isAfter(lastUpdate)) {
            reloadPropertiesFromDatabase();
            // check and update non encrypted tokens
            updateCurrentEncryptedValues();
        } else {
            LOG.info("Skip property update because max(LastUpdate) of properties in database is not changed: [{}].", lastUpdateFromDB);
        }
    }

    @Transactional
    public void refreshProperties() {
        // get update
        OffsetDateTime lastUpdateFromDB = getLastUpdate();
        if (lastUpdate == null || lastUpdateFromDB == null || lastUpdateFromDB.isAfter(lastUpdate)) {
            reloadPropertiesFromDatabasePrivate();
        } else {
            LOG.info("Skip property update because max(LastUpdate) of properties in database is not changed: [{}].", lastUpdateFromDB);
        }
    }

    @Transactional
    public void reloadPropertiesFromDatabase() {
        reloadPropertiesFromDatabasePrivate();
    }

    protected void reloadPropertiesFromDatabasePrivate() {
        if (!isRefreshProcess) {
            isRefreshProcess = true;
            DatabaseProperties newProperties = new DatabaseProperties(memEManager);
            if (newProperties.isEmpty()) {
                LOG.info("Database property table is empty! INITIALIZE DATABASE PROPERTIES");
                SMPConfigurationInitializer configurationInitializer = new SMPConfigurationInitializer(memEManager, environmentProperties);
                newProperties = configurationInitializer.getDatabaseProperties();
                newProperties.setLastUpdate(OffsetDateTime.now());
            }            // first update deprecated values

            Map<String, Object> resultProperties;
            try {
                resultProperties = validateConfiguration(newProperties);
            } catch (SMPRuntimeException ex) {
                LOG.error("Throwable error occurred while refreshing configuration. Configuration was not changed!  Error: [{}]", ExceptionUtils.getRootCauseMessage(ex));
                isRefreshProcess = false;
                return;
            }
            try {
                synchronized (cachedProperties) {
                    cachedProperties.clear();
                    cachedPropertyValues.clear();
                    cachedProperties.putAll(newProperties);
                    cachedPropertyValues.putAll(resultProperties);
                    // setup last update
                    lastUpdate = newProperties.getLastUpdate();
                }
            } finally {
                isRefreshProcess = false;
            }
            // update all listeners
            updatePropertyListeners();
        } else {
            LOG.warn("Refreshing of database properties is already in process!");
        }
    }

    /**
     * Application event when an {@code ApplicationContext} gets initialized or start
     */
    @EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        LOG.info("Application context is initialized: triggered  refresh  to update all property listeners");
        setInitializedTime(OffsetDateTime.now());
    }

    /**
     * Application event when an application stops {@code ApplicationContext}
     */
    @EventListener({ContextStoppedEvent.class})
    protected void contextStopEvent() {
        LOG.debug("Application context is stopped!");
        setInitializedTime(null);
    }

    protected void setInitializedTime(OffsetDateTime dateTime) {
        initiateDate = dateTime;
    }


    public OffsetDateTime getInitiateDate() {
        return initiateDate;
    }

    public boolean isApplicationInitialized() {
        return initiateDate != null;
    }

    private void updatePropertyListeners() {
        // wait to get all property listener beans to avoid cyclic initialization
        // some beans are using ConfigurationService also are in PropertyUpdateListener
        // for listener to update properties
        if (!isApplicationInitialized()) {
            LOG.debug("Application is not yet started. Skip Property Update Event for listeners!");
            return;
        }
        LOG.debug("Update all property listeners");
        Map<String, PropertyUpdateListener> updateListenerList = getPropertyUpdateListener();
        if (updateListenerList != null) {
            for (Map.Entry<String, PropertyUpdateListener> entry : updateListenerList.entrySet()) {
                String key = entry.getKey();
                PropertyUpdateListener value = entry.getValue();
                updateListener(key, value);
            }
        }
    }

    /**
     * To avoid circular dependencies (some update PropertyUpdateListener can use objects with ConfigurationService )
     */
    public Map<String, PropertyUpdateListener> getPropertyUpdateListener() {
        return applicationContext.getBeansOfType(PropertyUpdateListener.class);
    }

    protected void updateListener(String name, PropertyUpdateListener listener) {
        LOG.debug("updateListener [{}]", name);
        EnumMap<SMPPropertyEnum, Object> mapProp = new EnumMap<>(SMPPropertyEnum.class);
        for (SMPPropertyEnum prop : listener.handledProperties()) {
            LOG.debug("Put property [{}]", prop.getProperty());
            if (this.cachedPropertyValues == null) {
                LOG.error("cachedPropertyValues is FOR SOME REASON NULL ");
            }
            if (this.cachedPropertyValues != null && this.cachedPropertyValues.containsKey(prop.getProperty())) {
                LOG.debug("Put property [{}] value [{}]", prop.getProperty(),
                        this.cachedPropertyValues.get(prop.getProperty()));
                mapProp.put(prop, this.cachedPropertyValues.get(prop.getProperty()));
            } else {
                LOG.debug("Property [{}] does not exist in cached map!", prop.getProperty());
            }
        }
        listener.updateProperties(mapProp);
    }

    public OffsetDateTime getLastUpdate() {
        TypedQuery<OffsetDateTime> query = memEManager.createNamedQuery("DBConfiguration.maxUpdateDate", OffsetDateTime.class);
        return query.getSingleResult();
    }

    /**
     * Method returns all properties which were changed in database and not yet updated in application by the cron job
     *
     * @return List of changed properties.
     */
    public List<DBConfiguration> getPendingUpdateProperties() {
        if (lastUpdate == null) {
            LOG.debug("The properties were not yet loaded. Skip pending properties");
            return Collections.emptyList();
        }
        TypedQuery<DBConfiguration> query = memEManager.createNamedQuery("DBConfiguration.getPendingProperties",
                DBConfiguration.class);
        query.setParameter("updateDate", lastUpdate);
        return query.getResultList();
    }

    public Map<String, Object> validateConfiguration(Properties properties) {
        LOG.debug("Validate configuration properties.");
        // test if all mandatory properties exists
        List<String> lstMissingProperties = new ArrayList<>();
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            if (prop.isMandatory() && StringUtils.isEmpty(getProperty(properties, prop))) {
                lstMissingProperties.add(prop.getProperty());
            }
        }

        if (!lstMissingProperties.isEmpty()) {
            LOG.error("Missing mandatory properties: [{}]. Fix the SMP configuration!", lstMissingProperties);
        }
        // update deprecated values
        properties = updateDeprecatedValues(properties);
        Map<String, Object> propertyValues = parseProperties(properties);

        // property validation
        File encFile = (File) propertyValues.get(ENCRYPTION_FILENAME.getProperty());
        checkFileExist(encFile);

        File keyStore = (File) propertyValues.get(KEYSTORE_FILENAME.getProperty());
        checkFileExist(keyStore);

        // check SML integration data
        Boolean isSMLEnabled = (Boolean) propertyValues.get(SML_ENABLED.getProperty());
        if (isSMLEnabled != null && isSMLEnabled.booleanValue()) {
            // if SML is enabled then following properties are mandatory
            validateIfExists(propertyValues, SML_URL);
            validateIfExists(propertyValues, SML_PHYSICAL_ADDRESS);
            validateIfExists(propertyValues, SML_LOGICAL_ADDRESS);
        }
        return propertyValues;
    }

    @Transactional
    public void updateCurrentEncryptedValues() {
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            String value = getProperty(cachedProperties, prop);
            if (prop.isEncrypted() && !StringUtils.isBlank(value) && value.startsWith(SecurityUtils.DECRYPTED_TOKEN_PREFIX)) {
                String valToEncrypt = SecurityUtils.getNonEncryptedValue(value);
                setPropertyToDatabase(prop, valToEncrypt, prop.getDesc());
            }
        }
    }


    protected void validateBasicProperties(Properties properties) {
        LOG.debug("Validated the basic configuration properties.");
        // retrieve and validate  configuration dir and encryption filename
        // because they are important for 'parsing and validating' other parameters
        String encryptionKeyFilename = getProperty(properties, ENCRYPTION_FILENAME);
        if (StringUtils.isBlank(encryptionKeyFilename)) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Empty configuration folder. Property [%s] is mandatory", ENCRYPTION_FILENAME.getProperty()));
        }

        File configFolder = getSecurityFolder();
        if (!configFolder.exists()) {
            LOG.error("Configuration folder [{}] (absolute path: [{}]) does not exist. Try to create folder", configFolder.getPath(), configFolder.getAbsolutePath());
            if (!configFolder.mkdirs()) {
                throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Configuration folder does not exists and can not be created! Value: [%s] (Absolute path [%s])",
                        configFolder.getPath(), configFolder.getAbsolutePath()));
            }
        }
        if (!configFolder.isDirectory()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Configuration folder is not a folder! Value: [%s] (Absolute path [%s])",
                    configFolder.getPath(), configFolder.getAbsolutePath()));
        }

        File encryptionKeyFile = new File(configFolder, encryptionKeyFilename);
        if (!encryptionKeyFile.exists() || !encryptionKeyFile.isFile()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Encryption file does not exists or is not a File! Value:  [%s]",
                    encryptionKeyFile.getAbsolutePath()));
        }
    }

    /**
     * Method validates if new value for deprecated value is already set. If not it set the value from deprecated property if exists!
     *
     * @param properties
     * @return
     */
    public Properties updateDeprecatedValues(Properties properties) {
        if (!properties.containsKey(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty())
                && properties.containsKey(CLIENT_CERT_HEADER_ENABLED_DEPRECATED.getProperty())) {

            properties.setProperty(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty(),
                    properties.getProperty(CLIENT_CERT_HEADER_ENABLED_DEPRECATED.getProperty()));
        }

        return properties;
    }


    protected Map<String, Object> parseProperties(Properties properties) {
        LOG.debug("Parse configuration properties.");
        // retrieve and validate  configuration dir and encryption filename
        // because they are important for 'parsing and validating' other parameters
        validateBasicProperties(properties);
        String encryptionKeyFilename = getProperty(properties, ENCRYPTION_FILENAME);

        File configFolder = getSecurityFolder();
        File encryptionKeyFile = new File(configFolder, encryptionKeyFilename);

        HashMap<String, Object> propertyValues = new HashMap();
        // put the first two values

        propertyValues.put(ENCRYPTION_FILENAME.getProperty(), encryptionKeyFile);

        // parse properties
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            if (prop.equals(ENCRYPTION_FILENAME)) {
                // already checked and added to property values.
                continue;
            }
            String value = properties.getProperty(prop.getProperty(), prop.getDefValue());
            Object parsedProperty;
            if (prop.isEncrypted()) {
                // try to decrypt it.
                if (StringUtils.isBlank(value)) {
                    parsedProperty = null;
                } else if (value.startsWith(SecurityUtils.DECRYPTED_TOKEN_PREFIX)) {
                    parsedProperty = SecurityUtils.getNonEncryptedValue(value);
                } else {
                    parsedProperty = decryptString(prop, value, encryptionKeyFile);
                }
            } else {
                parsedProperty = PropertyUtils.parseProperty(prop, value, configFolder);
            }
            propertyValues.put(prop.getProperty(), parsedProperty);
        }
        LOG.debug("Parsed [{}] configuration properties.", propertyValues.size());
        return propertyValues;
    }

    private static void validateIfExists(Map<String, Object> propertyValues, SMPPropertyEnum key) {
        Object value = propertyValues.get(key.getProperty());
        if (value == null) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Missing property %s.", key.getProperty()));
        }
    }


    private static File checkFileExist(File file) {
        if (file == null || !file.exists()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("The file [%s] not exists.", file == null ? "null" : file.getAbsolutePath()));
        } else {
            if (!file.isFile()) {
                throw new SMPRuntimeException(CONFIGURATION_ERROR, file.getAbsolutePath() + " must be a file");
            }
        }
        return file;
    }


    private static String getProperty(Properties properties, SMPPropertyEnum key) {
        return StringUtils.trimToNull(properties.getProperty(key.getProperty()));
    }

    protected String decryptString(SMPPropertyEnum key, String value, File encryptionKey) {
        try {
            return SecurityUtils.decrypt(encryptionKey, value);
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Error occurred while decrypting the property: "
                    + key.getProperty() + "Error:" + ExceptionUtils.getRootCause(exc));
        }
    }

    public String encryptString(SMPPropertyEnum key, String value, File encryptionKey) {
        try {
            return SecurityUtils.encrypt(encryptionKey, value);
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Error occurred while encrypting the property: "
                    + key.getProperty() + "Error:" + ExceptionUtils.getRootCause(exc));
        }
    }

    public String encryptString(SMPPropertyEnum key, String value) {
        File encryptionKey = (File) cachedPropertyValues.get(ENCRYPTION_FILENAME.getProperty());
        return encryptString(key, value, encryptionKey);
    }

    public List<DBConfiguration> getPendingRestartProperties() {

        if (initiateDate == null) {
            LOG.warn("No pending restart properties because application is not yet initialized!");
            return Collections.emptyList();
        }
        TypedQuery<DBConfiguration> query = memEManager.createNamedQuery("DBConfiguration.getPendingRestartProperties",
                DBConfiguration.class);
        query.setParameter("serverStartedDate", initiateDate);
        query.setParameter("restartPropertyList", SMPPropertyEnum.getRestartOnChangeProperties().stream().map(SMPPropertyEnum::getProperty).collect(Collectors.toList()));

        return query.getResultList();
    }

    public boolean isServerRestartNeeded() {
        List<DBConfiguration> restartPendingProperties = getPendingRestartProperties();
        boolean restartNeeded = !restartPendingProperties.isEmpty();
        if (restartNeeded) {
            LOG.warn("Server restart is needed! Pending properties [{}]",
                    restartPendingProperties.stream().map(DBConfiguration::getProperty).collect(Collectors.toList()));
        }
        return restartNeeded;
    }

    public File getSecurityFolder() {
        return Paths.get(environmentProperties.getEnvPropertyValue(SMPEnvPropertyEnum.SECURITY_FOLDER)).toFile();
    }
}
