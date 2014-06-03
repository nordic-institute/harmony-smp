package eu.domibus.common.persistent;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Abstract base class for all entities in domibus
 *
 * @author Christian Koch
 */
@MappedSuperclass
public abstract class AbstractBaseEntity {

    @Id
    @Column(name = "Id")
    protected String id;

    public AbstractBaseEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractBaseEntity)) {
            return false;
        }
        AbstractBaseEntity other = (AbstractBaseEntity) obj;
        return getId().equals(other.getId());
    }
}

