package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.RedirectType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceEndpointList;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceInformationType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
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

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/conversion/";

    @Test
    public void testUnmarshalServiceInformation() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getRedirect());
        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();
        assertNotNull(serviceInformation);
        ServiceEndpointList serviceEndpointList = serviceInformation.getProcessList().getProcesses().get(0).getServiceEndpointList();
        String serviceDescription1 = serviceEndpointList.getEndpoints().get(0).getServiceDescription();
        String serviceDescription2 = serviceEndpointList.getEndpoints().get(1).getServiceDescription();
        assertEquals("This is the epSOS Patient Service List for the Polish NCP", serviceDescription1);
        assertEquals("This is the second epSOS Patient Service List for the Polish NCP", serviceDescription2);
    }

    @Test
    public void testUnmarshalRedirect() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithRedirect.xml");

        //when
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

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
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

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
