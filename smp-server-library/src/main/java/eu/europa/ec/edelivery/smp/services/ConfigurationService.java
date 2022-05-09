package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.auth.enums.SMPUserAuthenticationTypes;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertSuspensionMomentEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;


@Service
public class ConfigurationService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationService.class);

    private final ConfigurationDao configurationDAO;

    public ConfigurationService(ConfigurationDao configurationDAO) {
        this.configurationDAO = configurationDAO;
    }


    public Pattern getParticipantIdentifierSchemeRexExp() {
        return (Pattern) configurationDAO.getCachedPropertyValue(PARTC_SCH_REGEXP);
    }

    public String getParticipantIdentifierSchemeRexExpPattern() {
        return configurationDAO.getCachedProperty(PARTC_SCH_REGEXP);
    }

    public String getParticipantIdentifierSchemeRexExpMessage() {
        return (String) configurationDAO.getCachedPropertyValue(PARTC_SCH_REGEXP_MSG);
    }

    public Boolean getForceConcatenateEBCorePartyId() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(PARTC_EBCOREPARTYID_CONCATENATE);
        // true by default
        return value == null || value;
    }

    public Pattern getPasswordPolicyRexExp() {
        return (Pattern) configurationDAO.getCachedPropertyValue(PASSWORD_POLICY_REGULAR_EXPRESSION);
    }

    public String getPasswordPolicyRexExpPattern() {
        return configurationDAO.getCachedProperty(PASSWORD_POLICY_REGULAR_EXPRESSION);
    }

    public String getPasswordPolicyValidationMessage() {
        return configurationDAO.getCachedProperty(PASSWORD_POLICY_MESSAGE);
    }

    public Integer getPasswordPolicyValidDays() {
        return (Integer) configurationDAO.getCachedPropertyValue(PASSWORD_POLICY_VALID_DAYS);
    }

    public Integer getPasswordPolicyUIWarningDaysBeforeExpire() {
        return (Integer) configurationDAO.getCachedPropertyValue(PASSWORD_POLICY_WARNING_DAYS_BEFORE_EXPIRE);
    }

    public Boolean getPasswordPolicyForceChangeIfExpired() {
        return (Boolean) configurationDAO.getCachedPropertyValue(PASSWORD_POLICY_FORCE_CHANGE_EXPIRED);
    }

    public Integer getAccessTokenPolicyValidDays() {
        return (Integer) configurationDAO.getCachedPropertyValue(ACCESS_TOKEN_POLICY_VALID_DAYS);
    }

    public Integer getLoginMaxAttempts() {
        return (Integer) configurationDAO.getCachedPropertyValue(USER_MAX_FAILED_ATTEMPTS);
    }

    public Integer getLoginSuspensionTimeInSeconds() {
        return (Integer) configurationDAO.getCachedPropertyValue(USER_SUSPENSION_TIME);
    }

    public Integer getLoginFailDelayInMilliSeconds() {
        Integer delay =(Integer) configurationDAO.getCachedPropertyValue(USER_LOGIN_FAIL_DELAY);
        return delay==null? 1000:delay;
    }

    public Integer getAccessTokenLoginMaxAttempts() {
        return (Integer) configurationDAO.getCachedPropertyValue(ACCESS_TOKEN_MAX_FAILED_ATTEMPTS);
    }

    public Integer getAccessTokenLoginSuspensionTimeInSeconds() {
        return (Integer) configurationDAO.getCachedPropertyValue(ACCESS_TOKEN_SUSPENSION_TIME);
    }

    public Integer getAccessTokenLoginFailDelayInMilliSeconds() {
        Integer delay =(Integer) configurationDAO.getCachedPropertyValue(ACCESS_TOKEN_FAIL_DELAY);
        return delay==null? 1000:delay;
    }

    public Integer getHttpHeaderHstsMaxAge() {
        return (Integer) configurationDAO.getCachedPropertyValue(HTTP_HSTS_MAX_AGE);
    }

    public String getHttpHeaderContentSecurityPolicy() {
        return (String) configurationDAO.getCachedPropertyValue(HTTP_HEADER_SEC_POLICY);
    }

    public String getHttpProxyHost() {
        return configurationDAO.getCachedProperty(HTTP_PROXY_HOST);
    }

    public String getHttpNoProxyHosts() {
        return configurationDAO.getCachedProperty(HTTP_NO_PROXY_HOSTS);
    }

    public Optional<Integer> getHttpProxyPort() {
        Integer intVal = (Integer) configurationDAO.getCachedPropertyValue(HTTP_PROXY_PORT);
        return Optional.ofNullable(intVal);
    }

    public java.net.URL getSMLIntegrationUrl() {
        return (java.net.URL) configurationDAO.getCachedPropertyValue(SML_URL);
    }

    public String getProxyUsername() {
        return (String) configurationDAO.getCachedPropertyValue(HTTP_PROXY_USER);
    }

    public String getProxyCredentialToken() {
        return (String) configurationDAO.getCachedPropertyValue(HTTP_PROXY_PASSWORD);
    }

    public List<String> getCaseSensitiveDocumentScheme() {
        return (List<String>) configurationDAO.getCachedPropertyValue(CS_DOCUMENTS);
    }

    public List<String> getCaseSensitiveParticipantScheme() {
        return (List<String>) configurationDAO.getCachedPropertyValue(CS_PARTICIPANTS);
    }

    public boolean getParticipantSchemeMandatory() {
        // not mandatory by default
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(PARTC_SCH_MANDATORY);
        return value != null && value;
    }

    public boolean isProxyEnabled() {
        String proxyHost = configurationDAO.getCachedProperty(HTTP_PROXY_HOST);
        return !StringUtils.isBlank(proxyHost);
    }

    public boolean isSMLIntegrationEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SML_ENABLED);
        return value != null && value;
    }

    public boolean isSMLMultiDomainEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SML_PARTICIPANT_MULTIDOMAIN);
        return value != null && value;
    }

    public boolean isUrlContextEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(OUTPUT_CONTEXT_PATH);
        // by default is true - return false only in case is declared in configuration
        return value == null || value;
    }

    public boolean isClusterEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SMP_CLUSTER_ENABLED);
        return value != null && value;
    }

    public boolean encodedSlashesAllowedInUrl() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(ENCODED_SLASHES_ALLOWED_IN_URL);
        // by default is true - return false only in case is declared in configuration
        return value == null || value;
    }

    public String getTargetServerForCredentialValidation() {
        return (String) configurationDAO.getCachedPropertyValue(SMP_ALERT_CREDENTIALS_SERVER);
    }

    public String getSMLIntegrationSMPLogicalAddress() {
        return configurationDAO.getCachedProperty(SML_LOGICAL_ADDRESS);
    }

    public String getSMLIntegrationSMPPhysicalAddress() {
        return configurationDAO.getCachedProperty(SML_PHYSICAL_ADDRESS);
    }

    public boolean forceCRLValidation() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(CERTIFICATE_CRL_FORCE);
        // by default is not forced -> if missing is false!
        return value != null && value;
    }

    public boolean isExternalTLSAuthenticationWithClientCertHeaderEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED);
        // by default is not forced -> if missing is false!
        return value != null && value;
    }

    public boolean isExternalTLSAuthenticationWithSSLClientCertHeaderEnabled() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED);
        // by default is not forced -> if missing is false!
        return value != null && value;
    }


    public Pattern getCertificateSubjectRegularExpression() {
        return (Pattern) configurationDAO.getCachedPropertyValue(CERTIFICATE_SUBJECT_REGULAR_EXPRESSION);
    }

    public List<String> getAllowedCertificatePolicies() {
        return (List<String>) configurationDAO.getCachedPropertyValue(CERTIFICATE_ALLOWED_CERTIFICATEPOLICY_OIDS);
    }

    public String getSMLIntegrationServerCertSubjectRegExpPattern() {
        return configurationDAO.getCachedProperty(SML_TLS_SERVER_CERT_SUBJECT_REGEXP);
    }

    public Pattern getSMLIntegrationServerCertSubjectRegExp() {
        return (Pattern) configurationDAO.getCachedPropertyValue(SML_TLS_SERVER_CERT_SUBJECT_REGEXP);
    }


    public boolean smlDisableCNCheck() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SML_TLS_DISABLE_CN_CHECK);
        // by default is not forced
        return value != null && value;
    }

    public File getConfigurationFolder() {
        return (File) configurationDAO.getCachedPropertyValue(CONFIGURATION_DIR);
    }

    public File getTruststoreFile() {
        return (File) configurationDAO.getCachedPropertyValue(TRUSTSTORE_FILENAME);
    }

    public File getKeystoreFile() {
        return (File) configurationDAO.getCachedPropertyValue(KEYSTORE_FILENAME);
    }

    public String getTruststoreCredentialToken() {
        return (String) configurationDAO.getCachedPropertyValue(TRUSTSTORE_PASSWORD);
    }

    public String getKeystoreCredentialToken() {
        return (String) configurationDAO.getCachedPropertyValue(KEYSTORE_PASSWORD);
    }

    public boolean getSessionCookieSecure() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_SECURE);
        return value != null && value;
    }

    public Integer getSessionCookieMaxAge() {
        return (Integer) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_MAX_AGE);
    }

    public String getSessionCookieSameSite() {
        return (String) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_SITE);
    }

    public String getSessionCookiePath() {
        return (String) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_PATH);
    }

    public Integer getSessionIdleTimeoutForAdmin() {
        return (Integer) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN);
    }

    public Integer getSessionIdleTimeoutForUser() {
        return (Integer) configurationDAO.getCachedPropertyValue(UI_COOKIE_SESSION_IDLE_TIMEOUT_USER);
    }

    public boolean isSSOEnabledForUserAuthentication() {
        List<String> userAuthenticationTypes = getUIAuthenticationTypes();
        return userAuthenticationTypes != null && userAuthenticationTypes.contains(SMPUserAuthenticationTypes.SSO.name());
    }

    public String getCasUILabel() {
        return (String) configurationDAO.getCachedPropertyValue(SSO_CAS_UI_LABEL);
    }

    public java.net.URL getCasURL() {
        return (java.net.URL) configurationDAO.getCachedPropertyValue(SSO_CAS_URL);
    }

    public java.net.URL getCasCallbackUrl() {
        return (java.net.URL) configurationDAO.getCachedPropertyValue(SSO_CAS_CALLBACK_URL);
    }

    public String getCasSMPLoginRelativePath() {
        return (String) configurationDAO.getCachedPropertyValue(SSO_CAS_SMP_LOGIN_URI);
    }

    public String getCasURLPathLogin() {
        return (String) configurationDAO.getCachedPropertyValue(SSO_CAS_URLPATH_LOGIN);
    }

    public String getCasURLTokenValidation() {
        return (String) configurationDAO.getCachedPropertyValue(SSO_CAS_TOKEN_VALIDATION_URLPATH);
    }

    public Map<String, String> getCasTokenValidationParams() {
        return (Map<String, String>) configurationDAO.getCachedPropertyValue(SSO_CAS_TOKEN_VALIDATION_PARAMS);
    }

    public List<String> getCasURLTokenValidationGroups() {
        return (List<String>) configurationDAO.getCachedPropertyValue(SSO_CAS_TOKEN_VALIDATION_GROUPS);
    }

    public List<String> getUIAuthenticationTypes() {
        return (List<String>) configurationDAO.getCachedPropertyValue(UI_AUTHENTICATION_TYPES);
    }

    public List<String> getAutomationAuthenticationTypes() {
        return (List<String>) configurationDAO.getCachedPropertyValue(AUTOMATION_AUTHENTICATION_TYPES);
    }

    //-----------------------
    // before user suspended
    public Boolean getAlertUserLoginFailureEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_USER_LOGIN_FAILURE_ENABLED);
    }

    public AlertLevelEnum getAlertUserLoginFailureLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_USER_LOGIN_FAILURE_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertBeforeUserLoginFailureSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_USER_LOGIN_FAILURE_MAIL_SUBJECT);
    }

    //-----------------------
    // user suspended
    public Boolean getAlertUserSuspendedEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_USER_SUSPENDED_ENABLED);
    }

    public AlertLevelEnum getAlertUserSuspendedLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_USER_SUSPENDED_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertBeforeUserSuspendedSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_USER_SUSPENDED_MAIL_SUBJECT);
    }

    public AlertSuspensionMomentEnum getAlertBeforeUserSuspendedAlertMoment() {
        String moment = (String) configurationDAO.getCachedPropertyValue(ALERT_USER_SUSPENDED_MOMENT);
        return AlertSuspensionMomentEnum.valueOf(moment);
    }

    //-----------------------
    // before password expire
    public Boolean getAlertBeforeExpirePasswordEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_BEFORE_EXPIRATION_ENABLED);
    }

    public Integer getAlertBeforeExpirePasswordPeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_BEFORE_EXPIRATION_PERIOD);
    }

    public Integer getAlertBeforeExpirePasswordInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_BEFORE_EXPIRATION_INTERVAL);
    }

    public AlertLevelEnum getAlertBeforeExpirePasswordLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_BEFORE_EXPIRATION_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertBeforeExpirePasswordMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_BEFORE_EXPIRATION_MAIL_SUBJECT);
    }

    // expired passwords
    public Boolean getAlertExpiredPasswordEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_EXPIRED_ENABLED);
    }

    public Integer getAlertExpiredPasswordPeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_EXPIRED_PERIOD);
    }

    public Integer getAlertExpiredPasswordInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_EXPIRED_INTERVAL);
    }

    public AlertLevelEnum getAlertExpiredPasswordLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_EXPIRED_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertExpiredPasswordMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_PASSWORD_EXPIRED_MAIL_SUBJECT);
    }

    //-----------------------
    // before access token expire
    public Boolean getAlertBeforeExpireAccessTokenEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_ENABLED);
    }

    public Integer getAlertBeforeExpireAccessTokenPeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_PERIOD);
    }

    public Integer getAlertBeforeExpireAccessTokenInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_INTERVAL);
    }

    public AlertLevelEnum getAlertBeforeExpireAccessTokenLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertBeforeExpireAccessTokenMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_MAIL_SUBJECT);
    }

    // expired access token alerts
    public Boolean getAlertExpiredAccessTokenEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_EXPIRED_ENABLED);
    }

    public Integer getAlertExpiredAccessTokenPeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_EXPIRED_PERIOD);
    }

    public Integer getAlertExpiredAccessTokenInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_EXPIRED_INTERVAL);
    }

    public AlertLevelEnum getAlertExpiredAccessTokenLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_EXPIRED_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertExpiredAccessTokenMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_ACCESS_TOKEN_EXPIRED_MAIL_SUBJECT);
    }

    //-----------------------
    // before certificate expire
    public Boolean getAlertBeforeExpireCertificateEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_BEFORE_EXPIRATION_ENABLED);
    }

    public Integer getAlertBeforeExpireCertificatePeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_BEFORE_EXPIRATION_PERIOD);
    }

    public Integer getAlertBeforeExpireCertificateInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_BEFORE_EXPIRATION_INTERVAL);
    }

    public AlertLevelEnum getAlertBeforeExpireCertificateLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_BEFORE_EXPIRATION_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertBeforeExpireCertificateMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_BEFORE_EXPIRATION_MAIL_SUBJECT);
    }

    // expired access token alerts
    public Boolean getAlertExpiredCertificateEnabled() {
        return (Boolean) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_EXPIRED_ENABLED);
    }

    public Integer getAlertExpiredCertificatePeriod() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_EXPIRED_PERIOD);
    }

    public Integer getAlertExpiredCertificateInterval() {
        return (Integer) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_EXPIRED_INTERVAL);
    }

    public AlertLevelEnum getAlertExpiredCertificateLevel() {
        String level = (String) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_EXPIRED_LEVEL);
        return AlertLevelEnum.valueOf(level);
    }

    public String getAlertExpiredCertificateMailSubject() {
        return (String) configurationDAO.getCachedPropertyValue(ALERT_CERTIFICATE_EXPIRED_MAIL_SUBJECT);
    }


    public Integer getAlertCredentialsBatchSize() {
        return (Integer) configurationDAO.getCachedPropertyValue(SMP_ALERT_BATCH_SIZE);
    }

    public String getAlertEmailFrom() {
        return (String) configurationDAO.getCachedPropertyValue(SMP_ALERT_MAIL_FROM);
    }


}
