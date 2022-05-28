package eu.europa.ec.edelivery.smp.data.ui.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum.*;

public enum SMPPropertyEnum {
    OUTPUT_CONTEXT_PATH("contextPath.output", "true", "This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.", true, false, true, BOOLEAN),
    ENCODED_SLASHES_ALLOWED_IN_URL("encodedSlashesAllowedInUrl", "true", "Allow encoded slashes in context path. Set to true if slashes are are part of identifiers.", false, false, true, BOOLEAN),

    HTTP_FORWARDED_HEADERS_ENABLED("smp.http.forwarded.headers.enabled", "false", "Use (value true) or remove (value false) forwarded headers! There are security considerations for forwarded headers since an application cannot know if the headers were added by a proxy, as intended, or by a malicious client.", false, false, false, BOOLEAN),
    HTTP_HSTS_MAX_AGE("smp.http.httpStrictTransportSecurity.maxAge", "31536000", "How long(in seconds) HSTS should last in the browser's cache(default one year)", false, false, true, INTEGER),
    HTTP_HEADER_SEC_POLICY("smp.http.header.security.policy", "", "Content Security Policy (CSP) default-src 'self'; script-src 'self';  connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self';", false, false, true, STRING),
    // http proxy configuration
    HTTP_PROXY_HOST("smp.proxy.host", "", "The http proxy host", false, false, false, STRING),
    HTTP_NO_PROXY_HOSTS("smp.noproxy.hosts", "localhost|127.0.0.1", "list of nor proxy hosts. Ex.: localhost|127.0.0.1", false, false, false, STRING),
    HTTP_PROXY_PASSWORD("smp.proxy.password", "", "Base64 encrypted password for Proxy.", false, true, false, STRING),
    HTTP_PROXY_PORT("smp.proxy.port", "80", "The http proxy port", false, false, false, INTEGER),
    HTTP_PROXY_USER("smp.proxy.user", "", "The proxy user", false, false, false, STRING),

    PARTC_SCH_REGEXP("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex", "^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$", "Participant Identifier Schema of each PUT ServiceGroup request is validated against this schema.", false, false, false, REGEXP),
    PARTC_SCH_REGEXP_MSG("identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage",
            "Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: 'busdox-actorid-upis') and may only contain the following characters: [a-z0-9].", "Error message for UI", false, false, false, STRING),
    PARTC_SCH_MANDATORY("identifiersBehaviour.scheme.mandatory", "true", "Scheme for participant identifier is mandatory", false, false, false, BOOLEAN),

    PARTC_EBCOREPARTYID_CONCATENATE("identifiersBehaviour.ParticipantIdentifierScheme.ebCoreId.concatenate",
            "false", "Concatenate ebCore party id in XML responses <ParticipantIdentifier>urn:oasis:names:tc:ebcore:partyid-type:unregistered:test-ebcore-id</ParticipantIdentifier>", false, false, false, BOOLEAN),

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
    CERTIFICATE_ALLOWED_CERTIFICATEPOLICY_OIDS("smp.certificate.validation.allowedCertificatePolicyOIDs","","List of certificate policy OIDs separated by | where at least one must be in the CertifictePolicy extension", false, false,false, STRING),
    CERTIFICATE_SUBJECT_REGULAR_EXPRESSION("smp.certificate.validation.subjectRegex",".*","Regular expression to validate subject of the certificate", false, false,false, REGEXP),

    SMP_PROPERTY_REFRESH_CRON("smp.property.refresh.cronJobExpression", "0 48 */1 * * *", "Property refresh cron expression (def 12 minutes to each hour). Property change is refreshed at restart!", false, false, false, CRON_EXPRESSION),
    // UI COOKIE configuration
    UI_COOKIE_SESSION_SECURE("smp.ui.session.secure", "false", "Cookie is only sent to the server when a request is made with the https: scheme (except on localhost), and therefore is more resistant to man-in-the-middle attacks.", false, false, false, BOOLEAN),
    UI_COOKIE_SESSION_MAX_AGE("smp.ui.session.max-age", "", "Number of seconds until the cookie expires. A zero or negative number will expire the cookie immediately. Empty value will not set parameter", false, false, false, INTEGER),
    UI_COOKIE_SESSION_SITE("smp.ui.session.strict", "Lax", "Controls whether a cookie is sent with cross-origin requests, providing some protection against cross-site request forgery attacks. Possible values are: Strict, None, Lax. (Cookies with SameSite=None require a secure context/HTTPS)!!)", false, false, false, STRING),
    UI_COOKIE_SESSION_PATH("smp.ui.session.path", "", "A path that must exist in the requested URL, or the browser won't send the Cookie header.  Null/Empty value sets the authentication requests context by default. The forward slash (/) character is interpreted as a directory separator, and subdirectories will be matched as well: for Path=/docs, /docs, /docs/Web/, and /docs/Web/HTTP will all match", false, false, false, STRING),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN("smp.ui.session.idle_timeout.admin", "300", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for ADMIN users (System)!", false, false, false, INTEGER),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_USER("smp.ui.session.idle_timeout.user", "1800", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for users (Service group, SMP Admin)", false, false, false, INTEGER),
    SMP_CLUSTER_ENABLED("smp.cluster.enabled", "false", "Define if application is set in cluster. In not cluster environment, properties are updated on setProperty.", false, false,false, BOOLEAN),

    PASSWORD_POLICY_REGULAR_EXPRESSION("smp.passwordPolicy.validationRegex","^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,32}$",
            "Password minimum complexity rules!", false, false,false, REGEXP),

    PASSWORD_POLICY_MESSAGE("smp.passwordPolicy.validationMessage","Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character",
            "The error message shown to the user in case the password does not follow the regex put in the domibus.passwordPolicy.pattern property", false, false,false, STRING),
    PASSWORD_POLICY_VALID_DAYS("smp.passwordPolicy.validDays","90",
            "Number of days password is valid", false, false,false, INTEGER),
    PASSWORD_POLICY_WARNING_DAYS_BEFORE_EXPIRE("smp.passwordPolicy.warning.beforeExpiration","15",
            "How many days before expiration should the UI warn users at login", false, false,false, INTEGER),

    PASSWORD_POLICY_FORCE_CHANGE_EXPIRED("smp.passwordPolicy.expired.forceChange","true",
            "Force change password at UI login if expired", false, false,false, BOOLEAN),

    USER_LOGIN_FAIL_DELAY("smp.user.login.fail.delay","1000",
            "Delay response in ms on invalid username or password", false, false,false, INTEGER),

    USER_MAX_FAILED_ATTEMPTS("smp.user.login.maximum.attempt","5",
            "Number of console login attempt before the user is deactivated", false, false,false, INTEGER),
    USER_SUSPENSION_TIME("smp.user.login.suspension.time","3600",
            "Time in seconds for a suspended user to be reactivated. (if 0 the user will not be reactivated)", false, false,false, INTEGER),

    ACCESS_TOKEN_POLICY_VALID_DAYS("smp.accessToken.validDays","60",
            "Number of days access token is valid is valid", false, false,false, INTEGER),
    ACCESS_TOKEN_MAX_FAILED_ATTEMPTS("smp.accessToken.login.maximum.attempt","10",
            "Number of accessToken login attempt before the accessToken is deactivated", false, false,false, INTEGER),
    ACCESS_TOKEN_SUSPENSION_TIME("smp.accessToken.login.suspension.time","3600",
            "Time in seconds for a suspended accessToken to be reactivated. (if 0 the user will not be reactivated)", false, false,false, INTEGER),
    ACCESS_TOKEN_FAIL_DELAY("smp.accessToken.login.fail.delay","1000",
            "Delay in ms on invalid token id or token", false, false,false, INTEGER),

    // authentication
    UI_AUTHENTICATION_TYPES("smp.ui.authentication.types", "PASSWORD", "Set list of '|' separated authentication types: PASSWORD|SSO.", false, false, false, LIST_STRING),
    AUTOMATION_AUTHENTICATION_TYPES("smp.automation.authentication.types", "TOKEN|CERTIFICATE",
            "Set list of '|' separated application-automation authentication types (Web-Service integration). Currently supported TOKEN, CERTIFICATE: ex. TOKEN|CERTIFICATE", false, false, false, LIST_STRING),

    EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED("smp.automation.authentication.external.tls.clientCert.enabled", "false",
            "Authentication with external module as: reverse proxy. Authenticated data are send send to application using 'Client-Cert' HTTP header. Do not enable this feature " +
            "without properly configured reverse-proxy!", false, false, false, BOOLEAN),
    EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED("smp.automation.authentication.external.tls.SSLClientCert.enabled", "false",
            "Authentication with external module as: reverse proxy. Authenticated certificate is send to application using  'SSLClientCert' HTTP header. Do not enable this feature " +
            "without properly configured reverse-proxy!", false, false, false, BOOLEAN),

    // SSO configuration
    SSO_CAS_UI_LABEL("smp.sso.cas.ui.label", "EU Login", "The SSO service provider label.", false, false, true, STRING),
    SSO_CAS_URL("smp.sso.cas.url", "http://localhost:8080/cas/", "The SSO CAS URL endpoint", false, false, true, URL),
    SSO_CAS_URL_PATH_LOGIN("smp.sso.cas.urlPath.login", "login", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.urlpath.login}.", false, false, true, STRING),
    SSO_CAS_CALLBACK_URL("smp.sso.cas.callback.url", "http://localhost:8080/smp/ui/public/rest/security/cas", "The URL is the callback URL belonging to the local SMP Security System. If using RP make sure it target SMP path '/ui/public/rest/security/cas'", false, false, true, URL),
    SSO_CAS_SMP_LOGIN_URI("smp.sso.cas.smp.urlPath", "/smp/ui/public/rest/security/cas", "SMP relative path which triggers CAS authentication", false, false, true, STRING),
    SSO_CAS_SMP_USER_DATA_URL_PATH("smp.sso.cas.smp.user.data.urlPath", "userdata/myAccount.cgi", "Relative path for CAS user data. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.smp.user.data.urlpath}.", false, false, true, STRING),
    SSO_CAS_TOKEN_VALIDATION_URL_PATH("smp.sso.cas.token.validation.urlPath", "laxValidate", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.token.validation.urlpath}.", false, false, true, STRING),
    SSO_CAS_TOKEN_VALIDATION_PARAMS("smp.sso.cas.token.validation.params", "acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP", "The CAS token validation key:value properties separated with '|'.Ex: 'acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP'", false, false, true, MAP_STRING),
    SSO_CAS_TOKEN_VALIDATION_GROUPS("smp.sso.cas.token.validation.groups", "DIGIT_SMP|DIGIT_ADMIN", "'|' separated CAS groups user must belong to.", false, false, true, LIST_STRING),

    MAIL_SERVER_HOST("mail.smtp.host", "", "Email server - configuration for submitting the emails.", false,false, false, STRING),
    MAIL_SERVER_PORT("mail.smtp.port", "25", "Smtp mail port - configuration for submitting the emails.", false,false, false,INTEGER),
    MAIL_SERVER_PROTOCOL("mail.smtp.protocol", "smtp", "smtp mail protocol- configuration for submitting the emails.", false,false,false, STRING),
    MAIL_SERVER_USERNAME("mail.smtp.username", "", "smtp mail protocol- username for submitting the emails.", false,false,false, STRING),
    MAIL_SERVER_PASSWORD("mail.smtp.password", "", "smtp mail protocol - encrypted password for submitting the emails.", false,true,false, STRING),
    MAIL_SERVER_PROPERTIES("mail.smtp.properties", "", " key:value properties separated with '|'.Ex: mail.smtp.auth:true|mail.smtp.starttls.enable:true|mail.smtp.quitwait:false.", false, false,false, MAP_STRING),

    ALERT_USER_LOGIN_FAILURE_ENABLED("smp.alert.user.login_failure.enabled",
            "false", "Enable/disable the login failure alert of the authentication module.", false, false,false, BOOLEAN),
    ALERT_USER_LOGIN_FAILURE_LEVEL("smp.alert.user.login_failure.level",
            "LOW", "Alert level for login failure.", false, false,false, STRING),
    ALERT_USER_LOGIN_FAILURE_MAIL_SUBJECT("smp.alert.user.login_failure.mail.subject",
            "Login failure", "Login failure mail subject. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),

    ALERT_USER_SUSPENDED_ENABLED("smp.alert.user.suspended.enabled",
            "true", "Enable/disable the login suspended alert of the authentication module.", false, false,false, BOOLEAN),
    ALERT_USER_SUSPENDED_LEVEL("smp.alert.user.suspended.level",
            "HIGH", "Alert level for login suspended. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_USER_SUSPENDED_MAIL_SUBJECT("smp.alert.user.suspended.mail.subject",
            "Login credentials suspended", "Login suspended mail subject.", false, false,false, STRING),
    ALERT_USER_SUSPENDED_MOMENT("smp.alert.user.suspended.mail.moment",
            "WHEN_BLOCKED", "#When should the account disabled alert be triggered. Values: AT_LOGON: An alert will be triggered each time a user tries to login to a disabled account. WHEN_BLOCKED: An alert will be triggered once when the account got suspended.", false, false,false, STRING),

    ALERT_PASSWORD_BEFORE_EXPIRATION_ENABLED("smp.alert.password.imminent_expiration.enabled",
            "true", "Enable/disable the imminent password expiration alert", false, false,false, BOOLEAN),
    ALERT_PASSWORD_BEFORE_EXPIRATION_PERIOD("smp.alert.password.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.", false, false,false, INTEGER),
    ALERT_PASSWORD_BEFORE_EXPIRATION_INTERVAL("smp.alert.password.imminent_expiration.frequency_days",
            "5", "Interval between alerts.", false, false,false, INTEGER),
    ALERT_PASSWORD_BEFORE_EXPIRATION_LEVEL("smp.alert.password.imminent_expiration.level",
            "LOW", "Password imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_PASSWORD_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.password.imminent_expiration.mail.subject",
            "Password imminent expiration", "Password imminent expiration mail subject.", false, false,false, STRING),

    ALERT_PASSWORD_EXPIRED_ENABLED("smp.alert.password.expired.enabled",
            "true", "Enable/disable the password expiration alert", false, false,false, BOOLEAN),
    ALERT_PASSWORD_EXPIRED_PERIOD("smp.alert.password.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.", false, false,false, INTEGER),
    ALERT_PASSWORD_EXPIRED_INTERVAL("smp.alert.password.expired.frequency_days",
            "5", "Frequency in days between alerts.", false, false,false, INTEGER),
    ALERT_PASSWORD_EXPIRED_LEVEL("smp.alert.password.expired.level",
            "LOW", "Password expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_PASSWORD_EXPIRED_MAIL_SUBJECT("smp.alert.password.expired.mail.subject",
            "Password expired", "Password expiration mail subject.", false, false,false, STRING),

    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_ENABLED("smp.alert.accessToken.imminent_expiration.enabled",
            "true", "Enable/disable the imminent accessToken expiration alert", false, false,false, BOOLEAN),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_PERIOD("smp.alert.accessToken.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.", false, false,false, INTEGER),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_INTERVAL("smp.alert.accessToken.imminent_expiration.frequency_days",
            "5", "Frequency in days between alerts.", false, false,false, INTEGER),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_LEVEL("smp.alert.accessToken.imminent_expiration.level",
            "LOW", "AccessToken imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.accessToken.imminent_expiration.mail.subject",
            "Access token imminent expiration", "accessToken imminent expiration mail subject.", false, false,false, STRING),

    ALERT_ACCESS_TOKEN_EXPIRED_ENABLED("smp.alert.accessToken.expired.enabled",
            "true", "Enable/disable the accessToken expiration alert", false, false,false, BOOLEAN),
    ALERT_ACCESS_TOKEN_EXPIRED_PERIOD("smp.alert.accessToken.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.", false, false,false, INTEGER),
    ALERT_ACCESS_TOKEN_EXPIRED_INTERVAL("smp.alert.accessToken.expired.frequency_days",
            "5", "Frequency in days between alerts.", false, false,false, INTEGER),
    ALERT_ACCESS_TOKEN_EXPIRED_LEVEL("smp.alert.accessToken.expired.level",
            "LOW", "Access Token expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_ACCESS_TOKEN_EXPIRED_MAIL_SUBJECT("smp.alert.accessToken.expired.mail.subject",
            "Access token expired", "Password expiration mail subject.", false, false,false, STRING),

    ALERT_CERTIFICATE_BEFORE_EXPIRATION_ENABLED("smp.alert.certificate.imminent_expiration.enabled",
            "true", "Enable/disable the imminent certificate expiration alert", false, false,false, BOOLEAN),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_PERIOD("smp.alert.certificate.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.", false, false,false, INTEGER),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_INTERVAL("smp.alert.certificate.imminent_expiration.frequency_days",
            "5", "Frequency in days between alerts.", false, false,false, INTEGER),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_LEVEL("smp.alert.certificate.imminent_expiration.level",
            "LOW", "certificate imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.certificate.imminent_expiration.mail.subject",
            "Certificate imminent expiration", "Certificate imminent expiration mail subject.", false, false,false, STRING),

    ALERT_CERTIFICATE_EXPIRED_ENABLED("smp.alert.certificate.expired.enabled",
            "true", "Enable/disable the certificate expiration alert", false, false,false, BOOLEAN),
    ALERT_CERTIFICATE_EXPIRED_PERIOD("smp.alert.certificate.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.", false, false,false, INTEGER),
    ALERT_CERTIFICATE_EXPIRED_INTERVAL("smp.alert.certificate.expired.frequency_days",
            "5", "Frequency in days between alerts.", false, false,false, INTEGER),
    ALERT_CERTIFICATE_EXPIRED_LEVEL("smp.alert.certificate.expired.level",
            "LOW", "Certificate expiration alert level. Values: {LOW, MEDIUM, HIGH}", false, false,false, STRING),
    ALERT_CERTIFICATE_EXPIRED_MAIL_SUBJECT("smp.alert.certificate.expired.mail.subject",
            "Certificate expired", "Password expiration mail subject.", false, false,false, STRING),

    SMP_ALERT_CREDENTIALS_CRON("smp.alert.credentials.cronJobExpression", "0 52 4 */1 * *", "Property cron expression for triggering alert messages !", false, false, false, CRON_EXPRESSION),
    SMP_ALERT_CREDENTIALS_SERVER("smp.alert.credentials.serverInstance", "localhost",  "If smp.cluster.enabled is set to true then then instance (hostname) to generate report.", false, false, false, STRING),
    SMP_ALERT_BATCH_SIZE("smp.alert.credentials.batch.size", "200",  "Max alertes generated in a batch for the type", false, false, false, INTEGER),
    SMP_ALERT_MAIL_FROM("smp.alert.mail.from", "test@alert-send-mail.eu",  "Alert send mail", false, false, false, EMAIL),

    CLIENT_CERT_HEADER_ENABLED_DEPRECATED("authentication.blueCoat.enabled", "false", "Property was replaced by property: smp.automation.authentication.external.tls.clientCert.enabled", false, false, false, BOOLEAN),
    ;

    String property;
    String defValue;
    String desc;
    String valuePattern;

    boolean isEncrypted;
    boolean isMandatory;
    boolean restartNeeded;
    SMPPropertyTypeEnum propertyType;

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, boolean restartNeeded, SMPPropertyTypeEnum propertyType,String valuePattern) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
        this.isEncrypted = isEncrypted;
        this.isMandatory = isMandatory;
        this.restartNeeded = restartNeeded;
        this.propertyType = propertyType;
        this.valuePattern = valuePattern;
    }

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, boolean restartNeeded, SMPPropertyTypeEnum propertyType) {
        this(property, defValue, desc, isMandatory, isEncrypted, restartNeeded, propertyType, propertyType.errorTemplate);

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

    public boolean isRestartNeeded() {
        return restartNeeded;
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

    public static List<SMPPropertyEnum> getRestartOnChangeProperties() {
        return Arrays.asList(values()).stream().filter(val -> val.isRestartNeeded()).collect(Collectors.toList());
    }

    public String getValuePattern() {
        return valuePattern;
    }
}


