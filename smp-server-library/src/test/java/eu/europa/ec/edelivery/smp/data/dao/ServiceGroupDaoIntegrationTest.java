package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
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

    @Test
    public void testGetServiceGroupListNotEmpty(){
        // given
        // add additional domain
        DBDomain d2 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(d2);
        createAndSaveNewServiceGroups(10, TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_ID_1);
        createAndSaveNewServiceGroups(5, TEST_DOMAIN_CODE_2, TestConstants.TEST_SG_ID_2);
        //when
        List<DBServiceGroup> res = testInstance.getServiceGroupList(-1,-1,null, null,null);
        // then
        assertNotNull(res);
        assertEquals(15, res.size());

    }


    @Test
    public void testGetCaseInsensitive(){
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroup();

        ServiceGroupFilter sf = new ServiceGroupFilter();
        sf.setParticipantSchemeLike(sg.getParticipantScheme().toLowerCase());
        sf.setParticipantIdentifierLike(sg.getParticipantIdentifier().toLowerCase());
        List<DBServiceGroup> res = testInstance.getServiceGroupList(-1,-1,null, null,sf);
        assertEquals(1, res.size());

        ServiceGroupFilter sf2 = new ServiceGroupFilter();
        sf2.setParticipantSchemeLike(sg.getParticipantScheme().toUpperCase());
        sf2.setParticipantIdentifierLike(sg.getParticipantIdentifier().toUpperCase());

        List<DBServiceGroup> res2 = testInstance.getServiceGroupList(-1,-1,null, null,sf);
        assertEquals(1, res2.size());
        assertEquals(res.get(0).getId(), res2.get(0).getId());
        assertNotEquals(sf.getParticipantIdentifierLike(), sf2.getParticipantIdentifierLike());
        assertNotEquals(sf.getParticipantSchemeLike(), sf2.getParticipantSchemeLike());


    }

    @Test
    public void testGetServiceGroupListByDomain(){
        // given
        // add additional domain
        DBDomain d2 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(d2);
        createAndSaveNewServiceGroups(10, TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_ID_1);
        createAndSaveNewServiceGroups(5, TEST_DOMAIN_CODE_2, TestConstants.TEST_SG_ID_2);

        ServiceGroupFilter sgf = new ServiceGroupFilter();
        sgf.setDomain(d2);

        //when
        List<DBServiceGroup> res = testInstance.getServiceGroupList(-1,-1,null, null,sgf);
        // then
        assertNotNull(res);
        assertEquals(5, res.size());

    }

    @Test
    public void testGetServiceGroupListByOwnerAndDomain(){
        // given
        // add additional domain
        DBUser usr1  =  userDao.findUserByUsername(TestConstants.USERNAME_1).get();
        DBUser usr2  = userDao.findUserByUsername(TestConstants.USERNAME_3).get();
        assertNotNull(usr1);
        assertNotNull(usr2);

        DBDomain d2 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(d2);

        createAndSaveNewServiceGroups(2, TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_ID_1, usr1);
        createAndSaveNewServiceGroups(3, TEST_DOMAIN_CODE_2, TestConstants.TEST_SG_ID_2, usr1);
        createAndSaveNewServiceGroups(4, TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_ID_3, usr2);
        createAndSaveNewServiceGroups(8, TEST_DOMAIN_CODE_2, TestConstants.TEST_SG_ID_4, usr2);

        // test for domain two
        ServiceGroupFilter sgf = new ServiceGroupFilter();
        //when
        sgf.setDomain(d2);
        List<DBServiceGroup> res = testInstance.getServiceGroupList(-1,-1,null, null,sgf);
        // then
        assertNotNull(res);
        assertEquals(11, res.size());

        //when
        sgf.setDomain(null);
        sgf.setOwner(usr1);
        res = testInstance.getServiceGroupList(-1,-1,null, null,sgf);
        // then
        assertNotNull(res);
        assertEquals(5, res.size());
        //when
        sgf.setDomain(d2);
        sgf.setOwner(usr2);
        res = testInstance.getServiceGroupList(-1,-1,null, null,sgf);
        // then
        assertNotNull(res);
        assertEquals(8, res.size());

    }

    @Test
    public void testGetTableListEmpty(){

        // given

        //when
        List<DBServiceGroup> res = testInstance.getServiceGroupList(-1,-1,null, null,null);
        // then
        assertNotNull(res);
        assertTrue(res.isEmpty());

    }

}