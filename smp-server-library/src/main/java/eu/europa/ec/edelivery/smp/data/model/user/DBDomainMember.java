package eu.europa.ec.edelivery.smp.data.model.user;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Domain member authorization  table
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_DOMAIN_MEMBER",
        indexes = {@Index(name = "SMP_DOM_MEM_IDX", columnList = "FK_DOMAIN_ID, FK_USER_ID", unique = true)
})
@NamedQuery(name = QUERY_DOMAIN_MEMBER_ALL, query = "SELECT u FROM DBDomainMember u")
@NamedQuery(name = QUERY_DOMAIN_MEMBER_BY_USER_DOMAINS_COUNT, query = "SELECT count(c) FROM DBDomainMember c " +
        "WHERE c.user.id = :user_id and c.domain.id in (:domain_ids)")
@NamedQuery(name = QUERY_DOMAIN_MEMBER_BY_USER_DOMAINS, query = "SELECT c FROM DBDomainMember c " +
        "WHERE c.user.id = :user_id and c.domain.id in (:domain_ids)")
@NamedQuery(name = QUERY_DOMAIN_MEMBERS_COUNT, query = "SELECT count(c) FROM DBDomainMember c " +
        " WHERE c.domain.id = :domain_id")
@NamedQuery(name = QUERY_DOMAIN_MEMBERS, query = "SELECT c FROM DBDomainMember c " +
        " WHERE c.domain.id = :domain_id order by c.user.username")
@NamedQuery(name = QUERY_DOMAIN_MEMBERS_FILTER_COUNT, query = "SELECT count(c) FROM DBDomainMember c " +
        " WHERE c.domain.id = :domain_id AND (lower(c.user.fullName) like lower(:user_filter) OR  lower(c.user.username) like lower(:user_filter))")
@NamedQuery(name = QUERY_DOMAIN_MEMBERS_FILTER, query = "SELECT c FROM DBDomainMember c " +
        " WHERE c.domain.id = :domain_id  AND (lower(c.user.fullName) like lower(:user_filter) OR  lower(c.user.username) like lower(:user_filter))  order by c.user.username")
public class DBDomainMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOMAIN_MEMBER_SEQ")
    @GenericGenerator(name = "SMP_DOMAIN_MEMBER_SEQ", strategy = "native")
    @Column(name = "ID")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOMAIN_ID")
    private DBDomain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USER_ID")
    private DBUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBERSHIP_ROLE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_64)
    private MembershipRoleType role = MembershipRoleType.VIEWER;

    public DBDomainMember() {
        //Need this method for hibernate
        // Caused by: java.lang.NoSuchMethodException: eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain_$$_jvst7ad_2.<init>()
    }

    public DBDomainMember(DBDomain domain, DBUser user) {
        this.user = user;
        this.domain = domain;
    }


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DBDomainMember that = (DBDomainMember) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(id, that.id).append(domain, that.domain).append(user, that.user).append(role, that.role).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(id).append(role).toHashCode();
    }
}
