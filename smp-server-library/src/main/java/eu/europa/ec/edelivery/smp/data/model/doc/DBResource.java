package eu.europa.ec.edelivery.smp.data.model.doc;
/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.QUERY_RESOURCE_BY_IDENTIFIER_RESOURCE_DEF_DOMAIN;

@Entity
@Audited
// the SMP_SG_UNIQ_PARTC_IDX  is natural key
@Table(name = "SMP_RESOURCE",
        indexes = {@Index(name = "SMP_RS_UNIQ_IDENT_DOREDEF_IDX", columnList = "IDENTIFIER_SCHEME, IDENTIFIER_VALUE, FK_DOREDEF_ID", unique = true),
                @Index(name = "SMP_RS_ID_IDX", columnList = "IDENTIFIER_VALUE"),
                @Index(name = "SMP_RS_SCH_IDX", columnList = "IDENTIFIER_SCHEME")
        })
@org.hibernate.annotations.Table(appliesTo = "SMP_RESOURCE", comment = "SMP resource Identifier and scheme")
@NamedQueries({
        @NamedQuery(name = QUERY_RESOURCE_BY_IDENTIFIER_RESOURCE_DEF_DOMAIN, query = "SELECT d FROM DBResource d WHERE d.domainResourceDef.domain.id = :domain_id " +
                " AND d.domainResourceDef.resourceDef.id=:resource_def_id" +
                " AND d.identifierValue = :identifier_value " +
                " AND (:identifier_scheme IS NULL AND d.identifierScheme IS NULL " +
                " OR d.identifierScheme = :identifier_scheme)"
        ),
        @NamedQuery(name = "DBResource.getServiceGroupByID", query = "SELECT d FROM DBResource d WHERE d.id = :id"),
        @NamedQuery(name = "DBResource.getServiceGroupByIdentifier", query = "SELECT d FROM DBResource d WHERE d.identifierValue = :participantIdentifier " +
                " AND (:participantScheme IS NULL AND d.identifierScheme IS NULL " +
                " OR d.identifierScheme = :participantScheme)"),
        @NamedQuery(name = "DBResource.deleteById", query = "DELETE FROM DBResource d WHERE d.id = :id"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DBResource.deleteAllOwnerships", query = "DELETE FROM SMP_RESOURCE_MEMBER WHERE FK_SG_ID=:serviceGroupId")
})
public class DBResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_RESOURCE_SEQ")
    @GenericGenerator(name = "SMP_RESOURCE_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique ServiceGroup id")
    Long id;

    // resource groups
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SMP_GROUP_RESOURCE",
            joinColumns = @JoinColumn(name = "FK_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "FK_RESOURCE_ID")
    )
    private List<DBGroup> groups = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOREDEF_ID")
    private DBDomainResourceDef domainResourceDef;


    // Resource members
    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    List<DBResourceMember> resourceMembers = new ArrayList<>();

    @Column(name = "IDENTIFIER_VALUE", length = CommonColumnsLengths.MAX_IDENTIFIER_VALUE_VALUE_LENGTH, nullable = false)
    String identifierValue;

    @Column(name = "IDENTIFIER_SCHEME", length = CommonColumnsLengths.MAX_IDENTIFIER_VALUE_SCHEME_LENGTH)
    String identifierScheme;

    @Column(name = "SML_REGISTERED", nullable = false)
    private boolean smlRegistered = false;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;

    @Enumerated(EnumType.STRING)
    @Column(name = "VISIBILITY", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private VisibilityType visibility = VisibilityType.PUBLIC;


    @OneToOne(mappedBy = "dbServiceGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private DBServiceGroupExtension serviceGroupExtension;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String participantIdentifier) {
        this.identifierValue = participantIdentifier;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String participantScheme) {
        this.identifierScheme = participantScheme;
    }

    public void addMember(DBResourceMember u) {
        this.resourceMembers.add(u);
    }

    public void removeUser(DBResourceMember u) {
        this.resourceMembers.remove(u);
    }

    public List<DBResourceMember> getMembers() {
        return this.resourceMembers;
    }

    public List<DBGroup> getGroups() {
        return this.groups;
    }

    public void addGroup(DBGroup group) {
        this.groups.add(group);
    }


    public DBServiceGroupExtension getServiceGroupExtension() {
        return serviceGroupExtension;
    }

    public DBDocument getDocument() {
        return document;
    }

    public void setDocument(DBDocument document) {
        this.document = document;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public DBDomainResourceDef getDomainResourceDef() {
        return domainResourceDef;
    }

    public void setDomainResourceDef(DBDomainResourceDef domainResourceDef) {
        this.domainResourceDef = domainResourceDef;
    }

    public boolean isSmlRegistered() {
        return smlRegistered;
    }

    public void setSmlRegistered(boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }

    public void setServiceGroupExtension(DBServiceGroupExtension serviceGroupExtension) {
        if (serviceGroupExtension == null) {
            if (this.serviceGroupExtension != null) {
                this.serviceGroupExtension.setDbServiceGroup(null);
            }
        } else {
            serviceGroupExtension.setDbServiceGroup(this);
        }
        this.serviceGroupExtension = serviceGroupExtension;
    }

    @Transient
    public byte[] getExtension() {
        return getServiceGroupExtension() != null ? getServiceGroupExtension().getExtension() : null;
    }

    public void setExtension(byte[] extension) {

        if (extension == null) {
            if (this.serviceGroupExtension != null) {
                this.serviceGroupExtension.setExtension(null);
            }
        } else {
            if (this.serviceGroupExtension == null) {
                this.serviceGroupExtension = new DBServiceGroupExtension();
                this.serviceGroupExtension.setDbServiceGroup(this);
            }
            this.serviceGroupExtension.setExtension(extension);
        }
    }


    /**
     * Id is database suragete id + natural key!
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBResource that = (DBResource) o;
        return Objects.equals(id, that.id) &&

                Objects.equals(identifierValue, that.identifierValue) &&
                Objects.equals(identifierScheme, that.identifierScheme);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DBResource.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("participantIdentifier='" + identifierValue + "'")
                .add("participantScheme='" + identifierScheme + "'")
                .add("visibility='" + visibility + "'")
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, identifierValue, identifierScheme);
    }
}
