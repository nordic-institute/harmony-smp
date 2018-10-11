package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Database optimization: load xmlContent only when needed and
 * keep blobs/clobs in separate table!
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Audited
@Table(name = "SMP_SG_EXTENSION")
@NamedQueries({
        @NamedQuery(name = "DBServiceGroupExtension.deleteById", query = "DELETE FROM DBServiceGroupExtension d WHERE d.id = :id"),

})
public class DBServiceGroupExtension extends BaseEntity {

    @Id
    private Long id;

    @Lob
    @Column(name = "EXTENSION")
    byte[] extension;

    @OneToOne
    @JoinColumn(name = "ID")
    @MapsId
    DBServiceGroup dbServiceGroup;

    @Column(name = "CREATED_ON" , nullable = false)
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    LocalDateTime lastUpdatedOn;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBServiceGroup getDbServiceGroup() {
        return dbServiceGroup;
    }

    public void setDbServiceGroup(DBServiceGroup dbServiceGroup) {
        this.dbServiceGroup = dbServiceGroup;
    }

    public byte[] getExtension() {
        return extension;
    }

    public void setExtension(byte[] extension) {
        this.extension = extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBServiceGroupExtension that = (DBServiceGroupExtension) o;
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
