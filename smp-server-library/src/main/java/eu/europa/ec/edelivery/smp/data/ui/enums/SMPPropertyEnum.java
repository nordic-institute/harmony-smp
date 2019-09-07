package eu.europa.ec.edelivery.smp.data.ui.enums;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum SMPPropertyEnum {
    BLUE_COAT_ENABLED ("authentication.blueCoat.enabled","false","Authentication with Blue Coat means that all HTTP requests " +
            "having 'Client-Cert' header will be authenticated as username placed in the header.Never expose SMP to the WEB " +
            "without properly configured reverse-proxy and active blue coat.", false, false , SMPPropertyTypeEnum.BOOLEAN),

    OUTPUT_CONTEXT_PATH ("contextPath.output","true","This property controls pattern of URLs produced by SMP in GET ServiceGroup responses." , true, false , SMPPropertyTypeEnum.BOOLEAN),

    PARTC_SCH_REGEXP ("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex","^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)","Participant Identifier Schema of each PUT ServiceGroup request is validated against this schema.", false, false , SMPPropertyTypeEnum.REGEXP),
    CS_PARTICIPANTS("identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes","casesensitive-participant-scheme1|casesensitive-participant-scheme2","Specifies schemes of participant identifiers that must be considered CASE-SENSITIVE.", false, false , SMPPropertyTypeEnum.STRING),
    CS_DOCUMENTS("identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes","casesensitive-doc-scheme1|casesensitive-doc-scheme2","Specifies schemes of document identifiers that must be considered CASE-SENSITIVE.", false, false , SMPPropertyTypeEnum.STRING),

    SML_ENABLED("bdmsl.integration.enabled","false","BDMSL (SML) integration ON/OFF switch", false, false , SMPPropertyTypeEnum.BOOLEAN),
    SML_PARTICIPANT_MULTIDOMAIN("bdmsl.participant.multidomain.enabled","false","Set to true if SML support participant on multidomain", false, false , SMPPropertyTypeEnum.BOOLEAN),
    SML_URL("bdmsl.integration.url","http://localhost:8080/edelivery-sml","BDMSL (SML) endpoint", false, false , SMPPropertyTypeEnum.URL),
    SML_LOGICAL_ADDRESS("bdmsl.integration.logical.address","http://localhost:8080/smp/","Logical SMP endpoint which will be registered on SML when registering new domain", false, false , SMPPropertyTypeEnum.URL),
    SML_PHYSICAL_ADDRESS("bdmsl.integration.physical.address","0.0.0.0","Physical SMP endpoint which will be registered on SML when registering new domain.", false, false , SMPPropertyTypeEnum.STRING),

    HTTP_PROXY_HOST("smp.proxy.host", "", "The http proxy host", false,false, SMPPropertyTypeEnum.STRING),
    HTTP_NO_PROXY_HOSTS("smp.noproxy.hosts", "localhost|127.0.0.1", "list of nor proxy hosts. Ex.: localhost|127.0.0.1", false,false, SMPPropertyTypeEnum.STRING),
    HTTP_PROXY_PASSWORD("smp.proxy.password", "", "Base64 encrypted password for Proxy.", false, true,SMPPropertyTypeEnum.STRING),
    HTTP_PROXY_PORT("smp.http.port", "80", "The http proxy port", false, false, SMPPropertyTypeEnum.INTEGER),
    HTTP_PROXY_USER("smp.http.user", "", "The proxy user", false, false, SMPPropertyTypeEnum.STRING),


    KEYSTORE_PASSWORD("smp.keystore.password","","Encrypted keystore (and keys) password ", false, true, SMPPropertyTypeEnum.STRING),
    KEYSTORE_FILENAME("smp.keystore.filename","smp-keystore.jks","Keystore filename ", true, false , SMPPropertyTypeEnum.FILENAME),
    TRUSTSTORE_PASSWORD("smp.truststore.password","","Encrypted truststore password ", false, true, SMPPropertyTypeEnum.STRING),
    TRUSTSTORE_FILENAME("smp.truststore.filename","","Truststore filename ", false, false , SMPPropertyTypeEnum.FILENAME),
    CERTIFICATE_CRL_FORCE("smp.certificate.crl.force","false","If false then if CRL is not reachable ignore CRL validation", false, false , SMPPropertyTypeEnum.BOOLEAN),

    CONFIGURATION_DIR("configuration.dir","./","Path to the folder containing all the configuration files (keystore and encryption key)", true, false , SMPPropertyTypeEnum.PATH),
    ENCRYPTION_FILENAME("encryption.key.filename","encryptionPrivateKey.private","Key filename to encrypt passwords", false, false , SMPPropertyTypeEnum.FILENAME),
    KEYSTORE_PASSWORD_DECRYPTED("smp.keystore.password.decrypted","","Only for backup purposes when  password is automatically created. Store password somewhere save and delete this entry!", false, false , SMPPropertyTypeEnum.STRING),

    SML_KEYSTORE_PASSWORD("bdmsl.integration.keystore.password","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SML_KEYSTORE_PATH("bdmsl.integration.keystore.path","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SIGNATURE_KEYSTORE_PASSWORD("xmldsig.keystore.password","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SIGNATURE_KEYSTORE_PATH("xmldsig.keystore.classpath","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SML_PROXY_HOST("bdmsl.integration.proxy.server","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SML_PROXY_PORT("bdmsl.integration.proxy.port","","Deprecated", false, false , SMPPropertyTypeEnum.INTEGER),
    SML_PROXY_USER("bdmsl.integration.proxy.user","","Deprecated", false, false , SMPPropertyTypeEnum.STRING),
    SML_PROXY_PASSWORD("bdmsl.integration.proxy.password","","Deprecated", false, false , SMPPropertyTypeEnum.STRING);

    String property;
    String defValue;
    String desc;

    boolean isEncrypted;
    boolean isMandatory;
    SMPPropertyTypeEnum propertyType;

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, SMPPropertyTypeEnum propertyType) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
        this.isEncrypted=isEncrypted;
        this.isMandatory=isMandatory;
        this.propertyType=propertyType;

    }

    public String getProperty() {
        return property;
    }

    public String getDefValue() {
        return defValue;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public SMPPropertyTypeEnum getPropertyType() {
        return propertyType;
    }

    public static Optional<SMPPropertyEnum> getByProperty(String key) {
        String keyTrim = StringUtils.trimToNull(key);
        if (keyTrim == null) {
            return Optional.empty();
        }
        return Arrays.asList(values()).stream().filter(val -> val.getProperty().equalsIgnoreCase(keyTrim)).findAny();
    }
}


