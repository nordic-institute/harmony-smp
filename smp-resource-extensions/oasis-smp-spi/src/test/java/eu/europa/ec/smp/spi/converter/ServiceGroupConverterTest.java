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

import eu.europa.ec.dynamicdiscovery.core.extension.impl.OasisSMP10ServiceGroupReader;
import eu.europa.ec.dynamicdiscovery.exception.BindException;
import eu.europa.ec.smp.spi.testutils.XmlTestUtils;
import gen.eu.europa.ec.ddc.api.smp10.ServiceGroup;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 11/04/2017.
 */
class ServiceGroupConverterTest {

    OasisSMP10ServiceGroupReader testInstance = new OasisSMP10ServiceGroupReader();

    private static final String RES_PATH = "/examples/conversion/";


    @Test
    void testUnmashallingServiceGroup() throws Exception {

        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupOK.xml");

        //when
        ServiceGroup serviceGroup = testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        assertNotNull(serviceGroup);
        assertTrue(serviceGroup.getExtensions().isEmpty());
        assertEquals("http://poland.pl", serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences().get(0).getHref());
    }


    @Test
    void testVulnerabilityParsingDTD() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupWithDOCTYPE.xml");
        //when then
        BindException result = assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));
    }
}
