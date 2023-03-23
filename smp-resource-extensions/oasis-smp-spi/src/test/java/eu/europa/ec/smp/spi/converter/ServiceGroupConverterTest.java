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

import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.testutils.XmlTestUtils;
import gen.eu.europa.ec.ddc.api.smp10.ServiceGroup;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 11/04/2017.
 */
class ServiceGroupConverterTest {

    private static final String RES_PATH = "/examples/conversion/";


    @Test
    void testUnmashallingServiceGroup() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupOK.xml");

        //when
        ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceGroup);
        assertTrue(serviceGroup.getExtensions().isEmpty());
        assertEquals("http://poland.pl", serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences().get(0).getHref());
    }

    @Test
    void testExtractExtensionsPayload() throws Exception {
        //given
        String expectedExt = "<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension</ex:dummynode></Extension>";
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupWithExtension.xml");
        ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(inputDoc);

        //when
        byte[] val = ServiceGroupConverter.extractExtensionsPayload(serviceGroup);

        //then
        assertNotNull(val);
        assertEquals(expectedExt, new String(val, "UTF-8"));
    }

    @Test
    void testVulnerabilityParsingDTD() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupWithDOCTYPE.xml");

        //when then
        ResourceException result = assertThrows(ResourceException.class, () -> ServiceGroupConverter.unmarshal(inputDoc));

        assertEquals(ResourceException.ErrorCode.PARSE_ERROR, result.getErrorCode());
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));
    }
}
