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
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.mail.MailDataModel;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.*;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import eu.europa.ec.edelivery.smp.utils.SmpUrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Date;

import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_CREDENTIAL_ALERTS;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Alert service class is responsible for generating new alerts to database and submitting the notifications to
 * users.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class CredentialsAlertService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(CredentialsAlertService.class);
    final AlertDao alertDao;
    final MailService mailService;
    final ConfigurationService configurationService;
    final UserDao userDao;
    final CredentialDao credentialDao;
    final SMPDynamicCronTrigger alertCronTrigger;
    final SmpUrlBuilder smpUrlBuilder;

    public CredentialsAlertService(AlertDao alertDao,
                                   MailService mailService,
                                   ConfigurationService configurationService,
                                   UserDao userDao,
                                   CredentialDao credentialDao,
                                   SmpUrlBuilder smpUrlBuilder,
                                   @Qualifier(TRIGGER_BEAN_CREDENTIAL_ALERTS) SMPDynamicCronTrigger alertCronTrigger) {
        this.alertDao = alertDao;
        this.mailService = mailService;
        this.configurationService = configurationService;
        this.userDao = userDao;
        this.credentialDao = credentialDao;
        this.alertCronTrigger = alertCronTrigger;
        this.smpUrlBuilder = smpUrlBuilder;
    }

    public void alertBeforeCredentialExpire(DBCredential userCredential) {
        DBUser user = userCredential.getUser();
        LOG.info("Alert for credentials type name [{}:{}] for user [{}] is about to expire on [{}]",
                userCredential.getCredentialType(),
                userCredential.getName(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(userCredential.getExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialType credentialType = userCredential.getCredentialType();
        String credentialId = userCredential.getName();
        OffsetDateTime expiredOn = userCredential.getExpireOn();

        // alert specific properties
        String mailSubject;
        AlertLevelEnum alertLevel;
        if (credentialType == CredentialType.ACCESS_TOKEN) {
            mailSubject = configurationService.getAlertBeforeExpireAccessTokenMailSubject();
            alertLevel = configurationService.getAlertBeforeExpireAccessTokenLevel();
        } else if (credentialType == CredentialType.USERNAME_PASSWORD) {
            mailSubject = configurationService.getAlertBeforeExpirePasswordMailSubject();
            alertLevel = configurationService.getAlertBeforeExpirePasswordLevel();
        } else if (credentialType == CredentialType.CERTIFICATE) {
            mailSubject = configurationService.getAlertBeforeExpireCertificateMailSubject();
            alertLevel = configurationService.getAlertBeforeExpireCertificateLevel();
        } else {
            LOG.warn("Alert service for credential type [{}] is not supported! Skip alerts", credentialType);
            return;
        }

        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(userCredential, alert, credentialType, credentialId, expiredOn);
    }

    /**
     * Method generates "expired" alert for credentials
     *
     * @param userCredential
     */

    public void alertCredentialExpired(DBCredential userCredential) {
        DBUser user = userCredential.getUser();
        LOG.info("Alert access token [{}:{}] for user [{}] expired on [{}]",
                userCredential.getCredentialType(),
                userCredential.getName(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(userCredential.getExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialType credentialType = userCredential.getCredentialType();
        String credentialId = userCredential.getName();
        OffsetDateTime expiredOn = userCredential.getExpireOn();

        // alert specific properties
        String mailSubject;
        AlertLevelEnum alertLevel;

        if (credentialType == CredentialType.ACCESS_TOKEN) {
            mailSubject = configurationService.getAlertExpiredAccessTokenMailSubject();
            alertLevel = configurationService.getAlertExpiredAccessTokenLevel();
        } else if (credentialType == CredentialType.USERNAME_PASSWORD) {
            mailSubject = configurationService.getAlertExpiredPasswordMailSubject();
            alertLevel = configurationService.getAlertExpiredPasswordLevel();
        } else if (credentialType == CredentialType.CERTIFICATE) {
            mailSubject = configurationService.getAlertExpiredCertificateMailSubject();
            alertLevel = configurationService.getAlertExpiredCertificateLevel();
        } else {
            LOG.warn("Alert service for credential type [{}] is not supported! Skip alerts", credentialType);
            return;
        }


        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(userCredential, alert, credentialType, credentialId, expiredOn);
    }

    public void alertCredentialVerificationFailed(DBCredential credential) {
        LOG.info("Alert on Login failure [{}]!", credential);
        boolean loginFailureEnabled = configurationService.getAlertUserLoginFailureEnabled();
        if (!loginFailureEnabled) {
            LOG.warn("Alert Login failure is disabled!");
            return;
        }
        DBUser user = credential.getUser();
        CredentialType credentialType = credential.getCredentialType();
        if (credentialType != CredentialType.ACCESS_TOKEN && credentialType != CredentialType.USERNAME_PASSWORD) {
            LOG.error("Alert for suspended credentials type [{}] is not supported", credentialType);
            return;
        }

        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserLoginFailureSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertUserLoginFailureLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;

        Integer failureCount = credential.getSequentialLoginFailureCount();
        OffsetDateTime lastFailedLoginDate = credential.getLastFailedLoginAttempt();
        String credentialId = credential.getName();

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialVerificationFailed(user, alert,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate);


    }

    public void alertCredentialsSuspended(DBCredential credential) {

        boolean suspensionAlertEnabled = configurationService.getAlertUserSuspendedEnabled();
        if (!suspensionAlertEnabled) {
            LOG.info("Alert suspended is disabled!");
            return;
        }
        DBUser user = credential.getUser();
        CredentialType credentialType = credential.getCredentialType();
        if (credentialType != CredentialType.ACCESS_TOKEN && credentialType != CredentialType.USERNAME_PASSWORD) {
            LOG.error("Alert for suspended credentials type [{}] is not supported", credentialType);
            return;
        }

        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserSuspendedSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertUserSuspendedLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;

        Integer failureCount = credential.getSequentialLoginFailureCount();
        OffsetDateTime lastFailedLoginDate = credential.getLastFailedLoginAttempt();
        OffsetDateTime suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getAccessTokenLoginSuspensionTimeInSeconds());
        String credentialId = credential.getName();


        if (credentialType == CredentialType.ACCESS_TOKEN) {
            suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getAccessTokenLoginSuspensionTimeInSeconds());
        } else if (credentialType == CredentialType.USERNAME_PASSWORD) {
            suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getLoginSuspensionTimeInSeconds());
        }
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialSuspended(user, alert,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate, suspendedUtil);
    }

    public void alertCredentialExpiration(DBCredential credential,
                                          DBAlert alert,
                                          CredentialType credentialType,
                                          String credentialId,
                                          OffsetDateTime expirationDate
    ) {

        String serverName = HttpUtils.getServerAddress();
        // add alert properties
        alert.addProperty(CredentialsExpirationProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialsExpirationProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialsExpirationProperties.EXPIRATION_DATETIME.name(), expirationDate);
        alert.addProperty(CredentialsExpirationProperties.REPORTING_DATETIME.name(), alert.getReportingTime());
        alert.addProperty(CredentialsExpirationProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alert.addProperty(CredentialsExpirationProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, credential.getUser());
        // when alert about to expire - check if the next cron execution is expired
        // and set date sent tp null to ensure alert submission in next cron execution
        credentialDao.updateAlertSentForUserCredentials(credential,
                alert.getAlertType() == AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION
                        && isNextExecutionExpired(expirationDate) ?
                        null : OffsetDateTime.now());
    }

    public void alertCredentialVerificationFailed(DBUser user,
                                                  DBAlert alert,
                                                  CredentialType credentialType,
                                                  String credentialId,
                                                  Integer failedLoginCount,
                                                  OffsetDateTime lastFailedLoginDate
    ) {
        LOG.info("Prepare alert for credentials [{}] ", credentialId);
        String serverName = HttpUtils.getServerAddress();
        // add alert properties
        alert.addProperty(CredentialVerificationFailedProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialVerificationFailedProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialVerificationFailedProperties.FAILED_LOGIN_ATTEMPT.name(), failedLoginCount.toString());
        alert.addProperty(CredentialVerificationFailedProperties.LAST_LOGIN_FAILURE_DATETIME.name(), lastFailedLoginDate);
        alert.addProperty(CredentialVerificationFailedProperties.REPORTING_DATETIME.name(), alert.getReportingTime());
        alert.addProperty(CredentialVerificationFailedProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alert.addProperty(CredentialVerificationFailedProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    public void alertCredentialSuspended(DBUser user,
                                         DBAlert alert,
                                         CredentialType credentialType,
                                         String credentialId,
                                         Integer failedLoginCount,
                                         OffsetDateTime lastFailedLoginDate,
                                         OffsetDateTime suspendedUtil) {

        String serverName = HttpUtils.getServerAddress();
        // add alert properties
        alert.addProperty(CredentialSuspendedProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialSuspendedProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialSuspendedProperties.FAILED_LOGIN_ATTEMPT.name(), failedLoginCount.toString());
        alert.addProperty(CredentialSuspendedProperties.LAST_LOGIN_FAILURE_DATETIME.name(), lastFailedLoginDate);
        alert.addProperty(CredentialSuspendedProperties.SUSPENDED_UNTIL_DATETIME.name(), suspendedUtil);
        alert.addProperty(CredentialSuspendedProperties.REPORTING_DATETIME.name(), alert.getReportingTime());
        alert.addProperty(CredentialSuspendedProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alert.addProperty(CredentialSuspendedProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    /**
     * Method generates request reset alert for credentials and submit mail to the user
     *
     * @param user created
     */
    public void alertUserCreated(DBUser user) {

        String mailTo = user.getEmailAddress();
        String mailSubject = "New user created";
        AlertLevelEnum alertLevel = AlertLevelEnum.HIGH;
        AlertTypeEnum alertType = AlertTypeEnum.USER_CREATED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alert.addProperty(UserCreatedProperties.USERNAME.name(), user.getUsername());
        alert.addProperty(UserCreatedProperties.EMAIL.name(), user.getEmailAddress());
        alert.addProperty(UserCreatedProperties.FULL_NAME.name(), user.getFullName());
        alert.addProperty(UserCreatedProperties.ACTIVATED.name(), Boolean.toString(user.isActive()));

        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    /**
     * Method generates request reset alert for credentials and submit mail to the user
     *
     * @param user created
     */
    public void alertUserUpdated(DBUser user) {

        String mailTo = user.getEmailAddress();
        String mailSubject = "User data updated";
        AlertLevelEnum alertLevel = AlertLevelEnum.HIGH;
        AlertTypeEnum alertType = AlertTypeEnum.USER_UPDATED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alert.addProperty(UserUpdatedProperties.USERNAME.name(), user.getUsername());
        alert.addProperty(UserUpdatedProperties.EMAIL.name(), user.getEmailAddress());
        alert.addProperty(UserUpdatedProperties.FULL_NAME.name(), user.getFullName());
        alert.addProperty(UserUpdatedProperties.ACTIVATED.name(), Boolean.toString(user.isActive()));
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    /**
     * Method generates request reset alert for credentials and submit mail to the user
     *
     * @param credential credential to reset
     */
    public void alertCredentialRequestReset(DBCredential credential) {

        DBUser user = credential.getUser();
        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserSuspendedSubject();
        AlertLevelEnum alertLevel = AlertLevelEnum.HIGH;
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_REQUEST_RESET;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialRequestReset(credential.getResetToken(),
                alert,
                credential.getCredentialType(),
                credential.getName(),
                user);
    }

    public void alertCredentialRequestReset(String token,
                                            DBAlert alert,
                                            CredentialType credentialType,
                                            String credentialId,
                                            DBUser user) {


        URL resetUrl = configurationService.getCredentialsResetUrl();
        if (resetUrl == null) {
            try {
                resetUrl = smpUrlBuilder.buildSMPUriForApplication().toURL();
                LOG.warn("Reset URL is not set! Use default SMP URL [{}]", resetUrl);
            } catch (MalformedURLException e) {
                throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, e);
            }
        }
        String resetUrlPath = StringUtils.appendIfMissing(resetUrl.toString(), "/", "/") + "ui/#/reset-credential/" + token;
        String serverName = HttpUtils.getServerAddress();
        // add alert properties
        alert.addProperty(CredentialsResetRequestProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialsResetRequestProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialsResetRequestProperties.REPORTING_DATETIME.name(), alert.getReportingTime());
        alert.addProperty(CredentialsResetRequestProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alert.addProperty(CredentialsResetRequestProperties.RESET_URL.name(), resetUrlPath);
        alert.addProperty(CredentialsResetRequestProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }


    /**
     * Method generates request alert for credentials change and submit mail to the user
     *
     * @param credential credential changed
     */
    public void alertCredentialChanged(DBCredential credential) {

        DBUser user = credential.getUser();
        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserSuspendedSubject();
        AlertLevelEnum alertLevel = AlertLevelEnum.HIGH;
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_CHANGED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialChanged(user, alert, credential.getCredentialType(), credential.getName());

    }

    public void alertCredentialChanged(DBUser user, DBAlert alert,
                                       CredentialType credentialType,
                                       String credentialId) {

        String serverName = HttpUtils.getServerAddress();
        // add alert properties
        alert.addProperty(CredentialsChangedProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialsChangedProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialsChangedProperties.REPORTING_DATETIME.name(), alert.getReportingTime());
        alert.addProperty(CredentialsChangedProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alert.addProperty(CredentialsChangedProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    /**
     * Create Alert DB entity
     *
     * @param mailSubject
     * @param mailTo
     * @param level
     * @param alertType
     * @return
     */
    protected DBAlert createAlert(String username, String mailSubject,
                                  String mailTo,
                                  AlertLevelEnum level,
                                  AlertTypeEnum alertType) {

        DBAlert alert = new DBAlert();
        alert.setMailSubject(mailSubject);
        alert.setMailTo(mailTo);
        alert.setUsername(username);
        alert.setReportingTime(OffsetDateTime.now());
        alert.setAlertType(alertType);
        alert.setAlertLevel(level);
        alert.setAlertStatus(AlertStatusEnum.PROCESS);
        return alert;
    }

    /**
     * Submit mail  for the alert
     *
     * @param alert
     */
    public void submitAlertMail(DBAlert alert, DBUser user) {
        String mailTo = alert.getMailTo();
        if (StringUtils.isBlank(mailTo)) {
            LOG.warn("Can not send mail (empty mail) for alert [{}]!", alert);
            updateAlertStatus(alert, AlertStatusEnum.SUCCESS, "Alert created but mail not send (empty mail) for alert!");
            return;
        }


        String mailFrom = configurationService.getAlertEmailFrom();
        MailDataModel props = new MailDataModel(user.getSmpLocale(), alert);
        props.getModel().put(MailDataModel.CommonProperties.SMP_INSTANCE_NAME.name(),
                configurationService.getSMPInstanceName());
        props.getModel().put(MailDataModel.CommonProperties.CURRENT_DATETIME.name(),
                OffsetDateTime.now().format(ISO_DATE_TIME));

        try {
            mailService.sendMail(props, mailFrom, alert.getMailTo());
            updateAlertStatus(alert, AlertStatusEnum.SUCCESS, null);
        } catch (Throwable exc) {
            LOG.error("Can not send mail [{}] for alert [{}]! Error [{}]",
                    mailTo, alert, ExceptionUtils.getRootCauseMessage(exc));
            LOG.error("Error sending mail", exc);
            updateAlertStatus(alert, AlertStatusEnum.FAILED, ExceptionUtils.getRootCauseMessage(exc));
        }

    }

    public void updateAlertStatus(DBAlert alert, AlertStatusEnum status, String statusDesc) {
        alert.setAlertStatus(status);
        alert.setAlertStatusDesc(statusDesc);
        if (status == AlertStatusEnum.SUCCESS
                || status == AlertStatusEnum.FAILED) {
            alert.setProcessedTime(OffsetDateTime.now());
        }
        alertDao.update(alert);
    }

    public boolean isNextExecutionExpired(OffsetDateTime expireOn) {
        Date nextExecutionDate = alertCronTrigger.getNextExecutionDate();
        // get expire offset - presume that expired On was generated
        // on server in the same zone
        return nextExecutionDate == null || expireOn == null ||
                expireOn.isBefore(nextExecutionDate.toInstant().atOffset(expireOn.getOffset()));
    }
}
