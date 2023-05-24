package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED;
import static org.junit.Assert.*;

public class SMPEnvironmentPropertiesTest {

    @Test
    public void testUpdateDeprecatedValues() {
        String testValue = "test";
        Properties prop = new Properties();
        prop.setProperty(CLIENT_CERT_HEADER_ENABLED_DEPRECATED.getProperty(), testValue);

        Properties result = SMPEnvironmentProperties.updateDeprecatedValues(prop);

        assertTrue(result.containsKey(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
        assertEquals(testValue, result.getProperty(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
    }

    @Test
    public void readPropertiesFromFile() throws IOException {
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
    public void readPropertiesFromClasspath() throws IOException {
        String classpath = "/test-smp.config.properties";
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        // when
        Properties properties = testInstance.readProperties(classpath,true );
        // then
        assertTrue(properties.containsKey("test.read.property"));
        assertEquals("This property is from custom file", properties.getProperty("test.read.property"));
    }

    @Test
    public void getEnvProperties() {
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        Properties properties = testInstance.getEnvProperties();

        assertEquals(SMPEnvPropertyEnum.values().length, properties.size());
    }

    @Test
    public void getEnvPropertiesForNull() {
        SMPEnvironmentProperties testInstance = SMPEnvironmentProperties.getInstance();
        String value = testInstance.getEnvPropertyValue(SMPEnvPropertyEnum.LOG_CONFIGURATION_FILE);
        assertNull(value);

        Properties properties = testInstance.getEnvProperties();
        assertEquals("", properties.getProperty(SMPEnvPropertyEnum.LOG_CONFIGURATION_FILE.getProperty()));
    }


/*
    @Test
    public void getFileProperties() {
        Properties result = SMPEnvironmentProperties.getFileProperties("/test-smp.config.properties");
        assertNotNull(result);
        assertEquals("This property is from custom file",result.getProperty("test.read.property"));
    }

    @Test
    public void getFilePropertiesLegacyFallback() {
        Properties result = SMPEnvironmentProperties.getFileProperties("/prop-not-exists.properties");
        assertNotNull(result);
        // in the legacy fallback file the property is defined as: ${jdbc.user}
        assertEquals("This property is from fallback legacy file",result.getProperty("test.read.property"));
    }


    @Test
    public void updateLogConfigurationSetLogFolderProperty(){
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
