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

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.XmlTestUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 11/04/2017.
 */
public class ServiceGroupConverterTest {

    private static final String RES_PATH = "/examples/conversion/";

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Test
    public void toServiceGroupTest() {
        // set
        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();

        //when
        ServiceGroup serviceGroup = ServiceGroupConverter.toServiceGroup(sg);
        assertNotNull(serviceGroup);
        assertEquals(sg.getParticipantIdentifier(), serviceGroup.getParticipantIdentifier().getValue());
        assertEquals(sg.getParticipantScheme(), serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(1, serviceGroup.getExtensions().size());
    }

    @Test
    public void toServiceGroupTestMultiExtensions() throws UnsupportedEncodingException, JAXBException, XMLStreamException {
        // set
        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();
        sg.setExtension(ExtensionConverter.concatByteArrays(TestDBUtils.generateExtension(), TestDBUtils.generateExtension()));

        //when-then
        ServiceGroup serviceGroup = ServiceGroupConverter.toServiceGroup(sg);
        assertNotNull(serviceGroup);
        assertEquals(sg.getParticipantIdentifier(), serviceGroup.getParticipantIdentifier().getValue());
        assertEquals(sg.getParticipantScheme(), serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(2, serviceGroup.getExtensions().size());
    }

    @Test
    public void toServiceGroupTestIsEmpty() {
        // set
        //when
        ServiceGroup serviceGroup = ServiceGroupConverter.toServiceGroup(null);
        assertNull(serviceGroup);
    }

    @Test
    public void testInvalidExtension() {
        //given
        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();
        sg.setExtension("<This > is invalid extensions".getBytes());
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectCause(Matchers.isA(UnmarshalException.class));
        expectedExeption.expectMessage(Matchers.startsWith("Invalid extension for service group"));

        //when-then
        ServiceGroup serviceGroup = ServiceGroupConverter.toServiceGroup(sg);
    }


    @Test
    public void testUnmashallingServiceGroup() throws IOException {
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
    public void testExtractExtensionsPayload() throws IOException, JAXBException {
        //given
        String expectedExt = "<Extension xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension</ex:dummynode></Extension>";
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceGroupWithExtension.xml");
        assertTrue(inputDoc.contains(expectedExt));
        ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(inputDoc);

        //when
        byte[] val  = ServiceGroupConverter.extractExtensionsPayload(serviceGroup);

        //then
        assertNotNull(val);
        assertEquals(expectedExt, new String(val,"UTF-8"));
    }

    @Test
    public void testVulnerabilityParsingDTD() throws IOException {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectCause(Matchers.isA(SAXParseException.class));
        expectedExeption.expectMessage(Matchers.containsString("DOCTYPE is disallowed"));
        //given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "ServiceGroupWithDOCTYPE.xml");

        //when then
        ServiceGroupConverter.unmarshal(inputDoc);

        fail("DOCTYPE declaration must be blocked to prevent from XXE attacks");
    }
}
