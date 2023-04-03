package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.junit.Test;
import org.mockito.Mockito;

public class SMPAuthenticationProviderForUITest {


    CredentialService mockCredentialService = Mockito.mock(CredentialService.class);

    SMPAuthenticationProviderForUI testInstance = new SMPAuthenticationProviderForUI(mockCredentialService);

    @Test
    public void testValidateIfTokenIsSuspendedReset() {
        int starFailCount = 5;
        DBUser user = new DBUser();
        user.setUsername("TestToken");
        int suspensionSeconds = 100;
/*
        user.setLastFailedLoginAttempt(OffsetDateTime.now().minusSeconds(suspensionSeconds+10));
        user.setSequentialLoginFailureCount(starFailCount);
        doReturn(suspensionSeconds).when(mockConfigurationService).getLoginSuspensionTimeInSeconds();
        doReturn(starFailCount).when(mockConfigurationService).getLoginMaxAttempts();

        testInstance.validateIfUserAccountIsSuspended(user, Calendar.getInstance().getTimeInMillis());

        assertEquals(0, (int)user.getSequentialLoginFailureCount());
        assertEquals(null, user.getLastFailedLoginAttempt());

      */
    }
}
