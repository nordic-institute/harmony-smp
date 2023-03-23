package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.jasig.cas.client.validation.Assertion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SessionSecurityUtilsTest {

    @After
    public void afterUnitTest() {
        // clear authentication
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void encryptedEntityId() {
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();
        Long value = Long.valueOf(12332L);
        String result = SessionSecurityUtils.encryptedEntityId(value);

        assertNotNull(result);
        String decResult = SecurityUtils.decryptUrlSafe(token.getSecret(), result);
        assertEquals(value, Long.valueOf(decResult));
    }

    @Test
    public void decryptEntityId() {
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();
        Long value = Long.valueOf(12332L);
        String encValue = SecurityUtils.encryptURLSafe(token.getSecret(), value.toString());

        Long result = SessionSecurityUtils.decryptEntityId(encValue);

        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void getAuthenticationSecretFromSMPAuthenticationToken() {
        // given
        SMPAuthenticationToken token = setTestSMPAuthenticationToken();

        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();
        assertNotNull(result);
        assertEquals(token.getSecret(), result);
    }

    @Test
    public void getAuthenticationSecretFromCasAuthenticationToken() {
        // given
        CasAuthenticationToken token = setTestCasAuthenticationToken();


        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();
        assertNotNull(result);
        assertEquals(((SMPUserDetails) token.getUserDetails()).getSessionSecret(), result);
    }

    @Test
    public void getAuthenticationSecretNotSupported() {
        // given
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(token);

        SecurityUtils.Secret result = SessionSecurityUtils.getAuthenticationSecret();

        assertNull(result);
    }

    @Test
    public void getAuthenticationName() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String testName = "testName";
        Mockito.doReturn(testName).when(authentication).getName();

        String result = SessionSecurityUtils.getAuthenticationName();

        Assert.assertNotNull(result);
        Assert.assertEquals(testName, result);
    }

    @Test
    public void getSessionAuthenticationClasses() {
        List<Class> list = SessionSecurityUtils.getSessionAuthenticationClasses();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(SMPAuthenticationToken.class));
        Assert.assertTrue(list.contains(CasAuthenticationToken.class));
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
