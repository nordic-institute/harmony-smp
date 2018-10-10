package eu.europa.ec.edelivery.smp.exceptions;


/**
 * Error codes and message templates.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum ErrorCode {

    INVALID_ENCODING (500, "SMP:100",ErrorBusinessCode.TECHNICAL, "Unsupported or invalid encoding for %s!"),

    // domain error
    NO_DOMAIN (500,"SMP:110",ErrorBusinessCode.TECHNICAL, "No domain configured on SMP, at least one domain is mandatory!"),
    DOMAIN_NOT_EXISTS(404,"SMP:111",ErrorBusinessCode.NOT_FOUND, "Invalid domain '%s'!"),
    INVALID_DOMAIN_CODE(400,"SMP:112",ErrorBusinessCode.FORMAT_ERROR,"Provided Domain Code '%s' does not match required pattern: '%s'"),
    ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY(500,"SMP:113",ErrorBusinessCode.TECHNICAL,"More than one domain entry  (domain: '%s') is defined in database!"),
    MISSING_DOMAIN(400,"SMP:114",ErrorBusinessCode.MISSING_FIELD,"More than one domain registred on SMP. The domain must be defined!"),
    // user error messages
    INVALID_USER_NO_IDENTIFIERS (400,"SMP:120",ErrorBusinessCode.MISSING_FIELD,"Invalid user - no identifiers!"),
    ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY(500,"SMP:121",ErrorBusinessCode.TECHNICAL,"More than one user entry (username: '%s') is defined in database!"),
    ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY(504,"SMP:122",ErrorBusinessCode.TECHNICAL,"More than one certificate entry (cert. id: '%s') is defined in database!"),
    USER_NOT_EXISTS(400,"SMP:123",ErrorBusinessCode.USER_NOT_FOUND,"User not exists or wrong password!"), // OWASP recommendation\
    USER_IS_NOT_OWNER(400,"SMP:124",ErrorBusinessCode.UNAUTHORIZED,"User %s is not owner of service group (part. id: %s, part. sch.: '%s')!"), // OWASP recommendation


    // service group error
    ILLEGAL_STATE_SG_MULTIPLE_ENTRY (500,"SMP:130",ErrorBusinessCode.TECHNICAL,"More than one service group ( part. id: %s, part. sch.: '%s') is defined in database!"),
    SG_NOT_EXISTS(404,"SMP:131",ErrorBusinessCode.NOT_FOUND,"ServiceGroup not found (dpart. id: '%s', part. sch.: '%s')!"),
    SG_NOT_REGISTRED_FOR_DOMAIN(400,"SMP:131",ErrorBusinessCode.NOT_FOUND,"Service group not registred for domain (domain: %s, part. id:~ '%s', part. sch.: '%s')!"),
    INVALID_EXTENSION_FOR_SG (400,"SMP:132",ErrorBusinessCode.XSD_INVALID,"Invalid extension for service group (part. id: '%s', part. sch.: '%s'). Error: %s!"),
    // service metadata error
    ILLEGAL_STATE_SMD_MULTIPLE_ENTRY (500,"SMP:140",ErrorBusinessCode.TECHNICAL,"More than one service metadata ( doc. id: %s, doc. sch.: '%s') for participant ( part. id %s, part. sch. : '%s') is defined in database!"),
    METADATA_NOT_EXISTS(404,"SMP:141",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not found (part. id: '%s', part. sch.: '%s',doc. id: '%s', doc. sch.: '%s')!"),
    SMD_NOT_EXISTS_FOR_DOMAIN(404,"SMP:142",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not found for domain (domain: %s, part. id: '%s', part. sch.: '%s')!"),
    INVALID_SMD_XML (400,"SMP:143",ErrorBusinessCode.XSD_INVALID,"Invalid service metada. Error: %s"),

    // SML integration
    SML_INTEGRATION_EXCEPTION (500,"SMP:150",ErrorBusinessCode.TECHNICAL,"Could not create new DNS entry through SML! Error: %s "),

    //
    XML_SIGNING_EXCEPTION (500,"SMP:500",ErrorBusinessCode.TECHNICAL,"Error occured while signing response!"),

    JAXB_INITIALIZATION (500,"SMP:511",ErrorBusinessCode.TECHNICAL, "Could not create Unmarshaller for class %s!"),
    XML_PARSE_EXCEPTION (500,"SMP:512",ErrorBusinessCode.TECHNICAL, "Error occured while parsing input stream for %s.  Error: %s!"),

    //
    ;

    int httpCode;
    String messageTemplate;
    String errorCode;
    ErrorBusinessCode errorBusinessCode;

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
