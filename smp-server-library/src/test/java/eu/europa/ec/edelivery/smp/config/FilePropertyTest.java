package eu.europa.ec.edelivery.smp.config;

import org.junit.Test;

import java.util.Properties;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.config.FileProperty.PROPERTY_LOG_FOLDER;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED;
import static org.junit.Assert.*;

public class FilePropertyTest {

    @Test
    public void updateDeprecatedValues() {
        String testValue = "test";
        Properties prop = new Properties();
        prop.setProperty(CLIENT_CERT_HEADER_ENABLED_DEPRECATED.getProperty(), testValue);

        Properties result = FileProperty.updateDeprecatedValues(prop);

        assertTrue(result.containsKey(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
        assertEquals(testValue, result.getProperty(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED.getProperty()));
    }

    @Test
    public void getFileProperties() {
        Properties result = FileProperty.getFileProperties("/test-smp.config.properties");
        assertNotNull(result);
        assertEquals("This property is from custom file",result.getProperty("test.read.property"));
    }

    @Test
    public void getFilePropertiesLegacyFallback() {
        Properties result = FileProperty.getFileProperties("/prop-not-exists.properties");
        assertNotNull(result);
        // in the legacy fallback file the property is defined as: ${jdbc.user}
        assertEquals("This property is from fallback legacy file",result.getProperty("test.read.property"));
    }


    @Test
    public void updateLogConfigurationSetLogFolderProperty(){
        String newFolderVal = "NewVal-"+ UUID.randomUUID().toString();
        String currVal = System.getProperty(PROPERTY_LOG_FOLDER);
        FileProperty.updateLogConfiguration(newFolderVal, null, null);
        assertEquals(newFolderVal,  System.getProperty(PROPERTY_LOG_FOLDER) );
        assertNotEquals(newFolderVal,  currVal );
        if (currVal ==null) {
            System.getProperties().remove(PROPERTY_LOG_FOLDER);
        } else {
            System.setProperty(PROPERTY_LOG_FOLDER, currVal);
        }
    }
}
