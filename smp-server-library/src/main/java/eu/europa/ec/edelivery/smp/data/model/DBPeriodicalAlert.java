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
import eu.europa.ec.edelivery.smp.data.enums.ExpiringEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.QUERY_PERIODICAL_ALERTS_BY_ENTITY_IDENTIFIER_AND_ALERT_TYPE;

/**
 * Represents a periodical alert for expiring entities e.g. User passwords, user authentication access tokens or certificates
 * and system keys for signing or encryption. The table is generic, using PK of the credentials or alias from system keystores/truststores as entity identifier.
 *
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
@Entity
@Audited
@Table(name = "SMP_PERIODICAL_ALERT", comment = "SMP periodical alerts",
        indexes = {@Index(name = "SMP_ALERT_COMPOSITE_IDX", columnList = "ENTITY_IDENTIFIER, ENTITY_TYPE, ALERT_SCOPE", unique = true)
        })
@NamedQuery(name = QUERY_PERIODICAL_ALERTS_BY_ENTITY_IDENTIFIER_AND_ALERT_TYPE,
        query = "SELECT distinct a FROM DBPeriodicalAlert a" +
                " WHERE a.entityType = :entity_type" +
                " AND a.entityIdentifier = :identifier" +
                " AND a.alertScope = :alertScope")
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
    @ColumnDescription(comment = "Date and time when the last alert was sent for this entity")
    private OffsetDateTime lastAlertOn;

    @ColumnDescription(comment = "Entity identifier for which the alert is sent, credential database id, certificate alias, etc.")
    @Column(name = "ENTITY_IDENTIFIER")
    private String entityIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTITY_TYPE")
    private ExpiringEntity entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ALERT_SCOPE")
    private AlertScope alertScope;

    public DBPeriodicalAlert() {
        // Default constructor for JPA
    }

    public DBPeriodicalAlert(ExpiringEntity entityType, String entityIdentifier, AlertScope alertScope) {
        this.entityType = entityType;
        this.entityIdentifier = entityIdentifier;
        this.alertScope = alertScope;
    }

    @Override
    public Object getId() {
        return id;
    }

    public OffsetDateTime getLastAlertOn() {
        return lastAlertOn;
    }

    public void setLastAlertOn(OffsetDateTime expireAlertOn) {
        this.lastAlertOn = expireAlertOn;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public ExpiringEntity getEntityType() {
        return entityType;
    }

    public void setEntityType(ExpiringEntity entityType) {
        this.entityType = entityType;
    }

    public AlertScope getAlertScope() {
        return alertScope;
    }

    public void setAlertScope(AlertScope alertScope) {
        this.alertScope = alertScope;
    }
}
