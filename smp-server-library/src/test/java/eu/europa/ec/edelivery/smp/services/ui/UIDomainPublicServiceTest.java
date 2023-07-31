package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = UIDomainPublicService.class)
class UIDomainPublicServiceTest   extends AbstractJunit5BaseDao {

    @Autowired
    UIDomainPublicService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();

    }

    @Test
    public void testGetTableList() {
        ServiceResult<DomainPublicRO>  result = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(2, result.getCount().intValue());
    }

    @Test
    public void testGetAllDomainsForDomainAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForDomainAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllDomainsForDomainAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForDomainAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllDomainsForGroupAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForGroupAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllDomainsForGroupAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForGroupAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllDomainsForResourceAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForResourceAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllDomainsForResourceAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForResourceAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetDomainMembers() {
        ServiceResult<MemberRO>  result = testInstance.getDomainMembers(testUtilsDao.getD1().getId(), -1, -1, null);
        assertEquals(1, result.getCount().intValue());
        assertEquals(1, result.getServiceEntities().size());
    }

    @Test
    public void testGetResourceDefDomainList() {
        List<ResourceDefinitionRO>  result = testInstance.getResourceDefDomainList(testUtilsDao.getD1().getId());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetResourceDefDomainListFal() {
        BadRequestException result = assertThrows(BadRequestException.class, () ->
            testInstance.getResourceDefDomainList(-100L));

        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Domain does not exist in database"));

    }
}
