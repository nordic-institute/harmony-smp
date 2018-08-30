/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "smp_service_group")
@Audited
public class DBServiceGroup implements BaseEntity {

    private DBServiceGroupId serviceGroupId;
    private String extension;
    private Set<DBOwnership> ownerships = new HashSet<>();
    private Set<DBServiceMetadata> serviceMetadatas = new HashSet<>();
    private DBDomain domain;

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
    @Override
    public DBServiceGroupId getId() {
        return serviceGroupId;
    }

    @Lob
    @Column(name = "extension", length = 65535)
    public String getExtension() {
        return extension;
    }

    @OneToMany(fetch = LAZY, mappedBy = "serviceGroup", cascade = CascadeType.ALL)
    public Set<DBOwnership> getOwnerships() {
        return ownerships;
    }

    @OneToMany(fetch = LAZY, mappedBy = "serviceGroup", cascade = CascadeType.ALL)
    public Set<DBServiceMetadata> getServiceMetadatas() {
        return serviceMetadatas;
    }

    @ManyToOne(fetch = EAGER, optional = false)
    @JoinColumn(name = "domainId", nullable = false)
    public DBDomain getDomain() {
        return domain;
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

    public void setDomain(final DBDomain domain) {
        this.domain = domain;
    }
}
