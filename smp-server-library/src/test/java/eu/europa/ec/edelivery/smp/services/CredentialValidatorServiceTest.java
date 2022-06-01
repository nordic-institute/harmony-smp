package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Test
    public void validateCredentialsForBeforeExpireUsernames() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertBeforeExpirePasswordEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertBeforeExpirePasswordPeriod();
        doReturn(iInterval).when(mockConfigService).getAlertBeforeExpirePasswordInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getBeforePasswordExpireUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForBeforeExpireUsernames();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getBeforePasswordExpireUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertBeforeUsernamePasswordExpire(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }

    @Test
    public void validateCredentialsForExpiredUsernames() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertExpiredPasswordEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertExpiredPasswordPeriod();
        doReturn(iInterval).when(mockConfigService).getAlertExpiredPasswordInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getPasswordExpiredUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForExpiredUsernames();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getPasswordExpiredUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertUsernamePasswordExpired(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }

    @Test
    public void validateCredentialsForBeforeExpireAccessToken() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertBeforeExpireAccessTokenEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertBeforeExpireAccessTokenPeriod();
        doReturn(iInterval).when(mockConfigService).getAlertBeforeExpireAccessTokenInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getBeforeAccessTokenExpireUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForBeforeExpireAccessToken();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getBeforeAccessTokenExpireUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertBeforeAccessTokenExpire(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }

    @Test
    public void validateCredentialsForExpiredAccessToken() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertExpiredAccessTokenEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertExpiredAccessTokenPeriod();
        doReturn(iInterval).when(mockConfigService).getAlertExpiredAccessTokenInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getAccessTokenExpiredUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForExpiredAccessToken();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getAccessTokenExpiredUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertAccessTokenExpired(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }

    @Test
    public void validateCredentialsForBeforeExpireCertificate() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertBeforeExpireCertificateEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertBeforeExpireCertificatePeriod();
        doReturn(iInterval).when(mockConfigService).getAlertBeforeExpireCertificateInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getBeforeCertificateExpireUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForBeforeExpireCertificate();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getBeforeCertificateExpireUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertBeforeCertificateExpire(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }

    @Test
    public void validateCredentialsForExpiredCertificate() {
        DBUser user = Mockito.mock(DBUser.class);
        Integer iPeriod = 10;
        Integer iInterval = 15;
        Integer iBatchSize = 20;

        doReturn(true).when(mockConfigService).getAlertExpiredCertificateEnabled();
        doReturn(iPeriod).when(mockConfigService).getAlertExpiredCertificatePeriod();
        doReturn(iInterval).when(mockConfigService).getAlertExpiredCertificateInterval();
        doReturn(iBatchSize).when(mockConfigService).getAlertCredentialsBatchSize();
        doReturn(Collections.singletonList(user)).when(mockUserDao).getCertificateExpiredUsersForAlerts(anyInt(), anyInt(), anyInt());

        testInstance.validateCredentialsForExpiredCertificate();


        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> interval = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<DBUser> userCapture = ArgumentCaptor.forClass(DBUser.class);


        verify(mockUserDao, Mockito.times(1))
                .getCertificateExpiredUsersForAlerts(period.capture(), interval.capture(), batchSize.capture());
        verify(mockAlertService, Mockito.times(1))
                .alertCertificateExpired(userCapture.capture());

        assertEquals(iPeriod, period.getValue());
        assertEquals(iInterval, interval.getValue());
        assertEquals(iBatchSize, batchSize.getValue());
        assertEquals(user, userCapture.getValue());
    }
}