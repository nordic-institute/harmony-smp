package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverter {

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String DOC_SIGNED_SERVICE_METADATA_EMPTY = "<SignedServiceMetadata xmlns=\""+NS+"\"/>";

    static Unmarshaller jaxbUnmarshaller;

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller != null) {
            return jaxbUnmarshaller;
        }
        synchronized (ServiceMetadataConverter.class) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceMetadata.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller;
        }
    }

    public static Document toSignedServiceMetadatadaDocument(String serviceMetadataXml)  {
        try {
            Document docServiceMetadata = parse(serviceMetadataXml);
            Document root = parse(DOC_SIGNED_SERVICE_METADATA_EMPTY);
            Node imported = root.importNode(docServiceMetadata.getDocumentElement(), true);
            root.getDocumentElement().appendChild(imported);
            return root;
        }catch(ParserConfigurationException | SAXException | IOException e){
            throw new XmlParsingException(e);
        }
    }

    public static ServiceMetadata unmarshal(String serviceMetadataXml){
        try {
            Document serviceMetadataDoc = parse(serviceMetadataXml);
            ServiceMetadata serviceMetadata = getUnmarshaller().unmarshal(serviceMetadataDoc, ServiceMetadata.class).getValue();
            return serviceMetadata;
        } catch (SAXException | IOException | ParserConfigurationException | JAXBException e) {
            throw new XmlParsingException(e);
        }
    }

    private static Document parse(String serviceMetadataXml) throws SAXException, IOException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(serviceMetadataXml.getBytes());
        return getDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder();
    }

}
