/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.exceptions;


/**
 * Error codes and message templates.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum ErrorCode {

    UNAUTHORIZED_INVALID_USERNAME_PASSWORD(401, "SMP:001", ErrorBusinessCode.UNAUTHORIZED, null /*tmplMsg*/),
    UNAUTHORIZED(401, "SMP:003", ErrorBusinessCode.UNAUTHORIZED, null /*tmplMsg*/),

    USER_CHANGE_INVALID_NEW_CREDENTIAL(400, "SMP:010", ErrorBusinessCode.INVALID_INPUT_DATA, null /*tmplMsg*/),

    // domain error
    DOMAIN_NOT_EXISTS(404,"SMP:111", ErrorBusinessCode.NOT_FOUND, null /*tmplMsg*/),
    INVALID_DOMAIN_CODE(400,"SMP:112", ErrorBusinessCode.FORMAT_ERROR, null /*tmplMsg*/),
    ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY(500,"SMP:113", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    ILLEGAL_STATE_DOMAIN_GROUP_MULTIPLE_ENTRY(500,"SMP:115", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    INVALID_DOMAIN_DATA(400,"SMP:116", ErrorBusinessCode.INVALID_INPUT_DATA, null /*tmplMsg*/),
    GROUP_NOT_EXISTS(404,"SMP:117", ErrorBusinessCode.NOT_FOUND, null /*tmplMsg*/),

    // user error messages
    INVALID_USER_NO_IDENTIFIERS(400,"SMP:120", ErrorBusinessCode.MISSING_FIELD, null /*tmplMsg*/),
    ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY(500,"SMP:121", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY(504,"SMP:122", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    USER_NOT_EXISTS(400,"SMP:123", ErrorBusinessCode.USER_NOT_FOUND, null /*tmplMsg*/), // OWASP recommendation\
    INVALID_OWNER(400, "SMP:127", ErrorBusinessCode.NOT_FOUND, null /*tmplMsg*/),

    // service group error
    SG_NOT_EXISTS(404,"SMP:131", ErrorBusinessCode.NOT_FOUND, null /*tmplMsg*/),
    INVALID_EXTENSION_FOR_SG(400,"SMP:132", ErrorBusinessCode.XSD_INVALID, null /*tmplMsg*/),

    // service metadata error
    METADATA_NOT_EXISTS(404,"SMP:141", ErrorBusinessCode.NOT_FOUND, null /*tmplMsg*/),
    INVALID_SMD_XML(400,"SMP:143", ErrorBusinessCode.XSD_INVALID, null /*tmplMsg*/),

    // SML integration
    SML_INTEGRATION_EXCEPTION(500,"SMP:150", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    XML_SIGNING_EXCEPTION(500,"SMP:500", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),

    INTERNAL_ERROR_GENERIC(500,"SMP:501", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/), // "Internal error!"
    INVALID_REQUEST(400,"SMP:513", ErrorBusinessCode.TECHNICAL, "Invalid request [%s]. Error: %s!"),
    INTERNAL_ERROR(500,"SMP:514", ErrorBusinessCode.TECHNICAL, ),
    CERTIFICATE_ERROR(500,"SMP:515", ErrorBusinessCode.TECHNICAL, "Certificate error [%s]. Error: %s!"),
    CONFIGURATION_ERROR(500,"SMP:516", ErrorBusinessCode.TECHNICAL, "Configuration error: [%s]!"),

    MAIL_SUBMISSION_ERROR(500,"SMP:550", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),

    RESOURCE_DOCUMENT_MISSING(500,"SMP:180", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    RESOURCE_DOCUMENT_ERROR(500,"SMP:181", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    SUBRESOURCE_DOCUMENT_MISSING(500,"SMP:182", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
    SUBRESOURCE_DOCUMENT_ERROR(500,"SMP:183", ErrorBusinessCode.TECHNICAL, null /*tmplMsg*/),
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

    public String getMessage(Object ... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        return String.format(messageTemplate, args);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorBusinessCode getErrorBusinessCode() {
        return errorBusinessCode;
    }

}
