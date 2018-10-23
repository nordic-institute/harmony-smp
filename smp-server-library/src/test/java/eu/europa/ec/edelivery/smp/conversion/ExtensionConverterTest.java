/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.testutil.XmlTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.xml.sax.SAXException;
import org.xmlunit.matchers.CompareMatcher;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionConverterTest {

    private static final String WRAPPED_FORMAT = "<ExtensionsWrapper xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">%s</ExtensionsWrapper>";

    public static final String RES_PATH = "/examples/extensions/";

    private static final String UTF8_SEQUENCE = "ẞßÄäËëÏïÖöÜüẄẅŸÿЁёЇїӜӝ-Zażółć gęślą jaźń-ÆæØøÅå-ÀÆÇßãÿαΩƒ";

    @Test
    public void testMarshalOneExtension() throws JAXBException, XMLStreamException, IOException, SAXException {
        // given
        List<ExtensionType> list = createListExtensions(1);
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        // when
        byte[] xmlResult = ExtensionConverter.marshalExtensions(list);

        // then
        assertThat(xmlResult, CompareMatcher.isIdenticalTo(inputDoc));
    }

    @Test
    public void testMarshalTwoExtensions() throws JAXBException, XMLStreamException, IOException, SAXException {
        // given
        List<ExtensionType> list = createListExtensions(2);
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");

        // when
        byte[] xmlResult = ExtensionConverter.marshalExtensions(list, true);

        // then
        String wrappedXmlResult = String.format(WRAPPED_FORMAT, new String(xmlResult, "UTF-8"));
        String wrappedInputDoc = String.format(WRAPPED_FORMAT, inputDoc);
        assertThat(wrappedXmlResult, CompareMatcher.isIdenticalTo(wrappedInputDoc));
    }

    @Test
    public void testUtf8Handling() throws JAXBException, XMLStreamException, IOException {
        // given
        ExtensionType extension = new ExtensionType();
        extension.setExtensionName(UTF8_SEQUENCE);
        List<ExtensionType> extensions = Arrays.asList(extension);

        //when
        byte[] extensionsXml = ExtensionConverter.marshalExtensions(extensions);
        List<ExtensionType> resultExtensions = ExtensionConverter.unmarshalExtensions(extensionsXml);

        //then
        assertEquals(UTF8_SEQUENCE, resultExtensions.get(0).getExtensionName());
    }

    @Test
    public void testUnmarshal() throws IOException, JAXBException {
        // given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "extensionMarshal.xml");

        // when
        List<ExtensionType> extensions = ExtensionConverter.unmarshalExtensions(inputDoc);

        // then
        checkExtensions(extensions, 1);
    }

    @Test
    public void testUnmarshalTwoExtensions() throws IOException, JAXBException {
        // given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "extensionMarshalMore.xml");

        // when
        List<ExtensionType> extensions = ExtensionConverter.unmarshalExtensions(inputDoc);

        // then
        checkExtensions(extensions, 2);
    }

    private List<ExtensionType> createListExtensions(int size) {
        List<ExtensionType> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            ExtensionType extension = new ExtensionType();
            extension.setExtensionName("name" + i);
            extension.setExtensionVersionID("versionId" + i);
            extension.setExtensionReason("reason" + i);
            extension.setExtensionReasonCode("reasonCode" + i);
            extension.setExtensionID("id" + i);
            extension.setExtensionAgencyURI("agencyUri" + i);
            extension.setExtensionAgencyName("agencyName" + i);
            list.add(extension);
        }
        return list;
    }


    private void checkExtensions(List<ExtensionType> extensions, int size) {
        Assert.assertNotNull(extensions);
        assertEquals(size, extensions.size());
        int number = 1;
        for (ExtensionType extension : extensions) {
            Assert.assertNotNull(extension);
            assertEquals("name" + number, extension.getExtensionName());
            assertEquals("versionId" + number, extension.getExtensionVersionID());
            assertEquals("reason" + number, extension.getExtensionReason());
            assertEquals("reasonCode" + number, extension.getExtensionReasonCode());
            assertEquals("id" + number, extension.getExtensionID());
            assertEquals("agencyUri" + number, extension.getExtensionAgencyURI());
            assertEquals("agencyName" + number, extension.getExtensionAgencyName());
            assertNull(extension.getExtensionURI());
            number++;
        }
    }


}
