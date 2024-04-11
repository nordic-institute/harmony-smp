/*-
 * #START_LICENSE#
 * smp-webapp
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

import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;


class SMPAuthorizationServiceTest {

    UserRO user = null;
    SecurityContext mockSecurityContextSystemAdmin = null;
    SecurityContext mockSecurityContextSMPAdmin = null;
    ConversionService conversionService = Mockito.mock(ConversionService.class);
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    UserDao userDao = Mockito.mock(UserDao.class);
    DomainMemberDao domainMemberDao = Mockito.mock(DomainMemberDao.class);
    GroupMemberDao groupMemberDao = Mockito.mock(GroupMemberDao.class);
    ResourceMemberDao resourceMemberDao = Mockito.mock(ResourceMemberDao.class);

    SMPAuthorizationService testInstance = new SMPAuthorizationService(userDao, domainMemberDao, groupMemberDao, resourceMemberDao, conversionService,
            configurationService);


    @BeforeEach
    public void setup() {

        user = new UserRO();
        SMPUserDetails sysUserDetails = new SMPUserDetails(new DBUser() {{
            setId(10L);
            setUsername("sys_admin");
        }}, null, Collections.singletonList(SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN));
        SMPUserDetails smpUserDetails = new SMPUserDetails(new DBUser() {{
            setUsername("smp_user");
        }}, null, Collections.singletonList(SMPAuthority.S_AUTHORITY_USER));

        mockSecurityContextSystemAdmin = new SecurityContext() {
            final SMPAuthenticationToken smpa = new SMPAuthenticationToken("sg_admin", "test123", sysUserDetails);

            @Override
            public Authentication getAuthentication() {
                return smpa;
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
        mockSecurityContextSMPAdmin = new SecurityContext() {
            final SMPAuthenticationToken smpa = new SMPAuthenticationToken("smp_admin", "test123", smpUserDetails);

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
    void isSystemAdministratorLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isSystemAdministrator();
        assertTrue(bVal);
    }

    @Test
    void isCurrentlyLoggedInNotLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);

        BadCredentialsException result = assertThrows(BadCredentialsException.class,
                () -> testInstance.isCurrentlyLoggedIn("InvalidUserId."));
        assertThat(result.getMessage(), containsString("Invalid user id"));
    }

    @Test
    void isCurrentlyLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isCurrentlyLoggedIn(SessionSecurityUtils.encryptedEntityId(10L));
        assertTrue(bVal);
    }

    @Test
    void testGetUpdatedUserData() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().minusDays(1));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        assertTrue(user.isShowPasswordExpirationWarning());
        assertFalse(user.isForceChangeExpiredPassword());
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testGetUpdatedUserDataAboutToExpireNoWarning() {
        UserRO user = new UserRO();
        // password will expire in 11 days. But the warning is 10 days before expire
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(11));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        assertFalse(user.isShowPasswordExpirationWarning());
        assertFalse(user.isForceChangeExpiredPassword());
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testGetUpdatedUserDataAboutToExpireShowWarning() {
        UserRO user = new UserRO();
        // password will expire in 9 days. Warning is 10 days before expire
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(9));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        assertTrue(user.isShowPasswordExpirationWarning());
        assertFalse(user.isForceChangeExpiredPassword());
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testGetUpdatedUserDataForceChange() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(true).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        assertTrue(user.isForceChangeExpiredPassword());
        assertTrue(user.isPasswordExpired());
    }

    @Test
    void testGetUpdatedUserDataForceChangeFalse() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        assertFalse(user.isForceChangeExpiredPassword());
        assertTrue(user.isPasswordExpired());
    }

}
