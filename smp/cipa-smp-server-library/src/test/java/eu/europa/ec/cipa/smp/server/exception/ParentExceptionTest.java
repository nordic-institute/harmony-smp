package eu.europa.ec.cipa.smp.server.exception;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by migueti on 16/01/2017.
 */
abstract class ParentExceptionTest {

    private final Pattern PATTERN = Pattern.compile(".*?(:).*?(:).*?(:)([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    String checkXmlError(String xmlResponse, ErrorResponse.BusinessCode businessCode, String errorDescription) throws ParserConfigurationException, IOException, SAXException {
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
        assertEquals(businessCode.toString(), businessCodeNode.getTextContent());
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
}
