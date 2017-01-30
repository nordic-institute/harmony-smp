package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.w3c.dom.Document;
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
 * Created by migueti on 26/01/2017.
 */
public class ServiceGroupConverter {

    static Unmarshaller jaxbUnmarshaller;

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        if(jaxbUnmarshaller != null) {
            return jaxbUnmarshaller;
        }

        synchronized (ServiceGroupConverter.class) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller;
        }
    }

    public static ServiceGroup unmarshal(String serviceGroupXml) throws JAXBException {
        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new XmlParsingException(e);
        }
    }

    private static Document parse(String serviceGroupXml) throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(serviceGroupXml.getBytes());
        return getDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }
}
