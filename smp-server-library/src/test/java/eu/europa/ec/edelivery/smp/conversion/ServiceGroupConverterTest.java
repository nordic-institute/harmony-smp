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

import eu.europa.ec.edelivery.smp.exceptions.XmlParsingException;
import eu.europa.ec.edelivery.smp.testutil.XmlTestUtils;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 11/04/2017.
 */
public class ServiceGroupConverterTest {

    private static final String RES_PATH = "/eu/europa/ec/edelivery/smp/conversion/";

    @Test
    public void testUnmashallingServiceGroup() throws IOException, JAXBException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceGroupOK.xml");

        //when
        ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(inputDoc);

        //then
        assertNotNull(serviceGroup);
        assertTrue(serviceGroup.getExtensions().isEmpty());
        assertEquals("http://poland.pl", serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences().get(0).getHref());
    }

    @Test
    public void testVulnerabilityParsingDTD() throws IOException {
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceGroupWithDOCTYPE.xml");

        //when then
        try {
            ServiceGroupConverter.unmarshal(inputDoc);
        } catch (XmlParsingException e) {
            assertTrue(e.getMessage().contains("DOCTYPE is disallowed"));
            assertTrue(e.getCause() instanceof SAXParseException);
            return;
        }
        fail("DOCTYPE declaration must be blocked to prevent from XXE attacks");
    }
}
