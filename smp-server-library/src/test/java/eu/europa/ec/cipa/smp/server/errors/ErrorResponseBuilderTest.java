package eu.europa.ec.cipa.smp.server.errors;

import ec.services.smp._1.ErrorResponse;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.*;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;

/**
 * Created by migueti on 06/01/2017.
 */
public class ErrorResponseBuilderTest extends ParentExceptionTest {

    private final ErrorBusinessCode DEFAULT_BUSINESS_CODE = TECHNICAL;
    private final String DEFAULT_ERROR_DESCRIPTION = "Unexpected technical error occurred.";

    @Test
    public void testDifferentErrorIds() throws ParserConfigurationException, IOException, SAXException {
        Response result1 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();
        Response result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();

        ErrorResponse entity1 = (ErrorResponse) result1.getEntity();
        ErrorResponse entity2 = (ErrorResponse) result2.getEntity();

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
        Response result1 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(OTHER_ERROR).errorDescription("Business Error Description").build();
        Response result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(OTHER_ERROR).errorDescription("Business Error Description").build();

        ErrorResponse entity1 = (ErrorResponse) result1.getEntity();
        ErrorResponse entity2 = (ErrorResponse) result2.getEntity();

        final ErrorBusinessCode BC_BUSINESS_CODE = OTHER_ERROR;
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
        final ErrorBusinessCode BC_BUSINESS_CODE = MISSING_FIELD;
        Response result = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(BC_BUSINESS_CODE).build();

        ErrorResponse entity = (ErrorResponse) result.getEntity();

        String errorUniqueId = checkXmlError(entity, BC_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        final String STR_ERROR_DESCRIPTION = "Business Error Description";
        Response result = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).errorDescription(STR_ERROR_DESCRIPTION).build();

        ErrorResponse entity = (ErrorResponse) result.getEntity();

        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testBuildWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result = ErrorResponseBuilder.status(BAD_REQUEST).build();

        assertNotNull(result);
        ErrorResponse entity = (ErrorResponse) result.getEntity();
        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId);
        assertEquals(BAD_REQUEST.getStatusCode(), result.getStatus());
    }

    @Test
    public void testBuildTwoInstancesWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result1 = ErrorResponseBuilder.status(BAD_REQUEST).build();
        Response result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();

        assertNotNull(result1);
        ErrorResponse entity1 = (ErrorResponse) result1.getEntity();
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId1);
        assertEquals(BAD_REQUEST.getStatusCode(), result1.getStatus());

        assertNotNull(result2);
        ErrorResponse entity2 = (ErrorResponse) result2.getEntity();
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId2);
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), result2.getStatus());
    }

}
