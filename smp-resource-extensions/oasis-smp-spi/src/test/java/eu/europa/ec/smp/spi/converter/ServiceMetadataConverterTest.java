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

package eu.europa.ec.smp.spi.converter;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.OasisSMP10ServiceMetadataReader;
import eu.europa.ec.dynamicdiscovery.exception.BindException;
import eu.europa.ec.smp.spi.testutils.XmlTestUtils;
import gen.eu.europa.ec.ddc.api.smp10.RedirectType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceEndpointList;
import gen.eu.europa.ec.ddc.api.smp10.ServiceInformationType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceMetadata;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverterTest {

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String RES_PATH = "/examples/conversion/";

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    OasisSMP10ServiceMetadataReader testInstance = new OasisSMP10ServiceMetadataReader();

    @Test
    public void testUnmarshalServiceInformation() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        ServiceMetadata serviceMetadata = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

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
    public void testUnmarshalServiceInformationUtf8() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformationUtf8.xml");

        //when
        ServiceMetadata serviceMetadata = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        String serviceDescription = serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).getServiceDescription();
        assertEquals("--ö--ẞßÄäPLżółćNOÆæØøÅå", serviceDescription);

    }

    @Test
    public void testUnmarshalRedirect() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithRedirect.xml");

        //when
        ServiceMetadata serviceMetadata = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(serviceMetadata);
        assertNull(serviceMetadata.getServiceInformation());
        RedirectType redirect = serviceMetadata.getRedirect();
        assertNotNull(redirect);
        assertEquals("http://poland.pl", redirect.getHref());
        assertEquals("SAMPLE CERTIFICATE VALUE", redirect.getCertificateUID());
    }

    @Test
    public void testUnmarshalMalformedInput() throws Exception {

        byte[] inputDoc ="this is malformed XML body".getBytes();

        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("Content is not allowed in prolog"));
    }

    @Test
    public void testInvalidDocumentNamespace() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataMissingMandatoryFields.xml");
        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("unexpected element "));
    }

    @Test
    public void testToSignedServiceMetadataDocument() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithServiceInformation.xml");

        //when
        Document signedServiceMetadataDoc = DomUtils.toSignedServiceMetadata10Document(inputDoc);

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
    public void testVulnerabilityParsingDTD() throws Exception {

        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataWithDOCTYPE.xml");

        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));

    }

}
