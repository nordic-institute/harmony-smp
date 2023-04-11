package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBDomainMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainMemberDaoTest extends AbstractBaseDao {

    @Autowired
    UserDao userDao;
    @Autowired
    DomainDao domainDao;
    @Autowired
    DomainMemberDao testInstance;

    @Before
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        testUtilsDao.createDomains();
    }


    @Test
    public void testIsUserDomainsMember() {
        DBDomain domain = testUtilsDao.getD1();
        DBUser user = testUtilsDao.getUser1();
        addMemberToDomain(user, domain, MembershipRoleType.ADMIN);
        // then
        boolean result = testInstance.isUserDomainsMember(user, Collections.singletonList(domain));

        assertTrue(result);
    }

    @Test
    public void testIsUserDomainsMemberFalse() {

        // then
        boolean result = testInstance.isUserDomainsMember(testUtilsDao.getUser1(), Collections.singletonList(testUtilsDao.getD1()));

        assertFalse(result);
    }

    @Test
    public void testIsUserDomainsMemberWithRoleTrue() {
        DBDomain domain = testUtilsDao.getD1();
        DBUser user = testUtilsDao.getUser1();
        addMemberToDomain(user, domain, MembershipRoleType.ADMIN);
        // then
        boolean result = testInstance.isUserDomainMemberWithRole(user.getId(), Collections.singletonList(domain.getId()), MembershipRoleType.ADMIN);
        assertTrue(result);
        result = testInstance.isUserDomainMemberWithRole(user.getId(), Collections.singletonList(domain.getId()), MembershipRoleType.VIEWER);
        assertFalse(result);
    }

    @Test
    public void testGetDomainMembersEmpty() {
        DBDomain domain = testUtilsDao.getD1();
        // then
        Long resultCount = testInstance.getDomainMemberCount(domain.getId(), null);
        List<DBDomainMember> result = testInstance.getDomainMembers(domain.getId(), 0, 10, null);
        assertEquals(0, resultCount.intValue());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetDomainMembersOne() {
        DBDomain domain = testUtilsDao.getD1();
        DBUser user = testUtilsDao.getUser1();
        addMemberToDomain(user, domain, MembershipRoleType.ADMIN);
        // then
        Long resultCount = testInstance.getDomainMemberCount(domain.getId(), null);
        List<DBDomainMember> result = testInstance.getDomainMembers(domain.getId(), 0, 10, null);
        assertEquals(1, resultCount.intValue());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetDomainMembersOneFilter() {
        DBDomain domain = testUtilsDao.getD1();
        DBUser user = testUtilsDao.getUser1();
        addMemberToDomain(user, domain, MembershipRoleType.ADMIN);
        // then filter no match
        assertFilter("NotExistsAtAll", 0, domain);
        assertFilter(user.getUsername(), 1, domain);
        assertFilter(user.getFullName(), 1, domain);

        assertFilter(StringUtils.upperCase(user.getUsername()), 1, domain);
        assertFilter(StringUtils.upperCase(user.getFullName()), 1, domain);
        assertFilter(StringUtils.lowerCase(user.getUsername()), 1, domain);
        assertFilter(StringUtils.lowerCase(user.getFullName()), 1, domain);
        assertFilter("", 1, domain);
        assertFilter(null, 1, domain);
    }

    private void assertFilter(String filter, int expectedCount, DBDomain domain) {
        Long resultCount = testInstance.getDomainMemberCount(domain.getId(), filter);
        List<DBDomainMember> result = testInstance.getDomainMembers(domain.getId(), 0, 10, filter);
        assertEquals(expectedCount, resultCount.intValue());
        assertEquals(expectedCount, result.size());
    }

    private void addMemberToDomain(DBUser user, DBDomain domain, MembershipRoleType role) {
        DBDomainMember domainMember = new DBDomainMember();
        domainMember.setDomain(domain);
        domainMember.setUser(user);
        domainMember.setRole(role);
        testInstance.persistFlushDetach(domainMember);
    }
}
