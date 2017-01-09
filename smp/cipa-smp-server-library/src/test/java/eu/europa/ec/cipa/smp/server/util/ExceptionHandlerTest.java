package eu.europa.ec.cipa.smp.server.util;

import com.sun.jersey.api.NotFoundException;
import eu.europa.ec.cipa.smp.server.fault.NotFoundFault;
import eu.europa.ec.cipa.smp.server.hook.PostRegistrationFilter;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by migueti on 06/01/2017.
 */
public class ExceptionHandlerTest {

    @Test(expected = NotFoundFault.class)
    public void testHandleException() throws Exception {
        NotFoundException exception = Mockito.mock(NotFoundException.class);
        ExceptionHandler.handleException(exception);
    }

    @Test
    public void testHandleInexistentException() throws Exception {
        ExceptionHandler.handleException(null);
    }

    @Test
    public void testBuildResponse() {
        NotFoundException exception = Mockito.mock(NotFoundException.class);
        Response received = ExceptionHandler.buildResponse(exception);
        Response expected = Response.serverError().build();
        assertEquals(expected.getEntity(), received.getEntity());
        assertEquals(expected.getMetadata(), received.getMetadata());
        assertEquals(expected.getStatus(), received.getStatus());
        assertEquals(expected.getClass(), received.getClass());
    }

    @Test
    public void testBuildXmlresponse() throws IOException {
        HttpServletResponse aHttpResponse = Mockito.mock(HttpServletResponse.class);
        PostRegistrationFilter.HttpServletResponseWrapperWithStatus httpResponse = new PostRegistrationFilter.HttpServletResponseWrapperWithStatus(aHttpResponse);
        OutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        when(httpResponse.getWriter()).thenReturn(writer);
        ExceptionHandler.buildXmlResponse(httpResponse);
        assertTrue(stream.toString().length() > 0);
    }
}
