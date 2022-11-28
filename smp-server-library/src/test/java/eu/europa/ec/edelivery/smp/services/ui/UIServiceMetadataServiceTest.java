package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.XmlTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;


@ContextConfiguration(classes = {UIServiceGroupSearchService.class, UIServiceMetadataService.class})
public class UIServiceMetadataServiceTest extends AbstractServiceIntegrationTest {

    private static final String RES_PATH = "/examples/services/";
    private static final String RES_PATH_CONV = "/examples/conversion/";

    @Autowired
    protected UIServiceMetadataService testInstance;


    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSingleDomainEnv();
    }

    @Test
    public void getServiceMetadataXMLById() {
        Optional<DBServiceMetadata> smd = serviceMetadataDao.findServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOC_ID_1,
                TEST_DOC_SCHEMA_1);
        assertTrue(smd.isPresent());

        ServiceMetadataRO smdro = testInstance.getServiceMetadataXMLById(smd.get().getId());
        assertNotNull(smdro);
        assertNotNull(smdro.getXmlContent());
        assertEquals(smd.get().getId(), smdro.getId());
    }

    @Test
    public void validateServiceMetadataValid() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata("partId", "partSch");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifier());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifierScheme());
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()));

        smv = testInstance.validateServiceMetadata(smv);
        assertNull(smv.getErrorMessage());
    }

    @Test
    public void validateServiceMetadataRedirectValid() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadataRedirect("docId", "docSch", "http://10.1.1.10:1027/test-service-data");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifier());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifierScheme());
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()));

        smv = testInstance.validateServiceMetadata(smv);
        assertNull(smv.getErrorMessage());
    }

    @Test
    public void validateServiceMetadataRedirectInvalid() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadataRedirect("docId", "docSch", "");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifier());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifierScheme());
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()));

        smv = testInstance.validateServiceMetadata(smv);
        assertNotNull(smv.getErrorMessage());
        assertEquals("Redirect URL must must be empty!", smv.getErrorMessage());
    }


    @Test
    public void validateServiceMetadataParticipantNotMatch() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata("partId", "partSch");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifier());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifierScheme());
        smv.setParticipantIdentifier("partIdNotMatch");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()));

        smv = testInstance.validateServiceMetadata(smv);
        assertEquals("Participant identifier and scheme do not match!",smv.getErrorMessage());
    }

    @Test
    public void validateServiceMetadataDocumentNotMatch() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata("partId", "partSch");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifierScheme());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifier());
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()));

        smv = testInstance.validateServiceMetadata(smv);
        assertEquals("Document identifier and scheme do not match!",smv.getErrorMessage());
    }

    @Test
    public void validateServiceMetadataInvalidXML() {
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata("partId", "partSch");

        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier(md.getDocumentIdentifierScheme());
        smv.setDocumentIdentifierScheme(md.getDocumentIdentifier());
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent(new String(md.getXmlContent()) + "Something to invalidate xml");

        smv = testInstance.validateServiceMetadata(smv);
        assertEquals("SAXParseException: Content is not allowed in trailing section.",smv.getErrorMessage());
    }

    @Test
    public void testSearchAllEndpoints() throws IOException {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataDifferentCertificatesTypes.xml");
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        List<EndpointType> endpointTypeList =  testInstance.searchAllEndpoints(serviceMetadata);
        assertEquals(3, endpointTypeList.size());
    }

    @Test
    public void testSearchAllEndpointsEmptyList() throws IOException {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH_CONV + "ServiceMetadataWithRedirect.xml");
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);

        List<EndpointType> endpointTypeList =  testInstance.searchAllEndpoints(serviceMetadata);
        assertEquals(0, endpointTypeList.size());
    }

    @Test
    public void testValidateServiceMetadataCertificatesEmptyOK() throws IOException, CertificateException {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataDifferentCertificatesTypes.xml");
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);
        // then
        testInstance.validateServiceMetadataCertificates(serviceMetadata);
        // no error is expected
    }

    @Test
    public void testValidateServiceMetadataCertificatesRSAOK() throws IOException, CertificateException {
        ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
        UIServiceMetadataService testInstance = new UIServiceMetadataService(null, null,
                null, null,
                configurationService);

        Mockito.doReturn(Arrays.asList("RSA","ED25519","ED448")).when(configurationService).getAllowedDocumentCertificateTypes();

        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataDifferentCertificatesTypes.xml");
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);
        // then
        testInstance.validateServiceMetadataCertificates(serviceMetadata);

    }

    @Test
    public void testValidateServiceMetadataCertificatesNotAllowed() throws IOException{
        ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
        UIServiceMetadataService testInstance = new UIServiceMetadataService(null, null,
                null, null,
                configurationService);

        Mockito.doReturn(Collections.singletonList("testKeyAlg")).when(configurationService).getAllowedDocumentCertificateTypes();

        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceMetadataDifferentCertificatesTypes.xml");
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(inputDoc);
        // then
        CertificateException result  = assertThrows(CertificateException.class, () -> testInstance.validateServiceMetadataCertificates(serviceMetadata));
        // no error is expected
        assertEquals("Certificate does not have allowed key type!", result.getMessage());

    }
}