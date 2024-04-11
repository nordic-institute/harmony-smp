/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.model;


import eu.europa.ec.edelivery.smp.data.dao.SMPRevisionListener;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Implementation of hibernate envers Revision log entity.
 *
 * @author Joze Rihtarsic (thanks to Thomas Dussart (Domibus))
 * @since 4.1
 */
@Entity
@Table(name = "SMP_REV_INFO")
@RevisionEntity(SMPRevisionListener.class)
public class DBRevisionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_REVISION_SEQ")
    @GenericGenerator(name = "SMP_REVISION_SEQ", strategy = "native")
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    private long timestamp;
    /**
     * User involve in this modification
     */
    @Column(name = "USERNAME")
    private String userName;
    /**
     * Date of the modification.
     */
    @Column(name = "REVISION_DATE")
    private OffsetDateTime revisionDate;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public OffsetDateTime getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(OffsetDateTime revisionDate) {
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

    @Override
    public String toString() {
        return "DBRevisionLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", userName='" + userName + '\'' +
                ", revisionDate=" + revisionDate +
                '}';
    }
}
