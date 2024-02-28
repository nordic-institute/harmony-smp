/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SMPAuthenticationEventListenerTest {

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    // test instance
    SMPAuthenticationEventListener testInstance = new SMPAuthenticationEventListener(configurationService);


    @Test
    public void getSessionTimeoutForRolesSMPAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    @Test
    public void getSessionTimeoutForRolesSystemAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    public void getSessionTimeoutForRolesUser() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    @Test
    public void getSessionTimeoutForRolesUserAndSystem() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_USER, SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    public void getSessionTimeoutForRolesUserAndSMP() {
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
        Mockito.doReturn(secondsToTimeoutAdmin).when(configurationService).getSessionIdleTimeoutForAdmin();
        Mockito.doReturn(secondsToTimeoutUser).when(configurationService).getSessionIdleTimeoutForUser();
        // when
        int result = testInstance.getSessionTimeoutForRoles(authorities);
        //then
        assertEquals(expected, result);
    }
}
