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

import eu.europa.ec.edelivery.smp.config.DatabaseProperties;
import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.CONFIGURATION_ERROR;


@Repository(value = "configurationDao")
public class ConfigurationDao extends BaseDao<DBConfiguration> {




    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationDao.class);


    List<PropertyUpdateListener> updateListenerList = new ArrayList<>();

    boolean isRefreshProcess = false;
    Properties cachedProperties = new Properties();

    Map<String, Object> cachedPropertyValues = new HashMap();

    LocalDateTime lastUpdate = null;


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
    public DBConfiguration setPropertyToDatabase(SMPPropertyEnum key, String value, String description) {
        Optional<DBConfiguration> result = getConfigurationEntityFromDatabase(key);
        DBConfiguration configurationEntity;
        if (!result.isPresent()) {
            configurationEntity = new DBConfiguration();
            configurationEntity.setProperty(key.getProperty());
            configurationEntity.setValue(value);
            configurationEntity.setDescription(StringUtils.isBlank(description) ? key.getDesc() : description);
            configurationEntity.setCreatedOn(LocalDateTime.now());
            configurationEntity.setLastUpdatedOn(LocalDateTime.now());
            memEManager.persist(configurationEntity);

        } else {
            configurationEntity = result.get();
            configurationEntity.setValue(value);
            // set default  for null value
            if (description != null) {
                configurationEntity.setDescription(description);
            }
            configurationEntity.setLastUpdatedOn(LocalDateTime.now());
            configurationEntity = memEManager.merge(configurationEntity);
        }
        return configurationEntity;
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

    public String getCachedProperty(SMPPropertyEnum key) {
        if (lastUpdate == null) {
            // init properties
            refreshProperties();
        }
        return cachedProperties.getProperty(key.getProperty(), key.getDefValue());
    }

    public Object getCachedPropertyValue(SMPPropertyEnum key) {
        if (lastUpdate == null) {
            // init properties
            refreshProperties();
        }
        return cachedPropertyValues.get(key.getProperty());
    }

    @Transactional
    public void refreshProperties() {
        // get update
        LocalDateTime lastUpdateFromDB = getLastUpdate();
        if (lastUpdate == null || lastUpdateFromDB == null || lastUpdateFromDB.isAfter(lastUpdate)) {
            reloadPropertiesFromDatabase();
            // check and update non encrypted tokens
            updateCurrentEncryptedValues();

        } else {
            LOG.info("Skip property update because max(LastUpdate) of properties in database is not changed:"
                    + lastUpdateFromDB + ".");
        }
    }

    public void reloadPropertiesFromDatabase() {
        if (!isRefreshProcess) {
            isRefreshProcess = true;
            DatabaseProperties newProperties = new DatabaseProperties(memEManager);
            // first update deprecated values
            updateDeprecatedValues(newProperties);
            Map<String, Object> resultProperties = null;
            try {
                resultProperties = validateConfiguration(newProperties);
            } catch (Throwable ex) {
                ex.printStackTrace();
                LOG.error("Throwable error occurred while refreshing configuration. Configuration was not changed!  Error: {} ", ex.getMessage(), ex);
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
            updateListenerList.forEach(propertyUpdateListener -> propertyUpdateListener.propertiesUpdate());
        } else {
            LOG.warn("Refreshing of database properties is already in process!");
        }
    }

    public void addPropertyUpdateListener(PropertyUpdateListener listener){
        updateListenerList.add(listener);
    }
    public boolean removePropertyUpdateListener(PropertyUpdateListener listener){
        return updateListenerList.remove(listener);
    }

    public LocalDateTime getLastUpdate() {
        TypedQuery<LocalDateTime> query = memEManager.createNamedQuery("DBConfiguration.maxUpdateDate", LocalDateTime.class);
        return query.getSingleResult();
    }

    public Map<String, Object> validateConfiguration(Properties properties) {

        // test if all mandatory properties exists
        List<String> lstMissingProperties = new ArrayList<>();
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            if (prop.isMandatory() && StringUtils.isEmpty(getProperty(properties, prop))) {
                lstMissingProperties.add(prop.getProperty());
            }
        }

        if (!lstMissingProperties.isEmpty()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Missing mandatory properties:  %s",
                    String.join(",", lstMissingProperties)));
        }

        Map<String, Object> propertyValues = parseProperties(properties);

        // property validation
        File encFile = (File) propertyValues.get(ENCRYPTION_FILENAME.getProperty());
        checkFileExist(encFile);

        File keyStore = (File) propertyValues.get(KEYSTORE_FILENAME.getProperty());
        checkFileExist(keyStore);

        // check SML integration data
        Boolean isSMLEnabled = (Boolean) propertyValues.get(SML_ENABLED.getProperty());
        if (isSMLEnabled) {
            // if SML is enabled then following properties are mandatory
            validateIfExists(propertyValues, SML_URL);
            validateIfExists(propertyValues, SML_PHYSICAL_ADDRESS);
            validateIfExists(propertyValues, SML_LOGICAL_ADDRESS);
        }
        return propertyValues;
    }

    public void updateCurrentEncryptedValues() {
        File encryptionKey = (File) cachedPropertyValues.get(ENCRYPTION_FILENAME.getProperty());
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            String value = getProperty(cachedProperties, prop);
            if (prop.isEncrypted() && !StringUtils.isBlank(value) && value.startsWith( SecurityUtils.DECRYPTED_TOKEN_PREFIX)) {
                String valToEncrypt = SecurityUtils.getNonEncryptedValue(value);
                String encVal = encryptString(prop, valToEncrypt, encryptionKey);
                setPropertyToDatabase(prop, encVal, prop.getDesc());
            }
        }
    }


    protected Map<String, Object> parseProperties(Properties properties) {

        // retrieve and validate  configuration dir and encryption filename
        // because they are important for 'parsing and validating' other parameters
        String configurationDir = getProperty(properties, CONFIGURATION_DIR);
        if (StringUtils.isBlank(configurationDir)) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Empty configuration folder. Property '%s' is mandatory", CONFIGURATION_DIR.getProperty()));
        }

        String encryptionKeyFilename = getProperty(properties, ENCRYPTION_FILENAME);
        if (StringUtils.isBlank(encryptionKeyFilename)) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Empty configuration folder. Property '%s' is mandatory", CONFIGURATION_DIR.getProperty()));
        }

        File configFolder = new File(configurationDir);
        if (!configFolder.exists() || !configFolder.isDirectory()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Configuration folder does not exists or is not a folder! Value:  %s",
                    configurationDir));
        }

        File encryptionKeyFile = new File(configurationDir, encryptionKeyFilename);
        if (!encryptionKeyFile.exists() || !encryptionKeyFile.isFile()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Encryption file does not exists or is not a File! Value:  %s",
                    encryptionKeyFile.getAbsolutePath()));
        }

        Map<String, Object> propertyValues = new HashMap();
        // put the first two values
        propertyValues.put(CONFIGURATION_DIR.getProperty(), configFolder);
        propertyValues.put(ENCRYPTION_FILENAME.getProperty(), encryptionKeyFile);

        // parse properties
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            if (prop.equals(CONFIGURATION_DIR) || prop.equals(ENCRYPTION_FILENAME)) {
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

    public void updateDeprecatedValues(Properties properties) {
        // update deprecated properties from 4.1.1
        updateDeprecatedProperty(properties, HTTP_PROXY_HOST, SML_PROXY_HOST);
        updateDeprecatedProperty(properties, HTTP_PROXY_PORT, SML_PROXY_PORT);
        updateDeprecatedProperty(properties, HTTP_PROXY_USER, SML_PROXY_USER);
        updateDeprecatedProperty(properties, HTTP_PROXY_PASSWORD, SML_PROXY_PASSWORD);
    }

    public void updateDeprecatedProperty(Properties properties, SMPPropertyEnum newProperty, SMPPropertyEnum deprecatedProperty) {
        if (!properties.containsKey(newProperty.getProperty()) && properties.containsKey(deprecatedProperty.getProperty())) {
            properties.setProperty(newProperty.getProperty(), properties.getProperty(deprecatedProperty.getProperty()));
        }
    }
}