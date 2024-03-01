/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.error.xml.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class SMPSecurityExceptionHandlerTest {

    SMPSecurityExceptionHandler testInstance = new SMPSecurityExceptionHandler();

    @Test
    void isUITRestRequestPublic() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn(ResourceConstants.CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT).when(request).getRequestURI();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertTrue(result);
    }

    @Test
    void isUITRestRequestInternal() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn("smp" + ResourceConstants.CONTEXT_PATH_INTERNAL_APPLICATION).when(request).getRequestURI();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertTrue(result);
    }

    @Test
    void isUITRestRequestSMPServiceEndpoint() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn("/smp").when(request).getContextPath();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertFalse(result);
    }


    @Test
    void marshallToXML() throws JAXBException {
        ErrorResponse error = ErrorResponseBuilder.status(UNAUTHORIZED)
                .businessCode(ErrorBusinessCode.UNAUTHORIZED)
                .errorDescription("Test error Message")
                .buildBody();
        // when
        String resultString = testInstance.marshallToXML(error);
        // then
        assertNotNull(resultString);
        //calling the unmarshall method
        ErrorResponse result = (ErrorResponse) JAXBContext.newInstance(ErrorResponse.class)
                .createUnmarshaller()
                .unmarshal(new StringReader(resultString));

        assertEquals(error.getBusinessCode(), result.getBusinessCode());
        assertEquals(error.getErrorDescription(), result.getErrorDescription());
        assertEquals(error.getErrorUniqueId(), result.getErrorUniqueId());
    }

    @Test
    void marshallToJSon() throws IOException {
        ErrorResponse error = ErrorResponseBuilder.status(UNAUTHORIZED)
                .businessCode(ErrorBusinessCode.UNAUTHORIZED)
                .errorDescription("Test json error Message")
                .buildBody();
        // when
        String resultString = testInstance.marshallToJSon(error);
        // then
        assertNotNull(resultString);
        //calling the unmarshall method
        ErrorResponse result = (new ObjectMapper()).readValue(resultString, ErrorResponse.class);

        assertEquals(error.getBusinessCode(), result.getBusinessCode());
        assertEquals(error.getErrorDescription(), result.getErrorDescription());
        assertEquals(error.getErrorUniqueId(), result.getErrorUniqueId());
    }
    @Test
    void marshallUIError() throws JsonProcessingException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn(ResourceConstants.CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT).when(request).getRequestURI();
        ErrorResponse error = ErrorResponseBuilder.status(UNAUTHORIZED)
                .businessCode(ErrorBusinessCode.UNAUTHORIZED)
                .errorDescription("Test error Message")
                .buildBody();

        String resultString = testInstance.marshall(error, request);
        // then
        assertNotNull(resultString);
        //calling the unmarshall method for JSON
        ErrorResponse result = (new ObjectMapper()).readValue(resultString, ErrorResponse.class);

        assertEquals(error.getBusinessCode(), result.getBusinessCode());
        assertEquals(error.getErrorDescription(), result.getErrorDescription());
        assertEquals(error.getErrorUniqueId(), result.getErrorUniqueId());
    }

    @Test
    void marshallXMLError() throws  JAXBException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn("/smp/test-test-test::0001:test").when(request).getRequestURI();
        ErrorResponse error = ErrorResponseBuilder.status(UNAUTHORIZED)
                .businessCode(ErrorBusinessCode.UNAUTHORIZED)
                .errorDescription("Test error Message")
                .buildBody();

        String resultString = testInstance.marshall(error, request);
        // then
        assertNotNull(resultString);
        //calling the unmarshall method for XML
        ErrorResponse result = (ErrorResponse) JAXBContext.newInstance(ErrorResponse.class)
                .createUnmarshaller()
                .unmarshal(new StringReader(resultString));

        assertEquals(error.getBusinessCode(), result.getBusinessCode());
        assertEquals(error.getErrorDescription(), result.getErrorDescription());
        assertEquals(error.getErrorUniqueId(), result.getErrorUniqueId());
    }
}
