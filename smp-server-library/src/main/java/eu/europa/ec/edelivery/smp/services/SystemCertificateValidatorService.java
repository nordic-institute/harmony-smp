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


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Service for validating and alerting expiration of system certificates (i.e. SML integration certificates and
 * TLS certificates).
 *
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
public class SystemCertificateValidatorService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SystemCertificateValidatorService.class);

    private final ConfigurationService configurationService;

    private final UIKeystoreService uiKeystoreService;

    private final UITruststoreService uiTruststoreService;

    private final UserDao userDao;

    private final DomainDao domainDao;

    private final SystemCertificateAlertService alertService;

    public SystemCertificateValidatorService(ConfigurationService configurationService,
                                             UIKeystoreService uiKeystoreService,
                                             UITruststoreService uiTruststoreService,
                                             DomainDao domainDao,
                                             UserDao userDao,
                                             SystemCertificateAlertService alertService) {
        this.configurationService = configurationService;
        this.uiKeystoreService = uiKeystoreService;
        this.uiTruststoreService = uiTruststoreService;
        this.domainDao = domainDao;
        this.userDao = userDao;
        this.alertService = alertService;
    }

    public void validateSystemCertificates() {
        validateBeforeExpireCertificate();
        validateExpiredCertificate();
    }

    private void validateBeforeExpireCertificate() {
        Boolean alertExpired = configurationService.getAlertExpiredCertificateEnabled();
        if (alertExpired == null || !alertExpired) {
            LOG.debug("Future expiration of system certificate validation is disabled");
            return;
        }

        Integer days = configurationService.getAlertBeforeExpireSystemCertificatePeriod();
        Map<String, OffsetDateTime> aboutToExpireCertificates = uiKeystoreService.getAboutToExpireCertificateAliases(days);
        aboutToExpireCertificates.putAll(uiTruststoreService.getAboutToExpireCertificateAliases(days));

        List<DBUser> systemAdministrators = userDao.findUsersByApplicationRoles(EnumSet.of(ApplicationRoleType.SYSTEM_ADMIN));
        domainDao.getDomainsWithExpiringCertificates(aboutToExpireCertificates.keySet())
                .forEach(domain -> alertExpiringCertificate(systemAdministrators, domain, aboutToExpireCertificates));
    }

    private void validateExpiredCertificate() {
        Boolean alertExpired = configurationService.getAlertExpiredCertificateEnabled();
        if (alertExpired == null || !alertExpired) {
            LOG.debug("Expiration of system certificate validation is disabled");
            return;
        }

        Map<String, OffsetDateTime> expiredCertificates = uiKeystoreService.getExpiredCertificateAliases();
        expiredCertificates.putAll(uiTruststoreService.getExpiredCertificateAliases());

        List<DBUser> systemAdministrators = userDao.findUsersByApplicationRoles(EnumSet.of(ApplicationRoleType.SYSTEM_ADMIN));
        domainDao.getDomainsWithExpiringCertificates(expiredCertificates.keySet())
                .forEach(domain -> alertExpiringCertificate(systemAdministrators, domain, expiredCertificates));
    }

    private void alertExpiringCertificate(List<DBUser> systemAdministrators, DBDomain.DBDomainExpiringCertificateMapping domain, Map<String, OffsetDateTime> expirationDates) {
        if (domain.isMatchedSmlCertificate()) {
            String smlClientKeyChangeAlias = domain.getSmlClientKeyChangeAlias();
            OffsetDateTime expirationDate = expirationDates.get(smlClientKeyChangeAlias);
            systemAdministrators.forEach(systemAdministrator -> {
                    if (isCertificateAlreadyExpired(expirationDate)) {
                        alertService.alertSmlIntegrationCertificateExpired(systemAdministrator, domain.getDomainCode(), smlClientKeyChangeAlias, expirationDate);
                    } else {
                        alertService.alertBeforeSmlIntegrationCertificateExpire(systemAdministrator, domain.getDomainCode(), smlClientKeyChangeAlias, expirationDate);
                    }
                }
            );
        }
        if (domain.isMatchedSigningCertificate()) {
            String signatureKeyAlias = domain.getSignatureKeyAlias();
            OffsetDateTime expirationDate = expirationDates.get(signatureKeyAlias);
            systemAdministrators.forEach(systemAdministrator -> {
                    if (isCertificateAlreadyExpired(expirationDate)) {
                        alertService.alertSigningCertificateExpired(systemAdministrator, domain.getDomainCode(), signatureKeyAlias, expirationDate);
                    } else {
                        alertService.alertBeforeSigningCertificateExpire(systemAdministrator, domain.getDomainCode(), signatureKeyAlias, expirationDate);
                    }
                }
            );
        }
    }

    private boolean isCertificateAlreadyExpired(OffsetDateTime expirationDate) {
        return OffsetDateTime.now(ZoneOffset.UTC).isAfter(expirationDate);
    }
}
