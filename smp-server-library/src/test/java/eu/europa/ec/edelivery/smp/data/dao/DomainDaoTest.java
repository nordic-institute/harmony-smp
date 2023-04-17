package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The group of resources with shared resource management rights. The user with group admin has rights to create/delete
 * resources for the group.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainDaoTest extends AbstractBaseDao {

    @Autowired
    DomainDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();

    }
    @Test
    public void getDomainsByUserIdAndRolesCount() {
        // one for domain 1
        Long cnt = testInstance.getDomainsByUserIdAndDomainRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, cnt.intValue());

        // one for domain 2
        cnt = testInstance.getDomainsByUserIdAndDomainRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndDomainRolesCount(testUtilsDao.getUser1().getId());
        assertEquals(2, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndDomainRolesCount(testUtilsDao.getUser1().getId(),  MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, cnt.intValue());
    }

    @Test
    public void getDomainsByUserIdAndRoles() {
        // one for domain 1
        List<DBDomain> result = testInstance.getDomainsByUserIdAndDomainRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD1(), result.get(0));

        // one for domain 2
        result = testInstance.getDomainsByUserIdAndDomainRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD2(), result.get(0));

        result = testInstance.getDomainsByUserIdAndDomainRoles(testUtilsDao.getUser2().getId(), MembershipRoleType.VIEWER);
        assertEquals(0, result.size());

        result = testInstance.getDomainsByUserIdAndDomainRoles(testUtilsDao.getUser1().getId());
        assertEquals(2, result.size());

        result = testInstance.getDomainsByUserIdAndDomainRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, result.size());
    }

    @Test
    public void getDomainsByUserIdAndGroupRolesCount() {
        // one for domain 1
        Long cnt = testInstance.getDomainsByUserIdAndGroupRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, cnt.intValue());

        // one for domain 2
        cnt = testInstance.getDomainsByUserIdAndGroupRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndGroupRolesCount(testUtilsDao.getUser1().getId());
        assertEquals(2, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndGroupRolesCount(testUtilsDao.getUser1().getId(),  MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, cnt.intValue());
    }

    @Test
    public void getDomainsByUserIdAndGroupRoles() {
        // one for domain 1
        List<DBDomain> result = testInstance.getDomainsByUserIdAndGroupRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD1(), result.get(0));

        // one for domain 2
        result = testInstance.getDomainsByUserIdAndGroupRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD2(), result.get(0));

        result = testInstance.getDomainsByUserIdAndGroupRoles(testUtilsDao.getUser2().getId(), MembershipRoleType.VIEWER);
        assertEquals(0, result.size());

        result = testInstance.getDomainsByUserIdAndGroupRoles(testUtilsDao.getUser1().getId());
        assertEquals(2, result.size());

        result = testInstance.getDomainsByUserIdAndGroupRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, result.size());
    }

    @Test
    public void getDomainsByUserIdAndResourceRolesCount() {
        // one for domain 1
        Long cnt = testInstance.getDomainsByUserIdAndResourceRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, cnt.intValue());

        // one for domain 2
        cnt = testInstance.getDomainsByUserIdAndResourceRolesCount(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndResourceRolesCount(testUtilsDao.getUser1().getId());
        assertEquals(2, cnt.intValue());

        // all
        cnt = testInstance.getDomainsByUserIdAndResourceRolesCount(testUtilsDao.getUser1().getId(),  MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, cnt.intValue());
    }
    @Test
    public void getDomainsByUserIdAndResourceRoles() {
        // one for domain 1
        List<DBDomain> result = testInstance.getDomainsByUserIdAndResourceRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD1(), result.get(0));

        // one for domain 2
        result = testInstance.getDomainsByUserIdAndResourceRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getD2(), result.get(0));

        result = testInstance.getDomainsByUserIdAndResourceRoles(testUtilsDao.getUser2().getId(), MembershipRoleType.VIEWER);
        assertEquals(0, result.size());

        result = testInstance.getDomainsByUserIdAndResourceRoles(testUtilsDao.getUser1().getId());
        assertEquals(2, result.size());

        result = testInstance.getDomainsByUserIdAndResourceRoles(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER,  MembershipRoleType.ADMIN);
        assertEquals(2, result.size());
    }
}
