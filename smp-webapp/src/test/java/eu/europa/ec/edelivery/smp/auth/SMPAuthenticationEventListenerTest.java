package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class SMPAuthenticationEventListenerTest {

    ConfigurationService configurationService = Mockito.mock(ConfigurationService .class);;
    // test instance
    SMPAuthenticationEventListener testInstance = new SMPAuthenticationEventListener(configurationService);


    @Test
    public void getSessionTimeoutForRolesSMPAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_SMP_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    public void getSessionTimeoutForRolesSystemAdmin() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    public void getSessionTimeoutForRolesUser() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_SERVICE_GROUP);
        // when then
        assertTimeoutForAuthorities(authorities, false);
    }

    @Test
    public void getSessionTimeoutForRolesUserAndSystem() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_SERVICE_GROUP,SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    @Test
    public void getSessionTimeoutForRolesUserAndSMP() {
        // Given
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(SMPAuthority.S_AUTHORITY_SERVICE_GROUP,SMPAuthority.S_AUTHORITY_SMP_ADMIN);
        // when then
        assertTimeoutForAuthorities(authorities, true);
    }

    public void assertTimeoutForAuthorities(Collection<? extends GrantedAuthority> authorities, boolean isAdmin){
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