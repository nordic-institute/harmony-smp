package eu.europa.ec.edelivery.smp.data.model.user;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Resource member authorization  table
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_RESOURCE_MEMBER",
                indexes = {@Index(name = "SMP_RES_MEM_IDX", columnList = "FK_RESOURCE_ID, FK_USER_ID", unique = true)
})
@NamedQuery(name = QUERY_RESOURCE_MEMBER_ALL, query = "SELECT u FROM DBResourceMember u")
@NamedQuery(name = QUERY_RESOURCE_MEMBER_BY_USER_RESOURCE_COUNT, query = "SELECT count(c) FROM DBResourceMember c " +
        " WHERE c.user.id = :user_id AND c.resource.id = :resource_id")
@NamedQuery(name = QUERY_RESOURCE_MEMBER_BY_USER_DOMAIN_RESOURCE_COUNT, query = "SELECT count(c) FROM DBResourceMember c JOIN c.resource.domainResourceDef.domain d" +
        " WHERE c.user.id = :user_id AND d.id = :domain_id")
@NamedQuery(name = QUERY_RESOURCE_MEMBER_BY_USER_DOMAIN_RESOURCE_ROLE_COUNT, query = "SELECT count(c) FROM DBResourceMember c JOIN c.resource.domainResourceDef.domain d" +
        " WHERE c.user.id = :user_id AND d.id = :domain_id AND c.role=:membership_role")
@NamedQuery(name = QUERY_RESOURCE_MEMBER_BY_USER_RESOURCE, query = "SELECT c FROM DBResourceMember c " +
        " WHERE c.user.id = :user_id AND c.resource.id = :resource_id")
@NamedQuery(name = QUERY_RESOURCE_MEMBER_BY_USER_GROUP_RESOURCES_ROLE_COUNT, query = "SELECT count(c) FROM DBResourceMember c " +
        " WHERE c.user.id = :user_id AND c.resource.group.id = :group_id AND c.role= :membership_role ")

@NamedQuery(name = QUERY_RESOURCE_MEMBERS_COUNT, query = "SELECT count(c) FROM DBResourceMember c " +
        " WHERE c.resource.id = :resource_id")
@NamedQuery(name = QUERY_RESOURCE_MEMBERS, query = "SELECT c FROM DBResourceMember c " +
        " WHERE c.resource.id = :resource_id order by c.user.username")
@NamedQuery(name = QUERY_RESOURCE_MEMBERS_FILTER_COUNT, query = "SELECT count(c) FROM DBResourceMember c " +
        " WHERE c.resource.id = :resource_id AND (lower(c.user.fullName) like lower(:user_filter) OR lower(c.user.username) like lower(:user_filter))")
@NamedQuery(name = QUERY_RESOURCE_MEMBERS_FILTER, query = "SELECT c FROM DBResourceMember c " +
        " WHERE c.resource.id = :resource_id  AND (lower(c.user.fullName) like lower(:user_filter) OR lower(c.user.username) like lower(:user_filter))  order by c.user.username")
public class DBResourceMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_RESOURCE_MEMBER_SEQ")
    @GenericGenerator(name = "SMP_RESOURCE_MEMBER_SEQ", strategy = "native")
    @Column(name = "ID")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RESOURCE_ID")
    private DBResource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USER_ID")
    private DBUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBERSHIP_ROLE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_64)
    private MembershipRoleType role = MembershipRoleType.VIEWER;

    public DBResourceMember() {
        //Need this method for hibernate
        // Caused by: java.lang.NoSuchMethodException: eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain_$$_jvst7ad_2.<init>()
    }

    public DBResourceMember(DBResource resource, DBUser user) {
        this.user = user;
        this.resource = resource;
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBResource getResource() {
        return resource;
    }

    public void setResource(DBResource serviceGroup) {
        this.resource = serviceGroup;
    }

    public DBUser getUser() {
        return user;
    }

    public void setUser(DBUser domain) {
        this.user = domain;
    }


    public MembershipRoleType getRole() {
        return role;
    }

    public void setRole(MembershipRoleType role) {
        this.role = role;
    }
}
