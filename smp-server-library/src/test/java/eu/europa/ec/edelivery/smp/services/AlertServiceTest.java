/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.mail.MailDataModel;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialSuspendedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialVerificationFailedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialsExpirationProperties;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.utils.SmpUrlBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    AlertDao alertDao = Mockito.mock(AlertDao.class);
    MailService mailService = Mockito.mock(MailService.class);
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    UserDao userDao = Mockito.mock(UserDao.class);
    CredentialDao credentialDao = Mockito.mock(CredentialDao.class);
    SmpUrlBuilder smpUrlBuilder = Mockito.mock(SmpUrlBuilder.class);
    SMPDynamicCronTrigger alertCronTrigger = Mockito.mock(SMPDynamicCronTrigger.class);


    CredentialsAlertService testInstance = new CredentialsAlertService(alertDao,
            mailService,
            configurationService,
            userDao,
            credentialDao,
            smpUrlBuilder,
            alertCronTrigger);

    @Test
    void testCreateAlert() {
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

    @Test
    void testSubmitAlertMailNoMail() {

        DBAlert alert = new DBAlert();
        DBUser user = Mockito.mock(DBUser.class);

        testInstance.submitAlertMail(alert, user);

        verify(mailService, Mockito.never()).sendMail(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void alertBeforeUsernamePasswordExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertBeforeUsernamePasswordExpire");
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, OffsetDateTime.now().plusDays(1), null);
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(alertLevel).when(configurationService).getAlertBeforeExpirePasswordLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeCredentialExpire(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpirePasswordLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertUsernamePasswordExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertUsernamePasswordExpired");
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, OffsetDateTime.now().plusDays(-1), null);

        doReturn(alertLevel).when(configurationService).getAlertExpiredPasswordLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertCredentialExpired(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredPasswordLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertBeforeAccessTokenExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertBeforeAccessTokenExpire");
        DBCredential credential = TestDBUtils.createDBCredentialForUserAccessToken(user, null, OffsetDateTime.now().plusDays(1), null);

        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(alertLevel).when(configurationService).getAlertBeforeExpireAccessTokenLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeCredentialExpire(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpireAccessTokenLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }


    @Test
    void alertAccessTokenExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("alertAccessTokenExpired");
        DBCredential credential = TestDBUtils.createDBCredentialForUserAccessToken(user, null, OffsetDateTime.now().plusDays(-1), null);
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(alertLevel).when(configurationService).getAlertExpiredAccessTokenLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertCredentialExpired(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredAccessTokenLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertBeforeCertificateExpire() {
        // given
        DBUser user = TestDBUtils.createDBUser("user", "alertBeforeCertificateExpire");
        DBCredential credential = TestDBUtils.createDBCredentialForUserCertificate(user, null, OffsetDateTime.now().plusDays(1), null);
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(alertLevel).when(configurationService).getAlertBeforeExpireCertificateLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());
        // when
        testInstance.alertBeforeCredentialExpire(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertBeforeExpireCertificateLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertCertificateExpired() {
        // given
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        DBCredential credential = TestDBUtils.createDBCredentialForUserCertificate(user, null, OffsetDateTime.now().plusDays(1), null);
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";
        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;
        doReturn(alertLevel).when(configurationService).getAlertExpiredCertificateLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialsExpirationProperties.values())
                .map(CredentialsExpirationProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialExpired(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertExpiredCertificateLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void submitAlertMail() {
        String mailTo = "test.mail@domain.eu";
        String mailFrom = "test.mail@domain.eu";
        String mailSubject = "mailSubject";
        String language = "en";
        AlertTypeEnum template = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;
        DBUser user = Mockito.mock(DBUser.class);
        doReturn(language).when(user).getSmpLocale();
        DBAlert alert = new DBAlert();
        alert.setAlertType(template);
        alert.setMailTo(mailTo);
        alert.setMailSubject(mailSubject);
        alert.addProperty("test", "testValue");
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        testInstance.submitAlertMail(alert, user);

        ArgumentCaptor<MailDataModel> argModel = ArgumentCaptor.forClass(MailDataModel.class);
        ArgumentCaptor<String> argMailTo = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argFrom = ArgumentCaptor.forClass(String.class);

        verify(mailService, times(1))
                .sendMail(argModel.capture(), argFrom.capture(), argMailTo.capture());
        verify(alertDao, times(1)).update(alert);

        assertEquals(mailTo, argMailTo.getValue());
        assertEquals(mailFrom, argFrom.getValue());
        assertEquals(AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION, argModel.getValue().getMailType());
        assertEquals(language, argModel.getValue().getLanguage());
        assertEquals(3, argModel.getValue().getModel().size());
    }

    @Test
    void alertUsernameCredentialVerificationFailed() {
        DBUser user = TestDBUtils.createDBUser("user");
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, OffsetDateTime.now().plusDays(1), null);
        credential.setSequentialLoginFailureCount(5);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());

        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";

        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserLoginFailureEnabled();
        doReturn(alertLevel).when(configurationService).getAlertUserLoginFailureLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialVerificationFailedProperties.values())
                .map(CredentialVerificationFailedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialVerificationFailed(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserLoginFailureEnabled();
        verify(configurationService, times(1)).getAlertUserLoginFailureLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertTokenCredentialVerificationFailed() {
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        DBCredential credential = TestDBUtils.createDBCredentialForUserAccessToken(user, null, OffsetDateTime.now().plusDays(1), null);
        credential.setSequentialLoginFailureCount(5);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";

        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserLoginFailureEnabled();
        doReturn(alertLevel).when(configurationService).getAlertUserLoginFailureLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        //doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialVerificationFailedProperties.values())
                .map(CredentialVerificationFailedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialVerificationFailed(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserLoginFailureEnabled();
        verify(configurationService, times(1)).getAlertUserLoginFailureLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertUsernameCredentialsSuspended() {
        DBUser user = TestDBUtils.createDBUser("user", "alertUsernameCredentialsSuspended");
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, OffsetDateTime.now().plusDays(1), null);
        credential.setSequentialLoginFailureCount(5);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";

        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserSuspendedEnabled();
        doReturn(alertLevel).when(configurationService).getAlertUserSuspendedLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialSuspendedProperties.values())
                .map(CredentialSuspendedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialsSuspended(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserSuspendedEnabled();
        verify(configurationService, times(1)).getAlertUserSuspendedLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    @Test
    void alertTokenCredentialsSuspended() {
        DBUser user = TestDBUtils.createDBUser("user", "alertCertificateExpired");
        DBCredential credential = TestDBUtils.createDBCredentialForUserAccessToken(user, null, OffsetDateTime.now().plusDays(1), null);
        credential.setSequentialLoginFailureCount(5);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());

        String mailSubject = "mail subject";
        String mailFrom = "mail.from@test.eu";

        AlertLevelEnum alertLevel = AlertLevelEnum.MEDIUM;

        doReturn(true).when(configurationService).getAlertUserSuspendedEnabled();
        doReturn(alertLevel).when(configurationService).getAlertUserSuspendedLevel();
        doReturn(mailFrom).when(configurationService).getAlertEmailFrom();
        doReturn(123456).when(configurationService).getLoginSuspensionTimeInSeconds();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;
        List<String> expectedTemplateProperties = Arrays.stream(CredentialSuspendedProperties.values())
                .map(CredentialSuspendedProperties::name).collect(Collectors.toList());

        // when
        testInstance.alertCredentialsSuspended(credential);
        // then
        assertAlertSend(alertType, user.getEmailAddress(), mailFrom, mailSubject,
                expectedTemplateProperties);

        verify(configurationService, times(1)).getAlertUserSuspendedEnabled();
        verify(configurationService, times(1)).getAlertUserSuspendedLevel();
        verify(configurationService, times(1)).getAlertEmailFrom();
    }

    public void assertAlertSend(AlertTypeEnum alertType, String mailTo, String mailFrom, String mailSubject,
                                List<String> templateProperties) {

        ArgumentCaptor<MailDataModel> argModel = ArgumentCaptor.forClass(MailDataModel.class);
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


        MailDataModel model = argModel.getValue();
        assertEquals(alertType, model.getMailType());
        assertEquals("en", model.getLanguage());

        // test to contain all properties
        for (String prop : templateProperties) {
            assertTrue(model.getModel().containsKey(prop), prop);
        }
        // add two common properties: CURRENT_DATETIME, SMP_INSTANCE_NAME
        assertEquals(templateProperties.size() + 2, model.getModel().size());
    }


}
