package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBDomainMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.Assert.*;
/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class GroupMemberDaoTest extends AbstractBaseDao  {

    @Autowired
    GroupMemberDao testInstance;

    @Before
    public void init() {
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        testUtilsDao.createResources();
        testInstance.clearPersistenceContext();
    }

    @Test
    public void testIsUserGroupMember() {
        DBUser user = testUtilsDao.getUser1();
        DBGroup group = testUtilsDao.getGroupD1G1();

        DBGroupMember member = new DBGroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(MembershipRoleType.ADMIN);
        testUtilsDao.persistFlushDetach(member);
        // then
        boolean result = testInstance.isUserGroupMember(user, Collections.singletonList(group));

        assertTrue(result);
    }

    @Test
    public void testIsUserGroupMemberFalse() {
        DBUser user = testUtilsDao.getUser1();
        DBGroup group = testUtilsDao.getGroupD1G1();

        // then
        boolean result = testInstance.isUserGroupMember(user, Collections.singletonList(group));

        assertFalse(result);
    }

    @Test
    public void testIsUserGroupMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBGroup group = testUtilsDao.getGroupD1G1();

        DBGroupMember member = new DBGroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(MembershipRoleType.ADMIN);
        testUtilsDao.persistFlushDetach(member);
        // then
        boolean result = testInstance.isUserGroupMemberWithRole(user.getId(), Collections.singletonList(group.getId()), MembershipRoleType.ADMIN);
        assertTrue(result);
        result = testInstance.isUserGroupMemberWithRole(user.getId(), Collections.singletonList(group.getId()), MembershipRoleType.VIEWER);
        assertFalse(result);
    }

    @Test
    public void isUserAnyDomainGroupResourceMember() {
        DBUser user = testUtilsDao.getUser1();
        DBGroup group = testUtilsDao.getGroupD1G1();

        DBGroupMember member = new DBGroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(MembershipRoleType.ADMIN);
        testUtilsDao.persistFlushDetach(member);

        boolean result = testInstance.isUserAnyDomainGroupResourceMember(user, testUtilsDao.getD1());
        assertTrue(result);
        result = testInstance.isUserAnyDomainGroupResourceMember(user, testUtilsDao.getD2());
        assertFalse(result);

    }

    @Test
    public void isUserAnyDomainGroupResourceMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBGroup group = testUtilsDao.getGroupD1G1();

        DBGroupMember member = new DBGroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(MembershipRoleType.VIEWER);
        testUtilsDao.persistFlushDetach(member);

        boolean result = testInstance.isUserAnyDomainGroupResourceMemberWithRole(user, testUtilsDao.getD1(),MembershipRoleType.VIEWER);
        assertTrue(result);
        result = testInstance.isUserAnyDomainGroupResourceMemberWithRole(user, testUtilsDao.getD1(), MembershipRoleType.ADMIN);
        assertFalse(result);
    }
}
