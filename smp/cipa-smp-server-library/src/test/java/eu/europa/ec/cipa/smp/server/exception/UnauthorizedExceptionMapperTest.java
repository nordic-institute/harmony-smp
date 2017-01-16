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
public class UnauthorizedExceptionMapperTest extends ParentExceptionTest{

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();
        UnauthorizedException exception = new UnauthorizedException("Test exception");

        // when
        Response response = mapper.toResponse(exception);

        // then
        String entity = (String) response.getEntity();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        String errorUniqueId = checkXmlError(entity, ErrorResponse.BusinessCode.UNAUTHORIZED, "Test exception");
        assertNotNull(errorUniqueId);
    }
}
