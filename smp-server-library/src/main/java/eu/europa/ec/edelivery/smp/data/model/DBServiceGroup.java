package eu.europa.ec.edelivery.smp.data.model;
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

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;

@Entity
@Audited
// the SMP_SG_UNIQ_PARTC_IDX  is natural key
@Table(name = "SMP_SERVICE_GROUP",
        indexes = {@Index(name = "SMP_SG_UNIQ_PARTC_IDX", columnList = "PARTICIPANT_SCHEME, PARTICIPANT_IDENTIFIER", unique = true),
                @Index(name = "SMP_SG_PART_ID_IDX", columnList = "PARTICIPANT_IDENTIFIER", unique = false),
                @Index(name = "SMP_SG_PART_SCH_IDX", columnList = "PARTICIPANT_SCHEME", unique = false)
        })
@org.hibernate.annotations.Table(appliesTo = "SMP_SERVICE_GROUP", comment = "Service group data - Identifier and scheme")
@NamedQueries({
        @NamedQuery(name = "DBServiceGroup.getServiceGroupByID", query = "SELECT d FROM DBServiceGroup d WHERE d.id = :id"),
        @NamedQuery(name = "DBServiceGroup.getServiceGroup", query = "SELECT d FROM DBServiceGroup d WHERE d.participantIdentifier = :participantIdentifier and d.participantScheme = :participantScheme"),
        @NamedQuery(name = "DBServiceGroup.getServiceGroupList", query = "SELECT d FROM DBServiceGroup d WHERE d.participantIdentifier = :participantIdentifier and d.participantScheme = :participantScheme"),
        @NamedQuery(name = "DBServiceGroup.deleteById", query = "DELETE FROM DBServiceGroup d WHERE d.id = :id"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DBServiceGroup.deleteAllOwnerships", query = "DELETE FROM SMP_OWNERSHIP WHERE FK_SG_ID=:serviceGroupId")
})

public class DBServiceGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_SERVICE_GROUP_SEQ")
    @GenericGenerator(name = "SMP_SERVICE_GROUP_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique ServiceGroup id")
    Long id;

    @OneToMany(
            mappedBy = "serviceGroup",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<DBServiceGroupDomain> serviceGroupDomains;


    // fetch in on demand - reduce performance issue on big SG table (set it better option)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SMP_OWNERSHIP",
            joinColumns = @JoinColumn(name = "FK_SG_ID"),
            inverseJoinColumns = @JoinColumn(name = "FK_USER_ID")
    )
    @OrderColumn(name = "USERNAME")
    private Set<DBUser> users = new HashSet<>();


    @Column(name = "PARTICIPANT_IDENTIFIER", length = CommonColumnsLengths.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH, nullable = false)
    String participantIdentifier;

    @Column(name = "PARTICIPANT_SCHEME", length = CommonColumnsLengths.MAX_PARTICIPANT_IDENTIFIER_SCHEME_LENGTH, nullable = false)
    String participantScheme;


    @OneToOne(mappedBy = "dbServiceGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private DBServiceGroupExtension serviceGroupExtension;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public String getParticipantScheme() {
        return participantScheme;
    }

    public void setParticipantScheme(String participantScheme) {
        this.participantScheme = participantScheme;
    }


    public void addUser(DBUser u) {
        this.users.add(u);
    }

    public void removeUser(DBUser u) {
        this.users.remove(u);
    }

    public Set<DBUser> getUsers() {
        return this.users;
    }

    public DBServiceGroupExtension getServiceGroupExtension() {
        return serviceGroupExtension;
    }

    public void setServiceGroupExtension(DBServiceGroupExtension serviceGroupExtension) {
        if (serviceGroupExtension == null) {
            if (this.serviceGroupExtension != null) {
                this.serviceGroupExtension.setDbServiceGroup(null);
            }
        } else {
            serviceGroupExtension.setDbServiceGroup(this);
        }
        this.serviceGroupExtension = serviceGroupExtension;
    }

    public List<DBServiceGroupDomain> getServiceGroupDomains() {
        if (serviceGroupDomains == null) {
            serviceGroupDomains = new ArrayList<>();
        }
        return serviceGroupDomains;
    }


    public DBServiceGroupDomain addDomain(DBDomain domain) {
        DBServiceGroupDomain sgd = new DBServiceGroupDomain(this, domain);
        getServiceGroupDomains().add(sgd);
        return sgd;
    }

    public void removeDomain(String domainCode) {
        // find connecting object
        Optional<DBServiceGroupDomain> osgd = getServiceGroupDomains().stream()
                .filter(psgd -> domainCode.equals(psgd.getDomain().getDomainCode())).findFirst();
        if (osgd.isPresent()) {
            DBServiceGroupDomain dsg = osgd.get();
            getServiceGroupDomains().remove(dsg);
            dsg.setDomain(null);
            dsg.setServiceGroup(null);
        }
    }

    public Optional<DBServiceGroupDomain> findServiceGroupDomainForMetadata(String docId, String docSch) {
        for (DBServiceGroupDomain serviceGroupDomain : getServiceGroupDomains()) {
            for (DBServiceMetadata dbServiceMetadata : serviceGroupDomain.getServiceMetadata()) {
                if (Objects.equals(docId, dbServiceMetadata.getDocumentIdentifier())
                        && Objects.equals(docSch, dbServiceMetadata.getDocumentIdentifierScheme())) {
                    return Optional.of(serviceGroupDomain);
                }
            }
        }
        return Optional.empty();
    }


    @Transient
    public Optional<DBServiceGroupDomain> getServiceGroupForDomain(String domainCode) {
        // find connecting object
        return StringUtils.isBlank(domainCode) ? Optional.empty() : getServiceGroupDomains().stream()
                .filter(psgd -> domainCode.equals(psgd.getDomain().getDomainCode())).findFirst();
    }

    @Transient
    public byte[] getExtension() {
        return getServiceGroupExtension() != null ? getServiceGroupExtension().getExtension() : null;
    }

    public void setExtension(byte[] extension) {

        if (extension == null) {
            if (this.serviceGroupExtension != null) {
                this.serviceGroupExtension.setExtension(null);
            }
        } else {
            if (this.serviceGroupExtension == null) {
                this.serviceGroupExtension = new DBServiceGroupExtension();
                this.serviceGroupExtension.setDbServiceGroup(this);
            }
            this.serviceGroupExtension.setExtension(extension);
        }
    }


    /**
     * Id is database suragete id + natural key!
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBServiceGroup that = (DBServiceGroup) o;
        return Objects.equals(id, that.id) &&

                Objects.equals(participantIdentifier, that.participantIdentifier) &&
                Objects.equals(participantScheme, that.participantScheme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, participantIdentifier, participantScheme);
    }
}
