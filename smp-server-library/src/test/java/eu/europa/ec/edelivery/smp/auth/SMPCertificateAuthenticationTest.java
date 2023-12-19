/*-
 * #START_LICENSE#
 * smp-server-library
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

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SMPCertificateAuthenticationTest {

    PreAuthenticatedCertificatePrincipal mockPrincipal = Mockito.mock(PreAuthenticatedCertificatePrincipal.class);
    List<GrantedAuthority> mockListAuthorities = Collections.singletonList(Mockito.mock(GrantedAuthority.class));
    DBUser mockUser = Mockito.mock(DBUser.class);
    SMPCertificateAuthentication testInstance = new SMPCertificateAuthentication(mockPrincipal, mockListAuthorities, mockUser);
    @Test
    void testGetAuthorities() {

        Collection<? extends GrantedAuthority> result = testInstance.getAuthorities();

        assertNotNull(result);
        assertEquals(mockListAuthorities, result);

    }

    @Test
    void testGetCredentials() {
        String credential = "mockCredentials";
        Mockito.when(mockPrincipal.getCredentials()).thenReturn(credential);
        Object result = testInstance.getCredentials();

        assertNotNull(result);
        assertEquals(credential, result);
    }

    @Test
    void testGetDetails() {
        Object result = testInstance.getDetails();

        assertNotNull(result);
        assertEquals(mockPrincipal, result);
    }

    @Test
    void testGetPrincipal() {
        Object result = testInstance.getPrincipal();

        assertNotNull(result);
        assertEquals(mockPrincipal, result);
    }

    @Test
    void isAuthenticated() {
        boolean result = testInstance.isAuthenticated();

        assertFalse(result);
    }

    @Test
    void testSetAuthenticated() {
        boolean b = true;
        testInstance.setAuthenticated(b);

        assertTrue(testInstance.isAuthenticated());
    }

    @Test
    void testGetName() {
        String mockname = "mockName";
        Mockito.when(mockPrincipal.getName(Mockito.anyInt())).thenReturn(mockname);

        String result = testInstance.getName();

        assertNotNull(result);
        assertEquals(mockname, result);
    }

    @Test
    void testToString() {
        String mockname = "mockName";
        Mockito.when(mockPrincipal.getName(Mockito.anyInt())).thenReturn(mockname);
        String result = testInstance.toString();

        assertNotNull(result);
        assertEquals(mockname, result);
    }
}
