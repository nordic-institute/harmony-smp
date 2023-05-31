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

package eu.europa.ec.edelivery.smp.data.model.doc;

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

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

@Entity
@Audited
@Table(name = "SMP_RESOURCE",
        indexes = {@Index(name = "SMP_RS_UNIQ_IDENT_DOREDEF_IDX", columnList = "IDENTIFIER_SCHEME, IDENTIFIER_VALUE, FK_DOREDEF_ID", unique = true),
                @Index(name = "SMP_RS_ID_IDX", columnList = "IDENTIFIER_VALUE"),
                @Index(name = "SMP_RS_SCH_IDX", columnList = "IDENTIFIER_SCHEME")
        })
@org.hibernate.annotations.Table(appliesTo = "SMP_RESOURCE", comment = "SMP resource Identifier and scheme")
@NamedQuery(name = QUERY_RESOURCE_BY_IDENTIFIER_RESOURCE_DEF_DOMAIN, query = "SELECT d FROM DBResource d WHERE d.domainResourceDef.domain.id = :domain_id " +
        " AND d.domainResourceDef.resourceDef.id=:resource_def_id" +
        " AND d.identifierValue = :identifier_value " +
        " AND (:identifier_scheme IS NULL AND d.identifierScheme IS NULL " +
        " OR d.identifierScheme = :identifier_scheme)")

@NamedQuery(name = QUERY_RESOURCES_BY_DOMAIN_ID_RESOURCE_DEF_ID_COUNT, query = "SELECT count(d.id) FROM DBResource d WHERE d.domainResourceDef.domain.id = :domain_id " +
        " and d.domainResourceDef.resourceDef.id = :resource_def_id ")
@NamedQuery(name = QUERY_RESOURCES_BY_DOMAIN_ID_COUNT, query = "SELECT count(d.id) FROM DBResource d WHERE d.domainResourceDef.domain.id = :domain_id ")
@NamedQuery(name = QUERY_RESOURCE_FILTER_COUNT, query = "SELECT count(r.id) FROM DBResource r " +
        " JOIN DBDomainResourceDef dr ON dr.id = r.domainResourceDef.id  " +
        " WHERE (:group_id IS NULL OR r.group.id = :group_id) " +
        " AND (:user_id IS NULL OR r.id in (select rm.resource.id from DBResourceMember rm where rm.user.id = :user_id AND rm.role in (:membership_roles) )) " +
        " AND (:domain_id IS NULL OR dr.domain.id = :domain_id) " +
        " AND (:resource_def_id IS NULL OR dr.resourceDef.id = :resource_def_id) " +
        " AND (:resource_filter IS NULL OR lower(r.identifierValue) like lower(:resource_filter) OR (r.identifierScheme IS NOT NULL AND lower(r.identifierScheme) like lower(:resource_filter))) "
)
@NamedQuery(name = QUERY_RESOURCE_FILTER, query = "SELECT r FROM  DBResource r " +
        " JOIN DBDomainResourceDef dr ON dr.id = r.domainResourceDef.id  " +
        " WHERE (:group_id IS NULL OR r.group.id = :group_id) " +
        " AND (:user_id IS NULL OR r.id in (select rm.resource.id from DBResourceMember rm where rm.user.id = :user_id AND rm.role in (:membership_roles) )) " +
        " AND (:domain_id IS NULL OR dr.domain.id = :domain_id) " +
        " AND (:resource_def_id IS NULL OR dr.resourceDef.id = :resource_def_id) " +
        " AND (:resource_filter IS NULL OR lower(r.identifierValue) like lower(:resource_filter) OR (r.identifierScheme IS NOT NULL AND lower(r.identifierScheme) like lower(:resource_filter)) )" +
        "order by r.id asc")
@NamedQuery(name = "DBResource.getServiceGroupByID", query = "SELECT d FROM DBResource d WHERE d.id = :id")
@NamedQuery(name = "DBResource.getServiceGroupByIdentifier", query = "SELECT d FROM DBResource d WHERE d.identifierValue = :participantIdentifier " +
        " AND (:participantScheme IS NULL AND d.identifierScheme IS NULL " +
        " OR d.identifierScheme = :participantScheme)")
@NamedQuery(name = "DBResource.deleteById", query = "DELETE FROM DBResource d WHERE d.id = :id")

@NamedNativeQuery(name = "DBResource.deleteAllOwnerships", query = "DELETE FROM SMP_RESOURCE_MEMBER WHERE FK_SG_ID=:serviceGroupId")

// get All public
@NamedQuery(name = "DBResource.getPublicSearch2", query = "SELECT r FROM  DBResource r WHERE r.group.visibility='PUBLIC' " +
        " AND (r.group.domain.visibility='PUBLIC' " +
        "    OR :user_id IS NOT NULL " +
        "     AND ( (select count(dm.id) from DBDomainMember dm where dm.user.id = :user_id and dm.domain.id = r.group.domain.id) > 0 " +
        "      OR (select count(gm.id) from DBGroupMember gm where gm.user.id = :user_id and gm.group.domain.id = r.group.domain.id) > 0 " +
        "      OR (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.domain.id = r.group.domain.id) > 0 " +
        "     ) " +
        "  ) " +
        " AND (r.group.visibility='PUBLIC' " +
        "    OR  (:user_id IS NOT NULL " +
        "     AND ( (select count(gm.id) from DBGroupMember gm where gm.user.id = :user_id and gm.group.id = r.group.id) > 0 " +
        "      OR (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.id = r.group.id) > 0 " +
        "     ) )" +
        "  ) " +
        " AND ( r.visibility = 'PUBLIC' " +
        "   OR (:user_id IS NOT NULL " +
        "     AND (select count(id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.id = r.id) > 0 )) " +
        " AND (:resource_identifier IS NULL OR r.identifierValue like :resource_identifier )" +
        " AND (:resource_scheme IS NULL OR r.identifierScheme like :resource_scheme) order by r.identifierScheme, r.identifierValue"
)
@NamedQuery(name = QUERY_RESOURCE_ALL_FOR_USER, query = "SELECT DISTINCT r FROM  DBResource r LEFT JOIN DBResourceMember rm ON r.id = rm.resource.id WHERE " +
        " (:resource_identifier IS NULL OR r.identifierValue like :resource_identifier) " +
        " AND (:resource_scheme IS NULL OR r.identifierScheme like :resource_scheme) " +
        " AND :user_id IS NOT NULL AND rm.user.id = :user_id "  +
        " OR  r.visibility ='PUBLIC' " + // user must be member of the group or the group is public
        "   AND (:user_id IS NOT NULL " +
        "         AND  ((select count(gm.id) FROM  DBGroupMember gm where gm.user.id = :user_id and gm.group.id = r.group.id) > 0 " +
        "            OR  (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.id = r.group.id) > 0) " +
        "       OR  r.group.visibility = 'PUBLIC'  " +
        "           AND (r.group.domain.visibility = 'PUBLIC' " +
        "            OR  (select count(dm.id) from DBDomainMember dm where dm.user.id = :user_id and dm.domain.id = r.group.domain.id) > 0 " +
        "            OR (select count(gm.id) from DBGroupMember gm where gm.user.id = :user_id and gm.group.domain.id = r.group.domain.id) > 0 " +
        "            OR (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.domain.id = r.group.domain.id) > 0 " +
        "))"+
        "order by r.identifierScheme, r.identifierValue"
)
@NamedQuery(name = QUERY_RESOURCE_ALL_FOR_USER_COUNT, query = "SELECT count(distinct r.id) FROM  DBResource r LEFT JOIN DBResourceMember rm ON r.id = rm.resource.id WHERE " +
        " (:resource_identifier IS NULL OR r.identifierValue like :resource_identifier) " +
        " AND (:resource_scheme IS NULL OR r.identifierScheme like :resource_scheme) " +
        " AND :user_id IS NOT NULL AND rm.user.id = :user_id "  +
        " OR  r.visibility ='PUBLIC' " + // user must be member of the group or the group is public
        "   AND (:user_id IS NOT NULL " +
        "         AND  ((select count(gm.id) FROM  DBGroupMember gm where gm.user.id = :user_id and gm.group.id = r.group.id) > 0 " +
        "            OR  (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.id = r.group.id) > 0) " +
        "       OR  r.group.visibility = 'PUBLIC'  " +
        "           AND (r.group.domain.visibility = 'PUBLIC' " +
        "            OR  (select count(dm.id) from DBDomainMember dm where dm.user.id = :user_id and dm.domain.id = r.group.domain.id) > 0 " +
        "            OR (select count(gm.id) from DBGroupMember gm where gm.user.id = :user_id and gm.group.domain.id = r.group.domain.id) > 0 " +
        "            OR (select count(rm.id) from DBResourceMember rm where rm.user.id = :user_id and rm.resource.group.domain.id = r.group.domain.id) > 0 " +
        "))"
)
public class DBResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_RESOURCE_SEQ")
    @GenericGenerator(name = "SMP_RESOURCE_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique ServiceGroup id")
    Long id;

    @Column(name = "IDENTIFIER_VALUE", length = CommonColumnsLengths.MAX_IDENTIFIER_VALUE_VALUE_LENGTH, nullable = false)
    String identifierValue;

    @Column(name = "IDENTIFIER_SCHEME", length = CommonColumnsLengths.MAX_IDENTIFIER_VALUE_SCHEME_LENGTH)
    String identifierScheme;

    @Column(name = "SML_REGISTERED", nullable = false)
    private boolean smlRegistered = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "VISIBILITY", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private VisibilityType visibility = VisibilityType.PUBLIC;

    // The domain group list which handles the resource
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_GROUP_ID", nullable = true)
    private DBGroup group;

    // The domain to which the resource belongs
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_DOREDEF_ID")
    private DBDomainResourceDef domainResourceDef;


    // set only the remove to cascade!
    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY
    )
    private List<DBResourceMember> resourceMembers = new ArrayList<>();

    // set only the remove to cascade!
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;


    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY
    )
    private List<DBSubresource> subresources = new ArrayList<>();

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

    public DBGroup getGroup() {
        return group;
    }

    public void setGroup(DBGroup group) {
        this.group = group;
    }

    public List<DBSubresource> getSubresources() {
        return subresources;
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
