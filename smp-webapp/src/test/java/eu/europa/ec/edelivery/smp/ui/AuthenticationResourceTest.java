package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationService;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.utils.SMPCookieWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import java.time.OffsetDateTime;

public class AuthenticationResourceTest {

    SMPAuthenticationService authenticationService = Mockito.mock(SMPAuthenticationService.class);
    SMPAuthorizationService authorizationService = Mockito.mock(SMPAuthorizationService.class);
    ConversionService conversionService = Mockito.mock(ConversionService.class);
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    SMPCookieWriter smpCookieWriter = Mockito.mock(SMPCookieWriter.class);
    CsrfTokenRepository csrfTokenRepository = Mockito.mock(CsrfTokenRepository.class);
    UIUserService uiUserService = Mockito.mock(UIUserService.class);

    AuthenticationResource testInstance= new AuthenticationResource(authenticationService,
            authorizationService,
            conversionService,
            configurationService,
            smpCookieWriter,
            csrfTokenRepository,
            uiUserService);
    @Test
    public void testGetUpdatedUserData() {
        UserRO user  = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().minusDays(1));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();
        Mockito.doReturn(user).when(authorizationService).sanitize(Mockito.any());

        user = testInstance.getUpdatedUserData(user);

        Assert.assertTrue(user.isShowPasswordExpirationWarning());
        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertFalse(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataDoNotShowWarning() {
        UserRO user  = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().minusDays(11));
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();
        Mockito.doReturn(user).when(authorizationService).sanitize(Mockito.any());

        user = testInstance.getUpdatedUserData(user);

        Assert.assertFalse(user.isShowPasswordExpirationWarning());
        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertFalse(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataForceChange() {
        UserRO user  = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(true).when(configurationService).getPasswordPolicyForceChangeIfExpired();
        Mockito.doReturn(user).when(authorizationService).sanitize(Mockito.any());

        user = testInstance.getUpdatedUserData(user);

        Assert.assertTrue(user.isForceChangeExpiredPassword());
        Assert.assertTrue(user.isPasswordExpired());
    }

    @Test
    public void testGetUpdatedUserDataForceChangeFalse() {
        UserRO user  = new UserRO();
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        user.setPasswordExpired(true);
        Mockito.doReturn(10).when(configurationService).getPasswordPolicyUIWarningDaysBeforeExpire();
        Mockito.doReturn(false).when(configurationService).getPasswordPolicyForceChangeIfExpired();
        Mockito.doReturn(user).when(authorizationService).sanitize(Mockito.any());

        user = testInstance.getUpdatedUserData(user);

        Assert.assertFalse(user.isForceChangeExpiredPassword());
        Assert.assertTrue(user.isPasswordExpired());
    }
}
