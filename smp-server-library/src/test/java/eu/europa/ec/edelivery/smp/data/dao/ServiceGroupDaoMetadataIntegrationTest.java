package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;


/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceGroupDaoMetadataIntegrationTest extends ServiceGroupDaoIntegrationBase {


    @Test
    @Transactional
    public void persistNewServiceGroupWithMetadata() {
        //  given
        DBDomain d= domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();

        DBServiceGroup sg = new DBServiceGroup();
        sg.setParticipantIdentifier(TEST_SG_ID_1);
        sg.setParticipantScheme(TEST_SG_SCHEMA_1);
        sg.addDomain(d);

        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        sg.getServiceGroupDomains().get(0).addServiceMetadata(md);

        // when
        testInstance.persistFlushDetach(sg);


        // then
        DBServiceGroup res = testInstance.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1 ).get();
        assertTrue(sg!=res); // test different object instance
        assertNotNull(res.getId());
        assertEquals(TEST_SG_ID_1, res.getParticipantIdentifier()); // test equal method - same entity
        assertEquals(TEST_SG_SCHEMA_1, res.getParticipantScheme()); // test equal method - same entity
        assertEquals(1, res.getServiceGroupDomains().size()); // domain must be loaded
        assertEquals(d.getDomainCode(), res.getServiceGroupDomains().get(0).getDomain().getDomainCode()); // test loaded Domain

    }

    @Test
    @Transactional
    public void addMetadataToServiceGroup() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroup();
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        DBServiceMetadata md = TestDBUtils.createDBServiceMetadata(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        //when
        res.getServiceGroupDomains().get(0).addServiceMetadata(md);
        assertNotNull(md.getXmlContent());
        update(res);
        testInstance.clearPersistenceContext();
        // then
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertNotNull(res2);
        assertEquals(1, res2.getServiceGroupDomains().get(0).getServiceMetadata().size());
        assertTrue(Arrays.equals(md.getXmlContent(), res2.getServiceGroupDomains().get(0).getServiceMetadata().get(0).getXmlContent()));
    }

    @Test
    @Transactional
    public void updateServiceMetadataXML() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroupWithMetadata();
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        DBServiceMetadata md = res.getServiceGroupDomains().get(0).getServiceMetadata(0);

        byte[]  str = TestDBUtils.generateDocumentSample(sg.getParticipantIdentifier(),sg.getParticipantScheme(),
                md.getDocumentIdentifier(),md.getDocumentIdentifierScheme(),UUID.randomUUID().toString());
        assertNotEquals (str, md.getXmlContent());
        //when
        res.getServiceGroupDomains().get(0).getServiceMetadata(0).setXmlContent(str);
        update(res);

        testInstance.clearPersistenceContext();
        // then
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertNotNull(res2);
        assertEquals(1, res2.getServiceGroupDomains().get(0).getServiceMetadata().size());
        assertTrue(Arrays.equals(str, res2.getServiceGroupDomains().get(0).getServiceMetadata().get(0).getXmlContent()));

    }

    @Test
    @Transactional
    public void removeServiceMetadata() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroupWithMetadata();
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();

        assertEquals(1, res.getServiceGroupDomains().get(0).getServiceMetadata().size());
        DBServiceMetadata dsmd = res.getServiceGroupDomains().get(0).getServiceMetadata().get(0);

        // when
        res.getServiceGroupDomains().get(0).removeServiceMetadata(dsmd);
        testInstance.update(res);
        testInstance.clearPersistenceContext();
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();

        // then
        assertEquals(0, res.getServiceGroupDomains().get(0).getServiceMetadata().size());
    }

    @Test
    public void removeDBServiceGroupWithServiceMetadata() {
      // given
        DBServiceGroup sg = createAndSaveNewServiceGroupWithMetadata();
        Optional<DBServiceGroup> resOpt1 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme());
        assertTrue(resOpt1.isPresent());

        // when
        testInstance.removeServiceGroup(resOpt1.get());
        testInstance.clearPersistenceContext();

        // then
        Optional<DBServiceGroup> resOptDS = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme());
        assertFalse(resOptDS.isPresent());
    }
}