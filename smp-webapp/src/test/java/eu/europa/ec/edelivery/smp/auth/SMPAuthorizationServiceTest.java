package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SMPAuthorizationServiceTest {

    UserRO user = null;
    SecurityContext mockSecurityContextSystemAdmin = null;
    SecurityContext mockSecurityContextSMPAdmin = null;
    ServiceGroupService serviceGroupService = Mockito.mock(ServiceGroupService.class);
    ConversionService conversionService = Mockito.mock(ConversionService.class);
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    UserDao userDao = Mockito.mock(UserDao.class);
    DomainMemberDao domainMemberDao = Mockito.mock(DomainMemberDao.class);

    SMPAuthorizationService testInstance = new SMPAuthorizationService(serviceGroupService, conversionService,
            configurationService, userDao, domainMemberDao);


    @Before
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
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("sg_admin", "test123", sysUserDetails);

            @Override
            public Authentication getAuthentication() {
                return smpa;
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        };
        mockSecurityContextSMPAdmin = new SecurityContext() {
            SMPAuthenticationToken smpa = new SMPAuthenticationToken("smp_admin", "test123", smpUserDetails);

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
    public void isSystemAdministratorLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isSystemAdministrator();
        assertTrue(bVal);
    }

    @Test(expected = BadCredentialsException.class)
    public void isCurrentlyLoggedInNotLoggedIn() {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);

        testInstance.isCurrentlyLoggedIn("Invalid or null authentication for the session!");
    }

    @Test
    public void isCurrentlyLoggedIn() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then
        boolean bVal = testInstance.isCurrentlyLoggedIn(SessionSecurityUtils.encryptedEntityId(10L));
        assertTrue(bVal);
    }

    @Test
    public void isAuthorizedForManagingTheServiceMetadataGroupSMPAdmin() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSMPAdmin);
        // when then smp admin is always authorized to manage SMP
        boolean bVal = testInstance.isAuthorizedForManagingTheServiceMetadataGroup(10L);
        assertTrue(bVal);
    }

    @Test
    public void isAuthorizedForManagingTheServiceMetadataGroupSYSAdmin() throws Exception {
        // given
        SecurityContextHolder.setContext(mockSecurityContextSystemAdmin);
        // when then system admin is not  authorized to manage SMP
        boolean bVal = testInstance.isAuthorizedForManagingTheServiceMetadataGroup(10L);
        assertFalse(bVal);
    }

    @Test
    public void testGetUpdatedUserData() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().minusDays(1));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        Assert.assertTrue(user.isShowPasswordExpirationWarning());
        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertFalse(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataAboutToExpireNoWarning() {
        UserRO user = new UserRO();
        // password will expire in 11 days. But the warning is 10 days before expire
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(11));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        Assert.assertFalse(user.isShowPasswordExpirationWarning());
        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertFalse(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataAboutToExpireShowWarning() {
        UserRO user = new UserRO();
        // password will expire in 9 days. Warning is 10 days before expire
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(9));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        Assert.assertTrue(user.isShowPasswordExpirationWarning());
        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertFalse(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataForceChange() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(true).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        Assert.assertTrue(user.isForceChangeExpiredPassword());
        Assert.assertTrue(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataForceChangeFalse() {
        UserRO user = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();

        user = testInstance.getUpdatedUserData(user);

        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertTrue(user.isPasswordExpired());
    }

}
