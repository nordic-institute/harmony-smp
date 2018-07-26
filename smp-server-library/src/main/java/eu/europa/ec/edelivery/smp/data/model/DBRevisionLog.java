package eu.europa.ec.edelivery.smp.data.model;


import eu.europa.ec.edelivery.smp.data.dao.SMPRevisionListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Own implementation of hibernate-envers Revision entity, in order to store the user and the modification type.
 *
 * @author Thomas Dussart
 * @since 4.0
 */
@Entity
@Table(name = "SMP_REV_INFO")
@RevisionEntity(SMPRevisionListener.class)
public class DBRevisionLog {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    private long timestamp;
    /**
     * User involve in this modification
     */
    @Column(name = "username")
    private String userName;
    /**
     * Date of the modification.
     */
    @Column(name = "REVISION_DATE")
    private LocalDateTime revisionDate;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(LocalDateTime revisionDate) {
        this.revisionDate = revisionDate;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof DBRevisionLog)) {
            return false;
        } else {
            DBRevisionLog that = (DBRevisionLog)o;
            return this.id == that.id && this.timestamp == that.timestamp;
        }
    }

    public int hashCode() {
        int result = (int)this.id;
        result = 31 * result + (int)(this.timestamp ^ this.timestamp >>> 32);
        return result;
    }


}