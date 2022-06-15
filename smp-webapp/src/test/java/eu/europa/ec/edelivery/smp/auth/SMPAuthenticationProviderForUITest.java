package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.services.AlertService;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class SMPAuthenticationProviderForUITest {

    UserDao mockUserDao = Mockito.mock(UserDao.class);
    ConversionService mockConversionService = Mockito.mock(ConversionService.class);
    CRLVerifierService mockCrlVerifierService = Mockito.mock(CRLVerifierService.class);
    UITruststoreService mockTruststoreService = Mockito.mock(UITruststoreService.class);
    ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);
    AlertService mocAlertService = Mockito.mock(AlertService.class);
    SMPAuthenticationProviderForUI testInstance = new SMPAuthenticationProviderForUI(mockUserDao,
            mockConversionService,
            mockCrlVerifierService,
            mocAlertService,
            mockTruststoreService,
            mockConfigurationService);

    @Test
    public void testValidateIfTokenIsSuspendedReset(){
        int starFailCount = 5;
        DBUser user = new DBUser();
        user.setUsername("TestToken");
        int suspensionSeconds =100;

        user.setLastFailedLoginAttempt(OffsetDateTime.now().minusSeconds(suspensionSeconds+10));
        user.setSequentialLoginFailureCount(starFailCount);
        doReturn(suspensionSeconds).when(mockConfigurationService).getLoginSuspensionTimeInSeconds();
        doReturn(starFailCount).when(mockConfigurationService).getLoginMaxAttempts();

        testInstance.validateIfUserAccountIsSuspended(user);

        assertEquals(0, (int)user.getSequentialLoginFailureCount());
        assertEquals(null, user.getLastFailedLoginAttempt());
    }
}