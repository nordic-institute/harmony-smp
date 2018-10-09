package eu.europa.ec.edelivery.smp.logging;


import eu.europa.ec.edelivery.smp.logging.api.MessageCode;

/**
 * @author Cosmin Baciu (SMPMessageCode, Domibus 3.3+)
 * @since 4.1
 */
public enum SMPMessageCode implements MessageCode {

    BUS_SAVE_SERVICE_GROUP ("BUS-001", "Start inserting/updating ServiceGroup for part. Id: {} part. schema {}."),
    BUS_SAVE_SERVICE_GROUP_FAILED ("BUS-002", "Inserting/updating ServiceGroup for part. Id: {} part. schema {} failed! Error: [{}]"),

    BUS_INVALID_XML("BUS-010", "Invalid XML for {}. Error: [{}]"),


    SEC_UNSECURED_LOGIN_ALLOWED("SEC-001", "Unsecure login is allowed, no authentication will be performed"),
    ;

    String code;
    String message;

    SMPMessageCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
