package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

/**
 * Database optimization: load xmlContent only when needed and
 * keep blobs/clobs in separate table!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Audited
@Table(name = "SMP_SG_EXTENSION")
@org.hibernate.annotations.Table(appliesTo = "SMP_SG_EXTENSION", comment = "Service group extension blob")
@NamedQueries({
        @NamedQuery(name = "DBServiceGroupExtension.deleteById", query = "DELETE FROM DBServiceGroupExtension d WHERE d.id = :id"),

})
public class DBServiceGroupExtension extends BaseEntity {

    @Id
    @ColumnDescription(comment = "Shared primary key with master table SMP_RESOURCE")
    private Long id;

    @Lob
    @Column(name = "EXTENSION")
    @ColumnDescription(comment = "XML extension(s) for servicegroup ")
    byte[] extension;

    @OneToOne
    @JoinColumn(name = "ID")
    @MapsId
    DBResource dbServiceGroup;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBResource getDbServiceGroup() {
        return dbServiceGroup;
    }

    public void setDbServiceGroup(DBResource dbServiceGroup) {
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
}
