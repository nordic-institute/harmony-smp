/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

package eu.europa.ec.smp.peppol.converter;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.peppol.PeppolSMPServiceMetadataReader;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.smp.peppol.testutils.XmlTestUtils;
import eu.europa.ec.smp.spi.utils.DomUtils;
import gen.eu.europa.ec.ddc.api.peppol.RedirectType;
import gen.eu.europa.ec.ddc.api.peppol.ServiceEndpointList;
import gen.eu.europa.ec.ddc.api.peppol.ServiceInformationType;
import gen.eu.europa.ec.ddc.api.peppol.ServiceMetadata;
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

    private static final String NS = "http://busdox.org/serviceMetadata/publishing/1.0/";
    private static final String RES_PATH = "/examples/peppol/";

    PeppolSMPServiceMetadataReader testInstance = new PeppolSMPServiceMetadataReader();

    @Test
    void testUnmarshalServiceInformation() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithServiceOk.xml");

        //when
        ServiceMetadata subresource = (ServiceMetadata) testInstance.parseNativeAny(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(subresource);
        assertNull(subresource.getRedirect());
        ServiceInformationType serviceInformation = subresource.getServiceInformation();
        assertNotNull(serviceInformation);
        ServiceEndpointList serviceEndpointList = serviceInformation.getProcessList().getProcesses().get(0).getServiceEndpointList();
        String serviceDescription1 = serviceEndpointList.getEndpoints().get(0).getServiceDescription();
        assertEquals("OpenPeppol Playground C3 endpoint", serviceDescription1);
    }


    @Test
    void testUnmarshalRedirect() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithRedirect.xml");

        //when
        ServiceMetadata subresource = (ServiceMetadata) testInstance.parseNativeAny(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(subresource);
        assertNull(subresource.getServiceInformation());
        RedirectType redirect = subresource.getRedirect();
        assertNotNull(redirect);
        assertEquals("http://serviceMetadata2.eu/busdox-actoridupis%3A%3A0010%3A5798000000001/services/busdox-docidqns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AInvoice-2%3A%3AInvoice%23%23UBL-2.0", redirect.getHref());
        assertEquals("PID:9208-2001-3-279815395", redirect.getCertificateUID());
    }

    @Test
    void testUnmarshalMalformedInput() {

        byte[] inputDoc ="this is malformed XML body".getBytes();

        //when then
        TechnicalException result = assertThrows(TechnicalException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("Content is not allowed in prolog"));
    }

    @Test
    void testToSignedSubresourceDocument() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "SubresourceWithServiceOk.xml");

        //when
        Document signedServiceMetadataDoc = DomUtils.toSignedSubresourcePeppolDocument(inputDoc);

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
        TechnicalException result = assertThrows(TechnicalException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));
    }
}
