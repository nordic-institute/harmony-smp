package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SMPUserDetailsTest {

    @Test
    public void testInitSMPUserDetailsTest() {
        DBUser user = new DBUser();
        SecurityUtils.Secret secret = SecurityUtils.generatePrivateSymmetricKey();
        List<SMPAuthority> authorityList = Collections.singletonList(SMPAuthority.S_AUTHORITY_SERVICE_GROUP);

        SMPUserDetails testInstance = new SMPUserDetails(user,secret, authorityList);
        testInstance.setCasAuthenticated(true);

        assertEquals(user, testInstance.getUser());
        assertEquals(secret, testInstance.getSessionSecret());
        assertEquals(1, testInstance.getAuthorities().size());
        assertTrue(testInstance.getAuthorities().contains(SMPAuthority.S_AUTHORITY_SERVICE_GROUP));
        assertTrue(testInstance.isCasAuthenticated());
        assertEquals(user.isActive(), testInstance.isEnabled());
        // default values
        assertNull(testInstance.getPassword());
        assertTrue(testInstance.isAccountNonExpired());
        assertTrue(testInstance.isAccountNonLocked());
        assertTrue(testInstance.isCredentialsNonExpired());

    }

}