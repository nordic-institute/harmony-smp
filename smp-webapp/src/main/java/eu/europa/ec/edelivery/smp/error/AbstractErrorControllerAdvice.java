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
import eu.europa.ec.edelivery.smp.error.exceptions.SMPResponseStatusException;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode.TECHNICAL;
import static org.springframework.http.HttpStatus.*;

abstract class AbstractErrorControllerAdvice {

    static final Logger LOG = LoggerFactory.getLogger(AbstractErrorControllerAdvice.class);

    public ResponseEntity handleRuntimeException(RuntimeException runtimeException) {
        ResponseEntity response;
        if (runtimeException instanceof SMPRuntimeException) {
            SMPRuntimeException ex = (SMPRuntimeException)runtimeException;
            response = buildAndLog(HttpStatus.resolve(ex.getErrorCode().getHttpCode()), ex.getErrorCode().getErrorBusinessCode(), ex.getMessage(), ex);
        } else if (runtimeException instanceof SMPResponseStatusException ){
            SMPResponseStatusException ex = (SMPResponseStatusException)runtimeException;
            response = buildAndLog(ex.getStatus(), ex.getErrorBusinessCode(), ex.getMessage(), ex);
        } else if (runtimeException instanceof AuthenticationException ){
            AuthenticationException ex = (AuthenticationException)runtimeException;
            response = buildAndLog(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED, ex.getMessage(), ex);
        }else if (runtimeException instanceof AccessDeniedException){
            AccessDeniedException ex = (AccessDeniedException)runtimeException;
            response = buildAndLog(FORBIDDEN, ErrorBusinessCode.UNAUTHORIZED, ex.getMessage(), ex);
        }else if (runtimeException instanceof BadRequestException){
            BadRequestException ex = (BadRequestException)runtimeException;
            response = buildAndLog(UNPROCESSABLE_ENTITY, ex.getErrorBusinessCode(), ex.getMessage(), ex);
        }
        else if (runtimeException instanceof MalformedIdentifierException){
            MalformedIdentifierException ex = (MalformedIdentifierException)runtimeException;
            response = buildAndLog(BAD_REQUEST, ErrorBusinessCode.FORMAT_ERROR, ex.getMessage(), ex);
        }
        else {
            response = buildAndLog(INTERNAL_SERVER_ERROR, TECHNICAL, "Unexpected technical error occurred.", runtimeException);
        }


        String errorCodeId = response.getBody() instanceof  ErrorResponseRO?
                ((ErrorResponseRO) response.getBody()).getErrorUniqueId(): null;


        LOG.error("Unhandled exception occurred, unique ID: [{}]", errorCodeId, runtimeException);
        return response;
    }

    abstract ResponseEntity buildAndLog(HttpStatus status, ErrorBusinessCode businessCode, String msg, Exception exception);
}
