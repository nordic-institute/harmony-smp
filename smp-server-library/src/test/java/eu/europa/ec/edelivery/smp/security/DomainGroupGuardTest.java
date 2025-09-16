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
package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.security.PreAuthenticatedTokenPrincipal;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DomainGroupGuardTest extends AbstractJunit5BaseDao {

    @Autowired
    DomainGroupGuard testInstance;

    ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);
    SMPUserDetails userDetails = Mockito.mock(SMPUserDetails.class);
    File localeFolder = new File("target/locales");
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @BeforeEach
    public void prepareDatabase() {
        SecurityContextHolder.clearContext();
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();
        Mockito.when(configurationService.getLocaleFolder()).thenReturn(localeFolder);
    }


    @Test
    void testResolveAndAuthorizeForDomainDeleteInvalidRequestNotUser() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainDeleteInvalidRequestUserNotAuthorized() {
        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser3());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainCreateInvalidRequestUserNotAuthorized() {
        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser3());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    @Transactional
    void testResolveAndAuthorizeForDomainDeleteUserAuthorized() {
        configureMockSecurityContext(testUtilsDao.getUser1());

        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails);
        assertNotNull(domain);
    }

    @Test
    @Transactional
    void testResolveAndAuthorizeForDomainDeleteCreateAuthorized() {
        configureMockSecurityContext(testUtilsDao.getUser1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails);
        assertNotNull(domain);
    }

    @Test
    void testResolveAndAuthorizeForDomainCreateInvalidRequestNotUser() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainNoUserOK() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.READ);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails);
        assertNotNull(domain);
    }

    @Test
    void testResolveAndAuthorizeForDomain() {
        when(userDetails.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.READ);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, userDetails);
        assertNotNull(domain);
    }

    @Test
    void testCanReadPrivateDomainAnonymous() {
        DBDomain domain = Mockito.mock(DBDomain.class);
        when(domain.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(userDetails.getUser()).thenReturn(null);
        boolean result = testInstance.canRead(userDetails, domain);
        assertFalse(result);
    }

    @Test
    void testCanReadPrivateDomainUnAuthorized() {
        DBDomain domain = Mockito.mock(DBDomain.class);
        DBUser user = Mockito.mock(DBUser.class);
        when(domain.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(userDetails.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(-100L);
        when(domain.getId()).thenReturn(-100L);
        // then user is not authorized to read the domain
        boolean result = testInstance.canRead(userDetails, domain);
        assertFalse(result);
    }

    private void configureMockSecurityContext(DBUser user) {
        PreAuthenticatedTokenPrincipal principal = new PreAuthenticatedTokenPrincipal(user.getUsername(), "password");
        SMPAuthenticationToken token = new SMPAuthenticationToken(principal, null, userDetails);
        SecurityContextHolder.getContext().setAuthentication(token);
        when(userDetails.getUser()).thenReturn(user);
    }
}
