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
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SSO_CAS_SMP_USER_DATA_URL_PATH;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SSO_CAS_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigurationServiceTest {

    ConfigurationDao configurationDaoMock = mock(ConfigurationDao.class);
    ConfigurationService testInstance = spy(new ConfigurationService(configurationDaoMock));

    @BeforeEach
    public void setUp() {
        Mockito.clearInvocations(testInstance);
    }

    @Test
    void testGetCasUserDataURL() throws MalformedURLException {
        String casUrl = "http://test:123/path";
        String casUserDataPath = "userdata/data.hsp";
        doReturn(new URL(casUrl)).when(configurationDaoMock).getCachedPropertyValue(SSO_CAS_URL);
        doReturn(casUserDataPath).when(configurationDaoMock).getCachedPropertyValue(SSO_CAS_SMP_USER_DATA_URL_PATH);

        URL result = testInstance.getCasUserDataURL();
        assertNotNull(result);
        // expected - the same server but different context path
        assertEquals("http://test:123/" + casUserDataPath, result.toString());
    }

    @Test
    void getSessionTimeoutForRolesSMPAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    @Test
    void getSessionTimeoutForRolesSystemAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    void getSessionTimeoutForRolesUser() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    @Test
    void getSessionTimeoutForRolesUserAndSystem() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_USER, SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    void getSessionTimeoutForRolesUserAndSMP() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    public void assertTimeoutForAuthorities(Collection<? extends GrantedAuthority> authorities, boolean isAdmin) {
        // Given
        int secondsToTimeoutAdmin = 111;
        int secondsToTimeoutUser = 555;
        int expected = isAdmin ? secondsToTimeoutAdmin : secondsToTimeoutUser;
        // idle for admin
        Mockito.doReturn(secondsToTimeoutAdmin).when(testInstance).getSessionIdleTimeoutForAdmin();
        Mockito.doReturn(secondsToTimeoutUser).when(testInstance).getSessionIdleTimeoutForUser();
        // when
        int result = testInstance.getSessionTimeoutForRoles(authorities);
        //then
        assertEquals(expected, result);
    }
}
