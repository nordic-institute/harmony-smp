package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SMPAuthorizationServiceTest {

    DBUser mockUser = null;
    SecurityContext mockSecurityContextSystemAdmin = null;

    SecurityContext mockSecurityContextSGAdmin = null;

    SMPAuthorizationService testInstance = new SMPAuthorizationService();

    @Before
    public void setup() {

        DBUser user = new DBUser();
        user.setId((long) 10);



        mockSecurityContextSystemAdmin = new SecurityContext() {
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("smp_admin", "test123", Collections.singletonList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN), user);
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

    @Test
    public void isCurrentlyLoggedInNotLogedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);

        boolean bVal = testInstance.isCurrentlyLoggedIn((long) 1);
        assertFalse(bVal);
    }

    @Test
    public void isCurrentlyLoggedIn() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isCurrentlyLoggedIn((long) 10);
        assertTrue(bVal);
    }

    public void sanitize() {

    }
}