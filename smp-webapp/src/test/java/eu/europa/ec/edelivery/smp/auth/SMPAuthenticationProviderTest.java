package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.services.AlertService;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class SMPAuthenticationProviderTest {

    UserDao mockUserDao = Mockito.mock(UserDao.class);
    CRLVerifierService mockCrlVerifierService = Mockito.mock(CRLVerifierService.class);
    UITruststoreService mockTruststoreService = Mockito.mock(UITruststoreService.class);
    ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);
    AlertService mocAlertService = Mockito.mock(AlertService.class);

    SMPAuthenticationProvider testInstance = new SMPAuthenticationProvider(mockUserDao,
            mockCrlVerifierService,
            mockTruststoreService,
            mockConfigurationService,
            mocAlertService);

    @Test
    // response time for existing and non existing user should be "approx. equal"
    public void authenticateByUsernameTokenResponseTime() {
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken("User", "User");
        DBUser user = new DBUser();
        user.setId(1L);
        user.setAccessTokenIdentifier("User");
        user.setAccessToken(BCrypt.hashpw("InvalidPassword", BCrypt.gensalt()));
        user.setRole("MY_ROLE");

        doReturn(Optional.of(user)).when(mockUserDao).findUserByIdentifier(any());
        int count = 100;
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
                Matchers.lessThan(1000L));

    }
}