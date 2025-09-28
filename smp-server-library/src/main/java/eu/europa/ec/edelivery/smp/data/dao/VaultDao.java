/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2016 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.SMPClassLoaderProvider;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.vault.api.VaultApi;
import eu.europa.ec.edelivery.vault.api.VaultApiFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;


/**
 * Vault dao implementation class. This class is responsible for initializing the vault service.
 * and provides the methods to interact with the vault service.
 *
 * @author Joze RIHTARSIC
 * @since 5.2
 */
@Service
public class VaultDao implements PropertyUpdateListener {
    private static final Logger LOG = LoggerFactory.getLogger(VaultDao.class);
    private static final String SECRET_MASK = "*******";

    VaultApi vaultService;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        Boolean vaultEnabled = (Boolean) properties.get(VAULT_ENABLED);
        String vaultClassName = (String) properties.get(VAULT_IMPLEMENTATION_CLASSNAME);
        Properties vaultProperties = new Properties();
        if (properties.get(VAULT_CONFIGURATION) instanceof Map<?, ?> map) {
            vaultProperties.putAll(map);
        }
        String authenticationType = (String) properties.get(VAULT_AUTHENTICATION_TYPE);
        String authenticationCredentials = (String) properties.get(VAULT_AUTHENTICATION_VALUE);

        if (vaultEnabled == null || !vaultEnabled) {
            LOG.info("Vault is disabled, cleaning vault instance");
            cleanVault();
            return;
        }
        if (StringUtils.isBlank(vaultClassName)) {
            throw new SMPRuntimeException(ErrorMessageType.CONFIGURATION_VAULT_CLASSNAME_EMPTY);
        }
        if (vaultService != null) {
            LOG.info("Vault is already initialized, cleaning vault instance");
            cleanVault();

        }

        vaultService = initializeVaultInstance(vaultClassName, vaultProperties, authenticationType, authenticationCredentials);
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return List.of(VAULT_ENABLED,
                VAULT_IMPLEMENTATION_CLASSNAME,
                VAULT_CONFIGURATION,
                VAULT_AUTHENTICATION_TYPE,
                VAULT_AUTHENTICATION_VALUE);
    }

    public void cleanVault() {
        if (vaultService == null) {
            return;
        }
        vaultService.destroy();
        vaultService = null;
    }


    public VaultApi getVaultService() {
        return vaultService;
    }

    public byte[] getSecret(String key) {
        return vaultService.getSecret(key).getValue();
    }

    public DBConfiguration storeSecret(String key, byte[] value, String description) {
        vaultService.storeSecret(key, value, description);
        DBConfiguration res = new DBConfiguration();
        res.setProperty(key);
        res.setDescription(description);
        // use secret mask to avoid return real secret to the client
        res.setValue(SECRET_MASK);
        return res;
    }


    protected VaultApi initializeVaultInstance(String vaultName, Properties vaultProperties,
                                               String authenticationType, String authenticationCredentials) {
        LOG.debug("Initialize Vault for class/code [{}]", vaultName);

        if (StringUtils.isBlank(vaultName)) {
            throw new SMPRuntimeException(ErrorMessageType.CONFIGURATION_VAULT_CLASSNAME_EMPTY);
        }
        ClassLoader classLoader = SMPClassLoaderProvider.getClassLoader();
        LOG.info("Using classloader [{}] to load vault implementation class [{}]", classLoader, vaultName);
        VaultApi vaultInstance = VaultApiFactory.getVaultApi(vaultName, classLoader);
        vaultInstance.setCredentials(authenticationType, authenticationCredentials);
        vaultInstance.initVault(vaultProperties);
        LOG.debug("Successfully created Vault instance of type [{}] ", vaultName);
        return vaultInstance;
    }

    /**
     * This method is called when the properties are updated and the vault service is not null.
     * It will try to add missing the vault secrets from current database values.
     */
    public void updateVaultOnMissingProperties(Map<SMPPropertyEnum, byte[]> updateVaultProperties) {
        if (vaultService == null) {
            LOG.warn("Vault service is null, skipping update of missing vault secretes");
            return;
        }
        List<SMPPropertyEnum> vaultProperties = Arrays.stream(SMPPropertyEnum.values())
                .filter(SMPPropertyEnum::isEncrypted)
                .toList();

        for (SMPPropertyEnum property : vaultProperties) {
            // get the property from the database
            if (!updateVaultProperties.containsKey(property)) {
                LOG.debug("Property [{}] is not in the database, skipping", property);
                continue;
            }

            byte[] value = updateVaultProperties.get(property);
            if (value != null && value.length > 0) {
                // if the property is not in the vault, store it
                if (vaultService.getSecret(property.getProperty()) == null) {
                    LOG.debug("Adding missing vault secret for key [{}]", property.getProperty());
                    vaultService.storeSecret(property.getProperty(), value, property.getDesc());
                }
            }
        }
    }
}
