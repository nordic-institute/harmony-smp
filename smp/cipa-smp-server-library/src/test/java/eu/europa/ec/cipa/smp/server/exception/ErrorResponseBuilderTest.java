package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by migueti on 06/01/2017.
 */
public class ErrorResponseBuilderTest extends ParentExceptionTest {

    private final ErrorResponse.BusinessCode DEFAULT_BUSINESS_CODE = ErrorResponse.BusinessCode.TECHNICAL;
    private final String DEFAULT_ERROR_DESCRIPTION = "Unexpected technical error occurred.";

    @Test
    public void testDifferentErrorIds() throws ParserConfigurationException, IOException, SAXException {
        Response result1 = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).build();
        Response result2 = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).build();

        String entity1 = (String) result1.getEntity();
        String entity2 = (String) result2.getEntity();

        // test result 1
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);

    }

    @Test
    public void testDifferentErrorIdsWithBusinessCodeAndErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        Response result1 = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).businessCode(ErrorResponse.BusinessCode.OTHER_ERROR).errorDescription("Business Error Description").build();
        Response result2 = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).businessCode(ErrorResponse.BusinessCode.OTHER_ERROR).errorDescription("Business Error Description").build();

        String entity1 = (String) result1.getEntity();
        String entity2 = (String) result2.getEntity();

        final ErrorResponse.BusinessCode BC_BUSINESS_CODE = ErrorResponse.BusinessCode.OTHER_ERROR;
        final String STR_ERROR_DESCRIPTION = "Business Error Description";

        // test result 1
        String errorUniqueId1 = checkXmlError(entity1, BC_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(entity2, BC_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);
    }

    @Test
    public void testBusinessCode() throws ParserConfigurationException, IOException, SAXException {
        final ErrorResponse.BusinessCode BC_BUSINESS_CODE = ErrorResponse.BusinessCode.MISSING_FIELD;
        Response result = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).businessCode(BC_BUSINESS_CODE).build();

        String entity = (String) result.getEntity();

        String errorUniqueId = checkXmlError(entity, BC_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        final String STR_ERROR_DESCRIPTION = "Business Error Description";
        Response result = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).errorDescription(STR_ERROR_DESCRIPTION).build();

        String entity = (String) result.getEntity();

        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testBuildWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result = ErrorResponseBuilder.status(Status.BAD_REQUEST).build();

        assertNotNull(result);
        String entity = (String) result.getEntity();
        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
    }

    @Test
    public void testBuildTwoInstancesWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result1 = ErrorResponseBuilder.status(Status.BAD_REQUEST).build();
        Response result2 = ErrorResponseBuilder.status(Status.INTERNAL_SERVER_ERROR).build();

        assertNotNull(result1);
        String entity1 = (String) result1.getEntity();
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId1);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), result1.getStatus());

        assertNotNull(result2);
        String entity2 = (String) result2.getEntity();
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId2);
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), result2.getStatus());
    }

}
