/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.config.init;


import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Paths;
import java.security.KeyStore;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SMPKeystoreConfBuilderTest {

    @Test
    void testBuild(){
        File outputFolder = Paths.get("target").toFile();

        SecurityUtils.Secret secret = SecurityUtils.generatePrivateSymmetricKey(true);
        SMPConfigurationInitializer initPropertyService = Mockito.mock(SMPConfigurationInitializer.class);

        KeyStore keystore  = SMPKeystoreConfBuilder.create()
                .propertySecurityToken(TRUSTSTORE_PASSWORD)
                .propertyTruststoreDecToken(TRUSTSTORE_PASSWORD_DECRYPTED)
                .propertyType(TRUSTSTORE_TYPE)
                .propertyFilename(TRUSTSTORE_FILENAME)
                .outputFolder(outputFolder)
                .testMode(true)
                .secret(secret)
                .initPropertyService(initPropertyService)
                .build();

        assertNotNull(keystore);
    }
}
