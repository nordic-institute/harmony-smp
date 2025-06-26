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
package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.enums.AlertScope;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

@Entity
@Audited
@Table(name = "SMP_PERIODICAL_ALERT", comment = "SMP periodical alerts")
@NamedQuery(name = QUERY_PERIODICAL_ALERTS_BY_TYPES,
        query = "SELECT distinct a FROM DBPeriodicalAlert a" +
                " WHERE a.entityType IN :entityTypes")
@NamedQuery(name = QUERY_PERIODICAL_ALERTS_BY_CREDENTIAL_ENTITY_ID,
        query = "SELECT distinct a FROM DBPeriodicalAlert a" +
                " WHERE a.entityType = :entityType" +
                " AND a.entityIdentifier = :credentialEntityId")
@NamedQuery(name = QUERY_PERIODICAL_ALERTS_BY_SYSTEM_CERTIFICATE_ALIAS_AND_ALERT_TYPE,
        query = "SELECT distinct a FROM DBPeriodicalAlert a" +
                " WHERE a.entityType = :entityType" +
                " AND a.entityIdentifier = :certificateAlias" +
                " AND a.alertScope = :alertScope")
/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
public class DBPeriodicalAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_PERIODICAL_ALERT_SEQ")
    @GenericGenerator(name = "SMP_PERIODICAL_ALERT_SEQ", strategy = "native", parameters = {
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique periodical alert id")
    Long id;

    @Column(name = "LAST_ALERT_ON")
    @ColumnDescription(comment = "Generated last password expire alert")
    private OffsetDateTime expireAlertOn;

    @Column(name = "ENTITY_IDENTIFIER")
    private String entityIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTITY_TYPE")
    private CredentialType entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ALERT_SCOPE")
    private AlertScope alertScope;

    @Override
    public Object getId() {
        return id;
    }

    public OffsetDateTime getExpireAlertOn() {
        return expireAlertOn;
    }

    public void setExpireAlertOn(OffsetDateTime expireAlertOn) {
        this.expireAlertOn = expireAlertOn;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public CredentialType getEntityType() {
        return entityType;
    }

    public void setEntityType(CredentialType entityType) {
        this.entityType = entityType;
    }

    public AlertScope getAlertScope() {
        return alertScope;
    }

    public void setAlertScope(AlertScope alertScope) {
        this.alertScope = alertScope;
    }
}
