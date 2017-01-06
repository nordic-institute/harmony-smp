package eu.europa.ec.cipa.smp.server.conversion;

import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverter {

    private static final String NS = "http://busdox.org/serviceMetadata/publishing/1.0/";
    private static final String DOC_SIGNED_SERVICE_METADATA_EMPTY = "<SignedServiceMetadata xmlns=\""+NS+"\"/>";

    static Unmarshaller jaxbUnmarshaller;

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller != null) {
            return jaxbUnmarshaller;
        }
        synchronized (ServiceMetadataConverter.class) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceMetadataType.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller;
        }
    }

    public static Document toSignedServiceMetadatada(String serviceMetadataXml) throws ParserConfigurationException, SAXException, IOException {
        Document docServiceMetadata = parse(serviceMetadataXml);
        Document root = parse(DOC_SIGNED_SERVICE_METADATA_EMPTY);
        Node imported = root.importNode(docServiceMetadata.getDocumentElement(), true);
        root.getDocumentElement().appendChild(imported);
        return root;
    }

    private static Document parse(String serviceMetadataXml) throws SAXException, IOException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(serviceMetadataXml.getBytes());
        return getDocumentBuilder().parse(inputStream);
    }

    public static ServiceMetadataType unmarshall(String serviceMetadataXml) throws JAXBException, ParserConfigurationException, IOException, SAXException {
        Document serviceMetadataDoc = parse(serviceMetadataXml);
        ServiceMetadataType serviceMetadata = getUnmarshaller().unmarshal(serviceMetadataDoc, ServiceMetadataType.class).getValue();
        return serviceMetadata;
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder();
    }

    /*
    public static String extractServiceMetadataXmlToString(Document serviceMetadataDoc) throws JAXBException, TransformerException, UnsupportedEncodingException {
        NodeList elements = serviceMetadataDoc.getElementsByTagNameNS(NS, "ServiceInformation");
        if (elements.getLength() > 0) {
            Element element = (Element) elements.item(0);
            String elementStr = marshall(element);
            return elementStr;
        }else{
            return null;
        }

    }
    */

    /*
    private static String marshall(Node doc) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trans.transform(new DOMSource(doc), new StreamResult(stream));
        return stream.toString("UTF-8");
    }
    */
}
