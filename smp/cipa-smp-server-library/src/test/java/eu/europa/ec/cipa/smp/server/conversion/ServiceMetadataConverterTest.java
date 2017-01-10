package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.exception.XmlParsingException;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverterTest {

    private static final String NS = "http://busdox.org/serviceMetadata/publishing/1.0/";
    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/conversion/";

    @Test
    public void testUnmarshalServiceInformation() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        ServiceMetadataType serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getRedirect());
        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();
        assertNotNull(serviceInformation);
        ServiceEndpointList serviceEndpointList = serviceInformation.getProcessList().getProcess().get(0).getServiceEndpointList();
        String serviceDescription1 = serviceEndpointList.getEndpointAtIndex(0).getServiceDescription();
        String serviceDescription2 = serviceEndpointList.getEndpointAtIndex(1).getServiceDescription();
        assertEquals("invoice service AS2", serviceDescription1);
        assertEquals("invoice service", serviceDescription2);
    }

    @Test
    public void testUnmarshalRedirect() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithRedirect.xml");

        //when
        ServiceMetadataType serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getServiceInformation());
        RedirectType redirect = serviceMetadata.getRedirect();
        assertNotNull(redirect);
        assertEquals("http://poland.pl", redirect.getHref());
        assertEquals("SAMPLE CERTIFICATE VALUE", redirect.getCertificateUID());
    }

    @Test(expected = XmlParsingException.class)
    public void testUnmarshalMalformedInput() throws ParserConfigurationException, IOException, SAXException, JAXBException {
        //when
        ServiceMetadataConverter.unmarshal("this is malformed XML body");
    }

    @Test
    public void testUnmarshalMissingMandatoryFields() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataMissingMandatoryFields.xml");

        //when
        ServiceMetadataType serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        //Parsing did not throw an error, validation against XSD must be done separately
        assertNull(serviceMetadata.getServiceInformation());
        assertNull(serviceMetadata.getRedirect());
    }

    @Test
    public void testToSignedServiceMetadataDocument() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        Document signedServiceMetadataDoc = ServiceMetadataConverter.toSignedServiceMetadatadaDocument(inputDoc);

        //then
        Element root = signedServiceMetadataDoc.getDocumentElement();
        assertEquals("SignedServiceMetadata", root.getLocalName());
        assertEquals(NS, root.getNamespaceURI());

        NodeList children = root.getChildNodes();
        assertEquals(1, children.getLength());
        String resultServiceMetadata = XmlTestUtils.marshal(children.item(0));
        assertEquals(inputDoc, resultServiceMetadata);
    }

    @Test(expected = XmlParsingException.class)
    public void testToSignedServiceMetadataDocumentMalformedInput() throws ParserConfigurationException, IOException, SAXException, JAXBException {
        //when
        ServiceMetadataConverter.toSignedServiceMetadatadaDocument("this is malformed XML body");
    }

}
