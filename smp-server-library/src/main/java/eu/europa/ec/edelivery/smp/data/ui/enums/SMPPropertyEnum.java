package eu.europa.ec.edelivery.smp.data.ui.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPEnumConstants.*;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum.*;


public enum SMPPropertyEnum {

    OUTPUT_CONTEXT_PATH("contextPath.output", "true", "This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.",
            MANDATORY, NOT_ENCRYPTED, RESTART_NEEDED, BOOLEAN),
    ENCODED_SLASHES_ALLOWED_IN_URL("encodedSlashesAllowedInUrl", "true", "Allow encoded slashes in context path. Set to true if slashes are are part of identifiers.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, BOOLEAN),
    HTTP_FORWARDED_HEADERS_ENABLED("smp.http.forwarded.headers.enabled", "false", "Use (value true) or remove (value false) forwarded headers! There are security considerations for forwarded headers since an application cannot know if the headers were added by a proxy, as intended, or by a malicious client.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    HTTP_HSTS_MAX_AGE("smp.http.httpStrictTransportSecurity.maxAge", "31536000", "How long(in seconds) HSTS should last in the browser's cache(default one year)",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, INTEGER),
    HTTP_HEADER_SEC_POLICY("smp.http.header.security.policy", "", "Content Security Policy (CSP) default-src 'self'; script-src 'self';  connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self';",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    // http proxy configuration
    HTTP_PROXY_HOST("smp.proxy.host", "", "The http proxy host",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    HTTP_NO_PROXY_HOSTS("smp.noproxy.hosts", "localhost|127.0.0.1", "list of nor proxy hosts. Ex.: localhost|127.0.0.1",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    HTTP_PROXY_PASSWORD("smp.proxy.password", "", "Base64 encrypted password for Proxy.",
            OPTIONAL, ENCRYPTED, NO_RESTART_NEEDED, STRING),
    HTTP_PROXY_PORT("smp.proxy.port", "80", "The http proxy port",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    HTTP_PROXY_USER("smp.proxy.user", "", "The proxy user",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),

    PARTC_SCH_REGEXP("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex", "^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$",
            "Url expression for validating the participant schema!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),
    PARTC_SCH_REGEXP_MSG("identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage",
            "Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: 'busdox-actorid-upis') and may only contain the following characters: [a-z0-9].", "Error message for UI",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    PARTC_SCH_MANDATORY("identifiersBehaviour.scheme.mandatory", "true", "Scheme for participant identifier is mandatory",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    PARTC_SCH_SPLIT_REGEXP("identifiersBehaviour.splitPattern", "^(?i)\\s*?(?<scheme>urn:oasis:names:tc:ebcore:partyid-type:(iso6523:[0-9]{4}|unregistered(:[^:]+)?))::?(?<identifier>.+)?\\s*$",
             "Regular expression with groups <scheme> and <identifier> for splitting the identifiers to scheme and identifier part!",  OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),
    PARTC_SCH_URN_REGEXP("identifiersBehaviour.ParticipantIdentifierScheme.urn.concatenate",
            "", "Regular expression to detect URN party identifiers. If the party identifier schema matches the regexp, then the party identifier is concatenated with a single colon in XML responses. Else it is handled as OASIS SMP party identifier. Example: ^(?i)(urn:)|(mailto:).*$",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),

    CS_PARTICIPANTS("identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes", "sensitive-participant-sc1|sensitive-participant-sc2", "Specifies schemes of participant identifiers that must be considered CASE-SENSITIVE.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),
    CS_DOCUMENTS("identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes", "casesensitive-doc-scheme1|casesensitive-doc-scheme2", "Specifies schemes of document identifiers that must be considered CASE-SENSITIVE.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),

    DOCUMENT_RESTRICTION_CERT_TYPES("document.restriction.allowed.certificate.types", "", "Allowed certificate types registered when composing service metadata. Empty value means no restrictions, for other values see the java KeyFactory Algorithms for example RSA|EC|Ed25519|Ed448",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),

    // SML integration!
    SML_ENABLED("bdmsl.integration.enabled", "false", "BDMSL (SML) integration ON/OFF switch",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    SML_PARTICIPANT_MULTIDOMAIN("bdmsl.participant.multidomain.enabled", "false", "Set to true if SML support participant on multidomain",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, BOOLEAN),
    SML_URL("bdmsl.integration.url", "http://localhost:8080/edelivery-sml", "BDMSL (SML) endpoint",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, URL),
    SML_TLS_DISABLE_CN_CHECK("bdmsl.integration.tls.disableCNCheck", "false", "If SML Url is HTTPs - Disable CN check if needed.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    SML_TLS_SERVER_CERT_SUBJECT_REGEXP("bdmsl.integration.tls.serverSubjectRegex", ".*", "Regular expression for server TLS certificate subject verification  CertEx. .*CN=acc.edelivery.tech.ec.europa.eu.*.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),
    SML_TLS_TRUSTSTORE_USE_SYSTEM_DEFAULT("bdmsl.integration.tls.useSystemDefaultTruststore", "false", "If true use system default truststore for trusting TLS server certificate (Legacy behaviour to SMP 4.1 version), else use SMP truststore",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    SML_LOGICAL_ADDRESS("bdmsl.integration.logical.address", "http://localhost:8080/smp/", "Logical SMP endpoint which will be registered on SML when registering new domain",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, URL),
    SML_PHYSICAL_ADDRESS("bdmsl.integration.physical.address", "0.0.0.0", "Physical SMP endpoint which will be registered on SML when registering new domain.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    // keystore truststore
    KEYSTORE_PASSWORD("smp.keystore.password", "", "Encrypted keystore (and keys) password ",
            OPTIONAL, ENCRYPTED, NO_RESTART_NEEDED, STRING),
    KEYSTORE_TYPE("smp.keystore.type", "JKS", "Keystore type as JKS/PKCS12",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    KEYSTORE_FILENAME("smp.keystore.filename", "smp-keystore.jks", "Keystore filename ",
            MANDATORY, NOT_ENCRYPTED, NO_RESTART_NEEDED, FILENAME),
    TRUSTSTORE_TYPE("smp.truststore.type", "JKS", "Truststore type as JKS/PKCS12",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    TRUSTSTORE_PASSWORD("smp.truststore.password", "", "Encrypted truststore password ",
            OPTIONAL, ENCRYPTED, NO_RESTART_NEEDED, STRING),
    TRUSTSTORE_FILENAME("smp.truststore.filename", "", "Truststore filename ",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, FILENAME),
    TRUSTSTORE_ADD_CERT_ON_USER_UPDATE("smp.truststore.add.cert.onUserRegistration",
            "false", "Automatically add certificate to truststore when assigned to user.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    CERTIFICATE_CRL_FORCE("smp.certificate.crl.force", "false", "If false then if CRL is not reachable ignore CRL validation",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    CONFIGURATION_DIR("configuration.dir", "smp", "Path to the folder containing all the configuration files (keystore and encryption key)",
            MANDATORY, NOT_ENCRYPTED, RESTART_NEEDED, PATH),
    ENCRYPTION_FILENAME("encryption.key.filename", "encryptionPrivateKey.private", "Key filename to encrypt passwords",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, FILENAME),
    KEYSTORE_PASSWORD_DECRYPTED("smp.keystore.password.decrypted", "", "Only for backup purposes when  password is automatically created. Store password somewhere save and delete this entry!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    TRUSTSTORE_PASSWORD_DECRYPTED("smp.truststore.password.decrypted", "", "Only for backup purposes when  password is automatically created. Store password somewhere save and delete this entry!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    CERTIFICATE_ALLOWED_CERTIFICATEPOLICY_OIDS("smp.certificate.validation.allowedCertificatePolicyOIDs", "", "List of certificate policy OIDs separated by | where at least one must be in the CertifictePolicy extension",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),
    CERTIFICATE_SUBJECT_REGULAR_EXPRESSION("smp.certificate.validation.subjectRegex", ".*", "Regular expression to validate subject of the certificate",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),
    CERTIFICATE_ALLOWED_KEY_TYPES("smp.certificate.validation.allowed.certificate.types",
            "", "Allowed user certificate types. Empty value means no restrictions, for other values see the java KeyFactory Algorithms for examples: RSA|EC|Ed25519|Ed448",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),

    SMP_PROPERTY_REFRESH_CRON("smp.property.refresh.cronJobExpression", "0 48 */1 * * *", "Property refresh cron expression (def 12 minutes to each hour). Property change is refreshed at restart!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, CRON_EXPRESSION),
    // UI COOKIE configuration
    UI_COOKIE_SESSION_SECURE("smp.ui.session.secure", "false", "Cookie is only sent to the server when a request is made with the https: scheme (except on localhost), and therefore is more resistant to man-in-the-middle attacks.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    UI_COOKIE_SESSION_MAX_AGE("smp.ui.session.max-age", "", "Number of seconds until the cookie expires. A zero or negative number will expire the cookie immediately. Empty value will not set parameter",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    UI_COOKIE_SESSION_SITE("smp.ui.session.strict", "Lax", "Controls whether a cookie is sent with cross-origin requests, providing some protection against cross-site request forgery attacks. Possible values are: Strict, None, Lax. (Cookies with SameSite=None require a secure context/HTTPS)!!)",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    UI_COOKIE_SESSION_PATH("smp.ui.session.path", "", "A path that must exist in the requested URL, or the browser won't send the Cookie header.  Null/Empty value sets the authentication requests context by default. The forward slash (/) character is interpreted as a directory separator, and subdirectories will be matched as well: for Path=/docs, /docs, /docs/Web/, and /docs/Web/HTTP will all match",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN("smp.ui.session.idle_timeout.admin", "300", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for ADMIN users (System)!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    UI_COOKIE_SESSION_IDLE_TIMEOUT_USER("smp.ui.session.idle_timeout.user", "1800", "Specifies the time, in seconds, between client requests before the SMP will invalidate session for users (Service group, SMP Admin)",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    SMP_CLUSTER_ENABLED("smp.cluster.enabled", "false", "Define if application is set in cluster. In not cluster environment, properties are updated on setProperty.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),

    PASSWORD_POLICY_REGULAR_EXPRESSION("smp.passwordPolicy.validationRegex", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,32}$",
            "Password minimum complexity rules!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, REGEXP),

    PASSWORD_POLICY_MESSAGE("smp.passwordPolicy.validationMessage", "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character",
            "The error message shown to the user in case the password does not follow the regex put in the domibus.passwordPolicy.pattern property",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    PASSWORD_POLICY_VALID_DAYS("smp.passwordPolicy.validDays", "90", "Number of days password is valid",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    PASSWORD_POLICY_WARNING_DAYS_BEFORE_EXPIRE("smp.passwordPolicy.warning.beforeExpiration", "15",
            "How many days before expiration should the UI warn users at login",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),

    PASSWORD_POLICY_FORCE_CHANGE_EXPIRED("smp.passwordPolicy.expired.forceChange", "true",
            "Force change password at UI login if expired",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),

    USER_LOGIN_FAIL_DELAY("smp.user.login.fail.delay", "1000",
            "Delay response in ms on invalid username or password",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),

    USER_MAX_FAILED_ATTEMPTS("smp.user.login.maximum.attempt", "5",
            "The number of sequence login attempts when the user credentials get suspended. The login attempt count as a sequence login" +
                    " if there is less time between login attempts than defined in property: smp.user.login.suspension.time!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    USER_SUSPENSION_TIME("smp.user.login.suspension.time", "3600",
            "Time in seconds for a suspended user to be reactivated. (if 0 the user will not be reactivated)",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),

    ACCESS_TOKEN_POLICY_VALID_DAYS("smp.accessToken.validDays", "60",
            "Number of days access token is valid is valid",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ACCESS_TOKEN_MAX_FAILED_ATTEMPTS("smp.accessToken.login.maximum.attempt", "10",
            "Number of accessToken login attempt before the accessToken is deactivated",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ACCESS_TOKEN_SUSPENSION_TIME("smp.accessToken.login.suspension.time", "3600",
            "Time in seconds for a suspended accessToken to be reactivated. (if 0 the user will not be reactivated)",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ACCESS_TOKEN_FAIL_DELAY("smp.accessToken.login.fail.delay", "1000",
            "Delay in ms on invalid token id or token",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),

    // authentication
    UI_AUTHENTICATION_TYPES("smp.ui.authentication.types", "PASSWORD", "Set list of '|' separated authentication types: PASSWORD|SSO.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING),
    AUTOMATION_AUTHENTICATION_TYPES("smp.automation.authentication.types", "TOKEN|CERTIFICATE",
            "Set list of '|' separated application-automation authentication types (Web-Service integration). Currently supported TOKEN, CERTIFICATE: ex. TOKEN|CERTIFICATE",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, LIST_STRING
    ),

    EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED(".external.tls.clientCert.enabled", "false",
            "Authentication with external module as: reverse proxy. Authenticated data are send send to application using 'Client-Cert' HTTP header. Do not enable this feature " +
                    "without properly configured reverse-proxy!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED("smp.automation.authentication.external.tls.SSLClientCert.enabled", "false",
            "Authentication with external module as: reverse proxy. Authenticated certificate is send to application using  'SSLClientCert' HTTP header. Do not enable this feature " +
                    "without properly configured reverse-proxy!",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),

    // SSO configuration
    SSO_CAS_UI_LABEL("smp.sso.cas.ui.label", "EU Login", "The SSO service provider label.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    SSO_CAS_URL("smp.sso.cas.url", "http://localhost:8080/cas/", "The SSO CAS URL endpoint",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, URL),
    SSO_CAS_URL_PATH_LOGIN("smp.sso.cas.urlPath.login", "login", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.urlpath.login}.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    SSO_CAS_CALLBACK_URL("smp.sso.cas.callback.url", "http://localhost:8080/smp/ui/public/rest/security/cas", "The URL is the callback URL belonging to the local SMP Security System. If using RP make sure it target SMP path '/ui/public/rest/security/cas'",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, URL),
    SSO_CAS_SMP_LOGIN_URI("smp.sso.cas.smp.urlPath", "/smp/ui/public/rest/security/cas", "SMP relative path which triggers CAS authentication",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    SSO_CAS_SMP_USER_DATA_URL_PATH("smp.sso.cas.smp.user.data.urlPath", "userdata/myAccount.cgi", "Relative path for CAS user data. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.smp.user.data.urlpath}.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    SSO_CAS_TOKEN_VALIDATION_URL_PATH("smp.sso.cas.token.validation.urlPath", "laxValidate", "The CAS URL path for login. Complete URL is composed from parameters: ${smp.sso.cas.url}/${smp.sso.cas.token.validation.urlpath}.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, STRING),
    SSO_CAS_TOKEN_VALIDATION_PARAMS("smp.sso.cas.token.validation.params", "acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP", "The CAS token validation key:value properties separated with '|'.Ex: 'acceptStrengths:BASIC,CLIENT_CERT|assuranceLevel:TOP'",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, MAP_STRING),
    SSO_CAS_TOKEN_VALIDATION_GROUPS("smp.sso.cas.token.validation.groups", "DIGIT_SMP|DIGIT_ADMIN", "'|' separated CAS groups user must belong to.",
            OPTIONAL, NOT_ENCRYPTED, RESTART_NEEDED, LIST_STRING),

    MAIL_SERVER_HOST("mail.smtp.host", "", "Email server - configuration for submitting the emails.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    MAIL_SERVER_PORT("mail.smtp.port", "25", "Smtp mail port - configuration for submitting the emails.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    MAIL_SERVER_PROTOCOL("mail.smtp.protocol", "smtp", "smtp mail protocol- configuration for submitting the emails.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    MAIL_SERVER_USERNAME("mail.smtp.username", "", "smtp mail protocol- username for submitting the emails.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    MAIL_SERVER_PASSWORD("mail.smtp.password", "", "smtp mail protocol - encrypted password for submitting the emails.",
            OPTIONAL, ENCRYPTED, NO_RESTART_NEEDED, STRING),
    MAIL_SERVER_PROPERTIES("mail.smtp.properties", "", " key:value properties separated with '|'.Ex: mail.smtp.auth:true|mail.smtp.starttls.enable:true|mail.smtp.quitwait:false.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, MAP_STRING),

    ALERT_USER_LOGIN_FAILURE_ENABLED("smp.alert.user.login_failure.enabled",
            "false", "Enable/disable the login failure alert of the authentication module.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_USER_LOGIN_FAILURE_LEVEL("smp.alert.user.login_failure.level",
            "LOW", "Alert level for login failure. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_USER_LOGIN_FAILURE_MAIL_SUBJECT("smp.alert.user.login_failure.mail.subject",
            "Login failure", "Login failure mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_USER_SUSPENDED_ENABLED("smp.alert.user.suspended.enabled",
            "true", "Enable/disable the login suspended alert of the authentication module.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_USER_SUSPENDED_LEVEL("smp.alert.user.suspended.level",
            "HIGH", "Alert level for login suspended. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_USER_SUSPENDED_MAIL_SUBJECT("smp.alert.user.suspended.mail.subject",
            "Login credentials suspended", "Login suspended mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),
    ALERT_USER_SUSPENDED_MOMENT("smp.alert.user.suspended.mail.moment",
            "WHEN_BLOCKED", "When should the account disabled alert be triggered. Values: AT_LOGON: An alert will submit mail for all logon attempts to suspended account, WHEN_BLOCKED: An alert will be triggered only the first time when the account got suspended.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING, "^(AT_LOGON|WHEN_BLOCKED)$", "Allowed values are: AT_LOGON,WHEN_BLOCKED"),

    ALERT_PASSWORD_BEFORE_EXPIRATION_ENABLED("smp.alert.password.imminent_expiration.enabled",
            "true", "Enable/disable the imminent password expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_PASSWORD_BEFORE_EXPIRATION_PERIOD("smp.alert.password.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_PASSWORD_BEFORE_EXPIRATION_INTERVAL("smp.alert.password.imminent_expiration.frequency_days",
            "5", "Interval between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_PASSWORD_BEFORE_EXPIRATION_LEVEL("smp.alert.password.imminent_expiration.level",
            "LOW", "Password imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_PASSWORD_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.password.imminent_expiration.mail.subject",
            "Password imminent expiration", "Password imminent expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_PASSWORD_EXPIRED_ENABLED("smp.alert.password.expired.enabled",
            "true", "Enable/disable the password expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_PASSWORD_EXPIRED_PERIOD("smp.alert.password.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_PASSWORD_EXPIRED_INTERVAL("smp.alert.password.expired.frequency_days",
            "5", "Frequency in days between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_PASSWORD_EXPIRED_LEVEL("smp.alert.password.expired.level",
            "LOW", "Password expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_PASSWORD_EXPIRED_MAIL_SUBJECT("smp.alert.password.expired.mail.subject",
            "Password expired", "Password expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_ENABLED("smp.alert.accessToken.imminent_expiration.enabled",
            "true", "Enable/disable the imminent accessToken expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_PERIOD("smp.alert.accessToken.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_INTERVAL("smp.alert.accessToken.imminent_expiration.frequency_days",
            "5", "Frequency in days between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_LEVEL("smp.alert.accessToken.imminent_expiration.level",
            "LOW", "AccessToken imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.accessToken.imminent_expiration.mail.subject",
            "Access token imminent expiration", "accessToken imminent expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_ACCESS_TOKEN_EXPIRED_ENABLED("smp.alert.accessToken.expired.enabled",
            "true", "Enable/disable the accessToken expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_ACCESS_TOKEN_EXPIRED_PERIOD("smp.alert.accessToken.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_ACCESS_TOKEN_EXPIRED_INTERVAL("smp.alert.accessToken.expired.frequency_days",
            "5", "Frequency in days between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_ACCESS_TOKEN_EXPIRED_LEVEL("smp.alert.accessToken.expired.level",
            "LOW", "Access Token expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_ACCESS_TOKEN_EXPIRED_MAIL_SUBJECT("smp.alert.accessToken.expired.mail.subject",
            "Access token expired", "Password expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_CERTIFICATE_BEFORE_EXPIRATION_ENABLED("smp.alert.certificate.imminent_expiration.enabled",
            "true", "Enable/disable the imminent certificate expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_PERIOD("smp.alert.certificate.imminent_expiration.delay_days",
            "15", "Number of days before expiration as for how long before expiration the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_INTERVAL("smp.alert.certificate.imminent_expiration.frequency_days",
            "5", "Frequency in days between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_LEVEL("smp.alert.certificate.imminent_expiration.level",
            "LOW", "certificate imminent expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_CERTIFICATE_BEFORE_EXPIRATION_MAIL_SUBJECT("smp.alert.certificate.imminent_expiration.mail.subject",
            "Certificate imminent expiration", "Certificate imminent expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    ALERT_CERTIFICATE_EXPIRED_ENABLED("smp.alert.certificate.expired.enabled",
            "true", "Enable/disable the certificate expiration alert",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),
    ALERT_CERTIFICATE_EXPIRED_PERIOD("smp.alert.certificate.expired.delay_days",
            "30", "Number of days after expiration as for how long the system should send alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_CERTIFICATE_EXPIRED_INTERVAL("smp.alert.certificate.expired.frequency_days",
            "5", "Frequency in days between alerts.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    ALERT_CERTIFICATE_EXPIRED_LEVEL("smp.alert.certificate.expired.level",
            "LOW", "Certificate expiration alert level. Values: {LOW, MEDIUM, HIGH}",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(LOW|MEDIUM|HIGH)$", "Allowed values are: LOW, MEDIUM, HIGH"),
    ALERT_CERTIFICATE_EXPIRED_MAIL_SUBJECT("smp.alert.certificate.expired.mail.subject",
            "Certificate expired", "Certificate expiration mail subject.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING,
            "^(.{0,255})$", "Subject must have less than 256 character"),

    SMP_ALERT_CREDENTIALS_CRON("smp.alert.credentials.cronJobExpression", "0 52 4 */1 * *", "Property cron expression for triggering alert messages !",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, CRON_EXPRESSION),
    SMP_ALERT_CREDENTIALS_SERVER("smp.alert.credentials.serverInstance", "localhost", "If smp.cluster.enabled is set to true then then instance (hostname) to generate report.",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, STRING),
    SMP_ALERT_BATCH_SIZE("smp.alert.credentials.batch.size", "200", "Max alertes generated in a batch for the type",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, INTEGER),
    SMP_ALERT_MAIL_FROM("smp.alert.mail.from", "test@alert-send-mail.eu", "Alert send mail",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, EMAIL),
    // deprecated properties
    CLIENT_CERT_HEADER_ENABLED_DEPRECATED("authentication.blueCoat.enabled", "false", "Property was replaced by property: smp.automation.authentication.external.tls.clientCert.enabled",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),

    PARTC_EBCOREPARTYID_CONCATENATE("identifiersBehaviour.ParticipantIdentifierScheme.ebCoreId.concatenate", "false",
            "Concatenate ebCore party id in XML responses <ParticipantIdentifier>urn:oasis:names:tc:ebcore:partyid-type:unregistered:test-ebcore-id</ParticipantIdentifier>",
            OPTIONAL, NOT_ENCRYPTED, NO_RESTART_NEEDED, BOOLEAN),

    ;


    String property;
    String defValue;
    String desc;
    Pattern valuePattern;
    String errorValueMessage;

    boolean isEncrypted;
    boolean isMandatory;
    boolean restartNeeded;
    SMPPropertyTypeEnum propertyType;

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, boolean restartNeeded,
                    SMPPropertyTypeEnum propertyType, String valuePattern, String errorValueMessage) {
        this.property = property;
        this.defValue = defValue;
        this.desc = desc;
        this.isEncrypted = isEncrypted;
        this.isMandatory = isMandatory;
        this.restartNeeded = restartNeeded;
        this.propertyType = propertyType;
        this.valuePattern = Pattern.compile(valuePattern);
        this.errorValueMessage = errorValueMessage;
    }

    SMPPropertyEnum(String property, String defValue, String desc, boolean isMandatory, boolean isEncrypted, boolean restartNeeded, SMPPropertyTypeEnum propertyType) {
        this(property, defValue, desc, isMandatory, isEncrypted, restartNeeded, propertyType, propertyType.defValidationRegExp, propertyType.getErrorMessage(property));

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
        return Arrays.stream(values()).filter(val -> val.getProperty().equalsIgnoreCase(keyTrim)).findAny();
    }

    public static List<SMPPropertyEnum> getRestartOnChangeProperties() {
        return Arrays.stream(values()).filter(SMPPropertyEnum::isRestartNeeded).collect(Collectors.toList());
    }

    public Pattern getValuePattern() {
        return valuePattern;
    }

    public String getErrorValueMessage() {
        return this.errorValueMessage;
    }
}


