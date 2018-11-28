package eu.europa.ec.edelivery.smp.data.ui.enums;

public enum SMPPropertyEnum {
    BLUE_COAT_ENABLED ("authentication.blueCoat.enabled","false","Authentication with Blue Coat means that all HTTP requests " +
            "having 'Client-Cert' header will be authenticated as username placed in the header.Never expose SMP to the WEB " +
            "without properly configured reverse-proxy and active blue coat."),

    OUTPUT_CONTEXT_PATH ("contextPath.output","true","This property controls pattern of URLs produced by SMP in GET ServiceGroup responses."),
    PARTC_SCH_REGEXP ("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex","^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)","Participant Identifier Schema of each PUT ServiceGroup request is validated against this schema."),
    CS_PARTICIPANTS("identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes","casesensitive-participant-scheme1|casesensitive-participant-scheme2","Specifies schemes of participant identifiers that must be considered CASE-SENSITIVE."),
    CS_DOCUMENTS("identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes","casesensitive-doc-scheme1|casesensitive-doc-scheme2","Specifies schemes of document identifiers that must be considered CASE-SENSITIVE."),
    SML_ENABLED("bdmsl.integration.enabled","false","BDMSL (SML) integration ON/OFF switch"),
    SML_URL("bdmsl.integration.url","http://localhost:8080/edelivery-sml/","BDMSL (SML) endpoint"),

    SML_LOGICAL_ADDRESS("bdmsl.integration.logical.address","http://localhost:8080/smp/","BDMSL (SML) endpoint"),
    SML_PHYSICAL_ADDRESS("bdmsl.integration.physical.address","0.0.0.0","BDMSL (SML) endpoint"),

    SML_PROXY_HOST("bdmsl.integration.proxy.server","","Proxy "),
    SML_PROXY_PORT("bdmsl.integration.proxy.port","","Proxy "),
    SML_PROXY_USER("bdmsl.integration.proxy.user","","Proxy "),
    SML_PROXY_PASSWORD("bdmsl.integration.proxy.password","","Proxy "),
    KEYSTORE_PASSWORD("smp.keystore.password","","Encrypted keystore (and keys) password "),
    KEYSTORE_FILENAME("smp.keystore.filename","smp-keystore.jks","keystore path "),
    CONFIGURATION_DIR("configuration.dir","","Path to the folder containing all the configuration files (keystore and sig0 key)"),
    ENCRYPTION_FILENAME("encryption.key.filename","encryptionPrivateKey.private","Path to the folder containing all the configuration files (keystore and sig0 key)"),


    KEYSTORE_PASSWORD_DECRYPTED("smp.keystore.password.decrypted","","Only for backup purposes. This password was automatically created. Store password somewhere save and delete this entry!"),

    SML_KEYSTORE_PASSWORD("bdmsl.integration.keystore.password","","Deprecated "),
    SML_KEYSTORE_PATH("bdmsl.integration.keystore.path","","Deprecated "),
    SIGNATURE_KEYSTORE_PASSWORD("xmldsig.keystore.password","","Deprecated "),
    SIGNATURE_KEYSTORE_PATH("xmldsig.keystore.classpath","","Deprecated "),

;

    String property;
    String defValue;
    String desc;

    SMPPropertyEnum(String property, String defValue, String desc) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
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
}


