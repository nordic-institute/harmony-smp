/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.data.dao.QueryNames;
import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

/**
 * Database configuration entity for DomiSMP Domain specific configuration properties
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Entity
@Audited
@Table(name = "SMP_DOMAIN_CONFIGURATION",
        indexes = {

                @Index(name = "SMP_DOMAIN_CONF_IDX", columnList = "ID, PROPERTY_NAME, FK_DOMAIN_ID", unique = true),
        })
@NamedQuery(name = QueryNames.QUERY_DOMAIN_CONFIGURATION_ALL,
        query = "SELECT d FROM DBDomainConfiguration d where d.domain.id = :domain_id")
@org.hibernate.annotations.Table(appliesTo = "SMP_DOMAIN_CONFIGURATION", comment = "SMP domain configuration")
public class DBDomainConfiguration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOMAIN_CONF_SEQ")
    @GenericGenerator(name = "SMP_DOMAIN_CONF_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique domain configuration id")
    Long id;

    @Column(name = "PROPERTY_NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_512, nullable = false)
    @ColumnDescription(comment = "Property name/key")
    String property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOMAIN_ID", nullable = false)
    private DBDomain domain;

    @Column(name = "PROPERTY_VALUE", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Property value")
    String value;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Property description")
    String description;

    @Column(name = "USER_SYSTEM_DEFAULT", nullable = false)
    @ColumnDescription(comment = "Use system default value")
    boolean useSystemDefault = true;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUseSystemDefault() {
        return useSystemDefault;
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        this.useSystemDefault = useSystemDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDomainConfiguration that = (DBDomainConfiguration) o;
        return Objects.equals(property, that.property) && Objects.equals(domain, that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, domain);
    }
}
