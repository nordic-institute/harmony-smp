package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.errors.mappers.NotFoundExceptionMapper;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class NotFoundExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();
        NotFoundException exception = new NotFoundException("Test exceptions");

        // when
        Response response = mapper.toResponse(exception);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());

        String errorUniqueId = checkXmlError(entity, NOT_FOUND, "Test exceptions");
        assertNotNull(errorUniqueId);
    }
}
