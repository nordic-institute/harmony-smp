/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.X509CertificateTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.edelivery.smp.services.ui.UITruststoreServiceIntegrationTest.*;
import static org.junit.jupiter.api.Assertions.*;


public class CredentialServiceTest extends AbstractJunit5BaseDao {

    @Autowired
    CredentialService testInstance;
    @Autowired
    ConfigurationService configurationService;
    @Autowired
    ConfigurationDao configurationDao;

    ConfigurationService spyConfigurationService;

    @BeforeEach
    public void beforeMethods() throws IOException {
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        resetKeystore();
        configurationDao.reloadPropertiesFromDatabase();

        spyConfigurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(testInstance, "configurationService", spyConfigurationService);
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


    @Test
    public void authenticateByCertificateTokenOkWithRole() throws Exception {
        // given

        // must match the TestConstants.USER_CERT_3
        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest("CN=test example,O=European Commission,C=BE",
                new BigInteger("0dd0d2f98cc25205bc6c854d1cd88411", 16), Collections.emptyList());

        PreAuthenticatedCertificatePrincipal principal = X509CertificateUtils.extractPrincipalFromCertificate(cert);

        // when
        Authentication authentication = testInstance.authenticateByCertificateToken(principal);
        // then
        assertEquals(TestConstants.USER_CERT_3, authentication.getName());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_WS_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void authenticateByCertificateTokenNotTrusted() throws Exception {
        // given
        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest("CN=NotRegistered,O=European Commission,C=BE",
                new BigInteger("111111", 16), Collections.emptyList());

        PreAuthenticatedCertificatePrincipal principal = X509CertificateUtils.extractPrincipalFromCertificate(cert);

        // when
        BadCredentialsException result = assertThrows(BadCredentialsException.class, () -> testInstance.authenticateByCertificateToken(principal));
        // then
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.startsWith("Login failed"));
    }


    @Test
    public void testValidateCertificatePolicyLegacyMatchOk() {
        String certID = "CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE:000111";
        Mockito.doReturn(Arrays.asList(CERTIFICATE_POLICY_QCP_LEGAL, CERTIFICATE_POLICY_QCP_NATURAL))
                .when(spyConfigurationService).getAllowedCertificatePolicies();
        List<String> certPolicies = Collections.singletonList(CERTIFICATE_POLICY_QCP_NATURAL);
        testInstance.validateCertificatePolicyMatchLegacy(certID, certPolicies);
    }

    @Test
    public void testValidateCertificatePolicyLegacyMatchMatchEmpty() {
        String certID = "CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE:000111";
        Mockito.doReturn(Arrays.asList(CERTIFICATE_POLICY_QCP_LEGAL, CERTIFICATE_POLICY_QCP_NATURAL))
                .when(spyConfigurationService).getAllowedCertificatePolicies();
        List<String> certPolicies = Collections.emptyList();

        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.validateCertificatePolicyMatchLegacy(certID, certPolicies));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.startsWith("Certificate [" + certID + "] does not have CertificatePolicy extension."));
    }

    @Test
    public void testValidateCertificatePolicyLegacyMatchMismatch() {
        String certID = "CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE:000111";
        Mockito.doReturn(Arrays.asList(CERTIFICATE_POLICY_QCP_LEGAL, CERTIFICATE_POLICY_QCP_NATURAL))
                .when(spyConfigurationService).getAllowedCertificatePolicies();
        List<String> certPolicies = Collections.singletonList(CERTIFICATE_POLICY_QCP_LEGAL_QSCD);

        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.validateCertificatePolicyMatchLegacy(certID, certPolicies));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.startsWith("Certificate policy verification failed."));
    }


}
