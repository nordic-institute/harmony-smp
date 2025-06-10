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
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class SystemCertificateValidatorServiceTest {

    @Mock
    ConfigurationService configurationService;

    @Mock
    UIKeystoreService uiKeystoreService;

    @Mock
    UITruststoreService uiTruststoreService;

    @Mock
    UserDao userDao;

    @Mock
    DomainDao domainDao;

    @Mock
    SystemCertificateAlertService alertService;

    SystemCertificateValidatorService systemCertificateValidatorService;

    @Mock
    DBUser systemAdmin;

    @BeforeEach
    public void setup() {
        systemCertificateValidatorService = new SystemCertificateValidatorService(configurationService, uiKeystoreService, uiTruststoreService, domainDao, userDao, alertService);
    }

    @Test
    void validateSystemCertificates_ValidationsDisabled() {
        Mockito.when(configurationService.getAlertBeforeExpireCertificateEnabled()).thenReturn(Boolean.FALSE);
        Mockito.when(configurationService.getAlertExpiredCertificateEnabled()).thenReturn(Boolean.FALSE);

        systemCertificateValidatorService.validateSystemCertificates();

        Mockito.verify(userDao, Mockito.never()).findUsersByApplicationRoles(EnumSet.of(ApplicationRoleType.SYSTEM_ADMIN));
    }

    @Test
    void validateSystemCertificates_ValidateBeforeExpiration() {
        Mockito.when(configurationService.getAlertBeforeExpireCertificateEnabled()).thenReturn(Boolean.TRUE);
        Mockito.when(configurationService.getAlertExpiredCertificateEnabled()).thenReturn(Boolean.FALSE);
        Mockito.when(userDao.findUsersByApplicationRoles(EnumSet.of(ApplicationRoleType.SYSTEM_ADMIN))).thenReturn(List.of(systemAdmin));
        Mockito.when(configurationService.getAlertBeforeExpireSystemCertificatePeriod()).thenReturn(10);

        final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Map<String, OffsetDateTime> aboutToExpireCertificates = new HashMap<>();
        aboutToExpireCertificates.put("expiringInNineDays", now.plusDays(9));
        aboutToExpireCertificates.put("expiringInFiveDays", now.plusDays(5));
        Mockito.when(uiKeystoreService.getAboutToExpireCertificateAliases(10)).thenReturn(aboutToExpireCertificates);

        aboutToExpireCertificates = new HashMap<>();
        aboutToExpireCertificates.put("expiringInTwoDays", now.plusDays(2));
        aboutToExpireCertificates.put("expiringTomorrow", now.plusDays(1));
        Mockito.when(uiTruststoreService.getAboutToExpireCertificateAliases(10)).thenReturn(aboutToExpireCertificates);

        List<DBDomain.DBDomainExpiringCertificateMapping> certificateMappings = new ArrayList<>();
        final String unexpiringCertificateAlias = "unexpiring";
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain1", "expiringInNineDays", unexpiringCertificateAlias, true, false));
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain1", unexpiringCertificateAlias, "expiringInFiveDays", false, true));
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain2", "expiringInTwoDays", "expiringTomorrow", true, true));

        Mockito.when(domainDao.getDomainsWithExpiringCertificates(Set.of("expiringInNineDays", "expiringInFiveDays", "expiringInTwoDays", "expiringTomorrow"))).thenReturn(certificateMappings);

        systemCertificateValidatorService.validateSystemCertificates();

        Mockito.verify(alertService).alertBeforeSigningCertificateExpire(systemAdmin, "domain1", "expiringInNineDays", now.plusDays(9));
        Mockito.verify(alertService).alertBeforeSmlIntegrationCertificateExpire(systemAdmin, "domain1", "expiringInFiveDays", now.plusDays(5));
        Mockito.verify(alertService).alertBeforeSigningCertificateExpire(systemAdmin, "domain2", "expiringInTwoDays", now.plusDays(2));
        Mockito.verify(alertService).alertBeforeSmlIntegrationCertificateExpire(systemAdmin, "domain2", "expiringTomorrow", now.plusDays(1));
    }

    @Test
    void validateSystemCertificates_ValidateExpired() {
        Mockito.when(configurationService.getAlertBeforeExpireCertificateEnabled()).thenReturn(Boolean.FALSE);
        Mockito.when(configurationService.getAlertExpiredCertificateEnabled()).thenReturn(Boolean.TRUE);
        Mockito.when(userDao.findUsersByApplicationRoles(EnumSet.of(ApplicationRoleType.SYSTEM_ADMIN))).thenReturn(List.of(systemAdmin));

        final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Map<String, OffsetDateTime> expiredCertificates = new HashMap<>();
        expiredCertificates.put("expiredForNineDays", now.minusDays(9));
        expiredCertificates.put("expiredForFiveDays", now.minusDays(5));
        Mockito.when(uiKeystoreService.getExpiredCertificateAliases()).thenReturn(expiredCertificates);

        expiredCertificates = new HashMap<>();
        expiredCertificates.put("expiredForTwoDays", now.minusDays(2));
        expiredCertificates.put("expiredYesterday", now.minusDays(1));
        Mockito.when(uiTruststoreService.getExpiredCertificateAliases()).thenReturn(expiredCertificates);

        List<DBDomain.DBDomainExpiringCertificateMapping> certificateMappings = new ArrayList<>();
        final String unexpiredCertificateAlias = "unexpired";
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain1", "expiredForNineDays", unexpiredCertificateAlias, true, false));
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain1", unexpiredCertificateAlias, "expiredForFiveDays", false, true));
        certificateMappings.add(new DBDomain.DBDomainExpiringCertificateMapping("domain2", "expiredForTwoDays", "expiredYesterday", true, true));
        Mockito.when(domainDao.getDomainsWithExpiringCertificates(Set.of("expiredForNineDays", "expiredForFiveDays", "expiredForTwoDays", "expiredYesterday"))).thenReturn(certificateMappings);

        systemCertificateValidatorService.validateSystemCertificates();

        Mockito.verify(alertService).alertSigningCertificateExpired(systemAdmin, "domain1", "expiredForNineDays", now.minusDays(9));
        Mockito.verify(alertService).alertSmlIntegrationCertificateExpired(systemAdmin, "domain1", "expiredForFiveDays", now.minusDays(5));
        Mockito.verify(alertService).alertSigningCertificateExpired(systemAdmin, "domain2", "expiredForTwoDays", now.minusDays(2));
        Mockito.verify(alertService).alertSmlIntegrationCertificateExpired(systemAdmin, "domain2", "expiredYesterday", now.minusDays(1));
    }
}