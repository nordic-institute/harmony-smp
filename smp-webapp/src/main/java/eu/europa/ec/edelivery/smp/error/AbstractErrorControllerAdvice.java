package eu.europa.ec.edelivery.smp.error;


import eu.europa.ec.edelivery.smp.data.ui.exceptions.ErrorResponseRO;
import eu.europa.ec.edelivery.smp.error.exceptions.SMPResponseStatusException;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.MalformedIdentifierException;
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
            response = buildAndLog(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED, ex.getMessage(), ex);
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
