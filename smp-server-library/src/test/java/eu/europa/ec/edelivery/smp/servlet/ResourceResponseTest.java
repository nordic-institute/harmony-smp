package eu.europa.ec.edelivery.smp.servlet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceResponseTest {

    HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
    ResourceResponse testInstance = new ResourceResponse(mockHttpServletResponse);

    @Test
    void testGetHttpStatus() {
        int httpStatus = 200;
        Mockito.when(mockHttpServletResponse.getStatus()).thenReturn(httpStatus);
        int result = testInstance.getHttpStatus();

        assertEquals(httpStatus, result);
    }

    @Test
    void testSetHttpStatus() {
        int httpStatus = 200;
        testInstance.setHttpStatus(httpStatus);

        Mockito.verify(mockHttpServletResponse).setStatus(httpStatus);
    }

    @Test
    void testGetMimeType() {
        String mimeType = "mockMimeType";
        Mockito.when(mockHttpServletResponse.getContentType()).thenReturn(mimeType);
        String result = testInstance.getMimeType();

        assertEquals(mimeType, result);
    }

    @Test
    void testSetContentType() {
        String mimeType = "mockMimeType";
        testInstance.setContentType(mimeType);

        Mockito.verify(mockHttpServletResponse).setContentType(mimeType);
    }

    @Test
    void testGetHttpHeader() {
        String name = "mockName";
        String value = "mockValue";
        Mockito.when(mockHttpServletResponse.getHeader(name)).thenReturn(value);
        String result = testInstance.getHttpHeader(name);

        assertEquals(value, result);
    }

    @Test
    void testSetHttpHeader() {
        String name = "mockName";
        String value = "mockValue";
        testInstance.setHttpHeader(name, value);

        Mockito.verify(mockHttpServletResponse).setHeader(name, value);
    }

    @Test
    void testGetOutputStream() throws IOException {
        testInstance.getOutputStream();

        Mockito.verify(mockHttpServletResponse).getOutputStream();
    }
}
