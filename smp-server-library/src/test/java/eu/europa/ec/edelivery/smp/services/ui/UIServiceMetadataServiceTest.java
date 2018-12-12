package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;


@ContextConfiguration(classes = {UIServiceGroupSearchService.class, UIServiceMetadataService.class, SecurityUtilsServices.class})
public class UIServiceMetadataServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIServiceMetadataService testInstance;

    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSignleDomainEnv();
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
}