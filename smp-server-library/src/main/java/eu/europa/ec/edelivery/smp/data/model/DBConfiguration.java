/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Joze Rihtarsic .
 */
@Entity
@Table(name = "SMP_CONFIGURATION")
@NamedQueries({
        @NamedQuery(name = "DBConfiguration.getAll", query = "SELECT d FROM DBConfiguration d"),
        @NamedQuery(name = "DBConfiguration.maxUpdateDate",
                query = "SELECT max(config.lastUpdatedOn) from DBConfiguration config"),
})
@org.hibernate.annotations.Table(appliesTo = "SMP_CONFIGURATION", comment = "SMP user certificates")
public class DBConfiguration  extends BaseEntity  {

    @Id
    @Column(name = "PROPERTY", length = CommonColumnsLengths.MAX_TEXT_LENGTH_512, nullable = false, unique = true)
    @ColumnDescription(comment = "Property name/key")
    String property;
    @Column(name = "VALUE", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Property value")
    String value;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Property description")
    String description;


    @Column(name = "CREATED_ON", nullable = false)
    @ColumnDescription(comment = "Row inserted on date")
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    @ColumnDescription(comment = "Row modified on date")
    LocalDateTime lastUpdatedOn;



    public DBConfiguration() {

    }

    @Override
    public Object getId() {
        return property;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBConfiguration)) return false;
        DBConfiguration that = (DBConfiguration) o;
        return Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property);
    }
}
