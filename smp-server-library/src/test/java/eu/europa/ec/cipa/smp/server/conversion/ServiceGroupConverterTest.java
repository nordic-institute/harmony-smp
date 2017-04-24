package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
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

    private static final String RES_PATH = "/eu/europa/ec/cipa/smp/server/conversion/";

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
