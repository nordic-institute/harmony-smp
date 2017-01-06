package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.junit.Test;
import org.w3c.dom.Document;
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

    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/conversion/";

    @Test
    public void testUnmarshallServiceInformation() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        ServiceMetadataType serviceMetadata = ServiceMetadataConverter.unmarshall(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getRedirect());
        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();
        assertNotNull(serviceInformation);
        //Assert any random value value deeper from the tree:
        ServiceEndpointList serviceEndpointList = serviceInformation.getProcessList().getProcess().get(0).getServiceEndpointList();
        String serviceDescription1 = serviceEndpointList.getEndpointAtIndex(0).getServiceDescription();
        String serviceDescription2 = serviceEndpointList.getEndpointAtIndex(1).getServiceDescription();
        assertEquals("invoice service AS2", serviceDescription1);
        assertEquals("invoice service", serviceDescription2);
    }

    @Test
    public void testUnmarshallRedirect() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithRedirect.xml");

        //when
        ServiceMetadataType serviceMetadata = ServiceMetadataConverter.unmarshall(inputDoc);

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getServiceInformation());
        RedirectType redirect = serviceMetadata.getRedirect();
        assertNotNull(redirect);
        assertEquals("http://poland.pl", redirect.getHref());
        assertEquals("SAMPLE CERTIFICATE VALUE", redirect.getCertificateUID());
    }













    /*
    @Test
    public void testExtractServiceInformationXmlToStringPositive() throws IOException, SAXException, ParserConfigurationException, JAXBException, TransformerException {
        //given
        Document inputDoc = XmlTestUtils.loadDocument(RES_PATH + "ServiceInformationInsideServiceMetadata.xml");
        String expectedOutput = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        String serviceInformationXML = ServiceMetadataConverter.extractServiceMetadataXmlToString(inputDoc);

        //then
        assertEquals(expectedOutput, serviceInformationXML);
    }

    @Test
    public void testExtractServiceInformationXmlToStringEmpty() throws IOException, SAXException, ParserConfigurationException, JAXBException, TransformerException {
        //given
        Document inputDoc = XmlTestUtils.loadDocument(RES_PATH + "ServiceMetadataWithRedirect.xml");

        //when
        String serviceInformationXML = ServiceMetadataConverter.extractServiceMetadataXmlToString(inputDoc);

        //then
        assertNull(serviceInformationXML);
    }

    @Test
    public void testExtractRedirectXmlToStringPositive() throws IOException, SAXException, ParserConfigurationException, JAXBException, TransformerException {
        //given
        Document inputDoc = XmlTestUtils.loadDocument(RES_PATH + "ServiceMetadataWithRedirect.xml");
        String expectedOutput = XmlTestUtils.loadDocumentAsString(RES_PATH + "Redirect.xml");

        //when
        String serviceInformationXML = ServiceMetadataConverter.extractRedirectXmlToString(inputDoc);

        //then
        assertEquals(expectedOutput, serviceInformationXML);
    }

    @Test
    public void testExtractRedirectXmlToStringEmpty() throws IOException, SAXException, ParserConfigurationException, JAXBException, TransformerException {
        //given
        Document inputDoc = XmlTestUtils.loadDocument(RES_PATH + "ServiceInformationInsideServiceMetadata.xml");

        //when
        String serviceInformationXML = ServiceMetadataConverter.extractRedirectXmlToString(inputDoc);

        //then
        assertNull(serviceInformationXML);
    }
*/
}
