package eu.europa.ec.edelivery.smp.logging;


import eu.europa.ec.edelivery.smp.logging.api.MessageCode;

/**
 * @author Cosmin Baciu (SMPMessageCode, Domibus 3.3+)
 * @since 4.1
 */
public enum SMPMessageCode implements MessageCode {


    BUS_HTTP_PUT_SERVICE_GROUP ("BUS-001", "Http PUT ServiceGroup from user {} from host: {} for owner: {}. ServiceGroup with domain: {}, id: {}."),
    BUS_HTTP_PUT_END_SERVICE_GROUP ("BUS-002", "End http PUT ServiceGroup from user {}, host: {}, owner {}. ServiceGroup with domain {}, id: {}, created {}"),
    BUS_HTTP_DELETE_SERVICE_GROUP ("BUS-003", "Http DELETE ServiceGroup from user {} from host: {}. ServiceGroup id: {}."),
    BUS_HTTP_DELETE_END_SERVICE_GROUP ("BUS-004", "End Http DELETE ServiceGroup from user {} from host: {}. ServiceGroup id: {}."),
    BUS_HTTP_GET_SERVICE_GROUP ("BUS-005", "Http GET ServiceGroup from host: {}, ServiceGroup id: {}."),
    BUS_HTTP_GET_END_SERVICE_GROUP ("BUS-006", "End Http GET ServiceGroup from host: {}, ServiceGroup id: {}."),
    BUS_HTTP_GET_END_STATIC_CONTENT ("BUS-007", "End Http GET static content from host: {}, Path: {}."),


    BUS_HTTP_PUT_SERVICE_METADATA ("BUS-008", "Http PUT ServiceGroupMetadata from user {} from host: {}. ServiceGroup with domain: {}, ServiceGroup id: {} , metadata id {}."),
    BUS_HTTP_PUT_END_SERVICE_METADATA ("BUS-009", "End http PUT ServiceGroupMetadata from user {}, host: {}. ServiceGroup with domain {}, ServiceGroup id: {} , metadata id {}, created {}"),
    BUS_HTTP_DELETE_SERVICE_METADATA ("BUS-010", "Http DELETE ServiceGroupMetadata from user {} from host: {}. ServiceGroup id: {} , metadata id {}."),
    BUS_HTTP_DELETE_END_SERVICE_METADATA ("BUS-011", "End Http DELETE ServiceGroupMetadata from user {} from host: {}. ServiceGroup id: {} , metadata id {}."),

    BUS_HTTP_GET_SERVICE_METADATA ("BUS-012", "Http GET ServiceGroup from host: {}, ServiceGroup id: {}, metadata id {}."),
    BUS_HTTP_GET_END_SERVICE_METADATA ("BUS-013", "End Http GET ServiceGroup from host: {}, ServiceGroup id: {}, metadata id {}."),

    BUS_SAVE_SERVICE_GROUP ("BUS-014", "Start inserting/updating ServiceGroup for domain {}, part. Id: {} part. scheme {}."),
    BUS_SAVE_SERVICE_GROUP_FAILED ("BUS-015", "Inserting/updating ServiceGroup for domain {}, part. Id: {} part. scheme {} failed! Error: [{}]"),

    BUS_SML_REGISTER_SERVICE_GROUP("BUS-016", "Start registering participant:  part. Id: {} part. scheme {} to domain {}"),
    BUS_SML_REGISTER_END_SERVICE_GROUP("BUS-017", "End registering participant:  part. Id: {} part. scheme {} to domain {}"),
    BUS_SML_REGISTER_SERVICE_GROUP_ALREADY_REGISTERED("BUS-018", "Participant:  part. Id: {} part. scheme {} to domain {} marked as already registered to SML"),
    BUS_SML_REGISTER_SERVICE_GROUP_FAILED("BUS-019", "Participant registration:  part. Id: {} part. scheme {} to domain {} failed due to error: {}"),

    BUS_SML_UNREGISTER_SERVICE_GROUP("BUS-020", "Start unregistering participant:  part. Id: {} part. scheme {} to domain {}"),
    BUS_SML_UNREGISTER_END_SERVICE_GROUP("BUS-021", "End unregistering participant:  part. Id: {} part. scheme {} to domain {}"),
    BUS_SML_UNREGISTER_SERVICE_GROUP_ALREADY_REGISTERED("BUS-022", "Participant:  part. Id: {} part. scheme {} to domain {} marked as already unregistered to SML"),
    BUS_SML_UNREGISTER_SERVICE_GROUP_FAILED("BUS-023", "Participant unregistration:  part. Id: {} part. scheme {} to domain {} failed due to error: {}"),

    BUS_INVALID_XML("BUS-030", "Invalid XML for {}. Error: [{}]"),

    SEC_UNSECURED_LOGIN_ALLOWED("SEC-001", "Unsecure login is allowed, no authentication will be performed"),
    SEC_USER_AUTHENTICATED("SEC-002", "User [{}] is authenticated with role [{}]."),
    SEC_USER_NOT_EXISTS("SEC-003", "User [{}] not exists."),
        SEC_INVALID_USER_CREDENTIALS("SEC-004", "User [{}] has invalid credential [{}] type [{}] for target [{}]."),
    SEC_USER_CERT_NOT_EXISTS("SEC-005", "User certificate [{}] not exists."),
    SEC_USER_CERT_INVALID("SEC-006", "User certificate [{}] is invalid: [{}]."),
    SEC_USER_NOT_AUTHENTICATED("SEC-007", "User [{}]. Reason: [{}]."),
    SEC_USER_SUSPENDED("SEC-008", "User [{}] is temporarily suspended."),
    SEC_INVALID_TOKEN("SEC-009", "User [{}] has invalid token value for token id: [{}]."),
    SEC_TRUSTSTORE_CERT_INVALID("SEC-010", "Truststore certificate with alias [{}] is invalid: [{}]."),
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
