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
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.data.enums.ExpiringEntity.SYSTEM_CERTIFICATE;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
@Repository
public class PeriodicalAlertDao extends BaseDao<DBPeriodicalAlert> {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalAlertDao.class);

    /**
     * Find or create a periodical alert for the given entity type, identifier and alert scope.
     *
     * @param entityType       the type of the entity (e.g. USERNAME_PASSWORD, ACCESS_TOKEN, CERTIFICATE)
     * @param entityIdentifier the identifier of the entity (e.g. credential DB ID or certificate alias frm the keystore/truststore)
     * @param alertScope       the scope of the alert (e.g. USER_CREDENTIAL, SYSTEM_TRUSTSTORE, SYSTEM_KEYSTORE)
     * @return a DBPeriodicalAlert instance
     */
    public DBPeriodicalAlert findOrCreate(ExpiringEntity entityType, String entityIdentifier, AlertScope alertScope) {

        TypedQuery<DBPeriodicalAlert> query = memEManager.createNamedQuery(QUERY_PERIODICAL_ALERTS_BY_ENTITY_IDENTIFIER_AND_ALERT_TYPE, DBPeriodicalAlert.class);
        query.setParameter(PARAM_ENTITY_TYPE, entityType);
        query.setParameter(PARAM_IDENTIFIER, entityIdentifier);
        query.setParameter(PARAM_ALERT_SCOPE, alertScope);
        Optional<DBPeriodicalAlert> result = Optional.ofNullable(query.getSingleResultOrNull());
        return result.orElse(new DBPeriodicalAlert(entityType, entityIdentifier, alertScope));
    }

    /**
     * Finds (or creates if not exist) a periodical alert for user credentials and set the Alert event date.
     *
     * @param credential the user credential for which the alert is being set
     * @param dateTime   the date and time when the alert was sent
     */
    @Transactional
    public void updateAlertSentForUserCredentials(DBCredential credential, OffsetDateTime dateTime) {
        if (credential == null || credential.getId() == null) {
            LOG.warn("Credential is null, cannot update alert sent for user credentials which is not persisted in the database");
            return;
        }
        String entityIdentifier = getCredentialEntityId(credential.getId());
        ExpiringEntity expiringEntity = ExpiringEntity.getExpiringEntity(credential.getCredentialType());
        DBPeriodicalAlert periodicalAlert = findOrCreate(expiringEntity, entityIdentifier, AlertScope.USER_CREDENTIAL);
        periodicalAlert.setLastAlertOn(dateTime);
        persistOrUpdate(periodicalAlert);
    }

    /**
     * Finds (or creates if not exist) a periodical alert for system certificate and set the Alert event date.
     *
     * @param certificateAlias the alias of the system certificate for which the alert is being set
     * @param alertScope       the scope of the alert (e.g. USER_CREDENTIAL, SYSTEM_TRUSTSTORE, SYSTEM_KEYSTORE)
     * @param dateTime         the date and time when the alert was sent
     */
    @Transactional
    public void updateAlertSentForUserSystemCertificate(String certificateAlias, AlertScope alertScope, OffsetDateTime dateTime) {
        DBPeriodicalAlert periodicalAlert = findOrCreate(SYSTEM_CERTIFICATE, certificateAlias, alertScope);
        periodicalAlert.setLastAlertOn(dateTime);
        persistOrUpdate(periodicalAlert);
    }

    public boolean isSystemCertificateReadyForBeforeExpireAlerts(String certificateAlias, AlertScope alertScope, OffsetDateTime lastSendAlertDate) {

        DBPeriodicalAlert periodicalAlert = findOrCreate(SYSTEM_CERTIFICATE, certificateAlias, alertScope);
        if (periodicalAlert.getId() == null) {
            LOG.debug("No periodical alerts found for system certificates");
            return true;
        }

        LOG.info("Verify if alerts can be already sent for system certificate about to expire having alias [{}] in scope [{}] since last attempt [{}]", certificateAlias, alertScope, lastSendAlertDate);
        return periodicalAlert.getLastAlertOn() == null
                || periodicalAlert.getLastAlertOn().isBefore(lastSendAlertDate);
    }

    public boolean isSystemCertificateReadyForExpiredAlerts(String certificateAlias, AlertScope alertScope, OffsetDateTime expirationDate, OffsetDateTime lastSendAlertDate) {
        DBPeriodicalAlert periodicalAlert = findOrCreate(SYSTEM_CERTIFICATE, certificateAlias, alertScope);

        if (periodicalAlert.getId() == null) {
            LOG.debug("No periodical alerts found for system certificates");
            return true;
        }

        LOG.info("Verify if alerts can be already sent for expired system certificate having alias [{}] in scope [{}] and expiration date [{}] since last attempt [{}]", certificateAlias, alertScope, expirationDate, lastSendAlertDate);

        return periodicalAlert.getLastAlertOn() == null
                || periodicalAlert.getLastAlertOn().isBefore(expirationDate)
                || periodicalAlert.getLastAlertOn().isEqual(expirationDate)
                || periodicalAlert.getLastAlertOn().isBefore(lastSendAlertDate);
    }

    private String getCredentialEntityId(Long credentialId) {
        if (credentialId == null) {
            LOG.warn("Credential ID is null");
            return "";
        }
        return Long.toString(credentialId);
    }
}