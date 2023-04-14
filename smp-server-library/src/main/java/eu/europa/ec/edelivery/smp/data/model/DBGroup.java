/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * The group of resources with shared resource management rights. The user with group admin has rights to create/delete
 * resources for the group.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_GROUP",
        indexes = {@Index(name = "SMP_GRP_UNIQ_DOM_IDX", columnList = "NAME,FK_DOMAIN_ID", unique = true)
        })

@org.hibernate.annotations.Table(appliesTo = "SMP_GROUP", comment = "The group spans the resources belonging to the domain group.")
@NamedQuery(name = QUERY_GROUP_ALL, query = "SELECT u FROM DBGroup u")
@NamedQuery(name = QUERY_GROUP_BY_DOMAIN, query = "SELECT u FROM DBGroup u where u.domain.id = :domain_id")
@NamedQuery(name = QUERY_GROUP_BY_NAME_DOMAIN, query = "SELECT u FROM DBGroup u where u.groupName = :name and u.domain.id = :domain_id")
@NamedQuery(name = QUERY_GROUP_BY_NAME_DOMAIN_CODE, query = "SELECT u FROM DBGroup u where u.groupName = :name and u.domain.domainCode = :domain_code")
@NamedQuery(name = QUERY_GROUP_BY_USER_ROLES_COUNT, query = "SELECT count(c) FROM DBGroup c JOIN DBGroupMember dm ON c.id = dm.group.id " +
        " WHERE dm.role in (:membership_roles) and dm.user.id= :user_id")

@NamedQuery(name = QUERY_GROUP_BY_USER_ROLES, query = "SELECT c FROM DBGroup c JOIN DBGroupMember dm ON c.id = dm.group.id " +
        " WHERE dm.role in (:membership_roles) and dm.user.id= :user_id")
public class DBGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_GROUP_SEQ")
    @GenericGenerator(name = "SMP_GROUP_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique domain group id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOMAIN_ID", nullable = false)
    private DBDomain domain;

    @Column(name = "NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_512, nullable = false)
    @ColumnDescription(comment = "Domain Group name")
    String groupName;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Domain Group description")
    String groupDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "VISIBILITY", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private VisibilityType visibility = VisibilityType.PUBLIC;


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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "DBGroup{" +
                "id=" + id +
                ", domain=" + domain +
                ", groupName='" + groupName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DBGroup group = (DBGroup) o;

        return new EqualsBuilder().appendSuper(super.equals(o))
                .append(id, group.id)
                .append(domain, group.domain)
                .append(groupName, group.groupName)
                .append(groupDescription, group.groupDescription)
                .append(visibility, group.visibility)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode()).append(id)
                .append(domain)
                .append(groupName)
                .toHashCode();
    }
}
