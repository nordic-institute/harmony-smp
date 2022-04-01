package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;


/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes = {UIServiceGroupService.class, UIServiceMetadataService.class})
public class UIServiceGroupServiceIntegrationTest extends AbstractServiceIntegrationTest {
    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected UIServiceGroupService testInstance;

    @Autowired
    protected UIServiceMetadataService uiServiceMetadataService;


    protected void insertDataObjectsForOwner(int size, DBUser owner) {
        for (int i = 0; i < size; i++) {
            insertServiceGroup(String.format("%4d", i), true, owner);
        }
    }

    protected void insertDataObjects(int size) {
        insertDataObjectsForOwner(size, null);
    }

    protected DBServiceGroup insertServiceGroup(String id, boolean withExtension, DBUser owner) {
        DBServiceGroup d = TestDBUtils.createDBServiceGroup(String.format("0007:%s:utest", id), TestConstants.TEST_SG_SCHEMA_1, withExtension);
        if (owner!= null) {
            d.getUsers().add(owner);
        }
        serviceGroupDao.persistFlushDetach(d);
        return d;
    }


    @Test
    public void testGetTableListEmpty() {

        // given

        //when
        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);
        // then
        assertNotNull(res);
        assertEquals(0, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(0, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void testGetTableList15() {

        // given
        insertDataObjects(15);
        //when
        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);


        // then
        assertNotNull(res);
        assertEquals(15, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(15, res.getServiceEntities().size());
        assertNull(res.getFilter());

        // all table properties should not be null
        assertNotNull(res);
        assertNotNull(res.getServiceEntities().get(0).getParticipantIdentifier());
        assertNotNull(res.getServiceEntities().get(0).getParticipantScheme());
    }

    @Test
    public void testAddServiceWithMetadata() {

        // given
        DBDomain testDomain01 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        domainDao.persistFlushDetach(testDomain01);

        ServiceGroupRO sgnew = TestROUtils.createROServiceGroupForDomains(testDomain01);
        // add service metadata
        ServiceMetadataRO mtro = TestROUtils.createServiceMetadataDomain(testDomain01, sgnew, TestConstants.TEST_DOC_ID_1, TestConstants.TEST_DOC_SCHEMA_1);
        sgnew.getServiceMetadata().add(mtro);

        //when
        testInstance.updateServiceGroupList(Collections.singletonList(sgnew));

        // then
        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);

        assertNotNull(res);
        assertEquals(1, res.getCount().intValue());
        ServiceGroupRO sgAdded = res.getServiceEntities().get(0);
        ServiceGroupValidationRO sgExt = testInstance.getServiceGroupExtensionById(sgAdded.getId());


        // all table properties should not be null
        assertNotNull(sgAdded);
        assertEquals(sgnew.getParticipantIdentifier(), sgAdded.getParticipantIdentifier());
        assertEquals(sgnew.getParticipantScheme(), sgAdded.getParticipantScheme());
        assertNull(sgAdded.getExtension()); // with list extension must be empty - extension is retrived by some other call
        assertEquals(sgnew.getExtension(), sgExt.getExtension());
        assertEquals(1, sgAdded.getServiceGroupDomains().size());
        assertEquals(1, sgAdded.getServiceMetadata().size());
    }

    @Test
    public void testUpdateServiceGroupExtensionAndServiceMetadaXML() {

        // given
        DBDomain testDomain01 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        domainDao.persistFlushDetach(testDomain01);
        DBServiceGroup dbServiceGroup = TestDBUtils.createDBServiceGroup();
        dbServiceGroup.addDomain(testDomain01);
        DBServiceMetadata dbServiceMetadata = TestDBUtils.createDBServiceMetadata(dbServiceGroup.getParticipantIdentifier(), dbServiceGroup.getParticipantScheme());
        dbServiceGroup.getServiceGroupDomains().get(0).addServiceMetadata(dbServiceMetadata);
        serviceGroupDao.persistFlushDetach(dbServiceGroup);

        String newMetadataXML = TestROUtils.generateServiceMetadata(dbServiceGroup.getParticipantIdentifier(), dbServiceGroup.getParticipantScheme(),
                dbServiceMetadata.getDocumentIdentifier(), dbServiceMetadata.getDocumentIdentifierScheme());
        String newExtension = TestROUtils.generateExtension();

        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(1, res.getCount().intValue());
        ServiceGroupRO sgChange = res.getServiceEntities().get(0);
        ServiceMetadataRO smdXML = uiServiceMetadataService.getServiceMetadataXMLById(res.getServiceEntities().get(0).getServiceMetadata().get(0).getId());
        // test new extension
        assertNotEquals(newExtension, sgChange.getExtension());
        assertNotEquals(newMetadataXML, smdXML.getXmlContent());
        // set new extension
        sgChange.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        sgChange.setExtension(newExtension);
        sgChange.setExtensionStatus(EntityROStatus.UPDATED.getStatusNumber());
        // set new XMLContent
        sgChange.getServiceMetadata().get(0).setStatus(EntityROStatus.UPDATED.getStatusNumber());
        sgChange.getServiceMetadata().get(0).setXmlContentStatus(EntityROStatus.UPDATED.getStatusNumber());
        sgChange.getServiceMetadata().get(0).setXmlContent(newMetadataXML);

        //when
        testInstance.updateServiceGroupList(Collections.singletonList(sgChange));

        // then
        res = testInstance.getTableList(-1, -1, null, null, null);

        assertNotNull(res);
        assertEquals(1, res.getCount().intValue());
        ServiceGroupRO sgUpdated = res.getServiceEntities().get(0);
        ServiceGroupValidationRO sgExt = testInstance.getServiceGroupExtensionById(sgUpdated.getId());
        assertEquals(1, sgChange.getServiceMetadata().size());
        // retrive service metadata xml with special service - it is not retrieve by browsing list
        ServiceMetadataRO smdXMLNew = uiServiceMetadataService.getServiceMetadataXMLById(sgUpdated.getServiceMetadata().get(0).getId());

        // all table properties should not be null
        assertNotNull(sgUpdated);
        assertEquals(sgUpdated.getParticipantIdentifier(), sgUpdated.getParticipantIdentifier());
        assertEquals(sgUpdated.getParticipantScheme(), sgUpdated.getParticipantScheme());
        assertEquals(newExtension, sgExt.getExtension());
        assertEquals(1, sgChange.getServiceGroupDomains().size());
        assertNotNull(smdXMLNew.getXmlContent());
        assertEquals(newMetadataXML, smdXMLNew.getXmlContent());
    }



    @Test
    public void testUpdateServiceMatadataChangeDomain() {

        // given
        DBDomain testDomain01 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        domainDao.persistFlushDetach(testDomain01);
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);

        DBServiceGroup dbServiceGroup = TestDBUtils.createDBServiceGroup();
        dbServiceGroup.addDomain(testDomain01);
        DBServiceMetadata dbServiceMetadata = TestDBUtils.createDBServiceMetadata(dbServiceGroup.getParticipantIdentifier(), dbServiceGroup.getParticipantScheme());
        dbServiceGroup.getServiceGroupDomains().get(0).addServiceMetadata(dbServiceMetadata);
        // add second domain
        dbServiceGroup.addDomain(testDomain02);
        serviceGroupDao.persistFlushDetach(dbServiceGroup);

        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);

        assertNotNull(res);
        assertEquals(1, res.getCount().intValue());
        ServiceGroupRO sgChanged = res.getServiceEntities().get(0);
        ServiceMetadataRO smdToChange = sgChanged.getServiceMetadata().get(0);
        assertEquals(testDomain01.getDomainCode(), smdToChange.getDomainCode());
        assertEquals(testDomain01.getSmlSubdomain(), smdToChange.getSmlSubdomain());

        // then
        sgChanged.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        smdToChange.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        smdToChange.setDomainCode(testDomain02.getDomainCode());
        smdToChange.setSmlSubdomain(testDomain02.getSmlSubdomain());
        testInstance.updateServiceGroupList(Collections.singletonList(sgChanged));

        res = testInstance.getTableList(-1, -1, null, null, null);
        ServiceGroupRO sgUpdated = res.getServiceEntities().get(0);
        ServiceMetadataRO smdUpdated = sgUpdated.getServiceMetadata().get(0);

        assertEquals(testDomain02.getDomainCode(), smdUpdated.getDomainCode());
        assertEquals(testDomain02.getSmlSubdomain(), smdUpdated.getSmlSubdomain());

    }
    @Test
    public void testUpdateServiceMatadataChangeDomainReverseOrder() {

        // given
        DBDomain testDomain01 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);

        DBDomain testDomain02 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        domainDao.persistFlushDetach(testDomain01);

        DBServiceGroup dbServiceGroup = TestDBUtils.createDBServiceGroup();
        dbServiceGroup.addDomain(testDomain02);
        dbServiceGroup.addDomain(testDomain01);
        DBServiceMetadata dbServiceMetadata = TestDBUtils.createDBServiceMetadata(dbServiceGroup.getParticipantIdentifier(), dbServiceGroup.getParticipantScheme());
        dbServiceGroup.getServiceGroupDomains().get(1 ).addServiceMetadata(dbServiceMetadata);
        // add second domain

        serviceGroupDao.persistFlushDetach(dbServiceGroup);

        ServiceResult<ServiceGroupRO> res = testInstance.getTableList(-1, -1, null, null, null);

        assertNotNull(res);
        assertEquals(1, res.getCount().intValue());
        ServiceGroupRO sgChanged = res.getServiceEntities().get(0);
        ServiceMetadataRO smdToChange = sgChanged.getServiceMetadata().get(0);
        assertEquals(testDomain01.getDomainCode(), smdToChange.getDomainCode());
        assertEquals(testDomain01.getSmlSubdomain(), smdToChange.getSmlSubdomain());

        // then
        sgChanged.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        smdToChange.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        smdToChange.setDomainCode(testDomain02.getDomainCode());
        smdToChange.setSmlSubdomain(testDomain02.getSmlSubdomain());
        testInstance.updateServiceGroupList(Collections.singletonList(sgChanged));

        res = testInstance.getTableList(-1, -1, null, null, null);
        ServiceGroupRO sgUpdated = res.getServiceEntities().get(0);
        ServiceMetadataRO smdUpdated = sgUpdated.getServiceMetadata().get(0);

        assertEquals(testDomain02.getDomainCode(), smdUpdated.getDomainCode());
        assertEquals(testDomain02.getSmlSubdomain(), smdUpdated.getSmlSubdomain());

    }


    @Test
    public void validateExtensionVaild() throws IOException {
        // given
        ServiceGroupValidationRO sg = TestROUtils.getValidExtension();

        // when
        testInstance.validateServiceGroup(sg);

        // then
        assertNull(sg.getErrorMessage());
        assertNotNull(sg.getExtension());
    }

    @Test
    public void validateExtensionMultipleVaild() throws IOException {
        // given
        ServiceGroupValidationRO sg = TestROUtils.getValidMultipleExtension();

        // when
        testInstance.validateServiceGroup(sg);

        // then
        assertNull(sg.getErrorMessage());
        assertNotNull(sg.getExtension());
    }

    @Test
    public void validateExtensionCustomTextInvaldValid() throws IOException {
        // given
        ServiceGroupValidationRO sg = TestROUtils.getValidCustomText();

        // when
        testInstance.validateServiceGroup(sg);

        // then
        assertNotNull(sg.getErrorMessage());
        assertThat(sg.getErrorMessage(), containsString("Element 'ServiceGroup' cannot have character "));
        assertNotNull(sg.getExtension());
    }

    @Test
    public void validateExtensionInvalid() throws IOException {
        ServiceGroupValidationRO sg = TestROUtils.getInvalid();

        // when
        testInstance.validateServiceGroup(sg);

        // then
        assertNotNull(sg.getErrorMessage());
        assertThat(sg.getErrorMessage(), containsString("SAXParseException: cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\":ExtensionID}'."));
        assertNotNull(sg.getExtension());
    }

    @Test
    public void validateCustomExtension() throws IOException {
        ServiceGroupValidationRO sg = TestROUtils.getCustomExtension();

        // when
        testInstance.validateServiceGroup(sg);

        // then
        assertNull(sg.getErrorMessage());
        assertNotNull(sg.getExtension());
    }


    @Test
    public void getEmptyExtensionById() throws IOException {
        DBServiceGroup sg = insertServiceGroup("testExt", false, null);
        assertNotNull(sg);
        assertNotNull(sg.getId());
        assertNull(sg.getExtension());

        // when
        ServiceGroupValidationRO res = testInstance.getServiceGroupExtensionById(sg.getId());

        // then
        assertNotNull(res);
        assertNull(res.getExtension());
    }

    @Test
    public void getExtensionById() throws IOException {
        DBServiceGroup sg = insertServiceGroup("testExt", true, null);
        assertNotNull(sg);
        assertNotNull(sg.getId());
        assertNotNull(sg.getExtension());

        // when
        ServiceGroupValidationRO res = testInstance.getServiceGroupExtensionById(sg.getId());

        // then
        assertNotNull(res);
        assertNotNull(res.getExtension());
    }

}
