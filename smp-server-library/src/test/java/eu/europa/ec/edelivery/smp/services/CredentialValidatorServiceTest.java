package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class CredentialValidatorServiceTest {

    ConfigurationService mockConfigService = Mockito.mock(ConfigurationService.class);
    AlertService mockAlertService = Mockito.mock(AlertService.class);
    UserDao mockUserDao = Mockito.mock(UserDao.class);

    CredentialValidatorService testInstance = new CredentialValidatorService(mockConfigService, mockAlertService, mockUserDao);

    @Test
    public void testSkipCredentialValidationFalseNotCluster() {
        doReturn(false).when(mockConfigService).isClusterEnabled();
        boolean result = testInstance.skipCredentialValidation();
        assertFalse(result);
    }

    @Test
    public void testSkipCredentialValidationFalseClusterNotTargetServer() {
        doReturn(true).when(mockConfigService).isClusterEnabled();
        doReturn("NotTargetServer").when(mockConfigService).getTargetServerForCredentialValidation();
        boolean result = testInstance.skipCredentialValidation();

        assertTrue(result);
        verify(mockConfigService, Mockito.times(1)).getTargetServerForCredentialValidation();
    }

    @Test
    public void testSkipCredentialValidationClusterNotTargetServer() {
        String currentHostName = HttpUtils.getServerAddress();
        doReturn(true).when(mockConfigService).isClusterEnabled();
        doReturn(currentHostName).when(mockConfigService).getTargetServerForCredentialValidation();
        boolean result = testInstance.skipCredentialValidation();

        assertFalse(result);
        verify(mockConfigService, Mockito.times(1)).getTargetServerForCredentialValidation();
    }
}