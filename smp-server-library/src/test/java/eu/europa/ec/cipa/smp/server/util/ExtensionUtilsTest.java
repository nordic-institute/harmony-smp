package eu.europa.ec.cipa.smp.server.util;

import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionUtilsTest {

    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/util/";

    @Test
    public void testMarshal() throws IOException {
        ExtensionType extension = new ExtensionType();
        extension.setExtensionName("name1");
        extension.setExtensionVersionID("versionId1");
        extension.setExtensionReason("reason1");
        extension.setExtensionReasonCode("reasonCode1");
        extension.setExtensionID("id1");
        extension.setExtensionAgencyURI("agencyUri1");
        extension.setExtensionAgencyName("agencyName1");

        String xmlResult = ExtensionUtils.marshalExtension(extension, ExtensionUtils.EXT_TYPE_QNAME);

        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        assertEquals(inputDoc, xmlResult);
    }

    @Test
    public void testMarshalExtensionNull() {
        assertNull(ExtensionUtils.marshalExtension(null,ExtensionUtils.EXT_TYPE_QNAME));
    }

    @Test
    public void testMarshalQNameNull() {
        ExtensionType extension = new ExtensionType();
        assertNull(ExtensionUtils.marshalExtension(extension, null));
    }

    @Test
    public void testUnmarshal() throws IOException {

        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshal.xml");

        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

        checkExtensions(extensions, 1);
    }

    @Test
    public void testUnmarshalMoreThanOne() throws IOException {
        String inputDoc = XmlTestUtils.loadDocumentAsString(RES_PATH + "extensionMarshalMore.xml");

        List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(inputDoc);

        checkExtensions(extensions, 2);
    }


    private void checkExtensions(List<ExtensionType> extensions, int size) {
        assertNotNull(extensions);
        assertEquals(size, extensions.size());
        int number = 1;
        for(ExtensionType extension : extensions) {
            assertNotNull(extension);
            assertEquals("name"+number, extension.getExtensionName());
            assertEquals("versionId"+number, extension.getExtensionVersionID());
            assertEquals("reason"+number, extension.getExtensionReason());
            assertEquals("reasonCode"+number, extension.getExtensionReasonCode());
            assertEquals("id"+number, extension.getExtensionID());
            assertEquals("agencyUri"+number, extension.getExtensionAgencyURI());
            assertEquals("agencyName"+number, extension.getExtensionAgencyName());
            assertNull(extension.getExtensionURI());
            number++;
        }
    }


}
