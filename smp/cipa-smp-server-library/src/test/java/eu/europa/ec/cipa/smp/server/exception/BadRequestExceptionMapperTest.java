package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class BadRequestExceptionMapperTest extends ParentExceptionTest{

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();
        BadRequestException exception = new BadRequestException(ErrorResponse.BusinessCode.WRONG_FIELD, "Test exception");

        // when
        Response response = mapper.toResponse(exception);

        // then
        String entity = (String) response.getEntity();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        String errorUniqueId = checkXmlError(entity, ErrorResponse.BusinessCode.WRONG_FIELD, "Test exception");
        assertNotNull(errorUniqueId);
    }
}
