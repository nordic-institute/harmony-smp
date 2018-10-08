package eu.europa.ec.edelivery.smp.exceptions;


/**
 * Error codes and message templates.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum ErrorCode {

    INVALID_ENCODING ("SMP:100",ErrorBusinessCode.TECHNICAL, "Unsupported or invalid encoding for %s!"),

    // domain error
    NO_DOMAIN ("SMP:110",ErrorBusinessCode.TECHNICAL, "No domain configured on SMP, at least one domain is mandatory!"),
    DOMAIN_NOT_EXISTS("SMP:111",ErrorBusinessCode.NOT_FOUND, "Invalid domain '%s'!"),
    INVALID_DOMAIN_CODE("SMP:112",ErrorBusinessCode.FORMAT_ERROR,"Provided Domain Code '%s' does not match required pattern: '%s'"),
    ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY("SMP:113",ErrorBusinessCode.TECHNICAL,"More than one domain entry  (domain: '%s') is defined in database!"),
    MISSING_DOMAIN("SMP:114",ErrorBusinessCode.MISSING_FIELD,"More than one domain registred on SMP. The domain must be defined!"),
    // user error messages
    INVALID_USER_NO_IDENTIFIERS ("SMP:120",ErrorBusinessCode.MISSING_FIELD,"Invalid user - no identifiers!"),
    ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY("SMP:121",ErrorBusinessCode.TECHNICAL,"More than one user entry (username: '%s') is defined in database!"),
    ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY("SMP:122",ErrorBusinessCode.TECHNICAL,"More than one certificate entry (cert. id: '%s') is defined in database!"),
    USER_NOT_EXISTS("SMP:123",ErrorBusinessCode.NOT_FOUND,"User not exists or wrong password!"), // OWASP recommendation\
    USER_IS_NOT_OWNER("SMP:124",ErrorBusinessCode.UNAUTHORIZED,"User %s is not owner of service group (part. id: %s, part. sch.: '%s')!"), // OWASP recommendation


    // service group error
    ILLEGAL_STATE_SG_MULTIPLE_ENTRY ("SMP:130",ErrorBusinessCode.TECHNICAL,"More than one service group ( part. id: %s, part. sch.: '%s') is defined in database!"),
    SG_NOT_EXISTS("SMP:131",ErrorBusinessCode.NOT_FOUND,"Service group not exists (dpart. id: '%s', part. sch.: '%s')!"),
    SG_NOT_REGISTRED_FOR_DOMAIN("SMP:131",ErrorBusinessCode.NOT_FOUND,"Service group not registred for domain (domain: %s, part. id:~ '%s', part. sch.: '%s')!"),
    INVALID_EXTENSION_FOR_SG ("SMP:132",ErrorBusinessCode.XML_INVALID,"Invalid extension for service group (part. id: '%s', part. sch.: '%s'). Error: %s!"),
    // service metadata error
    ILLEGAL_STATE_SMD_MULTIPLE_ENTRY ("SMP:140",ErrorBusinessCode.TECHNICAL,"More than one service metadata ( doc. id: %s, doc. sch.: '%s') for participant ( part. id %s, part. sch. : '%s') is defined in database!"),
    METADATA_NOT_EXISTS("SMP:141",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not exist(part. id: '%s', part. sch.: '%s',doc. id: '%s', doc. sch.: '%s')!"),
    SMD_NOT_EXISTS_FOR_DOMAIN("SMP:142",ErrorBusinessCode.NOT_FOUND,"ServiceMetadata not exists for domain (domain: %s, part. id: '%s', part. sch.: '%s')!"),
    INVALID_SMD_XML ("SMP:143",ErrorBusinessCode.XML_INVALID,"Invalid service metada. Error: %s"),

    // SML integration
    SML_INTEGRATION_EXCEPTION ("SMP:150",ErrorBusinessCode.TECHNICAL,"Could not create new DNS entry through SML! Error: %s "),

    //
    XML_SIGNING_EXCEPTION ("SMP:500",ErrorBusinessCode.TECHNICAL,"Error occured while signing response!"),

    JAXB_INITIALIZATION ("SMP:511",ErrorBusinessCode.TECHNICAL, "Could not create Unmarshaller for class %s!"),
    XML_PARSE_EXCEPTION ("SMP:512",ErrorBusinessCode.TECHNICAL, "Error occured while parsing input stream for %s.  Error: %s!"),

    //
    ;


    String messageTemplate;

    String errorCode;
    ErrorBusinessCode errorBusinessCode;

    ErrorCode(String errorCode, ErrorBusinessCode ebc,  String tmplMsg) {
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
