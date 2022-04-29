package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class PropertyUtilsTest {

    private static final Object[] testTypeValues() {
        return new Object[][]{
                {STRING, "this is a string", true, true},
                {INTEGER, "1345", true, true},
                {INTEGER, " 1e345", false, false},
                {BOOLEAN, "true", true, true},
                {BOOLEAN, "false", true, true},
                {BOOLEAN, "fALse", true, true},
                {BOOLEAN, "fale ", false, true},
                {REGEXP, ".*", true, true},
                {REGEXP, ".*(**]", false, false},
                {EMAIL, "test@mail.com", true, true},
                {EMAIL, "test@2222.comsfs", false, false},
                {EMAIL, "test@coms.fs", false, false},
                {FILENAME, "myfilename.txt", true, true},
                {PATH, "./", true, true},
                {PATH, "./notexits", true, true}, // path will be created
        };
    }

    private static final Object[] testParsePropertiesToType() {
        return new Object[][]{

                {EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, "true", Boolean.class},
                {EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, "true", Boolean.class},
                {OUTPUT_CONTEXT_PATH, "true", Boolean.class},
                {PARTC_SCH_REGEXP, ".*", Pattern.class},
                {CS_PARTICIPANTS, "casesensitive-participant-scheme1|casesensitive-participant-scheme2", List.class},
                {CS_DOCUMENTS, "casesensitive-doc-scheme1|casesensitive-doc-scheme2", List.class},
                {SML_ENABLED, "true", Boolean.class},
                {SML_PARTICIPANT_MULTIDOMAIN, "true", Boolean.class},
                {SML_URL, "http://localhost:8080/sml", java.net.URL.class},
                {SML_LOGICAL_ADDRESS, "http://localhost:8080/smp", java.net.URL.class},
                {SML_PHYSICAL_ADDRESS, "0.0.0.0", String.class},
                {HTTP_PROXY_HOST, "local.proxy.local", String.class},
                {HTTP_NO_PROXY_HOSTS, "localhost|127.0.0.1", String.class},
                {HTTP_PROXY_PASSWORD, "ASDFs+dfswWE+=", String.class},
                {HTTP_PROXY_PORT, "80", Integer.class},
                {HTTP_PROXY_USER, "user", String.class},
                {KEYSTORE_PASSWORD, "ASDFs+dfswWE+=", String.class},
                {KEYSTORE_FILENAME, "file.jsk", File.class},
                {TRUSTSTORE_PASSWORD, "ASDFs+dfswWE+=", String.class},
                {TRUSTSTORE_FILENAME, "truststore.jks", File.class},
                {CERTIFICATE_CRL_FORCE, "true", Boolean.class},
                {CONFIGURATION_DIR, "./", File.class},
                {ENCRYPTION_FILENAME, "enc.key", File.class},
                {KEYSTORE_PASSWORD_DECRYPTED, "test", String.class}
        };
    }


    @Test
    @Parameters(method = "testParsePropertiesToType")
    public void testParsePropertiesToType(SMPPropertyEnum property, String value, Class cls) {
        // given
        File folder = new File("./target");
        //when then
        Object obj = PropertyUtils.parseProperty(property, value, folder);
        Assert.assertTrue(cls.isInstance(obj));

    }

    @Test
    @Parameters(method = "testTypeValues")
    public void testIsValidPropertyType(SMPPropertyTypeEnum propertyType, String value, boolean expected,
                                        boolean isNotNull) {
        //when
        boolean result = PropertyUtils.isValidPropertyType(propertyType, value);

        //then
        assertEquals(expected, result);
    }


    @Test
    public void testDefaultValues() {

        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            assertTrue("Invalid: " + prop.getProperty() + " - " + prop.getDesc() + " value: " + prop.getDefValue(), PropertyUtils.isValidProperty(prop, prop.getDefValue()));
        }
    }

    @Test
    public void testParseDefaultValues() {
        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            Object obj = PropertyUtils.parseProperty(prop, prop.getDefValue(), null);
            assertType(prop, obj);
        }
    }


    @Test
    @Parameters(method = "testTypeValues")
    public void testParsePropertyType(SMPPropertyTypeEnum propertyType, String value, boolean isValid,
                                      boolean isNotNull) {
        // given
        File folder = new File("./target");
        //when then
        Object obj = null;
        try {
            obj = PropertyUtils.parsePropertyType(propertyType, value, folder);
            assertTrue(isNotNull);
            assertNotNull(obj);
        } catch (SMPRuntimeException ex) {
            assertFalse(isNotNull);
            assertNull(obj);
        }
    }


    public static void assertType(SMPPropertyEnum prop, Object value) {
        if (value == null) {
            if (prop.isMandatory()) {
                Assert.fail("Default value for property: " + prop.getProperty() + " must not be empty!");
            }
            return;
        }
        switch (prop.getPropertyType()) {
            case BOOLEAN:
                Assert.assertEquals(Boolean.class, value.getClass());
                break;
            case EMAIL:
                Assert.assertEquals(String.class, value.getClass());
                break;
            case REGEXP:
                Assert.assertEquals(Pattern.class, value.getClass());
                break;
            case INTEGER:
                Assert.assertEquals(Integer.class, value.getClass());
                break;
            case LIST_STRING:
                Assert.assertTrue(List.class.isInstance(value));
                break;
            case MAP_STRING:
                Assert.assertTrue(Map.class.isInstance(value));
                break;
            case PATH:
            case FILENAME:
                Assert.assertEquals(File.class, value.getClass());
                break;
            case URL:
                Assert.assertEquals(java.net.URL.class, value.getClass());
                break;
            case STRING:
                Assert.assertEquals(String.class, value.getClass());
                break;
            default:
                fail("Unknown property type");
        }
    }
}