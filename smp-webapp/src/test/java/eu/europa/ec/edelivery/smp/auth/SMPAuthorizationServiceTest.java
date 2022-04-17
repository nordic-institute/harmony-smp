package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SMPAuthorizationServiceTest {

    DBUser user = null;
    SecurityContext mockSecurityContextSystemAdmin = null;
    SecurityContext mockSecurityContextSMPAdmin = null;
    SecurityContext mockSecurityContextSGAdmin = null;
    ServiceGroupService serviceGroupService = Mockito.mock(ServiceGroupService.class);

    SMPAuthorizationService testInstance = new SMPAuthorizationService(serviceGroupService);


    @Before
    public void setup() {

        user = new DBUser();
        user.setId((long) 10);


        mockSecurityContextSystemAdmin = new SecurityContext() {
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("sys_admin", "test123", Collections.singletonList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN), user);

            @Override
            public Authentication getAuthentication() {
                return smpa;
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
        mockSecurityContextSMPAdmin = new SecurityContext() {
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("smp_admin", "test123", Collections.singletonList(SMPAuthority.S_AUTHORITY_SMP_ADMIN), user);

            @Override
            public Authentication getAuthentication() {
                return smpa;
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
        mockSecurityContextSGAdmin = new SecurityContext() {
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("sg_admin", "test123", Collections.singletonList(SMPAuthority.S_AUTHORITY_SERVICE_GROUP), user);

            @Override
            public Authentication getAuthentication() {
                return smpa;
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
    }

    @Test
    public void isSystemAdministratorNotLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSGAdmin);
        // when then
        boolean bVal = testInstance.isSystemAdministrator();
        assertFalse(bVal);
    }

    @Test
    public void isSystemAdministratorLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isSystemAdministrator();
        assertTrue(bVal);
    }

    @Test(expected = BadCredentialsException.class)
    public void isCurrentlyLoggedInNotLogedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);

        testInstance.isCurrentlyLoggedIn("Invalid or null authentication for the session!");
    }

    @Test
    public void isCurrentlyLoggedIn() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isCurrentlyLoggedIn(SessionSecurityUtils.encryptedEntityId(10L));
        assertTrue(bVal);
    }

    @Test
    public void isAuthorizedForManagingTheServiceMetadataGroupSMPAdmin() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSMPAdmin);
        // when then smp admin is always authorized to manage SMP
        boolean bVal = testInstance.isAuthorizedForManagingTheServiceMetadataGroup(10L);
        assertTrue(bVal);
    }

    @Test
    public void isAuthorizedForManagingTheServiceMetadataGroupSYSAdmin() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then system admin is not  authorized to manage SMP
        boolean bVal = testInstance.isAuthorizedForManagingTheServiceMetadataGroup(10L);
        assertFalse(bVal);
    }

}