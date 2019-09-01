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
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    public String getProxyUsername() {
        return getAndTestForDeprecatedProperty(HTTP_PROXY_USER, SML_PROXY_USER);
    }


    public String getProxyPassword() {
        return getAndTestForDeprecatedProperty(HTTP_PROXY_PASSWORD, SML_PROXY_PASSWORD);
    }

    public String getProxyCredentialToken() {
        String valEnc =  getProxyPassword();
        if(!StringUtils.isBlank(valEnc)){
            return decryptString(HTTP_PROXY_PASSWORD, valEnc);
        }
        return null;
    }

    public boolean isProxyEnabled(){
        String proxyHost = configurationDAO.getProperty(HTTP_PROXY_HOST);
        return !StringUtils.isBlank(proxyHost);
    }


    public String getEncryptionFilename() {
        return configurationDAO.getProperty(ENCRYPTION_FILENAME);
    }

    public String getConfigurationFolder() {
        String path = configurationDAO.getProperty(CONFIGURATION_DIR);
        if(StringUtils.isBlank(path)) {
            path="";
        } else if (!path.endsWith(File.separator)) {
            // do not add this if path is blank!
            path+=File.separator;
        }
        return path;
    }


    public String getTruststoreFilename() {
        return configurationDAO.getProperty(TRUSTSTORE_FILENAME);
    }

    public String getKeystoreFilename() {
        return configurationDAO.getProperty(TRUSTSTORE_FILENAME);
    }

    public String getTruststoreCredentialToken() {
        String valEnc = configurationDAO.getProperty(TRUSTSTORE_PASSWORD);
        if(!StringUtils.isBlank(valEnc)){
            return decryptString(TRUSTSTORE_PASSWORD, valEnc);
        }
        return null;
    }
    public String getKeystoreCredentialToken() {
        String valEnc = configurationDAO.getProperty(KEYSTORE_PASSWORD);
        if(!StringUtils.isBlank(valEnc)){
            return decryptString(KEYSTORE_PASSWORD, valEnc);
        }
        return null;
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
