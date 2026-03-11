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
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED;
import static org.junit.jupiter.api.Assertions.*;

class SMPEnvironmentPropertiesTest {

    private static SMPEnvironmentProperties createWithEnv(Map<String, String> envVars) {
        return new SMPEnvironmentProperties(null) {
            @Override
            protected Map<String, String> getEnvironmentVariables() {
                return envVars != null ? envVars : Collections.emptyMap();
            }
        };
    }

    @Test
    void getPropertyValueReturnsExactEnvVarMatch() {
        Map<String, String> env = Map.of("smp.jdbc.url", "jdbc:mysql://exact");
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("jdbc:mysql://exact", instance.getPropertyValue("smp.jdbc.url", null));
    }

    @Test
    void getPropertyValueFallsBackToLowercasePosixEnvVar() {
        Map<String, String> env = Map.of("smp_jdbc_url", "jdbc:mysql://lower");
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("jdbc:mysql://lower", instance.getPropertyValue("smp.jdbc.url", null));
    }

    @Test
    void getPropertyValueFallsBackToUppercasePosixEnvVar() {
        Map<String, String> env = Map.of("SMP_JDBC_URL", "jdbc:mysql://upper");
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("jdbc:mysql://upper", instance.getPropertyValue("smp.jdbc.url", null));
    }

    @Test
    void getPropertyValueExactEnvVarTakesPriorityOverPosix() {
        Map<String, String> env = Map.of(
                "smp.jdbc.url", "jdbc:mysql://exact",
                "SMP_JDBC_URL", "jdbc:mysql://posix"
        );
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("jdbc:mysql://exact", instance.getPropertyValue("smp.jdbc.url", null));
    }

    @Test
    void getPropertyValueLowercasePosixTakesPriorityOverUppercase() {
        Map<String, String> env = new HashMap<>();
        env.put("smp_jdbc_url", "jdbc:mysql://lower");
        env.put("SMP_JDBC_URL", "jdbc:mysql://upper");
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("jdbc:mysql://lower", instance.getPropertyValue("smp.jdbc.url", null));
    }

    @Test
    void getPropertyValueReturnsDefaultWhenNoEnvVarMatches() {
        SMPEnvironmentProperties instance = createWithEnv(Collections.emptyMap());

        assertEquals("fallback", instance.getPropertyValue("smp.nonexistent.prop", "fallback"));
    }

    @Test
    void getPropertyValueReturnsNullDefaultWhenNoMatch() {
        SMPEnvironmentProperties instance = createWithEnv(Collections.emptyMap());

        assertNull(instance.getPropertyValue("smp.nonexistent.prop", null));
    }

    @Test
    void getPropertyValueConvertsDashesToUnderscores() {
        Map<String, String> env = Map.of("SMP_SOME_HYPHENATED_PROPERTY", "hyphen-value");
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals("hyphen-value", instance.getPropertyValue("smp.some-hyphenated.property", null));
    }

    @ParameterizedTest
    @CsvSource({
            "smp.jdbc.url,           SMP_JDBC_URL",
            "smp.jdbc.driver,        SMP_JDBC_DRIVER",
            "smp.jdbc.user,          SMP_JDBC_USER",
            "smp.jdbc.password,      SMP_JDBC_PASSWORD",
            "smp.datasource.jndi,    SMP_DATASOURCE_JNDI",
            "smp.configuration.file, SMP_CONFIGURATION_FILE",
            "smp.security.folder,    SMP_SECURITY_FOLDER",
            "smp.database.hibernate.dialect, SMP_DATABASE_HIBERNATE_DIALECT",
    })
    void getPropertyValueResolvesAllDropinEnvVarNames(String propertyName, String envVarName) {
        String expected = "test-value-" + envVarName;
        Map<String, String> env = Map.of(envVarName, expected);
        SMPEnvironmentProperties instance = createWithEnv(env);

        assertEquals(expected, instance.getPropertyValue(propertyName, null));
    }

    @Test
    void systemPropertyTakesPriorityOverEnvVar() {
        String propKey = "smp.test.priority." + UUID.randomUUID();
        Map<String, String> env = Map.of(propKey, "from-env");
        SMPEnvironmentProperties instance = createWithEnv(env);

        System.setProperty(propKey, "from-system");
        try {
            assertEquals("from-system", instance.getPropertyValue(propKey, null));
        } finally {
            System.clearProperty(propKey);
        }
    }

    @Test
    void testUpdateDeprecatedValues() {
        String testValue = "test";
        Properties prop = new Properties();
        prop.setProperty(CLIENT_CERT_HEADER_ENABLED_DEPRECATED.getProperty(), testValue);

        Properties result = SMPEnvironmentProperties.updateDeprecatedValues(prop);

        assertTrue(result.containsKey(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
        assertEquals(testValue, result.getProperty(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
    }

    @Test
    void readPropertiesFromFile() throws IOException {
        String value = UUID.randomUUID().toString();
        Path propertyPath =Paths.get("target","testReadPropertiesFromFile.properties");
        Files.write(propertyPath,("test="+value).getBytes(), StandardOpenOption.CREATE);
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        // when
        Properties properties = testInstance.readProperties(propertyPath.toFile().getAbsolutePath(),false );
        // then
        assertTrue(properties.containsKey("test"));
        assertEquals(value, properties.getProperty("test"));
    }

    @Test
    void readPropertiesFromClasspath() {
        String classpath = "/test-smp.config.properties";
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        // when
        Properties properties = testInstance.readProperties(classpath,true );
        // then
        assertTrue(properties.containsKey("test.read.property"));
        assertEquals("This property is from custom file", properties.getProperty("test.read.property"));
    }

    @Test
    void getEnvProperties() {
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        Properties properties = testInstance.getEnvProperties();

        assertEquals(SMPEnvPropertyEnum.values().length, properties.size());
    }

    @Test
    void getEnvPropertiesForNull() {
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        String value = testInstance.getEnvPropertyValue(SMPEnvPropertyEnum.LOG_CONFIGURATION_FILE);
        assertNull(value);

        Properties properties = testInstance.getEnvProperties();
        assertEquals("", properties.getProperty(SMPEnvPropertyEnum.LOG_CONFIGURATION_FILE.getProperty()));
    }


/*
    @Test
    void getFileProperties() {
        Properties result = SMPEnvironmentProperties.getFileProperties("/test-smp.config.properties");
        assertNotNull(result);
        assertEquals("This property is from custom file",result.getProperty("test.read.property"));
    }

    @Test
    void getFilePropertiesLegacyFallback() {
        Properties result = SMPEnvironmentProperties.getFileProperties("/prop-not-exists.properties");
        assertNotNull(result);
        // in the legacy fallback file the property is defined as: ${jdbc.user}
        assertEquals("This property is from fallback legacy file",result.getProperty("test.read.property"));
    }


    @Test
    void updateLogConfigurationSetLogFolderProperty(){
        String newFolderVal = "NewVal-"+ UUID.randomUUID().toString();
        String currVal = System.getProperty(PROPERTY_LOG_FOLDER);
        SMPEnvironmentProperties.updateLogConfiguration(newFolderVal, null, null);
        assertEquals(newFolderVal,  System.getProperty(PROPERTY_LOG_FOLDER) );
        assertNotEquals(newFolderVal,  currVal );
        if (currVal ==null) {
            System.getProperties().remove(PROPERTY_LOG_FOLDER);
        } else {
            System.setProperty(PROPERTY_LOG_FOLDER, currVal);
        }
    }

 */
}
