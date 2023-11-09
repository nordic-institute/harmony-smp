package utils;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc;

    public XMLUtils(String xmlStr) throws ParserConfigurationException {
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

//    public boolean isNodeContaingValue(String nodeName) {
//
//        NodeList nList = doc.getChildNodes();
//        for(Integer i=0; i = nList.getLength()){
//
//        }
//
//
//    }

}
