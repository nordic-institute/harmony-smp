package eu.europa.ec.edelivery.smp.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.error.xml.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class SMPSecurityExceptionHandlerTest {

    SMPSecurityExceptionHandler testInstance = new SMPSecurityExceptionHandler();

    @Test
    public void isUITRestRequestPublic() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn(ResourceConstants.CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT).when(request).getRequestURI();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertTrue(result);
    }

    @Test
    public void isUITRestRequestInternal() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn("smp" + ResourceConstants.CONTEXT_PATH_INTERNAL_APPLICATION).when(request).getRequestURI();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertTrue(result);
    }

    @Test
    public void isUITRestRequestSMPServiceEndpoint() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Mockito.doReturn("/smp").when(request).getContextPath();
        // when
        boolean result = testInstance.isUITRestRequest(request);
        // then
        assertFalse(result);
    }


    @Test
    public void marshallToXML() throws JAXBException {
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
    public void marshallToJSon() throws IOException {
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
    public void marshallUIError() throws JsonProcessingException {
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
    public void marshallXMLError() throws  JAXBException {
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
