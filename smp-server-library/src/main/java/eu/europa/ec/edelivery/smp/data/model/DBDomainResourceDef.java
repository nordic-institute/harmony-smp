package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Resource domain mapping table ensures data integrity - so that resource can be added only once to the domain.
 * Also keeps track if the resource is registered to the SML or not.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Entity
@Audited
@Table(name = "SMP_DOMAIN_RESOURCE_DEF",
        indexes = {@Index(name = "SMP_DOREDEF_UNIQ_DOM_RD_IDX", columnList = "FK_RESOURCE_DEF_ID, FK_DOMAIN_ID", unique = true)
        })
@NamedQuery(name = QUERY_DOMAIN_RESOURCE_DEF_ALL, query = "SELECT d FROM DBDomainResourceDef d order by d.domain.id, d.id asc")
@NamedQuery(name = QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_ALL, query = "SELECT d FROM DBDomainResourceDef d WHERE d.domain.id = :domain_id ")
@NamedQuery(name = QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_CODE_SEGMENT_URL, query = "SELECT d FROM DBDomainResourceDef d WHERE d.domain.domainCode=:domain_code AND d.resourceDef.urlSegment=:url_segment")
@NamedQuery(name = QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_RES_DEF, query = "SELECT d FROM DBDomainResourceDef d WHERE d.domain.id=:domain_id AND d.resourceDef.id=:resource_def_id")
@NamedQuery(name = QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_ID_RESDEF_IDENTIFIER, query = "SELECT d FROM DBDomainResourceDef d WHERE d.domain.id=:domain_id AND d.resourceDef.identifier=:resource_def_identifier")
public class DBDomainResourceDef extends BaseEntity {
    private static final long serialVersionUID = 1008583888835630003L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOMAIN_RESOURCE_DEF_SEQ")
    @GenericGenerator(name = "SMP_DOMAIN_RESOURCE_DEF_SEQ", strategy = "native")
    @Column(name = "ID")
    Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RESOURCE_DEF_ID")
    private DBResourceDef resourceDef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOMAIN_ID")
    private DBDomain domain;


    public DBDomainResourceDef() {
        //Need this method for hibernate
        // Caused by: java.lang.NoSuchMethodException: eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain_$$_jvst7ad_2.<init>()
    }

    public DBDomainResourceDef(DBResourceDef resourceDef, DBDomain domain) {
        this.domain = domain;
        this.resourceDef = resourceDef;
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

    public DBResourceDef getResourceDef() {
        return resourceDef;
    }

    public void setResourceDef(DBResourceDef resourceDef) {
        this.resourceDef = resourceDef;
    }

    @Override
    public String toString() {
        return "DBDomainResourceDef{" +
                "id=" + id +
                ", resourceDef=" + resourceDef +
                ", domain=" + domain +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDomainResourceDef that = (DBDomainResourceDef) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
