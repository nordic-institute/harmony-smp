package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.errors.mappers.UnknownUserExceptionMapper;
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
public class UnknownUserExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        UnknownUserExceptionMapper mapper = new UnknownUserExceptionMapper();
        UnknownUserException exception = new UnknownUserException("testUser");

        // when
        Response response = mapper.toResponse(exception);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        String errorUniqueId = checkXmlError(entity, TECHNICAL, "Unknown user 'testUser'");
        assertNotNull(errorUniqueId);
    }
}