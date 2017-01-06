package eu.europa.ec.cipa.smp.server.util;

import org.apache.commons.io.IOUtils;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class XmlTestUtils {

    private static final String UTF_8 = "UTF-8";

    public static String loadDocumentAsString(String docResourcePath) throws IOException {
        InputStream inputStream = XmlTestUtils.class.getResourceAsStream(docResourcePath);
        return IOUtils.toString(inputStream, UTF_8);
    }

    public static String marshal(Node doc) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trans.transform(new DOMSource(doc), new StreamResult(stream));
        return stream.toString(UTF_8);
    }

    public static String marshall(ServiceMetadataType serviceMetadata) throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceMetadataType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        JAXBElement<ServiceMetadataType> jaxbServiceMetadata = new ObjectFactory().createServiceMetadata(serviceMetadata);
        jaxbMarshaller.marshal(jaxbServiceMetadata, sw);
        return sw.toString();
    }
}
