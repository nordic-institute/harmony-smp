package eu.europa.ec.cipa.smp.server.errors.mappers;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.cipa.smp.server.errors.ParentExceptionTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.XSD_INVALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 20/01/2017.
 */
public class XmlParsingExceptionMapperTest extends ParentExceptionTest {

    @Test
    public void testToResponse() throws IOException, SAXException, ParserConfigurationException {
        // given
        XmlParsingExceptionMapper mapper = new XmlParsingExceptionMapper();
        Exception parentException = new Exception("Parent");
        XmlParsingException exception = new XmlParsingException(parentException);

        // when
        Response response = mapper.toResponse(exception);

        // then
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        String errorUniqueId = checkXmlError(entity, XSD_INVALID, "java.lang.Exception: Parent");
        assertNotNull(errorUniqueId);
    }
}
