package utils;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XMLUtils {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    Document doc;

    public XMLUtils(String xmlStr) throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setValidating(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        try {
            doc = dBuilder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            log.error("Error while parsing xml", e);
        }
    }

    public String printDoc() throws IOException {
        OutputFormat format = new OutputFormat(doc);
        format.setIndenting(false);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);
        return out.toString();
    }

    public boolean isNodePresent(String nodeName) {
        NodeList nList = doc.getElementsByTagName(nodeName);
        return nList.getLength() != 0;
    }

    public void setContextValueForNode(String nodeName, String attributeValue) {
        NodeList nList = doc.getElementsByTagName(nodeName);
        Node nNode = nList.item(0);
        nNode.setTextContent(attributeValue);
    }

    public void setAttributeValueForNode(String nodeName, String attributeName, String attributeValue) {
        NodeList nList = doc.getElementsByTagName(nodeName);
        Node nNode = nList.item(0);
        ((ElementImpl) nNode).setAttribute(attributeName, attributeValue);
    }

    public String getNodeValue(String nodeName) {
        NodeList nList = doc.getElementsByTagName(nodeName);
        if (nList.getLength() > 0) {
            return nList.item(0).getTextContent();
        }
        return null;
    }

    public String getAttributeValueForNode(String nodeName, String attributeName) {
        NodeList nList = doc.getElementsByTagName(nodeName);
        Node nNode = nList.item(0);
        return ((DeferredElementNSImpl) nNode).getAttribute(attributeName);
    }

    public void addNewNode(String parentNode, String newNode, String newNodeText) {
        Element newElement = doc.createElement(newNode);
        newElement.appendChild(doc.createTextNode(newNodeText));
        Node parentN = doc.getElementsByTagName(parentNode).item(0); // Or find the specific parent node you want
        parentN.appendChild(newElement);

    }
}
