package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;


@Service
public class ConfigurationService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private ConfigurationDao configurationDAO;

    public DBConfiguration setPropertyToDatabase(SMPPropertyEnum key, String value, String description) {
        String finalValue = StringUtils.trimToNull(value);
        if (finalValue == null) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Property: " + key.getProperty() + " cannot be null or empty!");
        }

        if (!PropertyUtils.isValidProperty(key, value)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, key.getPropertyType().getErrorMessage(key.getProperty()));
        }
        if (Objects.equals(key.getPropertyType(), SMPPropertyTypeEnum.BOOLEAN)) {
            finalValue = finalValue.toLowerCase();
        }

        // encrypt file
        if (key.isEncrypted() && !StringUtils.isEmpty(value)) {
            File file = (File) configurationDAO.getCachedPropertyValue(ENCRYPTION_FILENAME);
            finalValue = configurationDAO.encryptString(key, value, file);

        }
        DBConfiguration res = configurationDAO.setPropertyToDatabase(key, finalValue, description);
        if (key.isEncrypted()) {
            res.setValue("*******");
        }
        return res;
    }

    public Pattern getParticipantIdentifierSchemeRexExp() {
        return (Pattern) configurationDAO.getCachedPropertyValue(PARTC_SCH_REGEXP);
    }

    public String getParticipantIdentifierSchemeRexExpPattern() {
        return configurationDAO.getCachedProperty(PARTC_SCH_REGEXP);
    }

    public String getParticipantIdentifierSchemeRexExpMessage() {
        return configurationDAO.getCachedProperty(PARTC_SCH_REGEXP_MSG);
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

    public String getSMLIntegrationSMPLogicalAddress() {
        return configurationDAO.getCachedProperty(SML_LOGICAL_ADDRESS);
    }

    public String getSMLIntegrationSMPPhysicalAddress() {
        return configurationDAO.getCachedProperty(SML_PHYSICAL_ADDRESS);
    }

    public boolean forceCRLValidation() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(CERTIFICATE_CRL_FORCE);
        // by default is not froce
        return value != null && value;
    }

    public String getSMLIntegrationServerCertSubjectRegExp() {
        return configurationDAO.getCachedProperty(SML_TLS_SERVER_CERT_SUBJECT_REGEXP);
    }

    public boolean smlDisableCNCheck() {
        Boolean value = (Boolean) configurationDAO.getCachedPropertyValue(SML_TLS_DISABLE_CN_CHECK);
        // by default is not froce
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

}
