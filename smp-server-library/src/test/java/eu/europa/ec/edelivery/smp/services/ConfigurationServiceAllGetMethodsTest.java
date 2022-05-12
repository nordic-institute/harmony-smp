package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ConfigurationServiceAllGetMethodsTest {
    private static String TEST_STRING = "TestString";
    private static List<String> TEST_STRING_LIST = Arrays.asList("TestString1","TestString2","TestString3");
    private static Map<String, String> TEST_MAP = new HashMap<>();
    private static Pattern TEST_REXEXP= Pattern.compile(".*");
    private static File TEST_FILE= new File("/tmp/file");
    private static URL TEST_URL;
    static {
        try {
        TEST_URL=  new URL("http://test:123/path");
        } catch (Exception e) {
            fail("Fail to generated test data" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    ConfigurationDao configurationDaoMock = mock(ConfigurationDao.class);
    ConfigurationService testInstance = new ConfigurationService(configurationDaoMock);

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        // set property values for property, set value, method name, value or property, value (true) or property (false)
        return Arrays.asList(new Object[][] {
                {EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, Boolean.TRUE, "isExternalTLSAuthenticationWithClientCertHeaderEnabled", true},
                {EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, Boolean.TRUE, "isExternalTLSAuthenticationWithSSLClientCertHeaderEnabled", true},
                {OUTPUT_CONTEXT_PATH, Boolean.FALSE, "isUrlContextEnabled", true},
                //{HTTP_FORWARDED_HEADERS_ENABLED, Boolean.TRUE, "", true},
                {HTTP_HSTS_MAX_AGE, 1234, "getHttpHeaderHstsMaxAge", true},
                {HTTP_HEADER_SEC_POLICY, TEST_STRING, "getHttpHeaderContentSecurityPolicy", true},
                {HTTP_NO_PROXY_HOSTS,TEST_STRING, "getHttpNoProxyHosts", false},
                {HTTP_PROXY_HOST, TEST_STRING, "getHttpProxyHost", false},
                {HTTP_PROXY_PASSWORD, TEST_STRING, "getProxyCredentialToken", true},
                {HTTP_PROXY_PORT, 8800, "getHttpProxyPort", true},
                {HTTP_PROXY_USER, TEST_STRING, "getProxyUsername", true},
                {PARTC_SCH_REGEXP, TEST_REXEXP,"getParticipantIdentifierSchemeRexExp", true},
                {PARTC_SCH_REGEXP, TEST_STRING, "getParticipantIdentifierSchemeRexExpPattern", false},
                {PARTC_EBCOREPARTYID_CONCATENATE, Boolean.FALSE, "getForceConcatenateEBCorePartyId", true},
                {CS_PARTICIPANTS, TEST_STRING_LIST, "getCaseSensitiveParticipantScheme", true},
                {CS_DOCUMENTS, TEST_STRING_LIST, "getCaseSensitiveDocumentScheme", true},
                {SML_ENABLED, Boolean.FALSE, "isSMLIntegrationEnabled", true},
                {SML_PARTICIPANT_MULTIDOMAIN, Boolean.FALSE, "isSMLMultiDomainEnabled", true},
                {SML_URL,TEST_URL, "getSMLIntegrationUrl", true},
                {SML_TLS_DISABLE_CN_CHECK, Boolean.FALSE, "smlDisableCNCheck", true},
                {SML_TLS_SERVER_CERT_SUBJECT_REGEXP, TEST_REXEXP, "getSMLIntegrationServerCertSubjectRegExp", true},
                {SML_LOGICAL_ADDRESS, TEST_STRING,"getSMLIntegrationSMPLogicalAddress", false},
                {SML_PHYSICAL_ADDRESS, TEST_STRING, "getSMLIntegrationSMPPhysicalAddress", false},
                {KEYSTORE_PASSWORD, TEST_STRING, "getKeystoreCredentialToken", true},
                {KEYSTORE_FILENAME, TEST_FILE, "getKeystoreFile", true},
                {TRUSTSTORE_PASSWORD, TEST_STRING, "getTruststoreCredentialToken", true},
                {TRUSTSTORE_FILENAME, TEST_FILE, "getTruststoreFile", true},
                {CERTIFICATE_CRL_FORCE, Boolean.FALSE, "forceCRLValidation", true},
                {CONFIGURATION_DIR, TEST_FILE, "getConfigurationFolder", true},
                //{ENCRYPTION_FILENAME, TEST_STRING, "", true},
                //{KEYSTORE_PASSWORD_DECRYPTED, TEST_STRING, "", true},
                //{TRUSTSTORE_PASSWORD_DECRYPTED, TEST_STRING, "", true},
                {CERTIFICATE_ALLOWED_CERTIFICATEPOLICY_OIDS, TEST_STRING_LIST, "getAllowedCertificatePolicies", true},
                {CERTIFICATE_SUBJECT_REGULAR_EXPRESSION, TEST_REXEXP, "getCertificateSubjectRegularExpression", true},
                //{SMP_PROPERTY_REFRESH_CRON, TEST_STRING, "", true},
                {UI_COOKIE_SESSION_SECURE, Boolean.FALSE,  "getSessionCookieSecure", true},
                {UI_COOKIE_SESSION_MAX_AGE, 1111, "getSessionCookieMaxAge", true},
                {UI_COOKIE_SESSION_SITE, TEST_STRING, "getSessionCookieSameSite", true},
                {UI_COOKIE_SESSION_PATH, TEST_STRING, "getSessionCookiePath", true},
                {UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN, 12345, "getSessionIdleTimeoutForAdmin", true},
                {UI_COOKIE_SESSION_IDLE_TIMEOUT_USER, 222, "getSessionIdleTimeoutForUser", true},
                {PASSWORD_POLICY_REGULAR_EXPRESSION, TEST_REXEXP, "getPasswordPolicyRexExp", true},
                {PASSWORD_POLICY_MESSAGE, TEST_STRING, "getPasswordPolicyValidationMessage", false},
                {PASSWORD_POLICY_VALID_DAYS, 2, "getPasswordPolicyValidDays", true},
                {PASSWORD_POLICY_REGULAR_EXPRESSION, TEST_STRING, "getPasswordPolicyRexExpPattern", false},
                {PASSWORD_POLICY_WARNING_DAYS_BEFORE_EXPIRE, 10, "getPasswordPolicyUIWarningDaysBeforeExpire", true},
                {PASSWORD_POLICY_FORCE_CHANGE_EXPIRED, Boolean.TRUE, "getPasswordPolicyForceChangeIfExpired", true},
                {USER_LOGIN_FAIL_DELAY,1000, "getLoginFailDelayInMilliSeconds", true},
                {ACCESS_TOKEN_FAIL_DELAY,1000, "getAccessTokenLoginFailDelayInMilliSeconds", true},
                {USER_MAX_FAILED_ATTEMPTS, 55, "getLoginMaxAttempts", true},
                {USER_SUSPENSION_TIME, 3600, "getLoginSuspensionTimeInSeconds", true},
                {ACCESS_TOKEN_POLICY_VALID_DAYS, 1212, "getAccessTokenPolicyValidDays", true},
                {ACCESS_TOKEN_MAX_FAILED_ATTEMPTS, 2323, "getAccessTokenLoginMaxAttempts", true},
                {ACCESS_TOKEN_SUSPENSION_TIME, 22, "getAccessTokenLoginSuspensionTimeInSeconds", true},
                {UI_AUTHENTICATION_TYPES, TEST_STRING_LIST, "getUIAuthenticationTypes", true},
                {AUTOMATION_AUTHENTICATION_TYPES, TEST_STRING_LIST, "getAutomationAuthenticationTypes", true},
                {SSO_CAS_UI_LABEL, TEST_STRING, "getCasUILabel", true},
                {SSO_CAS_URL, TEST_URL, "getCasURL", true},
                {SSO_CAS_URLPATH_LOGIN, TEST_STRING, "getCasURLPathLogin", true},
                {SSO_CAS_CALLBACK_URL, TEST_URL, "getCasCallbackUrl", true},
                {SSO_CAS_TOKEN_VALIDATION_URLPATH, TEST_STRING, "getCasURLTokenValidation", true},
                {SSO_CAS_TOKEN_VALIDATION_PARAMS, TEST_MAP, "getCasTokenValidationParams", true},
                {SSO_CAS_TOKEN_VALIDATION_GROUPS, TEST_STRING_LIST, "getCasURLTokenValidationGroups", true},
                {PARTC_EBCOREPARTYID_CONCATENATE, Boolean.FALSE, "getForceConcatenateEBCorePartyId", true},
                {PARTC_SCH_MANDATORY, Boolean.FALSE, "getParticipantSchemeMandatory", true},
                {SMP_CLUSTER_ENABLED, Boolean.FALSE, "isClusterEnabled", true},
                {ENCODED_SLASHES_ALLOWED_IN_URL, Boolean.FALSE, "encodedSlashesAllowedInUrl", true},
                {SMP_ALERT_CREDENTIALS_SERVER, TEST_STRING, "getTargetServerForCredentialValidation", true},
                {SML_TLS_SERVER_CERT_SUBJECT_REGEXP, TEST_STRING, "getSMLIntegrationServerCertSubjectRegExpPattern", false},
                {SSO_CAS_SMP_LOGIN_URI, TEST_STRING, "getCasSMPLoginRelativePath", true},
                {ALERT_USER_LOGIN_FAILURE_ENABLED, Boolean.FALSE, "getAlertUserLoginFailureEnabled", true},
                {ALERT_USER_SUSPENDED_ENABLED,  Boolean.FALSE, "getAlertUserSuspendedEnabled", true},
                {ALERT_USER_SUSPENDED_MAIL_SUBJECT, TEST_STRING, "getAlertUserSuspendedSubject", true},
                {ALERT_PASSWORD_BEFORE_EXPIRATION_ENABLED,  Boolean.FALSE, "getAlertBeforeExpirePasswordEnabled", true},
                {ALERT_PASSWORD_BEFORE_EXPIRATION_PERIOD, 10, "getAlertBeforeExpirePasswordPeriod", true},
                {ALERT_PASSWORD_BEFORE_EXPIRATION_INTERVAL, 10, "getAlertBeforeExpirePasswordInterval", true},
                {ALERT_PASSWORD_BEFORE_EXPIRATION_MAIL_SUBJECT, TEST_STRING, "getAlertBeforeExpirePasswordMailSubject", true},
                {ALERT_PASSWORD_EXPIRED_ENABLED,  Boolean.FALSE, "getAlertExpiredPasswordEnabled", true},
                {ALERT_PASSWORD_EXPIRED_PERIOD, 10, "getAlertExpiredPasswordPeriod", true},
                {ALERT_PASSWORD_EXPIRED_INTERVAL, 10, "getAlertExpiredPasswordInterval", true},
                {ALERT_PASSWORD_EXPIRED_MAIL_SUBJECT, TEST_STRING, "getAlertExpiredPasswordMailSubject", true},
                {ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_ENABLED,  Boolean.FALSE, "getAlertBeforeExpireAccessTokenEnabled", true},
                {ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_PERIOD, 10, "getAlertBeforeExpireAccessTokenPeriod", true},
                {ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_INTERVAL, 10, "getAlertBeforeExpireAccessTokenInterval", true},
                {ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_MAIL_SUBJECT, TEST_STRING, "getAlertBeforeExpireAccessTokenMailSubject", true},
                {ALERT_ACCESS_TOKEN_EXPIRED_ENABLED,  Boolean.FALSE, "getAlertExpiredAccessTokenEnabled", true},
                {ALERT_ACCESS_TOKEN_EXPIRED_PERIOD, 10, "getAlertExpiredAccessTokenPeriod", true},
                {ALERT_ACCESS_TOKEN_EXPIRED_INTERVAL, 10, "getAlertExpiredAccessTokenInterval", true},
                {ALERT_ACCESS_TOKEN_EXPIRED_MAIL_SUBJECT, TEST_STRING, "getAlertExpiredAccessTokenMailSubject", true},

                {ALERT_CERTIFICATE_BEFORE_EXPIRATION_ENABLED,  Boolean.FALSE, "getAlertBeforeExpireCertificateEnabled", true},
                {ALERT_CERTIFICATE_BEFORE_EXPIRATION_PERIOD, 10, "getAlertBeforeExpireCertificatePeriod", true},
                {ALERT_CERTIFICATE_BEFORE_EXPIRATION_INTERVAL, 10, "getAlertBeforeExpireCertificateInterval", true},
                {ALERT_CERTIFICATE_BEFORE_EXPIRATION_MAIL_SUBJECT, TEST_STRING, "getAlertBeforeExpireCertificateMailSubject", true},

                {ALERT_CERTIFICATE_EXPIRED_ENABLED,  Boolean.FALSE, "getAlertExpiredCertificateEnabled", true},
                {ALERT_CERTIFICATE_EXPIRED_PERIOD, 10, "getAlertExpiredCertificatePeriod", true},
                {ALERT_CERTIFICATE_EXPIRED_INTERVAL, 10, "getAlertExpiredCertificateInterval", true},
                {ALERT_CERTIFICATE_EXPIRED_MAIL_SUBJECT, TEST_STRING, "getAlertExpiredCertificateMailSubject", true},
                {SMP_ALERT_BATCH_SIZE, 10, "getAlertCredentialsBatchSize", true},
                {SMP_ALERT_MAIL_FROM, TEST_STRING, "getAlertEmailFrom", true},

        });
    }
    private final SMPPropertyEnum property;
    private final Object value;
    private final String methodName;
    private final boolean fromValue;

    public ConfigurationServiceAllGetMethodsTest(SMPPropertyEnum property, Object value, String methodName,boolean fromValue) {
        this.property = property;
        this.value = value;
        this.methodName = methodName;
        this.fromValue = fromValue;
    }

    @Test
    public void testProperty() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if (fromValue) {
            doReturn(value).when(configurationDaoMock).getCachedPropertyValue(property);
        } else {
            doReturn(value).when(configurationDaoMock).getCachedProperty(property);
        }
        Object result = MethodUtils.invokeExactMethod(testInstance, methodName);
        if (result instanceof  Optional){
            assertEquals(value, ((Optional<?>) result).get());
        }else {
            assertEquals(value, result);
        }
    }
}