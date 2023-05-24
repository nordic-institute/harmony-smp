package eu.europa.ec.edelivery.smp.exceptions;


/**
 * Error codes and message templates.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum ErrorCode {

    UNAUTHORIZED_INVALID_USERNAME_PASSWORD(400, "SMP:001",ErrorBusinessCode.UNAUTHORIZED, "Login failed; Invalid userID or password!"),
    UNAUTHORIZED_CREDENTIAL_SUSPENDED(400, "SMP:002",ErrorBusinessCode.UNAUTHORIZED, "The user credential is suspended. Please try again later or contact your administrator."),
    UNAUTHORIZED(400, "SMP:003",ErrorBusinessCode.UNAUTHORIZED, "User not authorized!"),
    UNAUTHORIZED_INVALID_USER_IDENTIFIER(400, "SMP:004",ErrorBusinessCode.UNAUTHORIZED, "Invalid user identifier! User not authorized."),
    UNAUTHORIZED_INVALID_IDENTIFIER(400, "SMP:005",ErrorBusinessCode.UNAUTHORIZED, "Invalid entity identifier!  User not authorized to access the entity data"),

    INVALID_ENCODING (500, "SMP:100",ErrorBusinessCode.TECHNICAL, "Unsupported or invalid encoding for %s!"),
    SML_INVALID_IDENTIFIER (400,"SMP:101",ErrorBusinessCode.FORMAT_ERROR,"Malformed identifier, scheme and id should be delimited by double colon: %s "),

    // domain error
    NO_DOMAIN (500,"SMP:110",ErrorBusinessCode.TECHNICAL, "No domain configured on SMP, at least one domain is mandatory!"),
    DOMAIN_NOT_EXISTS(404,"SMP:111",ErrorBusinessCode.NOT_FOUND, "Invalid domain '%s'!"),
    INVALID_DOMAIN_CODE(400,"SMP:112",ErrorBusinessCode.FORMAT_ERROR,"Provided Domain Code '%s' does not match required pattern: '%s'"),
    ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY(500,"SMP:113",ErrorBusinessCode.TECHNICAL,"More than one domain entry  (domain: '%s') is defined in database!"),
    MISSING_DOMAIN(400,"SMP:114",ErrorBusinessCode.MISSING_FIELD,"More than one domain registered on SMP. The domain must be defined!"),
    ILLEGAL_STATE_DOMAIN_GROUP_MULTIPLE_ENTRY(500,"SMP:115",ErrorBusinessCode.TECHNICAL,"More than one group for domain entry  (group: '%s',  domain: '%s') is defined in database!"),


    // user error messages
    INVALID_USER_NO_IDENTIFIERS (400,"SMP:120",ErrorBusinessCode.MISSING_FIELD,"Invalid user - no identifiers!"),
    ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY(500,"SMP:121",ErrorBusinessCode.TECHNICAL,"More than one user entry (credential token: '%s') is defined in database!"),
    ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY(504,"SMP:122",ErrorBusinessCode.TECHNICAL,"More than one certificate entry (cert. id: '%s') is defined in database!"),
    USER_NOT_EXISTS(400,"SMP:123",ErrorBusinessCode.USER_NOT_FOUND,"User not exists or wrong password!"), // OWASP recommendation\
    USER_IS_NOT_OWNER(400,"SMP:124",ErrorBusinessCode.UNAUTHORIZED,"User %s is not owner of service group (part. id: %s, part. sch.: '%s')!"), // OWASP recommendation
    INVALID_CERTIFICATE_MESSAGE_DIGEST(500, "SMP:125", ErrorBusinessCode.TECHNICAL, "Could not initialize MessageDigest"),
    INVALID_CERTIFICATE_ENCODING(500, "SMP:126", ErrorBusinessCode.TECHNICAL, "Could not encode certificate"),
    INVALID_OWNER(400, "SMP:127", ErrorBusinessCode.NOT_FOUND, "Invalid owner id: %s"),

    // service group error
    ILLEGAL_STATE_SG_MULTIPLE_ENTRY (500,"SMP:130",ErrorBusinessCode.TECHNICAL,"More than one service group ( part. id: %s, part. sch.: '%s') is defined in database!"),
    SG_NOT_EXISTS(404,"SMP:131",ErrorBusinessCode.NOT_FOUND,"ServiceGroup not found (part. id: '%s', part. sch.: '%s')!"),
    SG_NOT_REGISTRED_FOR_DOMAIN(400,"SMP:131",ErrorBusinessCode.NOT_FOUND,"Service group not registered for domain (domain: %s, part. id: '%s', part. sch.: '%s')!"),
    INVALID_EXTENSION_FOR_SG (400,"SMP:132",ErrorBusinessCode.XSD_INVALID,"Invalid extension for service group (part. id: '%s', part. sch.: '%s'). Error: %s!"),
    DUPLICATE_DOMAIN_FOR_SG (400,"SMP:133",ErrorBusinessCode.INVALID_INPUT_DATA,"Repeated domain for Service group (part. id: '%s', part. sch.: '%s', domainCode %s, smlDomain %s).!"),
    MISSING_SG_ID (400,"SMP:134",ErrorBusinessCode.INVALID_INPUT_DATA,"Missing service group(part. id: '%s', part. sch.: '%s'!"),
    INVALID_SG_ID (400,"SMP:135",ErrorBusinessCode.INVALID_INPUT_DATA,"Invalid Id for Service group(part. id: '%s', part. sch.: '%s', id %d).!"),


    // service metadata error
    ILLEGAL_STATE_SMD_MULTIPLE_ENTRY (500,"SMP:140",ErrorBusinessCode.TECHNICAL,"More than one service metadata ( doc. id: %s, doc. sch.: '%s') for participant ( part. id %s, part. sch. : '%s') is defined in database!"),
    METADATA_NOT_EXISTS(404,"SMP:141",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not found (part. id: '%s', part. sch.: '%s',doc. id: '%s', doc. sch.: '%s')!"),
    SMD_NOT_EXISTS_FOR_DOMAIN(404,"SMP:142",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not found for domain (domain: %s, part. id: '%s', part. sch.: '%s')!"),
    INVALID_SMD_XML (400,"SMP:143",ErrorBusinessCode.XSD_INVALID,"Invalid service metadata. Error: %s"),
    INVALID_SMD_DOCUMENT_DATA(400,"SMP:143",ErrorBusinessCode.INVALID_INPUT_DATA,"XML serviceMetadata document (doc. id: '%s', doc. sch.: '%s') " +
            "do not match metadata request (doc. id: '%s', doc. sch.: '%s')."),
    ILLEGAL_STATE_SMD_ON_MULTIPLE_SGD (500,"SMP:144",ErrorBusinessCode.TECHNICAL,"Found than one service group domain for metadata id [%s] and user id [%s]!"),

    // SML integration
    SML_INTEGRATION_EXCEPTION (500,"SMP:150",ErrorBusinessCode.TECHNICAL,"Could not create new DNS entry through SML! Error: %s "),
    //
    XML_SIGNING_EXCEPTION (500,"SMP:500",ErrorBusinessCode.TECHNICAL,"Error occurred while signing response!"),
    JAXB_INITIALIZATION (500,"SMP:511",ErrorBusinessCode.TECHNICAL, "Could not create Unmarshaller for class [%s]!"),
    XML_PARSE_EXCEPTION (500,"SMP:512",ErrorBusinessCode.TECHNICAL, "Error occurred while parsing input stream for [%s].  Error: %s!"),
    INVALID_REQUEST(400,"SMP:513",ErrorBusinessCode.TECHNICAL, "Invalid request [%s]. Error: %s!"),
    INTERNAL_ERROR (500,"SMP:514",ErrorBusinessCode.TECHNICAL, "Internal error [%s]. Error: %s!"),
    CERTIFICATE_ERROR (500,"SMP:515",ErrorBusinessCode.TECHNICAL, "Certificate error [%s]. Error: %s!"),
    CONFIGURATION_ERROR (500,"SMP:516",ErrorBusinessCode.TECHNICAL, "Configuration error: [%s]!"),

    MAIL_SUBMISSION_ERROR (500,"SMP:550",ErrorBusinessCode.TECHNICAL, "Mail submission error: %s!"),

    RESOURCE_DOCUMENT_MISSING(500,"SMP:180",ErrorBusinessCode.TECHNICAL, "Empty document for the resource: [id: '%s', sch.: '%s']!"),
    RESOURCE_DOCUMENT_ERROR(500,"SMP:180",ErrorBusinessCode.TECHNICAL, "Error occurred while reading the resource document: [id: '%s', sch.: '%s']! Error [%s]"),





    //
    ;

    private final int httpCode;
    private final String messageTemplate;
    private final String errorCode;
    private final ErrorBusinessCode errorBusinessCode;

    public int getHttpCode() {
        return httpCode;
    }

    ErrorCode(int httpCode, String errorCode, ErrorBusinessCode ebc, String tmplMsg) {
        this.httpCode = httpCode;
        this.messageTemplate = tmplMsg;
        this.errorCode = errorCode;
        this.errorBusinessCode = ebc;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }
    public String getMessage(Object ... args) {
        return String.format(messageTemplate, args);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorBusinessCode getErrorBusinessCode() {
        return errorBusinessCode;
    }

}
