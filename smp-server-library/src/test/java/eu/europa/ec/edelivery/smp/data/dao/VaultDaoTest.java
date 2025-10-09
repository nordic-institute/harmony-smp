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


import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Vault  Business implementation class tests.
 *
 * @author Joze RIHTARSIC
 * @since 5.0
 */
public class VaultDaoTest {

    protected Path targetDirectory = Paths.get("target");
    VaultDao testInstance = Mockito.spy(new VaultDao());

    @BeforeEach
    void setUp() {

        Base64.Encoder encoder = Base64.getEncoder();
        // configure properties for vault
        Properties config = new Properties();
        config.setProperty("demo-vault.storage.type", "FILE");
        config.setProperty("demo-vault.file.name", "vault-secrets-" + UUID.randomUUID() + ".properties");
        config.setProperty("demo-vault.file.directory", targetDirectory.toFile().getAbsolutePath());

        config.setProperty("demo-vault.init.prefix." + KEYSTORE_PASSWORD.getProperty(), encoder.encodeToString("KEYSTORE_PASSWORD-secret".getBytes()));
        config.setProperty("demo-vault.init.prefix." + TRUSTSTORE_PASSWORD.getProperty(), encoder.encodeToString("TRUSTSTORE_PASSWORD-secret".getBytes()));
        config.setProperty("demo-vault.init.prefix." + MAIL_SERVER_PASSWORD.getProperty(), encoder.encodeToString("MAIL_SERVER_PASSWORD-secret".getBytes()));
        config.setProperty("demo-vault.init.prefix." + HTTP_PROXY_PASSWORD.getProperty(), encoder.encodeToString("HTTP_PROXY_PASSWORD-secret".getBytes()));

        //
        Map<SMPPropertyEnum, Object> properties = Map.of(
                VAULT_ENABLED, Boolean.TRUE,
                VAULT_IMPLEMENTATION_CLASSNAME, "eu.europa.ec.edelivery.vault.demo.DemoVault",
                VAULT_CONFIGURATION, config
        );
        // configure vault
        testInstance.updateProperties(properties);

    }

    @ParameterizedTest
    @CsvSource({
            "KEYSTORE_PASSWORD",
            "TRUSTSTORE_PASSWORD",
            "MAIL_SERVER_PASSWORD",
            "HTTP_PROXY_PASSWORD",
    })
    void testGetSecretFromVault(SMPPropertyEnum key) {
        // given // when
        String result = testInstance.getSecretAsString(key.getProperty());

        // then
        // see the secrets loaded to demo-vault in setUp() method
        assertEquals(key.name() + "-secret", result);
    }

    @ParameterizedTest
    @CsvSource({
            "KEYSTORE_PASSWORD",
            "TRUSTSTORE_PASSWORD",
            "MAIL_SERVER_PASSWORD",
            "HTTP_PROXY_PASSWORD",
    })
    void testSetPropertyToVault(SMPPropertyEnum property) {
        // given
        String value = "ThisIsMySecret-" + UUID.randomUUID();
        // when
        DBConfiguration result = testInstance.storeSecret(property.getProperty(), value, property + " description");

        //then
        assertNotNull(result);
        assertEquals(property.getProperty(), result.getProperty());
        assertEquals("*******", result.getValue());
        String resultFromVault = testInstance.getSecretAsString(property.getProperty());
        assertEquals(value, resultFromVault);
    }
}
