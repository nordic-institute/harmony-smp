package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.mail.MailModel;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialSuspendedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialVerificationFailedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialsExpirationProperties;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Ignore
public class AlertServiceTest {

    AlertDao alertDao = Mockito.mock(AlertDao.class);
    MailService mailService = Mockito.mock(MailService.class);
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    UserDao userDao = Mockito.mock(UserDao.class);
    SMPDynamicCronTrigger alertCronTrigger = Mockito.mock(SMPDynamicCronTrigger.class);


    CredentialsAlertService testInstance = new CredentialsAlertService(alertDao, mailService, configurationService,userDao,alertCronTrigger);

    @Test
    public void testCreateAlert() {
        String mailSubject = "mailSubject";
        String mailTo = "mailTo";
        String username = "username";
        AlertLevelEnum level = AlertLevelEnum.MEDIUM;
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;

        DBAlert alert = testInstance.createAlert(username, mailSubject, mailTo, level, alertType);

        assertNotNull(alert);
        assertNull(alert.getId());
        assertEquals(mailSubject, alert.getMailSubject());
        assertEquals(username, alert.getUsername());
        assertEquals(AlertStatusEnum.PROCESS, alert.getAlertStatus());
        assertEquals(mailTo, alert.getMailTo());
        assertEquals(level, alert.getAlertLevel());
        assertEquals(alertType, alert.getAlertType());
        assertNotNull(alert.getReportingTime());

    }
/*
    @Test
    public void testSubmitAlertMailNoMail() {

        DBAlert alert = new DBAlert();

        testInstance.submitAlertMail(alert);

        verify(mailService, Mockito.never()).sendMail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void alertBeforeUsernamePasswordExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertBeforeUsernamePasswordExpire");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        doReturn(mailSubject).when(configurationService).getAlertBeforeExpirePasswordMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertBeforeExpirePasswordLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeUsernamePasswordExpire(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpirePasswordMailSubject();
        verify(configurationService, times(1)).getAlertBeforeExpirePasswordLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertUsernamePasswordExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertUsernamePasswordExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        user.setPasswordExpireOn(OffsetDateTime.now().plusDays(1));
        doReturn(mailSubject).when(configurationService).getAlertExpiredPasswordMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertExpiredPasswordLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertUsernamePasswordExpired(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredPasswordMailSubject();
        verify(configurationService, times(1)).getAlertExpiredPasswordLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertBeforeAccessTokenExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertBeforeAccessTokenExpire");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        user.setAccessTokenExpireOn(OffsetDateTime.now().plusDays(1));
        doReturn(mailSubject).when(configurationService).getAlertBeforeExpireAccessTokenMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertBeforeExpireAccessTokenLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeAccessTokenExpire(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpireAccessTokenMailSubject();
        verify(configurationService, times(1)).getAlertBeforeExpireAccessTokenLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertAccessTokenExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertAccessTokenExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        user.setAccessTokenExpireOn(OffsetDateTime.now().plusDays(1));
        doReturn(mailSubject).when(configurationService).getAlertExpiredAccessTokenMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertExpiredAccessTokenLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertAccessTokenExpired(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredAccessTokenMailSubject();
        verify(configurationService, times(1)).getAlertExpiredAccessTokenLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertBeforeCertificateExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("user", "alertBeforeCertificateExpire");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(mailSubject).when(configurationService).getAlertBeforeExpireCertificateMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertBeforeExpireCertificateLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeCertificateExpire(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpireCertificateMailSubject();
        verify(configurationService, times(1)).getAlertBeforeExpireCertificateLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertCertificateExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(mailSubject).when(configurationService).getAlertExpiredCertificateMailSubject();
        doReturn(alertLevel).when(configurationService).getAlertExpiredCertificateLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialsExpirationProperties.values()).stream()
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCertificateExpired(user);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredCertificateMailSubject();
        verify(configurationService, times(1)).getAlertExpiredCertificateLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void submitAlertMail() {
        String mailTo = "test.mail@domain.eu";
        String mailFrom = "test.mail@domain.eu";
        String mailSubject = "mailSubject";
        AlertTypeEnum template = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        DBAlert alert = new DBAlert();
        alert.setAlertType(template);
        alert.setMailTo(mailTo);
        alert.setMailSubject(mailSubject);
        alert.addProperty("test", "testValue");
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        testInstance.submitAlertMail(alert);

        ArgumentCaptor<MailModel<Properties>> argModel = ArgumentCaptor.forClass(MailModel.class);
        ArgumentCaptor<String> argMailTo = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argFrom = ArgumentCaptor.forClass(String.class);

        verify(mailService, times(1))
                .sendMail(argModel.capture(), argFrom.capture(), argMailTo.capture());
        verify(alertDao, times(1)).update(alert);

        assertEquals(mailTo, argMailTo.getValue());
        assertEquals(mailFrom, argFrom.getValue());
        assertEquals(mailSubject, argModel.getValue().getSubject());
        assertEquals(template.getTemplate(), argModel.getValue().getTemplatePath());
        assertEquals(1, argModel.getValue().getModel().size());
    }

    @Test
    public void alertUsernameCredentialVerificationFailed() {
        DBUser user = TestDBUtils.createDBUser("user");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        user.setSequentialLoginFailureCount(5);
        user.setLastFailedLoginAttempt(OffsetDateTime.now());
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserLoginFailureEnabled();
        doReturn(mailSubject).when(configurationService).getAlertUserLoginFailureSubject();
        doReturn(alertLevel).when(configurationService).getAlertUserLoginFailureLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialVerificationFailedProperties.values()).stream()
                .map(CredentialVerificationFailedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialVerificationFailed(user, CredentialTypeEnum.USERNAME_PASSWORD);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserLoginFailureEnabled();
        verify(configurationService, times(1)).getAlertUserLoginFailureSubject();
        verify(configurationService, times(1)).getAlertUserLoginFailureLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertTokenCredentialVerificationFailed() {
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        user.setSequentialTokenLoginFailureCount(5);
        user.setLastTokenFailedLoginAttempt(OffsetDateTime.now());
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserLoginFailureEnabled();
        doReturn(mailSubject).when(configurationService).getAlertUserLoginFailureSubject();
        doReturn(alertLevel).when(configurationService).getAlertUserLoginFailureLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        //doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialVerificationFailedProperties.values()).stream()
                .map(CredentialVerificationFailedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialVerificationFailed(user, CredentialTypeEnum.ACCESS_TOKEN);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserLoginFailureEnabled();
        verify(configurationService, times(1)).getAlertUserLoginFailureSubject();
        verify(configurationService, times(1)).getAlertUserLoginFailureLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertUsernameCredentialsSuspended() {
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        user.setSequentialLoginFailureCount(5);
        user.setLastFailedLoginAttempt(OffsetDateTime.now());
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserSuspendedEnabled();
        doReturn(mailSubject).when(configurationService).getAlertUserSuspendedSubject();
        doReturn(alertLevel).when(configurationService).getAlertUserSuspendedLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialSuspendedProperties.values()).stream()
                .map(CredentialSuspendedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialsSuspended(user, CredentialTypeEnum.USERNAME_PASSWORD);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserSuspendedEnabled();
        verify(configurationService, times(1)).getAlertUserSuspendedSubject();
        verify(configurationService, times(1)).getAlertUserSuspendedLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    public void alertTokenCredentialsSuspended() {
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        user.setSequentialTokenLoginFailureCount(5);
        user.setLastTokenFailedLoginAttempt(OffsetDateTime.now());
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserSuspendedEnabled();
        doReturn(mailSubject).when(configurationService).getAlertUserSuspendedSubject();
        doReturn(alertLevel).when(configurationService).getAlertUserSuspendedLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;
        List<String> expectedTemplateProperties = Arrays.asList(CredentialSuspendedProperties.values()).stream()
                .map(CredentialSuspendedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialsSuspended(user, CredentialTypeEnum.ACCESS_TOKEN);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserSuspendedEnabled();
        verify(configurationService, times(1)).getAlertUserSuspendedSubject();
        verify(configurationService, times(1)).getAlertUserSuspendedLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }


    public void assertAlertSend(AlertTypeEnum alertType, String mailTo, String mailFrom, String mailSubject,
                                List<String> templateProperties) {

        ArgumentCaptor<MailModel<Properties>> argModel = ArgumentCaptor.forClass(MailModel.class);
        ArgumentCaptor<String> argMailFrom = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argMailTo = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DBAlert> argAlert = ArgumentCaptor.forClass(DBAlert.class);
        ArgumentCaptor<DBAlert> argAlertUpdate = ArgumentCaptor.forClass(DBAlert.class);


        verify(alertDao, times(1)).persistFlushDetach(argAlert.capture());
        verify(mailService, times(1))
                .sendMail(argModel.capture(), argMailFrom.capture(), argMailTo.capture());

        verify(alertDao, times(1)).update(argAlertUpdate.capture());

        assertEquals(mailTo, argMailTo.getValue());
        assertEquals(mailFrom, argMailFrom.getValue());


        MailModel<Properties> model = argModel.getValue();
        assertEquals(alertType.getTemplate(), model.getTemplatePath());
        assertEquals(mailSubject, model.getSubject());

        // test to contain all properties
        for (String prop : templateProperties) {

            assertTrue(prop, model.getModel().keySet().contains(prop));
        }
        assertEquals(templateProperties.size(), model.getModel().size());
    }

 */
}
