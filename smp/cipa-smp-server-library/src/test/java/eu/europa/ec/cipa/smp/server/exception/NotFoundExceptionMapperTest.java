package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class NotFoundExceptionMapperTest extends ParentExceptionTest{

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();
        NotFoundException exception = new NotFoundException("Test exception");

        // when
        Response response = mapper.toResponse(exception);

        // then
        String entity = (String) response.getEntity();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        String errorUniqueId = checkXmlError(entity, ErrorResponse.BusinessCode.NOT_FOUND, "Test exception");
        assertNotNull(errorUniqueId);
    }
}
