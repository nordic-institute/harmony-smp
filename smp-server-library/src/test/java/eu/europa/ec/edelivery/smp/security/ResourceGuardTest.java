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
package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ResourceGuardTest extends AbstractJunit5BaseDao {

    @Autowired
    ResourceGuard testInstance;
    SMPUserDetails userDetails = Mockito.mock(SMPUserDetails.class);

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();
    }

    @ParameterizedTest
    @ValueSource(strings = {"READ", "CREATE_UPDATE", "DELETE"})
    void testUserIsNotAuthorizedForActionOK(ResourceAction action) {
        // given - user is authorized - see  the createResourceMemberships
        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser1());
        boolean result1 = testInstance.userIsNotAuthorizedForAction(userDetails, action, testUtilsDao.getResourceD1G1RD1(), testUtilsDao.getD1());
        boolean result = testInstance.userIsAuthorizedForAction(userDetails, action, testUtilsDao.getResourceD1G1RD1(), testUtilsDao.getD1());

        assertTrue(result);
        assertEquals(result1, !result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"READ", "DELETE"})
    void testUserIsAuthorizedForActionOK(ResourceAction action) {
        // given - user is authorized - see  the createResourceMemberships
        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser1());
        boolean result = testInstance.userIsAuthorizedForAction(userDetails, action, testUtilsDao.getSubresourceD1G1RD1_S1());
        // then
        assertTrue(result);
    }

    @Test
    void testCanReadResourceForPrivateDomainOK() {
        // given - user is authorized - see  the createResourceMemberships
        testUtilsDao.getD1().setVisibility(VisibilityType.PRIVATE);

        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser1());
        boolean result = testInstance.canRead(userDetails, testUtilsDao.getSubresourceD1G1RD1_S1());
        // then
        assertTrue(result);
    }

    @Test
    void testCanReadResourceForPrivateDomainNotMember() {
        // given
        testUtilsDao.getD1().setVisibility(VisibilityType.PRIVATE);

        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser2());
        boolean result = testInstance.canRead(userDetails, testUtilsDao.getSubresourceD1G1RD1_S1());
        // then
        assertFalse(result);
    }

    @Test
    void testCanReadResourceForPrivateDomainAnonymous() {
        // given
        testUtilsDao.getD1().setVisibility(VisibilityType.PRIVATE);

        when(userDetails.getUser()).thenReturn(null);
        boolean result = testInstance.canRead(userDetails, testUtilsDao.getSubresourceD1G1RD1_S1());
        // then
        assertFalse(result);
    }
}
