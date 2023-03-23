package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.PropertiesMailModel;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialSuspendedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialVerificationFailedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialsExpirationProperties;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;

import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_CREDENTIAL_ALERTS;
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
    final SMPDynamicCronTrigger alertCronTrigger;

    public CredentialsAlertService(AlertDao alertDao,
                                   MailService mailService,
                                   ConfigurationService configurationService,
                                   UserDao userDao,
                                   @Qualifier(TRIGGER_BEAN_CREDENTIAL_ALERTS) SMPDynamicCronTrigger alertCronTrigger) {
        this.alertDao = alertDao;
        this.mailService = mailService;
        this.configurationService = configurationService;
        this.userDao = userDao;
        this.alertCronTrigger = alertCronTrigger;
    }

    public void alertBeforeUsernamePasswordExpire(DBUser user) {
        /*
        LOG.info("Alert username [{}] is about to expire on [{}]", user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getPasswordExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.USERNAME_PASSWORD;
        String credentialId = user.getUsername();
        OffsetDateTime expiredOn = user.getPasswordExpireOn();
        // alert specific properties
        String mailSubject = configurationService.getAlertBeforeExpirePasswordMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpirePasswordLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }

    public void alertUsernamePasswordExpired(DBUser user) {
        /*
        LOG.info("Alert username [{}] expired on [{}]", user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getPasswordExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.USERNAME_PASSWORD;
        String credentialId = user.getUsername();
        OffsetDateTime expiredOn = user.getPasswordExpireOn();

        // alert specific properties
        String mailSubject = configurationService.getAlertExpiredPasswordMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredPasswordLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }

    public void alertBeforeAccessTokenExpire(DBUser user) {
        /*
        LOG.info("Alert access token [{}] for user [{}] is about to expire on [{}]",
                user.getAccessToken(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getAccessTokenExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.ACCESS_TOKEN;
        String credentialId = user.getAccessTokenIdentifier();
        OffsetDateTime expiredOn = user.getAccessTokenExpireOn();

        // alert specific properties
        String mailSubject = configurationService.getAlertBeforeExpireAccessTokenMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpireAccessTokenLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }

    public void alertAccessTokenExpired(DBUser user) {
        /*
        LOG.info("Alert access token [{}] for user [{}] expired on [{}]",
                user.getAccessToken(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getAccessTokenExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.ACCESS_TOKEN;
        String credentialId = user.getAccessTokenIdentifier();
        OffsetDateTime expiredOn = user.getAccessTokenExpireOn();

        // alert specific properties
        String mailSubject = configurationService.getAlertExpiredAccessTokenMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredAccessTokenLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }


    public void alertBeforeCertificateExpire(DBUser user) {
        /*
        LOG.info("Alert Certificate [{}] for user [{}] is about to expire",
                user.getCertificate().getCertificateId(),
                user.getUsername());

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.CERTIFICATE;
        String credentialId = user.getCertificate().getCertificateId();
        OffsetDateTime expiredOn = user.getCertificate().getValidTo();

        // alert specific properties
        String mailSubject = configurationService.getAlertBeforeExpireCertificateMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpireCertificateLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }

    public void alertCertificateExpired(DBUser user) {
        /*
        LOG.info("Alert Certificate [{}] for user [{}] expired",
                user.getCertificate().getCertificateId(),
                user.getUsername());

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.CERTIFICATE;
        String credentialId = user.getCertificate().getCertificateId();
        OffsetDateTime expiredOn = user.getCertificate().getValidTo();

        // alert specific properties
        String mailSubject = configurationService.getAlertExpiredCertificateMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredCertificateLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_EXPIRED;

        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialExpiration(user, alert, credentialType, credentialId, expiredOn);

         */
    }

    public void alertCredentialVerificationFailed(DBUser user, CredentialType credentialType) {
        /*
        Boolean loginFailureEnabled = configurationService.getAlertUserLoginFailureEnabled();
        if (!loginFailureEnabled) {
            LOG.debug("Alert Login failure is disabled!");
            return;
        }

        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserLoginFailureSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertUserLoginFailureLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_VERIFICATION_FAILED;

        Integer failureCount;
        OffsetDateTime lastFailedLoginDate;
        String credentialId;

        if (credentialType == CredentialTypeEnum.ACCESS_TOKEN) {
            failureCount = user.getSequentialTokenLoginFailureCount();
            lastFailedLoginDate = user.getLastTokenFailedLoginAttempt();
            credentialId = user.getAccessTokenIdentifier();
        } else if (credentialType == CredentialTypeEnum.USERNAME_PASSWORD) {
            failureCount = user.getSequentialLoginFailureCount();
            lastFailedLoginDate = user.getLastFailedLoginAttempt();
            credentialId = user.getUsername();
        } else {
            LOG.error("Alert for suspended credentials type [{}] is not supported", credentialType);
            return;
        }
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertCredentialVerificationFailed(user, alert,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate);

         */
    }

    public void alertCredentialsSuspended(DBUser user, CredentialType credentialType) {
        /*
        Boolean suspensionAlertEnabled = configurationService.getAlertUserSuspendedEnabled();
        if (!suspensionAlertEnabled) {
            LOG.debug("Alert suspended is disabled!");
            return;
        }

        String mailTo = user.getEmailAddress();
        String mailSubject = configurationService.getAlertUserSuspendedSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertUserSuspendedLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIAL_SUSPENDED;

        Integer failureCount;
        OffsetDateTime lastFailedLoginDate;
        OffsetDateTime suspendedUtil;
        String credentialId;
        if (credentialType == CredentialTypeEnum.ACCESS_TOKEN) {
            failureCount = user.getSequentialTokenLoginFailureCount();
            lastFailedLoginDate = user.getLastTokenFailedLoginAttempt();
            suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getAccessTokenLoginSuspensionTimeInSeconds());
            credentialId = user.getAccessTokenIdentifier();
        } else if (credentialType == CredentialTypeEnum.USERNAME_PASSWORD) {
            failureCount = user.getSequentialLoginFailureCount();
            lastFailedLoginDate = user.getLastFailedLoginAttempt();
            suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getLoginSuspensionTimeInSeconds());
            credentialId = user.getUsername();
        } else {
            LOG.error("Alert for suspended credentials type [{}] is not supported", credentialType);
            return;
        }
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);

        alertCredentialSuspended(user, alert,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate, suspendedUtil);*/
    }

    public void alertCredentialExpiration(DBUser user,
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
        submitAlertMail(alert);
        // when alert about to expire - check if the next cron execution is expired
        // and set date sent tp null to ensure alert submission in next cron execution
        userDao.updateAlertSentForUserCredentials(user.getId(), credentialType,
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
        submitAlertMail(alert);
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
        submitAlertMail(alert);
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
    public void submitAlertMail(DBAlert alert) {
        String mailTo = alert.getMailTo();
        if (StringUtils.isBlank(mailTo)) {
            LOG.warn("Can not send mail (empty mail) for alert [{}]!", alert);
            updateAlertStatus(alert, AlertStatusEnum.SUCCESS, "Alert created but mail not send (empty mail) for alert!");
            return;
        }

        String mailFrom = configurationService.getAlertEmailFrom();
        PropertiesMailModel props = new PropertiesMailModel(alert);
        try {
            mailService.sendMail(props, mailFrom, alert.getMailTo());
            updateAlertStatus(alert, AlertStatusEnum.SUCCESS, null);
        } catch (Throwable exc) {
            LOG.error("Can not send mail (empty mail) for alert [{}]! Error [{}]",
                    alert, ExceptionUtils.getRootCauseMessage(exc));
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
