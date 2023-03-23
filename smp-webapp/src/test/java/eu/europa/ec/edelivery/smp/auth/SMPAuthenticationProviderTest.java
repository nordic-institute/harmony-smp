package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPAuthenticationProviderTest {

    UserDao mockUserDao = Mockito.mock(UserDao.class);
    CredentialDao mockCredentialDao = Mockito.mock(CredentialDao.class);
    ConversionService mockConversionService = Mockito.mock(ConversionService.class);
    CRLVerifierService mockCrlVerifierService = Mockito.mock(CRLVerifierService.class);
    UITruststoreService mockTruststoreService = Mockito.mock(UITruststoreService.class);
    ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);
    CredentialsAlertService mocAlertService = Mockito.mock(CredentialsAlertService.class);

    SMPAuthenticationProvider testInstance = new SMPAuthenticationProvider(mockUserDao,
            mockCredentialDao,
            mockConversionService,
            mockCrlVerifierService,
            mockTruststoreService,
            mockConfigurationService,
            mocAlertService);

    // response time for existing and non existing user should be "approx. equal"
    @Test
    @Ignore
    public void authenticateByAccessTokenResponseTime() {
        /*
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken("User", "User");
        int count = 10;
        DBUser user = new DBUser();
        user.setId(1L);
        user.setAccessTokenIdentifier("User");
        user.setAccessToken(BCrypt.hashpw("InvalidPassword", BCrypt.gensalt()));
        user.setRole("MY_ROLE");
        doReturn(1000).when(mockConfigurationService).getAccessTokenLoginFailDelayInMilliSeconds();
        doReturn(count+5).when(mockConfigurationService).getAccessTokenLoginMaxAttempts();


        doReturn(Optional.of(user)).when(mockUserDao).findUserByIdentifier(any());

        long averageExists = 0;
        long averageNotExist = 0;
        for (int i = 0; i < count; i++) {
            long userExistTime = Calendar.getInstance().getTimeInMillis();
            try {
                testInstance.authenticateByUsernameToken(userToken);
            } catch (BadCredentialsException ignore) {
            }
            averageExists += Calendar.getInstance().getTimeInMillis() - userExistTime;
        }

        doReturn(Optional.empty()).when(mockUserDao).findUserByIdentifier(any());
        for (int i = 0; i < count; i++) {
            long userExistTime = Calendar.getInstance().getTimeInMillis();
            try {
                testInstance.authenticateByUsernameToken(userToken);
            } catch (AuthenticationServiceException | BadCredentialsException ignore) {
            }
            averageNotExist += Calendar.getInstance().getTimeInMillis() - userExistTime;
        }

        // the average should be the same!
        assertThat("average difference between failed login must be less than 10ms", Math.abs(averageExists - averageNotExist),
                Matchers.lessThan(50L));
*/
    }
    /*
    @Test
    public void testLoginAttemptForAccessTokenFailed(){

        int starFailCount = 2;
        DBUser user = new DBUser();
        user.setSequentialTokenLoginFailureCount(starFailCount);
        long starTime =Calendar.getInstance().getTimeInMillis();
        doReturn(100).when(mockConfigurationService).getAccessTokenLoginMaxAttempts();
        // when
        BadCredentialsException error = assertThrows(BadCredentialsException.class,
                () -> testInstance.loginAttemptForAccessTokenFailed(user,true, starTime));

        assertEquals(SMPAuthenticationProvider.BAD_CREDENTIALS_EXCEPTION, error);
        assertEquals(starFailCount+1,(int)user.getSequentialTokenLoginFailureCount());
        verify(mocAlertService, times(1)).alertCredentialVerificationFailed(user, CredentialTypeEnum.ACCESS_TOKEN);

    }

    @Test
    public void testLoginAttemptForAccessTokenSuspended(){
        int starFailCount = 5;
        DBUser user = new DBUser();
        user.setSequentialTokenLoginFailureCount(starFailCount);
        long starTime =Calendar.getInstance().getTimeInMillis();
        doReturn(5).when(mockConfigurationService).getAccessTokenLoginMaxAttempts();
        // when
        BadCredentialsException error = assertThrows(BadCredentialsException.class,
                () -> testInstance.loginAttemptForAccessTokenFailed(user,true,starTime));

        assertEquals(SMPAuthenticationProvider.SUSPENDED_CREDENTIALS_EXCEPTION, error);
        assertEquals(starFailCount+1,(int)user.getSequentialTokenLoginFailureCount());
        verify(mocAlertService, times(1)).alertCredentialsSuspended(user, CredentialTypeEnum.ACCESS_TOKEN);
    }

    @Test
    public void testValidateIfTokenIsSuspendedReset(){
        int starFailCount = 5;
        DBUser user = new DBUser();
        user.setUsername("TestToken");
        int suspensionSeconds =100;

        user.setLastTokenFailedLoginAttempt(OffsetDateTime.now().minusSeconds(suspensionSeconds+10));
        user.setSequentialTokenLoginFailureCount(starFailCount);
        doReturn(suspensionSeconds).when(mockConfigurationService).getAccessTokenLoginSuspensionTimeInSeconds();
        doReturn(starFailCount).when(mockConfigurationService).getAccessTokenLoginMaxAttempts();

        testInstance.validateIfTokenIsSuspended(user, Calendar.getInstance().getTimeInMillis());

        assertEquals(0, (int)user.getSequentialTokenLoginFailureCount());
        assertEquals(null, user.getLastTokenFailedLoginAttempt());
    }
*/
}
