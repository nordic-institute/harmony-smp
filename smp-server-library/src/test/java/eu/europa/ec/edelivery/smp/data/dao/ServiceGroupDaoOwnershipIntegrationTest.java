package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;


/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceGroupDaoOwnershipIntegrationTest extends ServiceGroupDaoIntegrationBase {


    @Test
    @Transactional
    public void persistNewServiceGroupWithOwner() {
        Optional<DBUser> u1 = userDao.findUserByUsername(TestConstants.USERNAME_1);
        DBServiceGroup sg =TestDBUtils.createDBServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);

        sg.getUsers().add(u1.get());

        testInstance.persistFlushDetach(sg);
        testInstance.clearPersistenceContext();

        Optional<DBServiceGroup> res = testInstance.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        assertTrue(res.isPresent());
        assertTrue(sg!=res.get());
        assertEquals(sg, res.get());
        assertEquals(1, res.get().getUsers().size());
        assertEquals(u1.get(), res.get().getUsers().toArray()[0]);
    }

    @Test
    @Transactional
    public void mergeServiceGroupWithOwner() {
        DBServiceGroup o = createAndSaveNewServiceGroup();
        Optional<DBUser> u3 = userDao.findUserByUsername(TestConstants.USERNAME_3);
        Optional<DBServiceGroup> osg = testInstance.findServiceGroup(o.getParticipantIdentifier(), o.getParticipantScheme());
        DBServiceGroup sg = osg.get();
        assertEquals(0, sg.getUsers().size());
        assertFalse(sg.getUsers().contains(u3.get()));

        sg.getUsers().add(u3.get());

        testInstance.update(sg);
        testInstance.clearPersistenceContext();

        Optional<DBServiceGroup> res = testInstance.findServiceGroup(o.getParticipantIdentifier(), o.getParticipantScheme());
        assertTrue(res.isPresent());
        assertTrue(sg!=res.get());
        assertEquals(sg, res.get());
        assertEquals(1, res.get().getUsers().size());
        assertTrue(res.get().getUsers().contains(u3.get()));
    }

    @Test
    @Transactional
    public void removeOwnerFromServiceGroup() {

        // given
        DBServiceGroup sg = createAndSaveNewServiceGroupWithUsers();
        Optional<DBUser> u1 = userDao.findUserByUsername(TestConstants.USERNAME_1);
        Optional<DBUser> u2 = userDao.findUserByCertificateId(TestConstants.USER_CERT_2);
        Optional<DBServiceGroup> osg = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme());
        DBServiceGroup sgDb = osg.get();
        assertEquals(2, sgDb.getUsers().size());
        assertTrue(sgDb.getUsers().contains(u1.get()));
        assertTrue(sgDb.getUsers().contains(u2.get()));

        // when
        sgDb.getUsers().remove(u2.get());
        testInstance.update(sgDb);
        testInstance.clearPersistenceContext();

        // then
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(sgDb!=res);
        assertEquals(sgDb, res);
        assertEquals(1, res.getUsers().size());
        assertTrue(sgDb.getUsers().contains(u1.get()));
        assertFalse(res.getUsers().contains(u2));
    }

    @Test
    @Transactional
    public void addAndRemoveOwnerFromServiceGroup() {
        // given
        DBServiceGroup sg = createAndSaveNewServiceGroupWithUsers();
        Optional<DBUser> u1 = userDao.findUserByUsername(TestConstants.USERNAME_1);
        Optional<DBUser> u2 = userDao.findUserByCertificateId(TestConstants.USER_CERT_2);
        Optional<DBUser> u3 = userDao.findUserByUsername(TestConstants.USERNAME_3);
        Optional<DBServiceGroup> osg = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme());
        DBServiceGroup sgDb = osg.get();
        assertEquals(2, sgDb.getUsers().size());
        assertTrue(sgDb.getUsers().contains(u1.get()));
        assertTrue(sgDb.getUsers().contains(u2.get()));
        assertFalse(sgDb.getUsers().contains(u3.get()));
        //
        sgDb.getUsers().add(u3.get());
        sgDb.getUsers().remove(u2.get());
        testInstance.update(sgDb);
        testInstance.clearPersistenceContext();
        //then
        DBServiceGroup res = testInstance.findServiceGroup(sg.getParticipantIdentifier(), sg.getParticipantScheme()).get();
        assertTrue(sgDb!=res); // different object instances
        assertEquals(sgDb, res); // same objects
        assertEquals(2, res.getUsers().size());
        assertTrue(res.getUsers().contains(u1.get()));
        assertTrue(res.getUsers().contains(u3.get()));
        assertFalse(res.getUsers().contains(u2.get()));

    }


}