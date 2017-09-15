/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.error;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import eu.europa.ec.edelivery.error.exceptions.BadRequestException;
import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static eu.europa.ec.edelivery.error.ErrorBusinessCode.*;
import static java.lang.String.format;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


/**
 * Created by gutowpa on 14/09/2017.
 */
@RestControllerAdvice
public class ErrorMappingControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(getCallingClass());

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException ex) {
        ResponseEntity response = buildAndWarn(INTERNAL_SERVER_ERROR, TECHNICAL, "Unexpected technical error occurred.", ex);
        log.error("Unhandled exception ocured, unique ID: "+((ErrorResponse) response.getBody()).getErrorUniqueId(), ex);
        return response;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(BadRequestException ex) {
        return buildAndWarn(BAD_REQUEST, ex.getErrorBusinessCode(), ex.getMessage(), ex);
    }

    @ExceptionHandler(MalformedIdentifierException.class)
    public ResponseEntity handleMalformedIdentifierException(MalformedIdentifierException ex) {
        return buildAndWarn(BAD_REQUEST, FORMAT_ERROR, ex.getMessage(), ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException ex) {
        return buildAndWarn(NOT_FOUND, ErrorBusinessCode.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleUnauthorizedException(UnauthorizedException ex) {
        return buildAndWarn(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED, ex.getMessage(), ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException ex) {
        return buildAndWarn(UNAUTHORIZED, ErrorBusinessCode.UNAUTHORIZED, ex.getMessage(), ex);
    }

    @ExceptionHandler(UnknownUserException.class)
    public ResponseEntity handleUnknownUserException(UnknownUserException ex) {
        return buildAndWarn(BAD_REQUEST, USER_NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(XmlParsingException.class)
    public ResponseEntity handleXmlParsingException(XmlParsingException ex) {
        return buildAndWarn(BAD_REQUEST, XSD_INVALID, ex.getMessage(), ex);
    }

    @ExceptionHandler(XmlInvalidAgainstSchemaException.class)
    public ResponseEntity handleXmlInvalidAgainstSchemaException(XmlInvalidAgainstSchemaException ex) {
        return buildAndWarn(BAD_REQUEST, XSD_INVALID , ex.getMessage(), ex);
    }


    private ResponseEntity buildAndWarn(HttpStatus status, ErrorBusinessCode businessCode, String msg, Exception exception) {
        ResponseEntity response = ErrorResponseBuilder.status(status)
                .businessCode(businessCode)
                .errorDescription(msg)
                .build();

        String errorUniqueId = ((ErrorResponse) response.getBody()).getErrorUniqueId();
        String logMsg = format("Error unique ID: %s", errorUniqueId);

        log.warn(logMsg, exception);
        return response;
    }
    
}
