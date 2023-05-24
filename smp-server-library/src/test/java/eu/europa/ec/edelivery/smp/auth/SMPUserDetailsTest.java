package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SMPUserDetailsTest {

    @Test
    public void testInitSMPUserDetailsTest() {
        DBUser user = new DBUser();
        SecurityUtils.Secret secret = SecurityUtils.generatePrivateSymmetricKey(true);
        List<SMPAuthority> authorityList = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);

        SMPUserDetails testInstance = new SMPUserDetails(user,secret, authorityList);
        testInstance.setCasAuthenticated(true);

        assertEquals(user, testInstance.getUser());
        assertEquals(secret, testInstance.getSessionSecret());
        assertEquals(1, testInstance.getAuthorities().size());
        assertTrue(testInstance.getAuthorities().contains(SMPAuthority.S_AUTHORITY_USER));
        assertTrue(testInstance.isCasAuthenticated());
        assertEquals(user.isActive(), testInstance.isEnabled());
        // default values
        assertNull(testInstance.getPassword());
        assertTrue(testInstance.isAccountNonExpired());
        assertTrue(testInstance.isAccountNonLocked());
        assertTrue(testInstance.isCredentialsNonExpired());

    }

}
