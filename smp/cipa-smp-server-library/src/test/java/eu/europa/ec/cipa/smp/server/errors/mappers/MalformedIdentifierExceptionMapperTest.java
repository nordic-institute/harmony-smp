package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.mappers.MalformedIdentifierExceptionMapper;
import eu.europa.ec.smp.api.MalformedIdentifierException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.FORMAT_ERROR;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 19/01/2017.
 */
public class MalformedIdentifierExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        MalformedIdentifierExceptionMapper mapper = new MalformedIdentifierExceptionMapper();
        Exception exception = new Exception();
        MalformedIdentifierException malformedException = new MalformedIdentifierException("TEST_ID", exception);

        // when
        Response response = mapper.toResponse(malformedException);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());

        String errorUniqueId = checkXmlError(entity, FORMAT_ERROR, "Malformed identifier, scheme and id should be delimited by double colon: TEST_ID");
        assertNotNull(errorUniqueId);
    }
}
