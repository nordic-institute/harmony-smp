/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */

package eu.europa.ec.smp.spi.converter;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.oasis10.OasisSMP10ServiceMetadataReader;
import eu.europa.ec.dynamicdiscovery.exception.BindException;
import eu.europa.ec.smp.spi.testutils.XmlTestUtils;
import gen.eu.europa.ec.ddc.api.smp10.RedirectType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceEndpointList;
import gen.eu.europa.ec.ddc.api.smp10.ServiceInformationType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceMetadata;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by gutowpa on 05/01/2017.
 */
class SubresourceConverterTest {

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String RES_PATH = "/examples/oasis-smp-1.0/";

    OasisSMP10ServiceMetadataReader testInstance = new OasisSMP10ServiceMetadataReader();

    @Test
    void testUnmarshalServiceInformation() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithServiceOk.xml");

        //when
        ServiceMetadata subresource = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(subresource);
        assertNull(subresource.getRedirect());
        ServiceInformationType serviceInformation = subresource.getServiceInformation();
        assertNotNull(serviceInformation);
        ServiceEndpointList serviceEndpointList = serviceInformation.getProcessList().getProcesses().get(0).getServiceEndpointList();
        String serviceDescription1 = serviceEndpointList.getEndpoints().get(0).getServiceDescription();
        String serviceDescription2 = serviceEndpointList.getEndpoints().get(1).getServiceDescription();
        assertEquals("This is the epSOS Patient Service List for the Polish NCP", serviceDescription1);
        assertEquals("This is the second epSOS Patient Service List for the Polish NCP", serviceDescription2);
    }

    @Test
    void testUnmarshalServiceInformationUtf8() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithServiceInformationUtf8.xml");

        //when
        ServiceMetadata subresource = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        String serviceDescription = subresource.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).getServiceDescription();
        assertEquals("--ö--ẞßÄäPLżółćNOÆæØøÅå", serviceDescription);

    }

    @Test
    void testUnmarshalRedirect() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithRedirect.xml");

        //when
        ServiceMetadata subresource = (ServiceMetadata) testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(subresource);
        assertNull(subresource.getServiceInformation());
        RedirectType redirect = subresource.getRedirect();
        assertNotNull(redirect);
        assertEquals("http://poland.pl", redirect.getHref());
        assertEquals("SAMPLE CERTIFICATE VALUE", redirect.getCertificateUID());
    }

    @Test
    void testUnmarshalMalformedInput() {

        byte[] inputDoc ="this is malformed XML body".getBytes();

        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("Content is not allowed in prolog"));
    }

    @Test
    void testInvalidDocumentNamespace() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceMissingMandatoryFields.xml");
        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("unexpected element "));
    }

    @Test
    void testToSignedSubresourceDocument() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithServiceOk.xml");

        //when
        Document signedServiceMetadataDoc = DomUtils.toSignedSubresource10Document(inputDoc);

        //then
        Element root = signedServiceMetadataDoc.getDocumentElement();
        assertEquals("SignedServiceMetadata", root.getLocalName());
        assertEquals(NS, root.getNamespaceURI());

        NodeList children = root.getChildNodes();
        assertEquals(1, children.getLength());
        assertEquals("ServiceMetadata", children.item(0).getLocalName());
        assertEquals(NS, children.item(0).getNamespaceURI());

    }

    @Test
    void testVulnerabilityParsingDTD() throws Exception {

        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithDOCTYPE.xml");

        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));

    }

}
