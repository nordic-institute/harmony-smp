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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.error.xml.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
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
 * SMPSecurityExceptionHandler
 *
 * @author gutowpa
 * @author Joze Rihtarsic
 * @since 3.0
 */
public class SMPSecurityExceptionHandler extends BasicAuthenticationEntryPoint implements AccessDeniedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SMPSecurityExceptionHandler.class);

    public SMPSecurityExceptionHandler() {
        this.setRealmName("SMPSecurityRealm");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String errorMsg = authException.getMessage();
        if (authException instanceof BadCredentialsException) {
            errorMsg += " - Provided username/password or client certificate are invalid";
        }
        handle(request, response, authException, errorMsg);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        handle(request, response, accessDeniedException, accessDeniedException.getMessage());
    }

    private void handle(HttpServletRequest request, HttpServletResponse response, RuntimeException exception, String errorMsg) throws IOException {
        ResponseEntity respEntity = buildAndWarn(exception, errorMsg);
        String errorBody = marshall((ErrorResponse) respEntity.getBody(), request);
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
        LOG.warn("Security error:[{}] with [{}].", errorMsg, logMsg);
        LOG.debug(logMsg, exception);
        return response;
    }

    /**
     * Method marshals the response. If the request endpoint is UI it marshal it to JSON else to XML format

     * @param errorResponse - the error to marshal
     * @param request - The incoming HTTP request for the error
     * @return string representation of the error
     */
    protected String marshall(ErrorResponse errorResponse, HttpServletRequest request) {
        return isUITRestRequest(request)? marshallToJSon(errorResponse):marshallToXML(errorResponse);
    }

    /**
     * Method validates if the request was submitted to UI or to "Oasis-SMP service" endpoint. If the endpoint is UI it returns
     * true.
     * @param request - HTTP request to SMP
     * @return true if request targets the UI.
     */
    protected boolean isUITRestRequest(HttpServletRequest request){
        String contextPath = request!=null?request.getRequestURI():null;
        boolean result  = StringUtils.isNotBlank(contextPath)
                && StringUtils.containsAny(contextPath, ResourceConstants.CONTEXT_PATH_PUBLIC,ResourceConstants.CONTEXT_PATH_INTERNAL);
        LOG.debug("Context path: [{}] is UI rest request: [{}]", contextPath, result);
        return result;
    }

    /**
     * Marshal ErrorResponse to XML format
     *
     * @param errorResponse
     * @return xml string representation of the Error
     */
    protected String marshallToXML(ErrorResponse errorResponse) {
        LOG.debug("Marshal error [{}] to XML format", errorResponse);
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(ErrorResponse.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(errorResponse, sw);
            return sw.toString();
        } catch (JAXBException e) {
            LOG.error("Error occurred while marshal the error [{}], code: [{}], desc [{}].", errorResponse.getBusinessCode(), errorResponse.getErrorUniqueId(), errorResponse.getErrorDescription());
        }
        return null;
    }

    /**
     * Marshal ErrorResponse to JSON format
     *
     * @param errorResponse
     * @return json string representation of the Error
     */
    protected String marshallToJSon(ErrorResponse errorResponse) {
        LOG.debug("Marshal error [{}] to JSON format", errorResponse);
        try {
            return new ObjectMapper().writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            LOG.error("Error occurred while marshal the error [{}], code: [{}], desc [{}].", errorResponse.getBusinessCode(), errorResponse.getErrorUniqueId(), errorResponse.getErrorDescription());
        }
        return null;
    }

}
