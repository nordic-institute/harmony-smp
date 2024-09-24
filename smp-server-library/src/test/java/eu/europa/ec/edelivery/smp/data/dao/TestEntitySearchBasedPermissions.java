package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Test class for testing search based permissions for entities.
 * For validating the permissions and how entities are mapped to users, please see the following methods:
 * testUtilsDao.createResourcesForSearch();
 * @author Joze Rihtarsic
 * @since 5.1
 */
class TestEntitySearchBasedPermissions extends AbstractBaseDao {

    @Autowired
    DomainDao domainTestInstance;
    @Autowired
    ResourceDefDao resourceDefTestInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourcesForSearch();
    }

    @Test
    void testGetAllPublicDomains() {
        // when
        DBUser user = null;
        List<DBDomain> result = testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain(user, 1);
        Assertions.assertEquals(VisibilityType.PUBLIC, result.get(0).getVisibility());
    }

    @Test
    void testGetAllDomainsForUsersWithResources() {
        // when
        DBUser user = testUtilsDao.getUser1();
        List<DBDomain> result = testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain(user, 2);
        Assertions.assertEquals(VisibilityType.PUBLIC, result.get(0).getVisibility());
        Assertions.assertEquals(VisibilityType.PRIVATE, result.get(1).getVisibility());
    }

    @Test
    void testGetAllDomainsForUsersWithPrivateDomainMembership() {
        // when
        DBUser user = testUtilsDao.getUser3();
        List<DBDomain> result = testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain(user, 2);
        Assertions.assertEquals(VisibilityType.PUBLIC, result.get(0).getVisibility());
        Assertions.assertEquals(VisibilityType.PRIVATE, result.get(1).getVisibility());
    }

    @Test
    void testGetAllDomainsForUsersWithGroupMembershipOnPrivateDomain() {
        // when
        DBUser user = testUtilsDao.getUser4();
        List<DBDomain> result = testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain(user, 2);
        Assertions.assertEquals(VisibilityType.PUBLIC, result.get(0).getVisibility());
        Assertions.assertEquals(VisibilityType.PRIVATE, result.get(1).getVisibility());
    }

    @Test
    void testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain() {
        // when
        DBUser user = testUtilsDao.getUser5();
        List<DBDomain> result = testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain(user, 2);
        Assertions.assertEquals(VisibilityType.PUBLIC, result.get(0).getVisibility());
        Assertions.assertEquals(VisibilityType.PRIVATE, result.get(1).getVisibility());
    }

    @Test
    void testGetAllPublicResourceDefinitions() {
        // when
        testGetAllDResourceDefinitionsForUsersWithResources(null, 1);
    }

    @Test
    void testGetAllDResourceDefinitionsForUsersWithResources() {
        // givem
        DBUser user = testUtilsDao.getUser1();
        // when -then
        testGetAllDResourceDefinitionsForUsersWithResources(user, 2);
    }

    void testGetAllDResourceDefinitionsForUsersWithResources(DBUser user, long expectedCount) {
        // when
        List<DBResourceDef> result = resourceDefTestInstance.getAllResourceDefsForUser(user, -1, -1);
        Long resultCount = resourceDefTestInstance.getAllResourceDefsForUserCount(user);

        Assertions.assertEquals(expectedCount, result.size());
        Assertions.assertEquals(expectedCount, resultCount);
    }

    List<DBDomain> testGetAllDomainsForUsersWithResourceMembershipOnPrivateDomain( DBUser user, long expectedCount) {
        // when
        List<DBDomain> result = domainTestInstance.getAllDomainsForUser(user, -1, -1);
        Long resultCount = domainTestInstance.getAllDomainsForUserCount(user);

        Assertions.assertEquals(expectedCount, result.size());
        Assertions.assertEquals(expectedCount, resultCount);
        return result;
    }
}
