package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ParticipantSMLRecord;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMLStatusEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Ignore
@ContextConfiguration(classes = {UIServiceGroupService.class, UIServiceMetadataService.class,
        SmlIntegrationConfiguration.class})
public class UIServiceGroupServiceUpdateListIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    UIServiceGroupService testInstance;

    @Autowired
    UIServiceMetadataService uiServiceMetadataService;

    @Autowired
    SmlIntegrationConfiguration integrationMock;

    @Before
    public void setup() throws IOException {
        resetKeystore();
        setDatabaseProperty(SMPPropertyEnum.SML_PHYSICAL_ADDRESS, "0.0.0.0");
        setDatabaseProperty(SMPPropertyEnum.SML_LOGICAL_ADDRESS, "http://localhost/smp");
        setDatabaseProperty(SMPPropertyEnum.SML_URL, "http://localhost/edelivery-sml");
        setDatabaseProperty(SMPPropertyEnum.SML_ENABLED, "true");
        prepareDatabaseForMultipeDomainEnv();
        integrationMock.reset();
    }


    protected void insertDataObjectsForOwner(int size, DBUser owner) {
        for (int i = 0; i < size; i++) {
            insertServiceGroup(String.format("%4d", i), true, owner);
        }
    }

    protected void insertDataObjects(int size) {
        insertDataObjectsForOwner(size, null);
    }

    protected DBResource insertServiceGroup(String id, boolean withExtension, DBUser owner) {
        DBResource d = TestDBUtils.createDBResource(String.format("0007:%s:utest", id), TestConstants.TEST_SG_SCHEMA_1, withExtension);
        if (owner != null) {
          //  d.getUsers().add(owner);
        }
        serviceGroupDao.persistFlushDetach(d);
        return d;
    }

    @Test
    public void addNewServiceGroupTestSMLRecords() {
        // given
        DBDomain dbDomain1 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBDomain dbDomain2 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_2).get();
        ServiceGroupRO serviceGroupRO = TestROUtils.createROServiceGroupForDomains(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                dbDomain1, dbDomain2);
        // When
        List<ParticipantSMLRecord> lst = testInstance.addNewServiceGroup(serviceGroupRO);
        // then
        assertEquals(2, lst.size());
        assertEquals(SMLStatusEnum.REGISTER, lst.get(0).getStatus());
        assertEquals(SMLStatusEnum.REGISTER, lst.get(1).getStatus());
        assertEquals(dbDomain1, lst.get(0).getDomain());
        assertEquals(dbDomain2, lst.get(1).getDomain());
        assertEquals(lst.get(0).getParticipantIdentifier(), lst.get(1).getParticipantIdentifier());
        assertEquals(serviceGroupRO.getParticipantIdentifier(), lst.get(0).getParticipantIdentifier());
        assertEquals(serviceGroupRO.getParticipantScheme(), lst.get(0).getParticipantScheme());
    }
/*
    @Test
    @Transactional
    public void updateServiceGroupTestSMLRecordsRemoveDomain() {

        // given
        DBDomain dbDomain1 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBDomain dbDomain2 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_2).get();
        DBResource dbServiceGroup = TestDBUtils.createDBServiceGroupRandom();
        dbServiceGroup.addDomain(dbDomain1);
        dbServiceGroup.addDomain(dbDomain2);
        serviceGroupDao.persistFlushDetach(dbServiceGroup);
        ServiceGroupRO roToUpdate = testInstance.getServiceGroupById(dbServiceGroup.getId());
        // when
        ServiceGroupDomainRO dro = roToUpdate.getServiceGroupDomains().remove(0);
        List<ParticipantSMLRecord> lst = testInstance.updateServiceGroup(roToUpdate, true);
        // then
        assertEquals(1, lst.size());
        assertEquals(SMLStatusEnum.UNREGISTER, lst.get(0).getStatus());
        assertEquals(dro.getDomainCode(), lst.get(0).getDomain().getDomainCode());
        assertEquals(roToUpdate.getParticipantIdentifier(), lst.get(0).getParticipantIdentifier());
        assertEquals(roToUpdate.getParticipantScheme(), lst.get(0).getParticipantScheme());
    }

    @Test
    @Transactional
    public void updateServiceGroupTestSMLRecordsAddDomain() {

        // given
        DBDomain dbDomain1 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBDomain dbDomain2 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_2).get();
        DBResource dbServiceGroup = TestDBUtils.createDBServiceGroupRandom();
        dbServiceGroup.addDomain(dbDomain1);
        serviceGroupDao.persistFlushDetach(dbServiceGroup);
        ServiceGroupRO roToUpdate = testInstance.getServiceGroupById(dbServiceGroup.getId());
        // when
        ServiceGroupDomainRO sgr = new ServiceGroupDomainRO();
        sgr.setDomainCode(dbDomain2.getDomainCode());
        sgr.setSmlSubdomain(dbDomain2.getSmlSubdomain());
        sgr.setDomainId(dbDomain2.getId());
        roToUpdate.getServiceGroupDomains().add(sgr);
        List<ParticipantSMLRecord> lst = testInstance.updateServiceGroup(roToUpdate, true);
        // then
        assertEquals(1, lst.size());
        assertEquals(SMLStatusEnum.REGISTER, lst.get(0).getStatus());
        assertEquals(sgr.getDomainCode(), lst.get(0).getDomain().getDomainCode());
        assertEquals(roToUpdate.getParticipantIdentifier(), lst.get(0).getParticipantIdentifier());
    }
*/
    /*
    @Test
    @Transactional
    public void updateListSMLRecordsAddDomain() {

        // given
        DBDomain dbDomain1 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBDomain dbDomain2 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_2).get();
        DBResource dbServiceGroup1 = TestDBUtils.createDBServiceGroupRandom();
        DBResource dbServiceGroup2 = TestDBUtils.createDBServiceGroupRandom();
        dbServiceGroup1.addDomain(dbDomain1);
        dbServiceGroup2.addDomain(dbDomain1);
        serviceGroupDao.persistFlushDetach(dbServiceGroup1);
        serviceGroupDao.persistFlushDetach(dbServiceGroup2);
        ServiceGroupRO serviceGroupROAdd = TestROUtils.createROServiceGroupForDomains(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                dbDomain1, dbDomain2);
        ServiceGroupRO serviceGroupROUpdate = testInstance.getServiceGroupById(dbServiceGroup1.getId());
        ServiceGroupRO serviceGroupRORemove = testInstance.getServiceGroupById(dbServiceGroup2.getId());
        serviceGroupROAdd.setStatus(EntityROStatus.NEW.getStatusNumber());
        serviceGroupRORemove.setStatus(EntityROStatus.REMOVE.getStatusNumber());
        serviceGroupROUpdate.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        serviceGroupROUpdate.getServiceGroupDomains().clear();
        ServiceGroupDomainRO sgr = new ServiceGroupDomainRO();
        sgr.setDomainCode(dbDomain2.getDomainCode());
        sgr.setSmlSubdomain(dbDomain2.getSmlSubdomain());
        sgr.setDomainId(dbDomain2.getId());
        serviceGroupROUpdate.getServiceGroupDomains().add(sgr);

        List<ServiceGroupRO> lstRo = Arrays.asList(serviceGroupROAdd, serviceGroupRORemove, serviceGroupROUpdate);

        List<ParticipantSMLRecord> lst = testInstance.updateServiceGroupList(lstRo);
        // then
        assertEquals(5, lst.size());
        assertEquals(SMLAction.REGISTER, lst.get(0).getStatus());
        assertEquals(serviceGroupROAdd.getParticipantIdentifier(), lst.get(0).getParticipantIdentifier());
        assertEquals(SMLAction.REGISTER, lst.get(1).getStatus());
        assertEquals(serviceGroupROAdd.getParticipantIdentifier(), lst.get(1).getParticipantIdentifier());


        assertEquals(SMLAction.UNREGISTER, lst.get(2).getStatus());
        assertEquals(serviceGroupRORemove.getParticipantIdentifier(), lst.get(2).getParticipantIdentifier());

        assertEquals(SMLAction.REGISTER, lst.get(3).getStatus());
        assertEquals(dbDomain2.getDomainCode(), lst.get(3).getDomain().getDomainCode());
        assertEquals(serviceGroupROUpdate.getParticipantIdentifier(), lst.get(3).getParticipantIdentifier());
        assertEquals(SMLAction.UNREGISTER, lst.get(4).getStatus());
        assertEquals(dbDomain1.getDomainCode(), lst.get(4).getDomain().getDomainCode());

        assertEquals(5, integrationMock.getParticipantManagmentClientMocks().size());
    }
*/
}
