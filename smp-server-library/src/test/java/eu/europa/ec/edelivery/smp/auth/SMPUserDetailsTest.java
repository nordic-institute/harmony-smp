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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SMPUserDetailsTest {

    @Test
    void testInitSMPUserDetailsTest() {
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
