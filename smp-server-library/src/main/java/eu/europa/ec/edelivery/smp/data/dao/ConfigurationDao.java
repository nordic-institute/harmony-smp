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
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.CONFIGURATION_ERROR;


@Repository
public class ConfigurationDao extends BaseDao<DBConfiguration> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationDao.class);


    boolean isRefreshProcess = false;
    Properties applicationProperties = new Properties();

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

    public Optional<DBConfiguration> deletePropertyFromDatabase(SMPPropertyEnum key) {
        Optional<DBConfiguration> result = getConfigurationEntityFromDatabase(key);
        if (!result.isPresent()) {
            return Optional.empty();
        }
        memEManager.remove(result.get());
        return result;
    }

    @Transactional
    public String getProperty(SMPPropertyEnum key) {
        if (lastUpdate == null) {
            refreshProperties();
        }
        return applicationProperties.getProperty(key.getProperty(), key.getDefValue());
    }

    @Transactional
    public void refreshProperties() {
        // get update
        LocalDateTime lastUpdateFromDB = getLastUpdate();
        if (lastUpdate == null || lastUpdateFromDB == null || lastUpdateFromDB.isAfter(lastUpdate)) {
            reloadPropertiesFromDatabase();
        } else {
            LOG.info("Skip property update because max(LastUpdate) of properties in database is not changed:"
                    + lastUpdateFromDB + ".");
        }

    }

    public void reloadPropertiesFromDatabase() {
        if (!isRefreshProcess) {
            isRefreshProcess = true;
            DatabaseProperties newProperties = new DatabaseProperties(memEManager);
            try {
                validateConfiguration(newProperties);
            } catch (Throwable ex) {
                LOG.error("Throwable error occurred while refreshing configuration. Configuration was not changed!  Error: {} ", ex.getMessage());
                isRefreshProcess = false;
                return;
            }

            try {
                synchronized (applicationProperties) {
                    applicationProperties.clear();
                    applicationProperties.putAll(newProperties);
                    // setup last update
                    lastUpdate = newProperties.getLastUpdate();
                }
            } finally {
                isRefreshProcess = false;
            }

        } else {
            LOG.warn("Refreshing of database properties is already in process!");
        }
    }


    public LocalDateTime getLastUpdate() {
        TypedQuery<LocalDateTime> query = memEManager.createNamedQuery("DBConfiguration.maxUpdateDate", LocalDateTime.class);
        return query.getSingleResult();
    }

    public static void validateConfiguration(Properties props) {

            // test if all mandatory properties exists

            List<String> lstMissingProperties = new ArrayList<>();
            for (SMPPropertyEnum  prop: SMPPropertyEnum.values()) {
                if (prop.isMandatory() && StringUtils.isEmpty(getProperty(props, prop)) ) {
                    lstMissingProperties.add(prop.getProperty());
                }
            }
            if (!lstMissingProperties.isEmpty()) {
                throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Missing mandatory properties:  %s.",
                        String.join(",", lstMissingProperties)));
            }


            checkFileExist(getProperty(props, ENCRYPTION_FILENAME), props);
            checkFileExist(getProperty(props, KEYSTORE_FILENAME), props);

            if (Boolean.parseBoolean(getProperty(props, SML_ENABLED))) {
                checkIfExists(props, SML_URL);

                checkIfExists(props, SML_PHYSICAL_ADDRESS);
                checkIfExists(props, SML_LOGICAL_ADDRESS);
            }


    }

    private static void checkIfExists(Properties properties, SMPPropertyEnum key) {
        if (StringUtils.isEmpty(getProperty(properties, key))) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Missing property %s.", key.getProperty()));
        }
    }


    private static void checkIsInteger(Properties properties, SMPPropertyEnum key) {
        String value = properties.getProperty(key.getProperty());
        if (StringUtils.isBlank(value)) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Missing integer property %s.", key.getProperty()));
        }

        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("Invalid integer property %s, value: '%s'.", key.getProperty(), value));
        }
    }

    private static void checkFileExist(String file, Properties props) {
        String configurationDir = getProperty(props, CONFIGURATION_DIR);
        if (!new File(configurationDir, file).exists()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, String.format("The directory [%s] or file [%s] desn't exist.", configurationDir, file));
        } else {
            if (!new File(configurationDir, file).isFile()) {
                throw new SMPRuntimeException(CONFIGURATION_ERROR, file + " must be a file");
            }
        }
    }


    private static String getProperty(Properties properties, SMPPropertyEnum key) {
        return StringUtils.trimToNull(properties.getProperty(key.getProperty()));
    }

}