/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertNull;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionUtilsTest {

    private static final String WRAPPED_FORMAT = "<ExtensionsWrapper xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">%s</ExtensionsWrapper>";

    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/util/";

    @Test
    public void testMarshalOneExtension() throws JAXBException, XMLStreamException, IOException, SAXException {
        // given
        List<ExtensionType> list = createListExtensions(1);
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");
        XMLUnit.setIgnoreWhitespace(true);

        // when
        String xmlResult = ExtensionUtils.marshalExtensions(list);

        // then
        assertXMLEqual(inputDoc, xmlResult);
    }

    @Test
    public void testMarshalTwoExtensions() throws JAXBException, XMLStreamException, IOException, SAXException {
        // given
        List<ExtensionType> list = createListExtensions(2);
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");
        XMLUnit.setIgnoreWhitespace(true);

        // when
        String xmlResult = ExtensionUtils.marshalExtensions(list);

        // then
        String wrappedXmlResult = String.format(WRAPPED_FORMAT, xmlResult);
        String wrappedInputDoc = String.format(WRAPPED_FORMAT, inputDoc);
        assertXMLEqual(wrappedInputDoc, wrappedXmlResult);
    }

    @Test
    public void testUnmarshal() throws IOException, JAXBException {
        // given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        // when
        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

        // then
        checkExtensions(extensions, 1);
    }

    @Test
    public void testUnmarshalTwoExtensions() throws IOException, JAXBException {
        // given
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");

        // when
        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

        // then
        checkExtensions(extensions, 2);
    }

    private List<ExtensionType> createListExtensions(int size) {
        List<ExtensionType> list = new ArrayList<>();
        for(int i = 1 ; i <= size; i++) {
            ExtensionType extension = new ExtensionType();
            extension.setExtensionName("name"+i);
            extension.setExtensionVersionID("versionId"+i);
            extension.setExtensionReason("reason"+i);
            extension.setExtensionReasonCode("reasonCode"+i);
            extension.setExtensionID("id"+i);
            extension.setExtensionAgencyURI("agencyUri"+i);
            extension.setExtensionAgencyName("agencyName"+i);
            list.add(extension);
        }
        return list;
    }


    private void checkExtensions(List<ExtensionType> extensions, int size) {
        Assert.assertNotNull(extensions);
        Assert.assertEquals(size, extensions.size());
        int number = 1;
        for(ExtensionType extension : extensions) {
            Assert.assertNotNull(extension);
            Assert.assertEquals("name" + number, extension.getExtensionName());
            Assert.assertEquals("versionId" + number, extension.getExtensionVersionID());
            Assert.assertEquals("reason" + number, extension.getExtensionReason());
            Assert.assertEquals("reasonCode" + number, extension.getExtensionReasonCode());
            Assert.assertEquals("id" + number, extension.getExtensionID());
            Assert.assertEquals("agencyUri" + number, extension.getExtensionAgencyURI());
            Assert.assertEquals("agencyName" + number, extension.getExtensionAgencyName());
            assertNull(extension.getExtensionURI());
            number++;
        }
    }
}