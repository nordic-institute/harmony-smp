package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.support.CronExpression;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class PropertyUtilsTest {


    private static final List<SMPPropertyEnum> SENSITIVE_PROPERTIES = Arrays.asList(
            HTTP_PROXY_PASSWORD,
            KEYSTORE_PASSWORD,
            TRUSTSTORE_PASSWORD,
            KEYSTORE_PASSWORD_DECRYPTED,
            TRUSTSTORE_PASSWORD_DECRYPTED,
            MAIL_SERVER_PASSWORD);
    private static final File ROOT_FOLDER = Paths.get("target").toFile();


    private static final Object[] testTypeValues() {
        return new Object[][]{
                {STRING, "this is a string", true},
                {INTEGER, "1345", true},
                {INTEGER, " 1e345", false},
                {BOOLEAN, "true", true},
                {BOOLEAN, "false", true},
                {BOOLEAN, "fALse", true},
                {BOOLEAN, "fale ", false},
                {REGEXP, ".*", true},
                {REGEXP, ".*(**]", false},
                {EMAIL, "test@mail.com", true},
                {EMAIL, "test@2222.comsfs", false},
                {EMAIL, "test@coms.fs", false},
                {FILENAME, "myfilename.txt", true},
                {PATH, "./", true},
                {PATH, "./notexits", true}, // path will be created
        };
    }

    private static final Object[] testParsePropertiesToType() {
        return new Object[][]{

                {EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, "true", Boolean.class},
                {EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, "true", Boolean.class},
                {OUTPUT_CONTEXT_PATH, "true", Boolean.class},
                {PARTC_SCH_VALIDATION_REGEXP, ".*", Pattern.class},
                {CS_PARTICIPANTS, "casesensitive-participant-scheme1|casesensitive-participant-scheme2", List.class},
                {CS_DOCUMENTS, "casesensitive-doc-scheme1|casesensitive-doc-scheme2", List.class},
                {SML_ENABLED, "true", Boolean.class},
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
                {ENCRYPTION_FILENAME, "enc.key", File.class},
                {KEYSTORE_PASSWORD_DECRYPTED, "test", String.class}
        };
    }


    @Test
    @Parameters(method = "testParsePropertiesToType")
    public void testParsePropertiesToType(SMPPropertyEnum property, String value, Class cls) {
        //when then
        Object obj = PropertyUtils.parseProperty(property, value, ROOT_FOLDER);
        Assert.assertTrue(cls.isInstance(obj));

    }

    @Test
    @Parameters(method = "testTypeValues")
    public void testIsValidPropertyType(SMPPropertyTypeEnum propertyType, String value, boolean expected) {
        //when
        boolean result = PropertyUtils.isValidPropertyType(propertyType, value, ROOT_FOLDER);

        //then
        assertEquals(expected, result);
    }


    @Test
    public void testDefaultValues() {

        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            assertTrue( PropertyUtils.isValidProperty(prop, prop.getDefValue(), ROOT_FOLDER));
        }
    }

    @Test
    public void testParseDefaultValues() {

        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            Object obj = PropertyUtils.parseProperty(prop, prop.getDefValue(), ROOT_FOLDER);
            assertType(prop, obj);
        }
    }

    @Test
    public void testSubjectRegExpLength() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () ->
            PropertyUtils.isValidProperty(ALERT_USER_LOGIN_FAILURE_MAIL_SUBJECT,
                    "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789", ROOT_FOLDER));

        assertEquals("Configuration error: [Subject must have less than 256 character]!", result.getMessage());
    }


    @Test
    public void testSubjectRegExpValue() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () ->
                PropertyUtils.isValidProperty(ALERT_USER_SUSPENDED_LEVEL,
                        "value", ROOT_FOLDER));

        assertEquals("Configuration error: [Allowed values are: LOW, MEDIUM, HIGH]!", result.getMessage());
    }


    @Test
    public void testParseMapPropertiesToType() {
        //when then
        String value= "test1:val1|test2:val2|test3:val:with:colon|test4: val-no-spaces";

        Object obj = PropertyUtils.parseProperty(SML_CUSTOM_NAPTR_SERVICE_PARAMS, value, ROOT_FOLDER);
        assertEquals(HashMap.class, obj.getClass());
        Map<String, String> maRes= (Map<String, String>)obj;

        assertTrue(maRes.containsKey("test1"));
        assertEquals("val1", maRes.get("test1"));
        assertTrue(maRes.containsKey("test2"));
        assertEquals("val2", maRes.get("test2"));
        assertTrue(maRes.containsKey("test3"));
        assertEquals("val:with:colon", maRes.get("test3"));
        assertTrue(maRes.containsKey("test4"));
        assertEquals("val-no-spaces", maRes.get("test4"));
    }


    @Test
    @Parameters(method = "testTypeValues")
    public void testParsePropertyType(SMPPropertyTypeEnum propertyType, String value, boolean expectParseOk) {
        if (expectParseOk) {
            Object result = PropertyUtils.parsePropertyType(propertyType, value, ROOT_FOLDER);
            assertNotNull(result);
            assertType(propertyType, result);
        }
        else {
            SMPRuntimeException exception = assertThrows(SMPRuntimeException.class, ()->PropertyUtils.parsePropertyType(propertyType, value, ROOT_FOLDER));
            assertNotNull(exception.getErrorCode());
        }
    }
    public static void assertType(SMPPropertyTypeEnum prop, Object value) {
        switch (prop) {
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
            case CRON_EXPRESSION:
                Assert.assertEquals(CronExpression.class, value.getClass());
                break;
            default:
                fail("Unknown property type");
        }
    }

    public static void assertType(SMPPropertyEnum prop, Object value) {
        if (value == null) {
            if (prop.isMandatory()) {
                Assert.fail("Default value for property: " + prop.getProperty() + " must not be empty!");
            }
            return;
        }
        assertType(prop.getPropertyType(), value);
    }

    @Test
    public void testIsSensitiveData() {
        for (SMPPropertyEnum smpPropertyEnum: SMPPropertyEnum.values()){
            Assert.assertEquals(SENSITIVE_PROPERTIES.contains(smpPropertyEnum), PropertyUtils.isSensitiveData(smpPropertyEnum.getProperty()));
        }
    }

    @Test
    public void getMaskedData() {
        String testValue = "TestValue";
        for (SMPPropertyEnum smpPropertyEnum: SMPPropertyEnum.values()){
            String expectedValue = SENSITIVE_PROPERTIES.contains(smpPropertyEnum)?"*******":testValue;
            Assert.assertEquals(expectedValue, PropertyUtils.getMaskedData(smpPropertyEnum.getProperty(),testValue));
        }
    }
/*
    @Test
    public void matchAllValues(){
        System.out.println("Contains in values");

        List<String> enumList =  Arrays.stream(SMPPropertyEnum.values()).map(val-> val.getProperty()).collect(Collectors.toList());
        List<String> docList = Arrays.asList(docValues);

        System.out.println("Missing in documentation");
        for (String enumVal: enumList) {
            if (!docList.contains(enumVal)) {
                System.out.println("Missing: " + enumVal);
            }
        }

        for (String docVal: docList) {
            if (!enumList.contains(docVal)) {
                System.out.println("Not in use: " + docVal);
            }
        }

    }

    String[] docValues = new String[] {
            "contextPath.output",
            "encodedSlashesAllowedInUrl",
            "smp.http.forwarded.headers.enabled",
            "smp.http.httpStrictTransportSecurity.maxAge",
            "smp.http.header.security.policy",
            "smp.proxy.host",
            "smp.noproxy.hosts",
            "smp.proxy.password",
            "smp.proxy.port",
            "smp.proxy.user",
            "identifiersBehaviour.ParticipantIdentifierScheme.validationRegex",
            "identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage",
            "identifiersBehaviour.scheme.mandatory",
            "identifiersBehaviour.ParticipantIdentifierScheme.ebCoreId.concatenate",
            "identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes",
            "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes",
            "identifiersBehaviour.splitPattern",
            "identifiersBehaviour.ParticipantIdentifierScheme.urn.concatenate",
            "bdmsl.integration.enabled",
            "bdmsl.participant.multidomain.enabled",
            "bdmsl.integration.url",
            "bdmsl.integration.tls.disableCNCheck",
            "bdmsl.integration.tls.serverSubjectRegex",
            "bdmsl.integration.logical.address",
            "bdmsl.integration.physical.address",
            "bdmsl.integration.tls.useSystemDefaultTruststore",
            "smp.keystore.password",
            "smp.keystore.filename",
            "smp.keystore.type",
            "smp.truststore.password",
            "smp.truststore.filename",
            "smp.truststore.type",
            "smp.certificate.crl.force",
            "encryption.key.filename",
            "smp.keystore.password.decrypted",
            "smp.truststore.password.decrypted",
            "smp.certificate.validation.allowedCertificatePolicyOIDs",
            "smp.certificate.validation.subjectRegex",
            "smp.property.refresh.cronJobExpression",
            "smp.ui.session.secure",
            "smp.ui.session.max-age",
            "smp.ui.session.strict",
            "smp.ui.session.path",
            "smp.ui.session.idle_timeout.admin",
            "smp.ui.session.idle_timeout.user",
            "smp.cluster.enabled",
            "smp.passwordPolicy.validationRegex",
            "smp.passwordPolicy.validationMessage",
            "smp.passwordPolicy.validDays",
            "smp.passwordPolicy.warning.beforeExpiration",
            "smp.passwordPolicy.expired.forceChange",
            "smp.user.login.fail.delay",
            "smp.user.login.maximum.attempt",
            "smp.user.login.suspension.time",
            "smp.accessToken.validDays",
            "smp.accessToken.login.maximum.attempt",
            "smp.accessToken.login.suspension.time",
            "smp.accessToken.login.fail.delay",
            "smp.ui.authentication.types",
            "smp.automation.authentication.types",
            "smp.automation.authentication.external.tls.clientCert.enabled",
            "smp.automation.authentication.external.tls.SSLClientCert.enabled",
            "smp.sso.cas.ui.label",
            "smp.sso.cas.url",
            "smp.sso.cas.urlPath.login",
            "smp.sso.cas.callback.url",
            "smp.sso.cas.smp.urlPath",
            "smp.sso.cas.smp.user.data.urlPath",
            "smp.sso.cas.token.validation.urlPath",
            "smp.sso.cas.token.validation.params",
            "smp.sso.cas.token.validation.groups",
            "mail.smtp.host",
            "mail.smtp.port",
            "mail.smtp.protocol",
            "mail.smtp.username",
            "mail.smtp.password",
            "mail.smtp.properties",
            "smp.alert.user.login_failure.enabled",
            "smp.alert.user.login_failure.level",
            "smp.alert.user.login_failure.mail.subject",
            "smp.alert.user.suspended.enabled",
            "smp.alert.user.suspended.level",
            "smp.alert.user.suspended.mail.subject",
            "smp.alert.user.suspended.mail.moment",
            "smp.alert.password.imminent_expiration.enabled",
            "smp.alert.password.imminent_expiration.delay_days",
            "smp.alert.password.imminent_expiration.frequency_days",
            "smp.alert.password.imminent_expiration.level",
            "smp.alert.password.imminent_expiration.mail.subject",
            "smp.alert.password.expired.enabled",
            "smp.alert.password.expired.delay_days",
            "smp.alert.password.expired.frequency_days",
            "smp.alert.password.expired.level",
            "smp.alert.password.expired.mail.subject",
            "smp.alert.accessToken.imminent_expiration.enabled",
            "smp.alert.accessToken.imminent_expiration.delay_days",
            "smp.alert.accessToken.imminent_expiration.frequency_days",
            "smp.alert.accessToken.imminent_expiration.level",
            "smp.alert.accessToken.imminent_expiration.mail.subject",
            "smp.alert.accessToken.expired.enabled",
            "smp.alert.accessToken.expired.delay_days",
            "smp.alert.accessToken.expired.frequency_days",
            "smp.alert.accessToken.expired.level",
            "smp.alert.accessToken.expired.mail.subject",
            "smp.alert.certificate.imminent_expiration.enabled",
            "smp.alert.certificate.imminent_expiration.delay_days",
            "smp.alert.certificate.imminent_expiration.frequency_days",
            "smp.alert.certificate.imminent_expiration.level",
            "smp.alert.certificate.imminent_expiration.mail.subject",
            "smp.alert.certificate.expired.enabled",
            "smp.alert.certificate.expired.delay_days",
            "smp.alert.certificate.expired.frequency_days",
            "smp.alert.certificate.expired.level",
            "smp.alert.certificate.expired.mail.subject",
            "smp.alert.credentials.cronJobExpression",
            "smp.alert.credentials.serverInstance",
            "smp.alert.credentials.batch.size",
            "smp.alert.mail.from"
    };
    */
}
