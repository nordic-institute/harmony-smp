package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBDomainMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testIsUserDomainsMember() {
        DBUser user = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBDomain domain = TestDBUtils.createDBDomain();
        userDao.persistFlushDetach(user);
        domainDao.persistFlushDetach(domain);
        DBDomainMember domainMember = new DBDomainMember();
        domainMember.setDomain(domain);
        domainMember.setUser(user);
        testInstance.persistFlushDetach(domainMember);
        // then
        boolean result = testInstance.isUserDomainsMember(user, Collections.singletonList(domain));

        assertTrue(result);
    }

    @Test
    public void testIsUserDomainsMemberFalse() {
        DBUser user = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBDomain domain = TestDBUtils.createDBDomain();
        userDao.persistFlushDetach(user);
        domainDao.persistFlushDetach(domain);
        // then
        boolean result = testInstance.isUserDomainsMember(user, Collections.singletonList(domain));

        assertFalse(result);
    }

    @Test
    public void testIsUserDomainsMemberWithRoleTrue() {
        DBUser user = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBDomain domain = TestDBUtils.createDBDomain();
        userDao.persistFlushDetach(user);
        domainDao.persistFlushDetach(domain);
        DBDomainMember domainMember = new DBDomainMember();
        domainMember.setDomain(domain);
        domainMember.setUser(user);
        domainMember.setRole(MembershipRoleType.ADMIN);
        testInstance.persistFlushDetach(domainMember);
        // then
        boolean result = testInstance.isUserDomainMemberWithRole(user.getId(), Collections.singletonList(domain.getId()), MembershipRoleType.ADMIN);
        assertTrue(result);
        result = testInstance.isUserDomainMemberWithRole(user.getId(), Collections.singletonList(domain.getId()), MembershipRoleType.VIEWER);
        assertFalse(result);
    }
}
