package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service group domain
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Entity
@Audited
@Table(name = "SMP_SERVICE_GROUP_DOMAIN")
public class DBServiceGroupDomain extends BaseEntity {

    /*
    initially try with embeded id
    @EmbeddedId
    private DBServiceGroupDomainId i
    initially try with embeded id
    But then envers generated FK constraints:
    alter table SMP_SERVICE_METADATA_AUD add constraint FKbay.. foreign key (FK_DOMAIN_ID) references SMP_DOMAIN (ID);
    alter table SMP_SERVICE_METADATA_AUD add constraint FKn250.. foreign key (FK_SG_ID) references SMP_SERVICE_GROUP (ID);
    Tried to use  @ForeignKey(name="beda", value = ConstraintMode.NO_CONSTRAINT)) but did not helped....
    */

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sgd_generator")
    @SequenceGenerator(name = "sgd_generator", sequenceName = "SMP_SERVICE_GROUP_DOMAIN_SEQ")
    @Column(name = "ID")
    Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SG_ID")
    private DBServiceGroup serviceGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOMAIN_ID")
    private DBDomain domain;

    // list<> and set<> does not make any difference in hi
    // hibernate performance this case!
    // this list could also be on ServiceGroup entity because it does not make any difference for
    // dynamic discovery but it is here just user -service group admin to know for which
    // domain he orignally registred a service - to make metadata more organized...

    @OneToMany(mappedBy = "serviceGroupDomain", cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<DBServiceMetadata> serviceMetadata = new ArrayList<>();

    @Column(name = "CREATED_ON" , nullable = false)
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    LocalDateTime lastUpdatedOn;


    public DBServiceGroupDomain() {
        //Need this method for hibernate
        // Caused by: java.lang.NoSuchMethodException: eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain_$$_jvst7ad_2.<init>()
    }

    public DBServiceGroupDomain(DBServiceGroup serviceGroup, DBDomain domain) {
        this.domain = domain;
        this.serviceGroup = serviceGroup;
    }



    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBServiceGroup getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(DBServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }

    public void addServiceMetadata(DBServiceMetadata metadata) {
        serviceMetadata.add(metadata);
        metadata.setServiceGroupDomain(this);
    }

    public void removeServiceMetadata(DBServiceMetadata metadata) {
        serviceMetadata.remove(metadata);
        metadata.setServiceGroupDomain(null);
    }

    @Transient
    public DBServiceMetadata getServiceMetadata(int index) {
        return serviceMetadata.get(index);
    }

    /**
     * Method return metadata by documentIdentifier and document schema. Method is case sensitive!
     * @param docId
     * @param docSch
     * @return DBServiceMetadata or null if not found!
     */
    @Transient
    public DBServiceMetadata getServiceMetadata(String docId, String docSch) {

        return serviceMetadata.stream()
                .filter(smd -> Objects.equals(smd.getDocumentIdentifier(), docId)
                        && Objects.equals(smd.getDocumentIdentifierScheme(), docSch))
                .findFirst()
                .orElse(null);
    }

    @Transient
    public int serviceMetadataSize() {
        return serviceMetadata.size();
    }

    public List<DBServiceMetadata> getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBServiceGroupDomain that = (DBServiceGroupDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id);
    }

    @PrePersist
    public void prePersist() {
        if(createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        lastUpdatedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedOn = LocalDateTime.now();
    }

    // @Where annotation not working with entities that use inheritance
    // https://hibernate.atlassian.net/browse/HHH-12016
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(LocalDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}