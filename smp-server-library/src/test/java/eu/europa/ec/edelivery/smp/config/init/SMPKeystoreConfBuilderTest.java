package eu.europa.ec.edelivery.smp.config.init;



import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.TRUSTSTORE_FILENAME;
import static org.junit.jupiter.api.Assertions.*;

public class SMPKeystoreConfBuilderTest {

    @Test
    public void testBuild(){
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
