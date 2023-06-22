package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CredentialService.class})
public class CredentialServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    CredentialService testInstance;

    @Before
    public void beforeMethods() throws IOException {
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        resetKeystore();
        configurationDao.refreshProperties();
    }

    @Test
    public void authenticateByUsernamePasswordTestBadUsername() {
        // given
        String username = "usernameNotExists";
        String password = "password";
        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByUsernamePassword(username, password));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByUsernamePasswordTestOk() {
        // given
        String username = TestConstants.USERNAME_1;
        String password = TestConstants.USERNAME_1_PASSWORD;
        // when
        Authentication authentication = testInstance.authenticateByUsernamePassword(username, password);
        // then
        assertEquals(username, authentication.getName());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @Ignore
    public void authenticateByUsernamePasswordTestBadPassword() {
        // given
        String username = TestConstants.USERNAME_1;
        String password = "password";
        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByUsernamePassword(username, password));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByUsernamePasswordInactive() {
        testUtilsDao.deactivateUser(TestConstants.USERNAME_1);

        // given
        String username = TestConstants.USERNAME_1;
        String password = TestConstants.USERNAME_1_PASSWORD;
        // when then
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByUsernamePassword(username, password));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByUsernameCredentialsInactive() {
        DBCredential credential = testUtilsDao.getUser1().getUserCredentials().get(0);
        credential.setActive(false);
        testUtilsDao.merge(credential);

        // given
        String username = TestConstants.USERNAME_1;
        String password = TestConstants.USERNAME_1_PASSWORD;
        // when then
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByUsernamePassword(username, password));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByUsernameCredentialsSuspended() {
        DBCredential credential = testUtilsDao.getUser1().getUserCredentials().get(0);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        credential.setSequentialLoginFailureCount(100);
        testUtilsDao.merge(credential);
        testUtilsDao.clear();

        // given
        String username = TestConstants.USERNAME_1;
        String password = TestConstants.USERNAME_1_PASSWORD;
        // when then
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByUsernamePassword(username, password));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("The user credential is suspended. Please try again later or contact your administrator."));
    }

    @Test
    public void authenticateByUsernameCredentialsNotSuspendedAnymore() {
        DBCredential credential = testUtilsDao.getUser1().getUserCredentials().get(0);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now().minusDays(100));
        credential.setSequentialLoginFailureCount(100);
        testUtilsDao.merge(credential);
        testUtilsDao.clear();

        // given
        String username = TestConstants.USERNAME_1;
        String password = TestConstants.USERNAME_1_PASSWORD;
        // when then
        Authentication authentication = testInstance.authenticateByUsernamePassword(username, password);
        // then
        assertEquals(username, authentication.getName());
    }


    @Test
    public void authenticateByAccessTokenBadUsername() {
        // given
        String accessTokenName = "usernameNotExists";
        String accessTokenValue = "password";
        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByAccessTokenTestOk() {
        // given
        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = TestConstants.USERNAME_3_AT_PASSWORD;
        // when
        Authentication authentication = testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue);
        // then
        assertEquals(TestConstants.USERNAME_3_AT, authentication.getName());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_WS_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void authenticateByAccessTokenBadPassword() {
        // given
        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = "badPassword";

        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));
    }

    @Test
    public void authenticateByAccessTokenInactive() {
        testUtilsDao.deactivateUser(TestConstants.USERNAME_3);

        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = TestConstants.USERNAME_3_AT_PASSWORD;

        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));

    }

    @Test
    public void authenticateByAccessTokenCredentialsInactive() {
        DBCredential credential = testUtilsDao.getUser3().getUserCredentials().get(0);
        credential.setActive(false);
        testUtilsDao.merge(credential);

        // given
        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = TestConstants.USERNAME_3_AT_PASSWORD;

        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed; Invalid userID or password!"));

    }

    @Test
    public void authenticateByAccessTokenSuspended() {
        DBCredential credential = testUtilsDao.getUser3().getUserCredentials().get(0);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        credential.setSequentialLoginFailureCount(100);
        testUtilsDao.merge(credential);

        // given
        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = TestConstants.USERNAME_3_AT_PASSWORD;

        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("The user credential is suspended. Please try again later or contact your administrator."));
    }

    @Test
    public void authenticateByAccessTokenCredentialsNotSuspendedAnymore() {
        DBCredential credential = testUtilsDao.getUser3().getUserCredentials().get(0);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now().minusDays(100));
        credential.setSequentialLoginFailureCount(100);
        testUtilsDao.merge(credential);

        // given
        String accessTokenName = TestConstants.USERNAME_3_AT;
        String accessTokenValue = TestConstants.USERNAME_3_AT_PASSWORD;
        // when then
        Authentication authentication = testInstance.authenticateByAuthenticationToken(accessTokenName, accessTokenValue);
        // then
        assertEquals(TestConstants.USERNAME_3_AT, authentication.getName());
    }

}
