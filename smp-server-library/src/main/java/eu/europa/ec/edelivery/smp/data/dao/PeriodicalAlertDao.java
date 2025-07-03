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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.AlertScope;
import eu.europa.ec.edelivery.smp.data.enums.ExpiringEntity;
import eu.europa.ec.edelivery.smp.data.model.DBPeriodicalAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.data.enums.ExpiringEntity.SYSTEM_CERTIFICATE;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
@Repository
public class PeriodicalAlertDao extends BaseDao<DBPeriodicalAlert> {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalAlertDao.class);

    public DBPeriodicalAlert findOrCreate(ExpiringEntity entityType, String entityId, String alias, AlertScope alertScope) {
        Optional<DBPeriodicalAlert> result = Optional.empty();
        TypedQuery<DBPeriodicalAlert> query;
        switch (entityType) {
            case ACCESS_TOKEN, CERTIFICATE, USERNAME_PASSWORD:
                query = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_CREDENTIAL_ENTITY_ID, DBPeriodicalAlert.class);
                query.setParameter("entityType", entityType);
                query.setParameter("credentialEntityId", entityId);
                result = Optional.ofNullable(query.getSingleResultOrNull());
                break;
            case SYSTEM_CERTIFICATE:
                query = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_SYSTEM_CERTIFICATE_ALIAS_AND_ALERT_TYPE, DBPeriodicalAlert.class);
                query.setParameter("entityType", entityType);
                query.setParameter("certificateAlias", alias);
                query.setParameter("alertScope", alertScope);
                result = Optional.ofNullable(query.getSingleResultOrNull());
                break;
            default:
                LOG.warn("Unsupported entity type {}", entityType);
        }
        return result.orElse(new DBPeriodicalAlert());
    }

    @Transactional
    public void updateAlertSentForUserCredentials(DBCredential credential, OffsetDateTime dateTime) {
        // attach to jpa session if not already
        String entityIdentifier = getCredentialEntityId(credential.getId());
        ExpiringEntity expiringEntity = ExpiringEntity.getExpiringEntity(credential.getCredentialType());
        DBPeriodicalAlert periodicalAlert = findOrCreate(expiringEntity, entityIdentifier, null, null);
        periodicalAlert.setEntityType(expiringEntity);
        periodicalAlert.setEntityIdentifier(entityIdentifier);
        periodicalAlert.setExpireAlertOn(dateTime);
        persistOrUpdate(periodicalAlert);
    }

    @Transactional
    public void updateAlertSentForUserSystemCertificate(String certificateAlias, AlertScope alertScope, OffsetDateTime dateTime) {
        // attach to jpa session if not already
        DBPeriodicalAlert periodicalAlert = findOrCreate(SYSTEM_CERTIFICATE, null, certificateAlias, alertScope);
        periodicalAlert.setEntityType(SYSTEM_CERTIFICATE);
        periodicalAlert.setEntityIdentifier(certificateAlias);
        periodicalAlert.setAlertScope(alertScope);
        periodicalAlert.setExpireAlertOn(dateTime);
        persistOrUpdate(periodicalAlert);
    }

    public List<DBCredential> filterCredentialsBeforeExpireAlerts(List<DBCredential> credentials, OffsetDateTime lastSendAlertDate) {
        TypedQuery<DBPeriodicalAlert> filterAboutToExpire = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_TYPES, DBPeriodicalAlert.class);
        filterAboutToExpire.setParameter("entityTypes", credentials.stream().map(DBCredential::getCredentialType).map(ExpiringEntity::getExpiringEntity).collect(Collectors.toSet()));
        List<DBPeriodicalAlert> periodicalAlerts = filterAboutToExpire.getResultList();
        return credentials.stream()
                .filter(credential -> periodicalAlerts.stream().anyMatch(periodicalAlert ->
                                ExpiringEntity.getExpiringEntity(credential.getCredentialType()) == periodicalAlert.getEntityType()
                                && getCredentialEntityId(credential.getId()).equals(periodicalAlert.getEntityIdentifier())
                                && (periodicalAlert.getExpireAlertOn() == null
                                    || periodicalAlert.getExpireAlertOn().isBefore(lastSendAlertDate))))
                .collect(Collectors.toList());
    }

    public List<DBCredential> filterExpiredCredentialsAlerts(List<DBCredential> credentials, OffsetDateTime lastSendAlertDate) {
        TypedQuery<DBPeriodicalAlert> filterExpired = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_TYPES, DBPeriodicalAlert.class);
        filterExpired.setParameter("entityTypes", credentials.stream().map(DBCredential::getCredentialType).map(ExpiringEntity::getExpiringEntity).collect(Collectors.toSet()));
        List<DBPeriodicalAlert> periodicalAlerts = filterExpired.getResultList();

        return credentials.stream()
                .filter(credential -> periodicalAlerts.stream()
                        .filter(periodicalAlert -> periodicalAlert.getEntityType() == ExpiringEntity.getExpiringEntity(credential.getCredentialType()))
                        .filter(periodicalAlert -> periodicalAlert.getEntityIdentifier().equals(getCredentialEntityId(credential.getId())))
                        .anyMatch(periodicalAlert ->
                                periodicalAlert.getExpireAlertOn() == null
                                    || periodicalAlert.getExpireAlertOn().isBefore(credential.getExpireOn())
                                    || periodicalAlert.getExpireAlertOn().isEqual(credential.getExpireOn())
                                    || periodicalAlert.getExpireAlertOn().isBefore(lastSendAlertDate)))
                .collect(Collectors.toList());
    }

    public boolean isSystemCertificateReadyForBeforeExpireAlerts(String certificateAlias, AlertScope alertScope, OffsetDateTime lastSendAlertDate) {
        TypedQuery<DBPeriodicalAlert> filterExpired = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_TYPES, DBPeriodicalAlert.class);
        filterExpired.setParameter("entityTypes", Set.of(SYSTEM_CERTIFICATE));
        List<DBPeriodicalAlert> periodicalAlerts = filterExpired.getResultList();

        return periodicalAlerts.stream()
                .filter(periodicalAlert -> periodicalAlert.getEntityType() == SYSTEM_CERTIFICATE)
                .filter(periodicalAlert -> periodicalAlert.getEntityIdentifier().equals(certificateAlias))
                .filter(periodicalAlert -> periodicalAlert.getAlertScope() == alertScope)
                .anyMatch(periodicalAlert -> periodicalAlert.getExpireAlertOn() == null
                                            || periodicalAlert.getExpireAlertOn().isBefore(lastSendAlertDate));
    }

    public boolean isSystemCertificateReadyForExpiredAlerts(String certificateAlias, AlertScope alertScope, OffsetDateTime expirationDate, OffsetDateTime lastSendAlertDate) {
        TypedQuery<DBPeriodicalAlert> filterExpired = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_TYPES, DBPeriodicalAlert.class);
        filterExpired.setParameter("entityTypes", Set.of(SYSTEM_CERTIFICATE));
        List<DBPeriodicalAlert> periodicalAlerts = filterExpired.getResultList();

        return periodicalAlerts.stream()
                .filter(periodicalAlert -> periodicalAlert.getEntityType() == SYSTEM_CERTIFICATE)
                .filter(periodicalAlert -> periodicalAlert.getEntityIdentifier().equals(certificateAlias))
                .filter(periodicalAlert -> periodicalAlert.getAlertScope() == alertScope)
                .anyMatch(periodicalAlert -> periodicalAlert.getExpireAlertOn() == null
                                            || periodicalAlert.getExpireAlertOn().isBefore(expirationDate)
                                            || periodicalAlert.getExpireAlertOn().isEqual(expirationDate)
                                            || periodicalAlert.getExpireAlertOn().isBefore(lastSendAlertDate));
    }

    private String getCredentialEntityId(Long credentialId) {
        if (credentialId == null) {
            LOG.warn("Credential ID is null");
            return "";
        }
        return Long.toString(credentialId);
    }
}