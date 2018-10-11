package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
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
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceGroupDaoIntegrationTest extends ServiceGroupDaoIntegrationBase {

    @Test
    @Transactional
    public void persistServiceGroup() {
        //  given
        DBDomain d = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();

        DBServiceGroup sg = new DBServiceGroup();
        sg.setParticipantIdentifier(TEST_SG_ID_1);
        sg.setParticipantScheme(TEST_SG_SCHEMA_1);
        sg.addDomain(d);

        // when
        testInstance.persistFlushDetach(sg);

        // then
        DBServiceGroup res = testInstance.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1).get();
        assertTrue(sg != res); // test different object instance
        assertNotNull(res.getId());
        assertEquals(TEST_SG_ID_1, res.getParticipantIdentifier()); // test equal method - same entity
        assertEquals(TEST_SG_SCHEMA_1, res.getParticipantScheme()); // test equal method - same entity
        assertEquals(1, res.getServiceGroupDomains().size()); // domain must be loaded
        assertEquals(d.getDomainCode(), res.getServiceGroupDomains().get(0).getDomain().getDomainCode()); // test loaded Domain

    }

    @Test
    @Transactional
    public void persistServiceGroupExtension() {
        // given
        DBDomain d = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();

        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();
        byte[] extension = String.format(TestConstants.SIMPLE_EXTENSION_XML, UUID.randomUUID().toString()).getBytes();
        sg.setExtension(extension);
        sg.addDomain(d);

        // when
        testInstance.persistFlushDetach(sg);

        // then
        DBServiceGroup res = testInstance.find(sg.getId());
        assertTrue(sg != res); // test different object instance
        assertEquals(sg, res); // test equal method - same entity
        assertEquals(sg.getParticipantIdentifier(), res.getParticipantIdentifier()); // test equal method - same entity
        assertEquals(sg.getParticipantScheme(), res.getParticipantScheme()); // test equal method - same entity
        assertTrue(Arrays.equals(extension, res.getExtension())); // test loaded Domain
    }

    @Test
    public void updateServiceGroupExtension() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroup();
        byte[] extension1 = String.format(TestConstants.SIMPLE_EXTENSION_XML, UUID.randomUUID().toString()).getBytes();
        // when
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        res.setExtension(extension1);
        update(res);

        // then
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(res != res2); // test different object instance
        assertEquals(res, res2); // test equal method - same entity
        assertEquals(res.getParticipantIdentifier(), res2.getParticipantIdentifier()); // test equal method - same entity
        assertEquals(res.getParticipantScheme(), res2.getParticipantScheme()); // test equal method - same entity
        assertTrue(Arrays.equals(extension1, res2.getExtension())); // test loaded Domain
    }

    @Test
    @Transactional
    public void persistTwoDomainServiceGroup() {
        // given
        DBDomain d2 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(d2);
        DBServiceGroup sg = createAndSaveNewServiceGroup();

        // when
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(sg != res); // test different object instance
        assertEquals(1, res.getServiceGroupDomains().size()); // test equal method - same entity
        assertTrue(res.getServiceGroupForDomain(TEST_DOMAIN_CODE_1).isPresent());
        res.addDomain(d2);
        update(res); // execute

        // than
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(res != res2); // test different object instance
        assertEquals(2, res2.getServiceGroupDomains().size()); // test equal method - same entity
        assertTrue(res2.getServiceGroupForDomain(TEST_DOMAIN_CODE_2).isPresent());
    }

    @Test
    @Transactional
    public void removeDomainServiceGroup() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroup();
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(sg != res); // test different object instance
        assertEquals(1, res.getServiceGroupDomains().size()); // test equal method - same entity
        assertTrue(res.getServiceGroupForDomain(TEST_DOMAIN_CODE_1).isPresent());

        // when
        res.removeDomain(TEST_DOMAIN_CODE_1);
        update(res);

        // then
        DBServiceGroup res2 = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(res != res2); // test different object instance
        assertEquals(0, res2.getServiceGroupDomains().size()); // test equal method - same entity
    }

    @Test
    public void findServiceGroupNotExists() {
        // given
        createAndSaveNewServiceGroup();

        // then
        Optional<DBServiceGroup> res = testInstance.findServiceGroup(TestConstants.TEST_SG_ID_2, TestConstants.TEST_SG_SCHEMA_1);
        assertFalse(res.isPresent());
    }

    @Test
    public void removeEmptyServiceGroup() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroup();

        // than
        Optional<DBServiceGroup> optRes = testInstance.findServiceGroup(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
        assertTrue(optRes.isPresent());
        testInstance.removeServiceGroup(optRes.get());

        // test
        Optional<DBServiceGroup> optResDel = testInstance.findServiceGroup(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
        assertFalse(optResDel.isPresent());
    }

}