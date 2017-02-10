package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.mappers.RuntimeExceptionMapper;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.TECHNICAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class RuntimeExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        RuntimeExceptionMapper mapper = new RuntimeExceptionMapper();
        RuntimeException exception = new RuntimeException("Test exceptions");

        // when
        Response response = mapper.toResponse(exception);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String errorUniqueId = checkXmlError(entity, TECHNICAL, "Technical error occurred: Test exceptions");
        assertNotNull(errorUniqueId);
    }
}
