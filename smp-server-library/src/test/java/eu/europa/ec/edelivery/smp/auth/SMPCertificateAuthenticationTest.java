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
