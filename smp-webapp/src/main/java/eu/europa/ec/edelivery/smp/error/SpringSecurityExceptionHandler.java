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

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Created by gutowpa on 27/01/2017.
 */

public class SpringSecurityExceptionHandler extends BasicAuthenticationEntryPoint implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityExceptionHandler.class);

    public SpringSecurityExceptionHandler() {
        this.setRealmName("SMPSecurityRealm");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String errorMsg = authException.getMessage();
        if (authException instanceof BadCredentialsException) {
            errorMsg += " - Provided username/password or client certificate are invalid";
        }
        handle(response, authException, errorMsg);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        handle(response, accessDeniedException, accessDeniedException.getMessage());
    }

    private void handle(HttpServletResponse response, RuntimeException exception, String errorMsg) throws IOException {
        ResponseEntity respEntity = buildAndWarn(exception, errorMsg);
        String errorBody = marshall((ErrorResponse) respEntity.getBody());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(TEXT_HTML_VALUE);
        response.getOutputStream().print(errorBody);
    }

    private ResponseEntity buildAndWarn(RuntimeException exception, String errorMsg) {
        ResponseEntity response = ErrorResponseBuilder.status(UNAUTHORIZED)
                .businessCode(ErrorBusinessCode.UNAUTHORIZED)
                .errorDescription(errorMsg)
                .build();

        String errorUniqueId = ((ErrorResponse) response.getBody()).getErrorUniqueId();
        String logMsg = format("Error unique ID: %s", errorUniqueId);
        log.warn("Security error:[{}] with [{}].", errorMsg, logMsg);
        log.debug(logMsg, exception);
        return response;
    }

    private static String marshall(ErrorResponse errorResponse) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(ErrorResponse.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(errorResponse, sw);
            return sw.toString();
        } catch (JAXBException e) {
            return e.getMessage();
        }
    }

}
