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
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.mail.MailDataModel;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.SystemCertificateExpirationProperties;
import eu.europa.ec.edelivery.smp.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static eu.europa.ec.edelivery.smp.utils.DateTimeUtils.formatOffsetDateTimeWithLocal;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
@Service
public class SystemCertificateAlertService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SystemCertificateValidatorService.class);

    private final AlertDao alertDao;
    private final MailService mailService;
    private final ConfigurationService configurationService;

    private static final String CERTIFICATE_TYPE_SML_INTEGRATION = "DomiSML Integration Certificate";

    private static final String CERTIFICATE_TYPE_SIGNING = "Signing Certificate";

    public SystemCertificateAlertService(AlertDao alertDao, MailService mailService, ConfigurationService configurationService) {
        this.alertDao = alertDao;
        this.mailService = mailService;
        this.configurationService = configurationService;
    }

    public void alertBeforeSigningCertificateExpire(DBUser user, String domain, String certificateAlias, OffsetDateTime expirationDate) {
        alertBeforeSystemCertificateExpire(user, domain, certificateAlias, CERTIFICATE_TYPE_SIGNING, expirationDate);
    }

    public void alertBeforeSmlIntegrationCertificateExpire(DBUser user, String domain, String certificateAlias, OffsetDateTime expirationDate) {
        alertBeforeSystemCertificateExpire(user, domain, certificateAlias, CERTIFICATE_TYPE_SML_INTEGRATION, expirationDate);

    }

    private void alertBeforeSystemCertificateExpire(DBUser user, String domain, String certificateAlias, String certificateType, OffsetDateTime expirationDate) {
        if (user.getApplicationRole() != ApplicationRoleType.SYSTEM_ADMIN) {
            LOG.warn("Alerting before imminent expiration of system certificates is only meant for system administrators");
            return;
        }

        LOG.info("Alert for system certificate of type [{}] having alias [{}] used in domain [{}] about to expire on [{}]",
                certificateType, certificateAlias, domain, ISO_LOCAL_DATE_TIME.format(expirationDate));

        String mailTo = user.getEmailAddress();

        // alert specific properties
        AlertTypeEnum alertType = AlertTypeEnum.SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION;
        AlertLevelEnum alertLevel = configurationService.getAlertBeforeExpireSystemCertificateLevel();
        String mailSubject = alertType.name() + " " + certificateType;
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertSystemCertificateExpiration(user, alert, domain, certificateAlias, certificateType, expirationDate);
    }

    public void alertSigningCertificateExpired(DBUser user, String domain, String certificateAlias, OffsetDateTime expirationDate) {
        alertSystemCertificateExpired(user, domain, certificateAlias, CERTIFICATE_TYPE_SIGNING, expirationDate);
    }

    public void alertSmlIntegrationCertificateExpired(DBUser user, String domain, String certificateAlias, OffsetDateTime expirationDate) {
        alertSystemCertificateExpired(user, domain, certificateAlias, CERTIFICATE_TYPE_SML_INTEGRATION, expirationDate);
    }

    private void alertSystemCertificateExpired(DBUser user, String domain, String certificateAlias, String certificateType, OffsetDateTime expirationDate) {
        if (user.getApplicationRole() != ApplicationRoleType.SYSTEM_ADMIN) {
            LOG.warn("Alerting expiration of system certificates is only meant for system administrators");
            return;
        }

        LOG.info("Alert for system certificate of type [{}] having alias [{}] used in domain [{}] expired on [{}]",
                certificateType, certificateAlias, domain, ISO_LOCAL_DATE_TIME.format(expirationDate));

        String mailTo = user.getEmailAddress();

        // alert specific properties
        AlertLevelEnum alertLevel = configurationService.getAlertExpiredSystemCertificateLevel();
        AlertTypeEnum alertType = AlertTypeEnum.SYSTEM_CERTIFICATE_EXPIRED;
        String mailSubject = alertType.name() + " " + certificateType;
        DBAlert alert = createAlert(user.getUsername(), mailSubject, mailTo, alertLevel, alertType);
        alertSystemCertificateExpiration(user, alert, domain, certificateAlias, certificateType, expirationDate);
    }

    protected DBAlert createAlert(String username, String mailSubject,
                                  String mailTo,
                                  AlertLevelEnum level,
                                  AlertTypeEnum alertType) {
        String serverName = HttpUtils.getServerAddress();

        DBAlert alert = new DBAlert();
        alert.setMailSubject(mailSubject);
        alert.setMailTo(mailTo);
        alert.setUsername(username);
        alert.setReportingTime(OffsetDateTime.now());
        alert.setAlertType(alertType);
        alert.setAlertLevel(level);
        alert.setAlertStatus(AlertStatusEnum.PROCESS);
        alert.addProperty(SystemCertificateExpirationProperties.SERVER_NAME.name(), serverName);
        return alert;
    }

    private void alertSystemCertificateExpiration(DBUser user, DBAlert alert, String domain, String certificateAlias, String certificateType, OffsetDateTime expirationDate) {
        alert.addProperty(SystemCertificateExpirationProperties.CERTIFICATE_ALIAS.name(), certificateAlias);
        alert.addProperty(SystemCertificateExpirationProperties.CERTIFICATE_TYPE.name(), certificateType);
        alert.addProperty(SystemCertificateExpirationProperties.DOMAIN_CODE.name(), domain);
        alert.addProperty(SystemCertificateExpirationProperties.EXPIRATION_DATETIME.name(), formatOffsetDateTimeWithLocal(expirationDate, user.getSmpLocale()));
        alert.addProperty(SystemCertificateExpirationProperties.ALERT_LEVEL.name(), alert.getAlertLevel().name());
        alertDao.persistFlushDetach(alert);
        // submit alerts
        submitAlertMail(alert, user);
    }

    public void submitAlertMail(DBAlert alert, DBUser user) {
        String mailTo = alert.getMailTo();
        if (StringUtils.isBlank(mailTo)) {
            LOG.warn("Can not send mail (empty mail) for alert [{}]!", alert);
            return;
        }

        String mailFrom = configurationService.getAlertEmailFrom();

        MailDataModel props = new MailDataModel(user.getSmpLocale(), alert);

        // add additional common properties to the model
        props.getModel().put(MailDataModel.CommonProperties.SMP_INSTANCE_NAME.name(),
                configurationService.getSMPInstanceName());
        props.getModel().put(MailDataModel.CommonProperties.CURRENT_DATETIME.name(),
                formatOffsetDateTimeWithLocal(OffsetDateTime.now(), user.getSmpLocale()));

        try {
            mailService.sendMail(props, mailFrom, mailTo);
        } catch (Throwable exc) {
            LOG.error("Can not send mail [{}] for alert [{}]! Error [{}]",
                    mailTo, alert, ExceptionUtils.getRootCauseMessage(exc));
            LOG.error("Error sending mail", exc);
        }
    }
}
