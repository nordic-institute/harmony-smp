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
    public void testBuild() throws ParserConfigurationException, IOException, SAXException {
        String result1 = ErrorResponseBuilder.newInstance().build();
        String result2 = ErrorResponseBuilder.newInstance().build();

        // test result 1
        String errorUniqueId1 = checkXmlError(result1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(result2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);

    }

    @Test
    public void testBuildWithBusinessCodeAndErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        String result1 = ErrorResponseBuilder.newInstance().setBusinessCode("BUSINESSCODE").setErrorDescription("Business Error Description").build();
        String result2 = ErrorResponseBuilder.newInstance().setBusinessCode("BUSINESSCODE").setErrorDescription("Business Error Description").build();

        final String STR_BUSINESS_CODE = "BUSINESSCODE";
        final String STR_ERROR_DESCRIPTION = "Business Error Description";

        // test result 1
        String errorUniqueId1 = checkXmlError(result1, STR_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(result2, STR_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);
    }

    @Test
    public void testSetBusinessCode() throws ParserConfigurationException, IOException, SAXException {
        final String STR_BUSINESS_CODE = "BUSINESSCODE";
        String result = ErrorResponseBuilder.newInstance().setBusinessCode(STR_BUSINESS_CODE).build();

        String errorUniqueId = checkXmlError(result, STR_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testSetErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        final String STR_ERROR_DESCRIPTION = "Business Error Description";
        String result = ErrorResponseBuilder.newInstance().setErrorDescription(STR_ERROR_DESCRIPTION).build();

        String errorUniqueId = checkXmlError(result, DEFAULT_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testBuildWithStatus() throws IOException, SAXException, ParserConfigurationException {
        Response result = ErrorResponseBuilder.newInstance().build(Response.Status.BAD_REQUEST);

        assertNotNull(result);
        String entity = (String) result.getEntity();
        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());

    }
}
