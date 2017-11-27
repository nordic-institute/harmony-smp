/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "smp_service_group")
public class DBServiceGroup implements Serializable {

    private DBServiceGroupId serviceGroupId;
    private String extension;
    private Set<DBOwnership> ownerships = new HashSet<>();
    private Set<DBServiceMetadata> serviceMetadatas = new HashSet<DBServiceMetadata>();

    public DBServiceGroup() {
    }

    public DBServiceGroup(final DBServiceGroupId serviceGroupId) {
        this.serviceGroupId = serviceGroupId;
    }

    public DBServiceGroup(final DBServiceGroupId serviceGroupId,
                          final String extension,
                          final Set<DBOwnership> ownerships,
                          final Set<DBServiceMetadata> serviceMetadatas) {

        this.serviceGroupId = serviceGroupId;
        this.extension = extension;
        this.ownerships = ownerships;
        this.serviceMetadatas = serviceMetadatas;
    }

    @EmbeddedId
    public DBServiceGroupId getId() {
        return serviceGroupId;
    }

    @Lob
    @Column(name = "extension", length = 65535)
    public String getExtension() {
        return extension;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceGroup", cascade = CascadeType.ALL)
    public Set<DBOwnership> getOwnerships() {
        return ownerships;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceGroup", cascade = CascadeType.ALL)
    public Set<DBServiceMetadata> getServiceMetadatas() {
        return serviceMetadatas;
    }

    public void setId(final DBServiceGroupId serviceGroupId) {
        this.serviceGroupId = serviceGroupId;
    }

    public void setExtension(String extensions) {
        this.extension = extensions;
    }

    public void setOwnerships(final Set<DBOwnership> ownerships) {
        this.ownerships = ownerships;
    }

    public void setServiceMetadatas(final Set<DBServiceMetadata> serviceMetadatas) {
        this.serviceMetadatas = serviceMetadatas;
    }
}
