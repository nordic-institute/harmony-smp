/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.error;

import eu.europa.ec.edelivery.smp.data.ui.exceptions.ErrorResponseRO;
import eu.europa.ec.edelivery.smp.error.exceptions.SMPResponseStatusException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.lang.String.format;


/**
 * Exception Handler for the UI package. Method returns JSON response objects.
 *
 * @author gutowpa
 * @author Joze Rihtarsic
 * @since 4.2
 */
@RestControllerAdvice("eu.europa.ec.edelivery.smp.ui.external")
public class UIErrorControllerAdvice extends AbstractErrorControllerAdvice {


    @Override
    @ExceptionHandler({BadCredentialsException.class, RuntimeException.class, SMPRuntimeException.class, SMPResponseStatusException.class, AuthenticationException.class,})
    public ResponseEntity handleRuntimeException(RuntimeException ex) {
        return super.handleRuntimeException(ex);
    }

    ResponseEntity buildAndLog(HttpStatus status, ErrorBusinessCode businessCode, String msg, Exception exception) {

        ResponseEntity response = ErrorResponseBuilder.status(status)
                .businessCode(businessCode)
                .errorDescription(msg)
                .buildJSon();

        String errorUniqueId = ((ErrorResponseRO) response.getBody()).getErrorUniqueId();
        String logMsg = format("UI Error unique ID: %s", errorUniqueId);
        LOG.warn(logMsg, exception);
        return response;
    }

}
