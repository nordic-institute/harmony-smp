package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.AlertDao;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.CredentialTypeEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.PropertiesMailModel;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialSuspendedProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.CredentialsExpirationProperties;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Alert service class is responsible for generating new alerts to database and submitting the notifications to
 * users.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class AlertService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AlertService.class);
    final AlertDao alertDao;
    final MailService mailService;
    final ConfigurationService configurationService;

    public AlertService(AlertDao alertDao, MailService mailService, ConfigurationService configurationService) {
        this.alertDao = alertDao;
        this.mailService = mailService;
        this.configurationService = configurationService;
    }

    public void alertBeforeUsernamePasswordExpire(DBUser user) {
        LOG.info("Alert username [{}] is about to expire on [{}]", user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getPasswordExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.USERNAME_PASSWORD;
        String credentialId = user.getUsername();
        OffsetDateTime expiredOn = user.getPasswordExpireOn();
        // alert specific properties
        String mailSubject = configurationService.getAlertBeforeExpirePasswordMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpirePasswordLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_IMMINENT_EXPIRATION;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }

    public void alertUsernamePasswordExpired(DBUser user) {
        LOG.info("Alert username [{}] expired on [{}]", user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getPasswordExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.USERNAME_PASSWORD;
        String credentialId = user.getUsername();
        OffsetDateTime expiredOn = user.getPasswordExpireOn();

        // alert specific properties
        String mailSubject = configurationService.getAlertExpiredPasswordMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredPasswordLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_EXPIRED;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }

    public void alertBeforeAccessTokenExpire(DBUser user) {
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
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_IMMINENT_EXPIRATION;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }

    public void alertAccessTokenExpired(DBUser user) {
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
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_EXPIRED;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }


    public void alertBeforeCertificateExpire(DBUser user) {
        LOG.info("Alert Certificate [{}] for user [{}] is about to expire on [{}]",
                user.getCertificate().getCertificateId(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getAccessTokenExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.CERTIFICATE;
        String credentialId = user.getCertificate().getCertificateId();
        OffsetDateTime expiredOn = user.getCertificate().getValidTo();

        // alert specific properties
        String mailSubject = configurationService.getAlertBeforeExpireCertificateMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpireCertificateLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_IMMINENT_EXPIRATION;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }

    public void alertCertificateExpired(DBUser user) {
        LOG.info("Alert Certificate [{}] for user [{}] expired on [{}]",
                user.getCertificate().getCertificateId(),
                user.getUsername(),
                ISO_LOCAL_DATE_TIME.format(user.getAccessTokenExpireOn()));

        String mailTo = user.getEmailAddress();
        CredentialTypeEnum credentialType = CredentialTypeEnum.CERTIFICATE;
        String credentialId = user.getCertificate().getCertificateId();
        OffsetDateTime expiredOn = user.getCertificate().getValidTo();

        // alert specific properties
        String mailSubject = configurationService.getAlertExpiredCertificateMailSubject();
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredCertificateLevel();
        AlertTypeEnum alertType = AlertTypeEnum.CREDENTIALS_EXPIRED;

        alertCredentialExpiration(mailSubject, mailTo,
                credentialType, credentialId, expiredOn,
                alertLevel, alertType);
    }


    public void alertCredentialExpiration(String mailSubject,
                                          String mailTo,
                                          CredentialTypeEnum credentialType,
                                          String credentialId,
                                          OffsetDateTime expirationDate,
                                          AlertLevelEnum level,
                                          AlertTypeEnum alertType) {

        // create alert
        OffsetDateTime reportDate = OffsetDateTime.now();
        String serverName = HttpUtils.getServerAddress();

        DBAlert alert = new DBAlert();
        alert.setProcessed(false);
        alert.setMailSubject(mailSubject);
        alert.setMailTo(mailTo);
        alert.setReportingTime(reportDate);
        alert.setAlertType(alertType);
        alert.setAlertLevel(level);
        alert.addProperty(CredentialsExpirationProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialsExpirationProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialsExpirationProperties.EXPIRATION_DATETIME.name(), expirationDate);
        alert.addProperty(CredentialsExpirationProperties.REPORTING_DATETIME.name(), reportDate);
        alert.addProperty(CredentialsExpirationProperties.ALERT_LEVEL.name(), level.name());
        alert.addProperty(CredentialsExpirationProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert);
    }

    public void submitAlertMail(DBAlert alert) {
        String mailTo = alert.getMailTo();
        if (StringUtils.isBlank(mailTo)) {
            LOG.warn("Can not send mail (empty mail) for alert [{}]!", alert);
            return;
        }

        String mailFrom = configurationService.getAlertEmailFrom();
        PropertiesMailModel props = new PropertiesMailModel(alert);
        mailService.sendMail(props, mailFrom, alert.getMailTo());

        alert.setProcessed(true);
        alert.setProcessedTime(OffsetDateTime.now());
        alertDao.update(alert);
    }

    public void alertUsernamePasswordCredentialsSuspended(DBUser user) {
        String mailTo = user.getEmailAddress();
        String mailSubject = "User account is suspended";
        AlertLevelEnum level = AlertLevelEnum.LOW;
        AlertTypeEnum alertType = AlertTypeEnum.ACCOUNT_SUSPENDED;
        Integer failureCount = user.getSequentialLoginFailureCount();
        OffsetDateTime lastFailedLoginDate = user.getLastFailedLoginAttempt();
        OffsetDateTime suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getLoginSuspensionTimeInSeconds());
        CredentialTypeEnum credentialType = CredentialTypeEnum.USERNAME_PASSWORD;
        String credentialId = user.getUsername();

        alertCredentialSuspended(mailSubject, mailTo,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate, suspendedUtil,
                level, alertType);
    }

    public void alertAccessTokenCredentialsSuspended(DBUser user) {
        String mailTo = user.getEmailAddress();
        String mailSubject = "User access token is suspended";
        AlertLevelEnum level = AlertLevelEnum.LOW;
        AlertTypeEnum alertType = AlertTypeEnum.ACCOUNT_SUSPENDED;
        Integer failureCount = user.getSequentialTokenLoginFailureCount();
        OffsetDateTime lastFailedLoginDate = user.getLastTokenFailedLoginAttempt();
        OffsetDateTime suspendedUtil = lastFailedLoginDate.plusSeconds(configurationService.getAccessTokenLoginSuspensionTimeInSeconds());
        CredentialTypeEnum credentialType = CredentialTypeEnum.ACCESS_TOKEN;
        String credentialId = user.getAccessTokenIdentifier();

        alertCredentialSuspended(mailSubject, mailTo,
                credentialType, credentialId,
                failureCount, lastFailedLoginDate, suspendedUtil,
                level, alertType);
    }


    public void alertCredentialSuspended(String mailSubject,
                                         String mailTo,
                                         CredentialTypeEnum credentialType,
                                         String credentialId,
                                         Integer failedLoginCount,
                                         OffsetDateTime lastFailedLoginDate,
                                         OffsetDateTime suspendedUtil,
                                         AlertLevelEnum level,
                                         AlertTypeEnum alertType) {

        OffsetDateTime reportDate = OffsetDateTime.now();
        String serverName = HttpUtils.getServerAddress();

        DBAlert alert = new DBAlert();
        alert.setProcessed(false);
        alert.setMailSubject(mailSubject);
        alert.setMailTo(mailTo);
        alert.setReportingTime(reportDate);
        alert.setAlertType(alertType);
        alert.setAlertLevel(level);
        alert.addProperty(CredentialSuspendedProperties.CREDENTIAL_TYPE.name(), credentialType.name());
        alert.addProperty(CredentialSuspendedProperties.CREDENTIAL_ID.name(), credentialId);
        alert.addProperty(CredentialSuspendedProperties.FAILED_LOGIN_ATTEMPT.name(), failedLoginCount.toString());
        alert.addProperty(CredentialSuspendedProperties.LAST_LOGIN_FAILURE_DATETIME.name(), lastFailedLoginDate);
        alert.addProperty(CredentialSuspendedProperties.SUSPENDED_UNTIL_DATETIME.name(), suspendedUtil);
        alert.addProperty(CredentialSuspendedProperties.REPORTING_DATETIME.name(), reportDate);
        alert.addProperty(CredentialSuspendedProperties.ALERT_LEVEL.name(), level.name());
        alert.addProperty(CredentialSuspendedProperties.SERVER_NAME.name(), serverName);
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert);

    }

}
