package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
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
public class ErrorResponseBuilderTest {

    private final String DEFAULT_BUSINESS_CODE = "TECHNICAL";
    private final String DEFAULT_ERROR_DESCRIPTION = "Unexpected technical error occurred.";

    private final Pattern PATTERN = Pattern.compile(".*?(:).*?(:).*?(:)([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    private String checkXmlError(String xmlResponse, String businessCode, String errorDescription) throws ParserConfigurationException, IOException, SAXException {
        assertNotNull(xmlResponse);
        assertTrue(xmlResponse.length() > 0);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
        Element docElement = doc.getDocumentElement();
        NodeList childs = docElement.getChildNodes();
        Node businessCodeNode = childs.item(0);
        Node errorDescriptionNode = childs.item(1);
        Node errorUniqueIdNode = childs.item(2);
        assertEquals("ErrorResponse", docElement.getNodeName());
        assertEquals("BusinessCode", businessCodeNode.getNodeName());
        assertEquals(businessCode, businessCodeNode.getTextContent());
        assertEquals("ErrorDescription", errorDescriptionNode.getNodeName());
        assertEquals(errorDescription, errorDescriptionNode.getTextContent());
        assertEquals("ErrorUniqueId", errorUniqueIdNode.getNodeName());

        String errorUniqueId = null;
        Matcher matcher = PATTERN.matcher(errorUniqueIdNode.getTextContent());
        if (matcher.find()) {
            errorUniqueId = matcher.group(4);
        }
        return errorUniqueId;
    }

    @Test
    public void testDifferentErrorIds() throws ParserConfigurationException, IOException, SAXException {
        Response result1 = ErrorResponseBuilder.status().build();
        Response result2 = ErrorResponseBuilder.status().build();

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
        Response result1 = ErrorResponseBuilder.status().businessCode("BUSINESSCODE").errorDescription("Business Error Description").build();
        Response result2 = ErrorResponseBuilder.status().businessCode("BUSINESSCODE").errorDescription("Business Error Description").build();

        String entity1 = (String) result1.getEntity();
        String entity2 = (String) result2.getEntity();

        final String STR_BUSINESS_CODE = "BUSINESSCODE";
        final String STR_ERROR_DESCRIPTION = "Business Error Description";

        // test result 1
        String errorUniqueId1 = checkXmlError(entity1, STR_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(entity2, STR_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);
    }

    @Test
    public void testBusinessCode() throws ParserConfigurationException, IOException, SAXException {
        final String STR_BUSINESS_CODE = "BUSINESSCODE";
        Response result = ErrorResponseBuilder.status().businessCode(STR_BUSINESS_CODE).build();

        String entity = (String) result.getEntity();

        String errorUniqueId = checkXmlError(entity, STR_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        final String STR_ERROR_DESCRIPTION = "Business Error Description";
        Response result = ErrorResponseBuilder.status().errorDescription(STR_ERROR_DESCRIPTION).build();

        String entity = (String) result.getEntity();

        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testBuildWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result = ErrorResponseBuilder.status(Response.Status.BAD_REQUEST).build();

        assertNotNull(result);
        String entity = (String) result.getEntity();
        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
    }

    @Test
    public void testBuildTwoInstancesWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result1 = ErrorResponseBuilder.status(Response.Status.BAD_REQUEST).build();
        Response result2 = ErrorResponseBuilder.status().build();

        assertNotNull(result1);
        String entity1 = (String) result1.getEntity();
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId1);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result1.getStatus());

        assertNotNull(result2);
        String entity2 = (String) result2.getEntity();
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId2);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result2.getStatus());
    }

}
