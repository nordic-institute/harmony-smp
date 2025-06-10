/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2025 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.mail.MailDataModel;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.SystemCertificateExpirationProperties;
import eu.europa.ec.edelivery.smp.utils.DateTimeUtils;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SystemCertificateAlertServiceTest {

    @Mock
    AlertDao alertDao;

    @Mock
    MailService mailService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    DBUser user;

    @Mock
    DBAlert alert;

    SystemCertificateAlertService systemCertificateAlertService;

    @BeforeEach
    public void setup() {
        systemCertificateAlertService = new SystemCertificateAlertService(alertDao, mailService, configurationService);
    }

    @Test
    void testCreateAlert() {
        String mailSubject = "System Certificate Alert";
        String username = "username";
        String mailTo = "username@email.com";
        AlertTypeEnum alertType = AlertTypeEnum.SYSTEM_CERTIFICATE_EXPIRED;
        AlertLevelEnum alertLevel = AlertLevelEnum.HIGH;

        DBAlert alert = systemCertificateAlertService.createAlert(username, mailSubject, mailTo, alertLevel, alertType);

        assertEquals(username, alert.getUsername());
        assertEquals(mailTo, alert.getMailTo());
        assertEquals(mailSubject, alert.getMailSubject());
        assertTrue(ChronoUnit.SECONDS.between(alert.getReportingTime().atZoneSimilarLocal(ZoneOffset.UTC), alert.getReportingTime()) < 10); // no more than 10 seconds diff
        assertSame(alertType, alert.getAlertType());
        assertSame(alertLevel, alert.getAlertLevel());
        assertSame(AlertStatusEnum.PROCESS, alert.getAlertStatus());
        assertEquals(HttpUtils.getServerAddress(), alert.getProperties().get(SystemCertificateExpirationProperties.SERVER_NAME.name()).getValue());
    }

    @Test
    void alertBeforeSigningCertificateExpire_NonSystemAdministratorUsers() {
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.USER);

        systemCertificateAlertService.alertBeforeSigningCertificateExpire(user, null, null, null);

        Mockito.verify(user, Mockito.never()).getEmailAddress();
        Mockito.verify(configurationService, Mockito.never()).getAlertBeforeExpireSystemCertificateLevel();
        Mockito.verify(mailService, Mockito.never()).sendMail(any(MailDataModel.class), anyString(), anyString());
    }

    @Test
    void alertBeforeSmlIntegrationCertificateExpire_NonSystemAdministratorUsers() {
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.USER);

        systemCertificateAlertService.alertBeforeSmlIntegrationCertificateExpire(user, null, null, null);

        Mockito.verify(user, Mockito.never()).getEmailAddress();
        Mockito.verify(configurationService, Mockito.never()).getAlertBeforeExpireSystemCertificateLevel();
        Mockito.verify(mailService, Mockito.never()).sendMail(any(MailDataModel.class), anyString(), anyString());
    }

    @Test
    void alertSigningCertificateExpired_NonSystemAdministratorUsers() {
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.USER);

        systemCertificateAlertService.alertSigningCertificateExpired(user, null, null, null);

        Mockito.verify(user, Mockito.never()).getEmailAddress();
        Mockito.verify(configurationService, Mockito.never()).getAlertBeforeExpireSystemCertificateLevel();
        Mockito.verify(mailService, Mockito.never()).sendMail(any(MailDataModel.class), anyString(), anyString());
    }

    @Test
    void alertSmlIntegrationCertificateExpired_NonSystemAdministratorUsers() {
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.USER);

        systemCertificateAlertService.alertSmlIntegrationCertificateExpired(user, null, null, null);

        Mockito.verify(user, Mockito.never()).getEmailAddress();
        Mockito.verify(configurationService, Mockito.never()).getAlertBeforeExpireSystemCertificateLevel();
        Mockito.verify(mailService, Mockito.never()).sendMail(any(MailDataModel.class), anyString(), anyString());
    }


    @Test
    void submitAlertMail_MailToBlank() {
        Mockito.when(alert.getMailTo()).thenReturn(" ");

        systemCertificateAlertService.submitAlertMail(alert, user);

        Mockito.verify(mailService, Mockito.never()).sendMail(any(MailDataModel.class), anyString(), anyString());
    }

    @Test
    void submitAlertMail_SendException() {
        Mockito.when(alert.getMailTo()).thenReturn("mailTo");
        Mockito.doThrow(new RuntimeException("rootCauseMessage")).when(mailService).sendMail(any(MailDataModel.class), any(), eq("mailTo"));

        try(MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
            systemCertificateAlertService.submitAlertMail(alert, user);
            mockedStatic.verify(() -> ExceptionUtils.getRootCauseMessage(argThat(exception -> exception.getMessage().contains("rootCauseMessage"))), Mockito.atLeastOnce());
        }
    }

    @Test
    void alertBeforeSigningCertificateExpire() {
        final OffsetDateTime UTC_2050_01_01_00_00_00 = OffsetDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.SYSTEM_ADMIN);
        Mockito.when(user.getEmailAddress()).thenReturn("mailTo");
        Mockito.when(user.getUsername()).thenReturn("username");
        Mockito.when(user.getSmpLocale()).thenReturn("en");
        Mockito.when(configurationService.getAlertEmailFrom()).thenReturn("mailFrom");
        Mockito.when(configurationService.getSMPInstanceName()).thenReturn("SMP");
        Mockito.when(configurationService.getAlertBeforeExpireSystemCertificateLevel()).thenReturn(AlertLevelEnum.MEDIUM);

        try(MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
            systemCertificateAlertService.alertBeforeSigningCertificateExpire(user, "domain", "alias", UTC_2050_01_01_00_00_00);
            mockedStatic.verify(() -> ExceptionUtils.getRootCauseMessage(any()), Mockito.never());
        }

        verify(alertDao).persistFlushDetach(argThat(alert -> isAlertValid(alert, AlertTypeEnum.SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION, AlertLevelEnum.MEDIUM,
                "mailTo", "SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION Signing Certificate", "username")));
        verify(mailService).sendMail(argThat(model -> isMailDataModelValid(model, "SMP", "alias", "Signing Certificate", "domain", UTC_2050_01_01_00_00_00, "MEDIUM")),
                eq("mailFrom"), eq("mailTo"));
    }

    @Test
    void alertBeforeSmlIntegrationCertificateExpire() {
        final OffsetDateTime UTC_2050_01_01_00_00_00 = OffsetDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.SYSTEM_ADMIN);
        Mockito.when(user.getEmailAddress()).thenReturn("mailTo");
        Mockito.when(user.getUsername()).thenReturn("username");
        Mockito.when(user.getSmpLocale()).thenReturn("en");
        Mockito.when(configurationService.getAlertEmailFrom()).thenReturn("mailFrom");
        Mockito.when(configurationService.getSMPInstanceName()).thenReturn("SMP");
        Mockito.when(configurationService.getAlertBeforeExpireSystemCertificateLevel()).thenReturn(AlertLevelEnum.LOW);

        try(MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
            systemCertificateAlertService.alertBeforeSmlIntegrationCertificateExpire(user, "domain", "alias", UTC_2050_01_01_00_00_00);
            mockedStatic.verify(() -> ExceptionUtils.getRootCauseMessage(any()), Mockito.never());
        }

        verify(alertDao).persistFlushDetach(argThat(alert -> isAlertValid(alert, AlertTypeEnum.SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION, AlertLevelEnum.LOW,
                "mailTo", "SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION DomiSML Integration Certificate", "username")));
        verify(mailService).sendMail(argThat(model -> isMailDataModelValid(model, "SMP", "alias", "DomiSML Integration Certificate", "domain", UTC_2050_01_01_00_00_00, "LOW")),
                eq("mailFrom"), eq("mailTo"));
    }

    @Test
    void alertSigningCertificateExpired() {
        final OffsetDateTime UTC_2050_01_01_00_00_00 = OffsetDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.SYSTEM_ADMIN);
        Mockito.when(user.getEmailAddress()).thenReturn("mailTo");
        Mockito.when(user.getUsername()).thenReturn("username");
        Mockito.when(user.getSmpLocale()).thenReturn("en");
        Mockito.when(configurationService.getAlertEmailFrom()).thenReturn("mailFrom");
        Mockito.when(configurationService.getSMPInstanceName()).thenReturn("SMP");
        Mockito.when(configurationService.getAlertExpiredSystemCertificateLevel()).thenReturn(AlertLevelEnum.HIGH);

        try(MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
            systemCertificateAlertService.alertSigningCertificateExpired(user, "domain", "alias", UTC_2050_01_01_00_00_00);
            mockedStatic.verify(() -> ExceptionUtils.getRootCauseMessage(any()), Mockito.never());
        }

        verify(alertDao).persistFlushDetach(argThat(alert -> isAlertValid(alert, AlertTypeEnum.SYSTEM_CERTIFICATE_EXPIRED, AlertLevelEnum.HIGH,
                "mailTo", "SYSTEM_CERTIFICATE_EXPIRED Signing Certificate", "username")));
        verify(mailService).sendMail(argThat(model -> isMailDataModelValid(model, "SMP", "alias", "Signing Certificate", "domain", UTC_2050_01_01_00_00_00, "HIGH")),
                eq("mailFrom"), eq("mailTo"));
    }

    @Test
    void alertSmlIntegrationCertificateExpired() {
        final OffsetDateTime UTC_2050_01_01_00_00_00 = OffsetDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Mockito.when(user.getApplicationRole()).thenReturn(ApplicationRoleType.SYSTEM_ADMIN);
        Mockito.when(user.getEmailAddress()).thenReturn("mailTo");
        Mockito.when(user.getUsername()).thenReturn("username");
        Mockito.when(user.getSmpLocale()).thenReturn("en");
        Mockito.when(configurationService.getAlertEmailFrom()).thenReturn("mailFrom");
        Mockito.when(configurationService.getSMPInstanceName()).thenReturn("SMP");
        Mockito.when(configurationService.getAlertExpiredSystemCertificateLevel()).thenReturn(AlertLevelEnum.HIGH);

        try(MockedStatic<ExceptionUtils> mockedStatic = Mockito.mockStatic(ExceptionUtils.class)) {
            systemCertificateAlertService.alertSmlIntegrationCertificateExpired(user, "domain", "alias", UTC_2050_01_01_00_00_00);
            mockedStatic.verify(() -> ExceptionUtils.getRootCauseMessage(any()), Mockito.never());
        }

        verify(alertDao).persistFlushDetach(argThat(alert -> isAlertValid(alert, AlertTypeEnum.SYSTEM_CERTIFICATE_EXPIRED, AlertLevelEnum.HIGH,
                "mailTo", "SYSTEM_CERTIFICATE_EXPIRED DomiSML Integration Certificate", "username")));
        verify(mailService).sendMail(argThat(model -> isMailDataModelValid(model, "SMP", "alias", "DomiSML Integration Certificate", "domain", UTC_2050_01_01_00_00_00, "HIGH")),
                eq("mailFrom"), eq("mailTo"));
    }

    private boolean isAlertValid(DBAlert alert, AlertTypeEnum type, AlertLevelEnum level, String mailTo, String mailSubject, String username) {
        return alert.getAlertStatus() == AlertStatusEnum.PROCESS
                && alert.getAlertLevel() == level
                && alert.getAlertType() == type
                && alert.getMailTo().equals(mailTo)
                && alert.getMailSubject().equals(mailSubject)
                && alert.getUsername().equals(username)
                && alert.getReportingTime() != null;
    }

    private boolean isMailDataModelValid(MailDataModel model, String smpInstanceName, String certificateAlias, String certificateType, String domainCode, OffsetDateTime expirationDateTime, String level) {
        return model.getModel().get(MailDataModel.CommonProperties.SMP_INSTANCE_NAME.name()).equals(smpInstanceName)
                && model.getModel().get(SystemCertificateExpirationProperties.SERVER_NAME.name()) != null
                && model.getModel().get(SystemCertificateExpirationProperties.CERTIFICATE_ALIAS.name()).equals(certificateAlias)
                && model.getModel().get(SystemCertificateExpirationProperties.CERTIFICATE_TYPE.name()).equals(certificateType)
                && model.getModel().get(SystemCertificateExpirationProperties.DOMAIN_CODE.name()).equals(domainCode)
                && model.getModel().get(SystemCertificateExpirationProperties.EXPIRATION_DATETIME.name()).equals(DateTimeUtils.formatOffsetDateTimeWithLocal(expirationDateTime, "en"))
                && model.getModel().get(SystemCertificateExpirationProperties.ALERT_LEVEL.name()).equals(level)
                && model.getModel().get(MailDataModel.CommonProperties.CURRENT_DATETIME.name()) != null;
    }
}