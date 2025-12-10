/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.error;

import eu.europa.ec.dynamicdiscovery.exception.MalformedIdentifierException;
import eu.europa.ec.edelivery.smp.data.ui.exceptions.ErrorResponseRO;
import eu.europa.ec.edelivery.smp.error.xml.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.*;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.utils.LocaleUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode.TECHNICAL;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType.*;
import static org.springframework.http.HttpStatus.*;

abstract class AbstractErrorControllerAdvice {

    static final Logger LOG = LoggerFactory.getLogger(AbstractErrorControllerAdvice.class);

    private final SMPExceptionLanguageService smpExceptionLanguageService;

    AbstractErrorControllerAdvice(SMPExceptionLanguageService smpExceptionLanguageService) {
        this.smpExceptionLanguageService = smpExceptionLanguageService;
    }

    public ResponseEntity<?> handleRuntimeException(RuntimeException runtimeException) {
        ResponseEntity<?> response;
        String currentLocale = LocaleUtils.getCurrentLocale();
        if (runtimeException instanceof BadRequestException ex) {
            response = buildAndLog(UNPROCESSABLE_ENTITY, ex.getErrorBusinessCode(),
                smpExceptionLanguageService.getMessageTranslation(UI_BAD_REQUEST_EXCEPTION.getMessageCode(), currentLocale), ex);
        } else if (runtimeException instanceof I18NException ex) {
            response = buildAndLog(HttpStatus.resolve(ex.getErrorCode().getHttpCode()),
                    ex.getErrorCode(),
                    smpExceptionLanguageService.getMessageTranslation(ex.getMessageCode(), ex.getMessageArgs(), currentLocale),
                    runtimeException);
        } else if (runtimeException instanceof AuthenticationServiceException ex) {
            response = buildAndLog(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED,
                    smpExceptionLanguageService.getMessageTranslation(UNAUTHORIZED_INVALID_BEARER_TOKEN.getMessageCode(), currentLocale), ex);
        }
        else if (runtimeException instanceof AuthenticationException ex) {
            response = buildAndLog(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED,
                    smpExceptionLanguageService.getMessageTranslation(UI_AUTHENTICATION_EXCEPTION.getMessageCode(), currentLocale), ex);
        } else if (runtimeException instanceof AccessDeniedException ex) {
            response = buildAndLog(FORBIDDEN, ErrorBusinessCode.UNAUTHORIZED,
                    smpExceptionLanguageService.getMessageTranslation(UI_ACCESS_DENIED_EXCEPTION.getMessageCode(), currentLocale), ex);
        } else if (runtimeException instanceof MalformedIdentifierException ex) {
            response = buildAndLog(BAD_REQUEST, ErrorBusinessCode.FORMAT_ERROR,
                    smpExceptionLanguageService.getMessageTranslation(UI_MALFORMED_IDENTIFIER_EXCEPTION.getMessageCode(),
                            Map.of(ErrorMessageArgument.ERROR.getArgumentName(), ExceptionUtils.getRootCauseMessage(ex)),
                            currentLocale), ex);
        } else {
            response = buildAndLog(INTERNAL_SERVER_ERROR, TECHNICAL,
                    smpExceptionLanguageService.getMessageTranslation(UI_INTERNAL_ERROR.getMessageCode(), currentLocale), runtimeException);
        }

        String errorCodeId = "N/A";
        Object body = response.getBody();
        if (body instanceof ErrorResponseRO error) {
            errorCodeId = error.getErrorUniqueId();
        } else if (body instanceof ErrorResponse error) {
            errorCodeId = error.getErrorUniqueId();
        }

        LOG.error("Unhandled exception occurred, unique ID: [{}] with cause [{}]", errorCodeId, ExceptionUtils.getRootCauseMessage(runtimeException));
        return response;
    }

    ResponseEntity<?> buildAndLog(HttpStatus status, ErrorBusinessCode businessCode, String msg, Exception exception) {
        return buildAndLog(status, ErrorCode.INTERNAL_ERROR_GENERIC, businessCode, msg, exception);
    }

    ResponseEntity<?> buildAndLog(HttpStatus status, ErrorCode errorCode, String msg, Exception exception) {
        return buildAndLog(status, errorCode, errorCode.getErrorBusinessCode(), msg, exception);
    }

    abstract ResponseEntity<?> buildAndLog(HttpStatus status, ErrorCode errorCode,
                                           ErrorBusinessCode businessCode,
                                           String msg, Exception exception);
}
