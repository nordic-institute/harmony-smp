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
        List<ExtensionType> list = createListExtensions(1);

        String xmlResult = ExtensionUtils.marshalExtensions(list);

        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        XMLUnit.setIgnoreWhitespace(true);
        assertXMLEqual(inputDoc, xmlResult);
    }

    @Test
    public void testMarshalTwoExtensions() throws JAXBException, XMLStreamException, IOException, SAXException {
        List<ExtensionType> list = createListExtensions(2);

        String xmlResult = ExtensionUtils.marshalExtensions(list);

        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");

        XMLUnit.setIgnoreWhitespace(true);
        String wrappedXmlResult = String.format(WRAPPED_FORMAT, xmlResult);
        String wrappedInputDoc = String.format(WRAPPED_FORMAT, inputDoc);
        assertXMLEqual(wrappedInputDoc, wrappedXmlResult);
    }

    @Test
    public void testUnmarshal() throws IOException, JAXBException {

        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

        checkExtensions(extensions, 1);
    }

    @Test
    public void testUnmarshalTwoExtensions() throws IOException, JAXBException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");

        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

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
