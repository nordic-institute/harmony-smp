package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.errors.mappers.UnauthorizedExceptionMapper;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnauthorizedExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();
        UnauthorizedException exception = new UnauthorizedException("Test exceptions");

        // when
        Response response = mapper.toResponse(exception);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        String errorUniqueId = checkXmlError(entity, UNAUTHORIZED, "Test exceptions");
        assertNotNull(errorUniqueId);
    }
}
