package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ResourceMemberDaoTest extends AbstractBaseDao {

    @Autowired
    UserDao userDao;
    @Autowired
    ResourceDao resourceDao;
    @Autowired
    ResourceMemberDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        testUtilsDao.createResources();
    }

    @Test
    public void testIsUserDomainsMember() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();

        DBResourceMember resourceMember = new DBResourceMember();

        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        testInstance.persistFlushDetach(resourceMember);
        // then
        boolean result = testInstance.isUserResourceMember(user, resource);

        assertTrue(result);
    }

    @Test
    public void testIsUserDomainsMemberFalse() {
        // user is not member to resource D2G1RD1 by default
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        // then
        boolean result = testInstance.isUserResourceMember(user, resource);

        assertFalse(result);
    }

    @Test
    public void testIsUserDomainsMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();

        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.ADMIN);
        testInstance.persistFlushDetach(resourceMember);
        // then
        boolean result = testInstance.isUserResourceMemberWithRole(user.getId(), resource.getId(), MembershipRoleType.ADMIN);
        assertTrue(result);
        result = testInstance.isUserResourceMemberWithRole(user.getId(), resource.getId(), MembershipRoleType.VIEWER);
        assertFalse(result);
    }


    @Test
    public void isUserAnyDomainResourceMember() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();
        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.ADMIN);
        testInstance.persistFlushDetach(resourceMember);
        // when
        boolean result = testInstance.isUserAnyDomainResourceMember(user, testUtilsDao.getD2());
        // then
        assertTrue(result);
        // when
        result = testInstance.isUserAnyDomainResourceMember(user, testUtilsDao.getD1());
        // then
        assertFalse(result);

    }

    @Test
    public void isUserAnyDomainResourceMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();
        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.VIEWER);
        testInstance.persistFlushDetach(resourceMember);
        // when
        boolean result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD2(), MembershipRoleType.VIEWER);
        // then
        assertTrue(result);
        // when
        result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD2(), MembershipRoleType.ADMIN);
        // then
        assertFalse(result);

        result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD1(), MembershipRoleType.VIEWER);
        // then
        assertFalse(result);
    }

}
