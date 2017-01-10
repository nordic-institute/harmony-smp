package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by migueti on 06/01/2017.
 */
public class ErrorResponseBuilderTest {

    @Test
    public void testBuild() throws IOException, ParserConfigurationException, SAXException {
        String result = ErrorResponseBuilder.build();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(result.getBytes("UTF-8")));
        assertEquals("ErrorResponse", doc.getDocumentElement().getNodeName());
        assertEquals("BusinessCode", doc.getDocumentElement().getChildNodes().item(0).getNodeName());
        assertEquals("TECHNICAL", doc.getDocumentElement().getChildNodes().item(0).getTextContent());
        assertEquals("ErrorDescription", doc.getDocumentElement().getChildNodes().item(1).getNodeName());
        assertEquals("Unexpected technical error occurred.", doc.getDocumentElement().getChildNodes().item(1).getTextContent());
        assertEquals("ErrorUniqueId", doc.getDocumentElement().getChildNodes().item(2).getNodeName());
        // we will not check text content for error unique id because we don't know the timestamp
    }
}
