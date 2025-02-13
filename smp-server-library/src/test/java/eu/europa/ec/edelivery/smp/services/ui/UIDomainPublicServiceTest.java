/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = UIDomainEditService.class)
class UIDomainPublicServiceTest   extends AbstractJunit5BaseDao {

    @Autowired
    UIDomainEditService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();

    }

    @Test
    void testGetTableList() {
        ServiceResult<DomainPublicRO>  result = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(3, result.getCount().intValue());
    }

    @Test
    void testGetAllDomainsForDomainAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForDomainAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllDomainsForDomainAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForDomainAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllDomainsForGroupAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForGroupAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllDomainsForGroupAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForGroupAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllDomainsForResourceAdminUser() {
        List<DomainRO> result = testInstance.getAllDomainsForResourceAdminUser(testUtilsDao.getUser1().getId());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllDomainsForResourceAdminUser3() {
        List<DomainRO> result = testInstance.getAllDomainsForResourceAdminUser(testUtilsDao.getUser3().getId());
        assertEquals(0, result.size());
    }

    @Test
    void testGetDomainMembers() {
        ServiceResult<MemberRO>  result = testInstance.getDomainMembers(testUtilsDao.getD1().getId(), -1, -1, null);
        assertEquals(1, result.getCount().intValue());
        assertEquals(1, result.getServiceEntities().size());
    }

    @Test
    void testGetResourceDefDomainList() {
        List<ResourceDefinitionRO>  result = testInstance.getResourceDefDomainList(testUtilsDao.getD1().getId());
        assertEquals(2, result.size());
    }

    @Test
    void testGetResourceDefDomainListFal() {
        BadRequestException result = assertThrows(BadRequestException.class, () ->
            testInstance.getResourceDefDomainList(-100L));

        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Domain does not exist in database"));

    }
}
