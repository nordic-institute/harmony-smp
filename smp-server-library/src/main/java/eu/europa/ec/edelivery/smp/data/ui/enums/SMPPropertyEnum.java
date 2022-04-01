package eu.europa.ec.edelivery.smp.data.ui.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum.*;

public enum SMPPropertyEnum {
    BLUE_COAT_ENABLED("authentication.blueCoat.enabled", "false", "Authentication with Blue Coat means that all HTTP requests " +
            "having 'Client-Cert' header will be authenticated as username placed in the header.Never expose SMP to the WEB " +
            "without properly configured reverse-proxy and active blue coat.", false, false, false, BOOLEAN),

    OUTPUT_CONTEXT_PATH("contextPath.output", "true", "This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.", true, false, true, BOOLEAN),
    HTTP_FORWARDED_HEADERS_ENABLED("smp.http.forwarded.headers.enabled", "false", "Use (value true) or remove (value false) forwarded headers! There are security considerations for forwarded headers since an application cannot know if the headers were added by a proxy, as intended, or by a malicious client.", false, false, false, BOOLEAN),
    HTTP_HSTS_MAX_AGE("smp.http.httpStrictTransportSecurity.maxAge", "31536000", "How long(in seconds) HSTS should last in the browser's cache(default one year)", false, false, true, INTEGER),
    HTTP_HEADER_SEC_POLICY("smp.http.header.security.policy", "default-src 'self'; script-src 'self'; child-src 'none'; connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self';", "Content Security Policy (CSP)", false, false, true, STRING),
    // http proxy configuration
    HTTP_PROXY_HOST("smp.proxy.host", "", "The http proxy host", false, false, false, STRING),
    HTTP_NO_PROXY_HOSTS("smp.noproxy.hosts", "localhost|127.0.0.1", "list of nor proxy hosts. Ex.: localhost|127.0.0.1", false, false, false, STRING),
    HTTP_PROXY_PASSWORD("smp.proxy.password", "", "Base64 encrypted password for Proxy.", false, true, false, STRING),
    HTTP_PROXY_PORT("smp.proxy.port", "80", "The http proxy port", false, false, false, INTEGER),
    HTTP_PROXY_USER("smp.proxy.user", "", "The proxy user", false, false, false, STRING),

    PARTC_SCH_REGEXP("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex", "^((?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)|urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$)", "Participant Identifier Schema of each PUT ServiceGroup request is validated against this schema.", false, false, false, REGEXP),
    PARTC_SCH_REGEXP_MSG("identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage",
            "Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: 'busdox-actorid-upis') and may only contain the following characters: [a-z0-9].", "Error message for UI", false, false, false, STRING),
    CS_PARTICIPANTS("identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes", "sensitive-participant-sc1|sensitive-participant-sc2", "Specifies schemes of participant identifiers that must be considered CASE-SENSITIVE.", false, false, false, LIST_STRING),
    CS_DOCUMENTS("identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes", "casesensitive-doc-scheme1|casesensitive-doc-scheme2", "Specifies schemes of document identifiers that must be considered CASE-SENSITIVE.", false, false, false, LIST_STRING),
    // SML integration!
    SML_ENABLED("bdmsl.integration.enabled", "false", "BDMSL (SML) integration ON/OFF switch", false, false, false, BOOLEAN),
    SML_PARTICIPANT_MULTIDOMAIN("bdmsl.participant.multidomain.enabled", "false", "Set to true if SML support participant on multidomain", false, false, true, BOOLEAN),
    SML_URL("bdmsl.integration.url", "http://localhost:8080/edelivery-sml", "BDMSL (SML) endpoint", false, false, false, URL),
    SML_TLS_DISABLE_CN_CHECK("bdmsl.integration.tls.disableCNCheck", "false", "If SML Url is HTTPs - Disable CN check if needed.", false, false, false, BOOLEAN),
    SML_TLS_SERVER_CERT_SUBJECT_REGEXP("bdmsl.integration.tls.serverSubjectRegex", ".*", "Regular expression for server TLS certificate subject verification  CertEx. .*CN=acc.edelivery.tech.ec.europa.eu.*.", false, false, false, REGEXP),
    SML_LOGICAL_ADDRESS("bdmsl.integration.logical.address", "http://localhost:8080/smp/", "Logical SMP endpoint which will be registered on SML when registering new domain", false, false, false, URL),
    SML_PHYSICAL_ADDRESS("bdmsl.integration.physical.address", "0.0.0.0", "Physical SMP endpoint which will be registered on SML when registering new domain.", false, false, false, STRING),
    // keystore truststore
    KEYSTORE_PASSWORD("smp.keystore.password", "", "Encrypted keystore (and keys) password ", false, true, false, STRING),
    KEYSTORE_FILENAME("smp.keystore.filename", "smp-keystore.jks", "Keystore filename ", true, false, false, FILENAME),
    TRUSTSTORE_PASSWORD("smp.truststore.password", "", "Encrypted truststore password ", false, true, false, STRING),
    TRUSTSTORE_FILENAME("smp.truststore.filename", "", "Truststore filename ", false, false, false, FILENAME),
    CERTIFICATE_CRL_FORCE("smp.certificate.crl.force", "false", "If false then if CRL is not reachable ignore CRL validation", false, false, false, BOOLEAN),
    CONFIGURATION_DIR("configuration.dir", "smp", "Path to the folder containing all the configuration files (keystore and encryption key)", true, false, true, PATH),
    ENCRYPTION_FILENAME("encryption.key.filename", "encryptionPrivateKey.private", "Key filename to encrypt passwords", false, false, true, FILENAME),
    KEYSTORE_PASSWORD_DECRYPTED("smp.keystore.password.decrypted", "", "Only for backup purposes when  password is automatically created. Store password somewhere save and delete this entry!", false, false, false, STRING),
    TRUSTSTORE_PASSWORD_DECRYPTED("smp.truststore.password.decrypted", "", "Only for backup purposes when  password is automatically created. Store password somewhere save and delete this entry!", false, false, false, STRING),
    CERTIFICATE_ALLOWED_CERTIFICATEPOLICY_OIDS("smp.certificate.validation.allowedCertificatePolicyOIDs","","List of certificate policy OIDs separated by comma where at least one must be in the CertifictePolicy extension", false, false,false, STRING),
    CERTIFICATE_SUBJECT_REGULAR_EXPRESSION("smp.certificate.validation.subjectRegex",".*","Regular expression to validate subject of the certificate", false, false,false, REGEXP),

    SMP_PROPERTY_REFRESH_CRON("smp.property.refresh.cronJobExpression", "0 48 */1 * * *", "Property refresh cron expression (def 12 minutes to each hour). Property change is refreshed at restart!", false, false, true, STRING),
    // UI COOKIE configuration
    UI_COOKIE_SESSION_SECURE("smp.ui.session.secure", "false", "Cookie is only sent to the server when a request is made with the https: scheme (except on localhost), and therefore is more resistant to man-in-the-middle attacks.", false, false, false, BOOLEAN),
    UI_COOKIE_SESSION_MAX_AGE("smp.ui.session.max-age", "", "Number of seconds until the cookie expires. A zero or negative number will expire the cookie immediately. Empty value will not set parameter", false, false, false, INTEGER),
    UI_COOKIE_SESSION_SITE("smp.ui.session.strict", "Lax", "Controls whether a cookie is sent with cross-origin requests, providing some protection against cross-site request forgery attacks. Possible values are: Strict, None, Lax. (Cookies with SameSite=None require a secure context/HTTPS)!!)", false, false, false, STRING),
    UI_COOKIE_SESSION_PATH("smp.ui.session.path", "", "A path that must exist in the requested URL, or the browser won't send the Cookie header.  Null/Empty value sets the authentication requests context by default. The forward slash (/) character is interpreted as a directory separator, and subdirectories will be matched as well: for Path=/docs, /docs, /docs/Web/, and /docs/Web/HTTP will all match", false, false, false, STRING),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN("smp.ui.session.idle_timeout.admin", "300", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for ADMIN users (System)!", false, false, false, INTEGER),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_USER("smp.ui.session.idle_timeout.user", "1800", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for users (Service group, SMP Admin)", false, false, false, INTEGER),

    PASSWORD_POLICY_REGULAR_EXPRESSION("smp.passwordPolicy.validationRegex","^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,32}$",
            "Password minimum complexity rules!", false, false,false, REGEXP),

    PASSWORD_POLICY_MESSAGE("smp.passwordPolicy.validationMessage","Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character",
            "The error message shown to the user in case the password does not follow the regex put in the domibus.passwordPolicy.pattern property", false, false,false, STRING),
    PASSWORD_POLICY_VALID_DAYS("smp.passwordPolicy.validDays","90",
            "Number of days password is valid", false, false,false, INTEGER),

    ACCESS_TOKEN_POLICY_VALID_DAYS("smp.accessToken.validDays","60",
            "Number of days access token is valid is valid", false, false,false, INTEGER),


        // authentication
    UI_AUTHENTICATION_TYPES("smp.ui.authentication.types", "PASSWORD", "Set list of '|' separated authentication types: PASSWORD|SSO.", false, false, false, LIST_STRING),
    AUTOMATION_AUTHENTICATION_TYPES("smp.automation.authentication.types", "PASSWORD|CERTIFICATE", "Set list of '|' separated application-automation authentication types (Web-Service integration). Currently supported PASSWORD, CERT: ex. PASSWORD|CERT", false, false, false, LIST_STRING),
    // SSO configuration
    SSO_CAS_UI_LABEL("smp.sso.cas.ui.label", "EU Login", "The SSO service provider label.", false, false, true, STRING),
    SSO_CAS_URL("smp.sso.cas.url", "http://localhost:8080/cas/", "The SSO CAS URL enpoint", false, false, true, URL),
    SSO_CAS_URLPATH_LOGIN("smp.sso.cas.urlpath.login", "login", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.urlpath.login}.", false, false, true, STRING),
    SSO_CAS_CALLBACK_URL("smp.sso.cas.callback.url", "http://localhost:8080/smp/ui/rest/security/cas", "The URL is the callback URL belonging to the local SMP Security System. If using RP make sure it target SMP path '/ui/rest/security/cas'", false, false, true, URL),
    SSO_CAS_TOKEN_VALIDATION_URLPATH("smp.sso.cas.token.validation.urlpath", "http://localhost:8080/cas/", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.urlpath.token.validation}.", false, false, true, STRING),
    SSO_CAS_TOKEN_VALIDATION_PARAMS("smp.sso.cas.token.validation.params", "acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP", "The CAS token validation key:value properties separated with '|'.Ex: 'acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP'", false, false, true, MAP_STRING),
    SSO_CAS_TOKEN_VALIDATION_GROUPS("smp.sso.cas.token.validation.groups", "DIGIT_SMP|DIGIT_ADMIN", "'|' separated CAS groups user must belong to.", false, false, true, LIST_STRING),

    //deprecated properties
    SML_KEYSTORE_PASSWORD("bdmsl.integration.keystore.password", "", "Deprecated", false, false, false, STRING),
    SML_KEYSTORE_PATH("bdmsl.integration.keystore.path", "", "Deprecated", false, false, false, STRING),
    SIGNATURE_KEYSTORE_PASSWORD("xmldsig.keystore.password", "", "Deprecated", false, false, false, STRING),
    SIGNATURE_KEYSTORE_PATH("xmldsig.keystore.classpath", "", "Deprecated", false, false, false, STRING),
    SML_PROXY_HOST("bdmsl.integration.proxy.server", "", "Deprecated", false, false, false, STRING),
    SML_PROXY_PORT("bdmsl.integration.proxy.port", "", "Deprecated", false, false, false, INTEGER),
    SML_PROXY_USER("bdmsl.integration.proxy.user", "", "Deprecated", false, false, false, STRING),
    SML_PROXY_PASSWORD("bdmsl.integration.proxy.password", "", "Deprecated", false, false, false, STRING),
    ;


    String property;
    String defValue;
    String desc;

    boolean isEncrypted;
    boolean isMandatory;
    boolean restartNeeded;
    SMPPropertyTypeEnum propertyType;

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, boolean restartNeeded, SMPPropertyTypeEnum propertyType) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
        this.isEncrypted = isEncrypted;
        this.isMandatory = isMandatory;
        this.restartNeeded = restartNeeded;
        this.propertyType = propertyType;
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


