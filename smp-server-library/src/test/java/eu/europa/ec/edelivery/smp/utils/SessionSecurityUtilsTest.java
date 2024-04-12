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
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.jasig.cas.client.validation.Assertion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2q
 */
class SessionSecurityUtilsTest {

    @AfterEach
    public void afterUnitTest() {
        // clear authentication
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void encryptedEntityId() {
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();
        Long value = 12332L;
        String result = SessionSecurityUtils.encryptedEntityId(value);

        assertNotNull(result);
        String decResult = SecurityUtils.decryptUrlSafe(token.getSecret(), result);
        assertEquals(value, Long.valueOf(decResult.substring(0, decResult.indexOf('#'))));
    }

    @Test
    void decryptEntityId() {
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();
        Long value = 12332L;
        String encValue = SecurityUtils.encryptURLSafe(token.getSecret(), value.toString());

        Long result = SessionSecurityUtils.decryptEntityId(encValue);

        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    void getAuthenticationSecretFromSMPAuthenticationToken() {
        // given
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();

        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();
        assertNotNull(result);
        assertEquals(token.getSecret(), result);
    }

    @Test
    void getAuthenticationSecretFromCasAuthenticationToken() {
        // given
        CasAuthenticationToken token = setTestCasAuthenticationToken();


        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();
        assertNotNull(result);
        assertEquals(((SMPUserDetails) token.getUserDetails()).getSessionSecret(), result);
    }

    @Test
    void getAuthenticationSecretNotSupported() {
        // given
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(token);

        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();

        assertNull(result);
    }

    @Test
    void getAuthenticationName() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String testName = "testName";
        Mockito.doReturn(testName).when(authentication).getName();

        String result = SessionSecurityUtils.getAuthenticationName();

        assertNotNull(result);
        assertEquals(testName, result);
    }

    @Test
    void getSessionAuthenticationClasses() {
        List<Class> list = SessionSecurityUtils.getSessionAuthenticationClasses();
        assertEquals(4, list.size());
        assertTrue(list.contains(SMPAuthenticationToken.class));
        assertTrue(list.contains(CasAuthenticationToken.class));
    }

    public SMPAuthenticationToken setTestSMPAuthenticationToken() {
        SecurityUtils.Secret secret = SecurityUtils.generatePrivateSymmetricKey(true);
        SMPAuthenticationToken token = new SMPAuthenticationToken(null, null, new SMPUserDetails(null, secret, null));
        SecurityContextHolder.getContext().setAuthentication(token);
        return token;
    }

    public CasAuthenticationToken setTestCasAuthenticationToken() {
        SecurityUtils.Secret secret = SecurityUtils.generatePrivateSymmetricKey(true);
        List<SMPAuthority> smpAuthorities = Collections.singletonList(SMPAuthority.S_AUTHORITY_USER);
        CasAuthenticationToken token = new CasAuthenticationToken("test", "test", "test", smpAuthorities,
                new SMPUserDetails(null, secret, smpAuthorities), Mockito.mock(Assertion.class));
        SecurityContextHolder.getContext().setAuthentication(token);
        return token;
    }
}
