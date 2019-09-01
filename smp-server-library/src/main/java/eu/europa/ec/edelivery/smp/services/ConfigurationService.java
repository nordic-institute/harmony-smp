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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.Objects;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;


@Service
public class ConfigurationService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private ConfigurationDao configurationDAO;

    @Autowired
    private  SecurityUtilsServices securityUtilsService;

    
    public Optional<DBConfiguration> findConfigurationProperty(String propertyName)  {
        return configurationDAO.findConfigurationProperty(propertyName);
    }


    public DBConfiguration setPropertyToDatabase(SMPPropertyEnum key, String value, String description)  {
        String finalValue = StringUtils.trimToNull(value);
        if (finalValue == null) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Property: " + key.getProperty() + " cannot be null or empty!");
        }

        if (!PropertyUtils.isValidProperty(key, value)) {
            throw  new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, key.getPropertyType().getMessage(key.getProperty()));
        }
        if (Objects.equals(key.getPropertyType(), SMPPropertyTypeEnum.BOOLEAN)) {
            finalValue = finalValue.toLowerCase();
        }

        // encrypt file
        if (key.isEncrypted() && !StringUtils.isEmpty(value) ) {
            finalValue = encryptString(key, value);

        }
        DBConfiguration res = configurationDAO.setPropertyToDatabase(key, finalValue, description);
        if (key.isEncrypted()) {
            res.setValue("*******");
        }
        return res;
    }

    private String getAndTestForDeprecatedProperty(SMPPropertyEnum newProperty, SMPPropertyEnum oldProperty){
        String value = configurationDAO.getProperty(newProperty);
        if (StringUtils.isBlank(value)) {
            value = configurationDAO.getProperty(oldProperty);
            if (!StringUtils.isBlank(value)) {
                LOG.warn("Using deprecated property: " + newProperty.getProperty()
                        +"! Please replace property with: " + oldProperty.getProperty());
            }
        }
        return value;
    }

    public String getHttpProxyHost() {
        return getAndTestForDeprecatedProperty(HTTP_PROXY_HOST, SML_PROXY_HOST);
    }


    public String getHttpNoProxyHosts() {
        return configurationDAO.getProperty(HTTP_NO_PROXY_HOSTS);
    }


    public int getHttpProxyPort() {
        String port =  getAndTestForDeprecatedProperty(HTTP_PROXY_PORT, SML_PROXY_PORT);

        int iPort = -1;
        if (StringUtils.isBlank(port)) {
            try {
                iPort = Integer.parseInt(port);
            }catch (NumberFormatException nfe){
                LOG.warn("could not parse proxy port number: " + port +"! Use default port 80.");
                iPort = 80;
            }
        }
        return iPort;
    }


    public String getHttpUsername() {
        return getAndTestForDeprecatedProperty(HTTP_PROXY_USER, SML_PROXY_USER);
    }


    public String getHttpPassword() {
        return getAndTestForDeprecatedProperty(HTTP_PROXY_PASSWORD, SML_PROXY_PASSWORD);
    }

    public String getDecryptedHttpPassword() {
        String valEnc =  getHttpPassword();
        if(!StringUtils.isBlank(valEnc)){
            return decryptString(HTTP_PROXY_PASSWORD, valEnc);
        }
        return null;
    }



    public boolean isProxyEnabled(){
        String proxhHost = configurationDAO.getProperty(HTTP_PROXY_HOST);
        return StringUtils.isBlank(proxhHost);
    }
/*


 
    public DBConfiguration getPropertyFromDatabase(SMPPropertyEnum key)  {
        Optional<DBConfiguration> property = configurationDAO.getPropertyFromDatabase(key);
        return handlePropertyResult(key, property);
    }

/*
    public ConfigurationBO deletePropertyFromDatabase(SMLPropertyEnum key) throws TechnicalException {
        Optional<ConfigurationBO> property = configurationDAO.deletePropertyFromDatabase(key);
        return handlePropertyResult(key, property);
    }

    protected ConfigurationBO handlePropertyResult(SMLPropertyEnum key, Optional<ConfigurationBO> property) throws TechnicalException {
        if (!property.isPresent()) {
            throw new BadRequestException("Property: " + key.getProperty() + " not exists in database!");
        }
        ConfigurationBO result = property.get();
        if (key.isEncrypted()) {
            result.setValue("*******");
        }
        return result;
    }
   
    public String getMonitorPassword() {
        return configurationDAO.getProperty(ADMIN_PASSWORD);
    }

    public boolean isBlueCoatEnable() {
        return Boolean.parseBoolean(configurationDAO.getProperty(BLUE_COAT_ENABLED));
    }

    public String getSMPCertRegularExpresion() {
        return configurationDAO.getProperty(AUTH_SMP_CERT_REGEXP);
    }

    public String getCertificateChangeCron() {
        return configurationDAO.getProperty(CERT_CHANGE_CRON);
    }
   
    public String getConfigurationFolder() {
        return configurationDAO.getProperty(CONFIGURATION_DIR);
    }
   
    public String getInconsistencyReportCron() {
        return configurationDAO.getProperty(DIA_CRON);
    }

    public String getInconsistencyReportMailTo() {
        return configurationDAO.getProperty(DIA_MAIL_TO);
    }
   
    public String getInconsistencyReportMailFrom() {
        return configurationDAO.getProperty(DIA_MAIL_FROM);
    }

    public String getInconsistencyReportGenerateByInstance() {
        return configurationDAO.getProperty(DIA_GENERATE_SERVER);
    }

    public String getMailSMPTHost() {
        return configurationDAO.getProperty(MAIL_SERVER_HOST);
    }

    public int getMailSMPTPort() {
        return Integer.parseInt(configurationDAO.getProperty(MAIL_SERVER_PORT));
    }

    public boolean isDNSSig0Enabled() {
        return Boolean.parseBoolean(configurationDAO.getProperty(DNS_SIG0_ENABLED));
    }

    public String getDNSSig0KeyFilename() {
        return configurationDAO.getProperty(DNS_SIG0_KEY_FILENAME);
    }
   
    public String getDNSSig0KeyName() {
        return configurationDAO.getProperty(DNS_SIG0_PKEY_NAME);
    }
   
    public boolean isDNSEnabled() {
        return Boolean.parseBoolean(configurationDAO.getProperty(DNS_ENABLED));
    }

   
    public String getDNSServer() {
        return configurationDAO.getProperty(DNS_SERVER);
    }

   
    public String getDNSPublisherPrefix() {
        return configurationDAO.getProperty(DNS_PUBLISHER_PREFIX);
    }

   
    public String getEncryptionFilename() {
        return configurationDAO.getProperty(ENCRYPTION_FILENAME);
    }

   
    public boolean isProxyEnabled() {
        return Boolean.parseBoolean(configurationDAO.getProperty(PROXY_ENABLED));
    }

   
    public String getHttpProxyHost() {
        return configurationDAO.getProperty(HTTP_PROXY_HOST);
    }

   
    public String getHttpNoProxyHosts() {
        return configurationDAO.getProperty(HTTP_NO_PROXY_HOSTS);
    }

   
    public int getHttpProxyPort() {
        return Integer.parseInt(configurationDAO.getProperty(HTTP_PROXY_PORT));
    }

   
    public String getHttpUsername() {
        return configurationDAO.getProperty(HTTP_PROXY_USER);
    }

   
    public String getHttpPassword() {
        return configurationDAO.getProperty(HTTP_PROXY_PASSWORD);
    }

   
    public boolean isSignResponseEnabled() {
        return Boolean.parseBoolean(configurationDAO.getProperty(SING_RESPONSE));
    }

   
    public String getSignAlias() {
        return configurationDAO.getProperty(SIGNATURE_ALIAS);
    }

   
    public String getKeystoreFilename() {
        return configurationDAO.getProperty(KEYSTORE_FILENAME);
    }

   
    public String getKeystorePassword() {
        return configurationDAO.getProperty(KEYSTORE_PASSWORD);
    }

   
    public int getListPageSize() {
        return Integer.parseInt(configurationDAO.getProperty(PAGE_SIZE));
    }

   
    public boolean isUnsecureLoginEnabled() {
        return Boolean.parseBoolean(configurationDAO.getProperty(UNSEC_LOGIN));
    }
*/

    public String getEncryptionFilename() {
        return configurationDAO.getProperty(ENCRYPTION_FILENAME);
    }

    public String getConfigurationFolder() {
        return configurationDAO.getProperty(CONFIGURATION_DIR);
    }

    protected String decryptString(SMPPropertyEnum key, String value)  {
        try {
            Path location = Paths.get(getConfigurationFolder(), getEncryptionFilename());
            return securityUtilsService.decrypt(location.toAbsolutePath().toFile(), value);
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Error occurred while decrypting the property: "
                    + key.getProperty() + "Error:" + ExceptionUtils.getRootCause(exc));
        }
    }

    protected String encryptString(SMPPropertyEnum key, String value)  {
        try {
            Path location = Paths.get(getConfigurationFolder(), getEncryptionFilename());
            return securityUtilsService.encrypt(location.toAbsolutePath().toFile(), value);
        } catch (Exception exc) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Error occurred while encrypting the property: "
                    + key.getProperty() + "Error:" + ExceptionUtils.getRootCause(exc));
        }
    }
}
