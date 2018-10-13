/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.conversion;


import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.XmlTestUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.RedirectType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceEndpointList;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceInformationType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverterTest {

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String RES_PATH = "/examples/conversion/";

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Test
    public void testUnmarshalServiceInformation() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

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
    public void testUnmarshalServiceInformationUtf8() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformationUtf8.xml");

        //when
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        //then
        String serviceDescription = serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).getServiceDescription();
        assertEquals("--ö--ẞßÄäPLżółćNOÆæØøÅå", serviceDescription);

    }

    @Test
    public void testUnmarshalRedirect() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        byte[]  inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithRedirect.xml");

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

    @Test
    public void testUnmarshalMalformedInput() throws ParserConfigurationException, IOException, SAXException, JAXBException {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(Matchers.startsWith("Invalid service metada. Error"));
        //when
        ServiceMetadataConverter.unmarshal("this is malformed XML body".getBytes());
    }

    @Test
    public void testUnmarshalMissingMandatoryFields() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        //given
        byte[]  inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataMissingMandatoryFields.xml");

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
        byte[]  inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        Document signedServiceMetadataDoc = ServiceMetadataConverter.toSignedServiceMetadatadaDocument(inputDoc);

        //then
        Element root = signedServiceMetadataDoc.getDocumentElement();
        assertEquals("SignedServiceMetadata", root.getLocalName());
        assertEquals(NS, root.getNamespaceURI());

        NodeList children = root.getChildNodes();
        assertEquals(1, children.getLength());
        byte[] resultServiceMetadata = XmlTestUtils.marshallToByteArray(children.item(0));
        assertTrue(Arrays.equals(inputDoc, resultServiceMetadata));
    }

    @Test
    public void testToSignedServiceMetadataDocumentMalformedInput() throws ParserConfigurationException, IOException, SAXException, JAXBException {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(Matchers.startsWith("Invalid service metada. Error:"));
        //when
        ServiceMetadataConverter.toSignedServiceMetadatadaDocument("this is malformed XML body".getBytes());
    }

    @Test
    public void testVulnerabilityParsingDTD() throws IOException {

        //given
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(Matchers.containsString("DOCTYPE is disallowed"));
        expectedExeption.expectCause(Matchers.isA(SAXParseException.class));


        byte[]  inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithDOCTYPE.xml");

        ServiceMetadataConverter.unmarshal(inputDoc);

        fail("DOCTYPE declaration must be blocked to prevent from XXE attacks");
    }
}
