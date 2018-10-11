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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Audited
@Table(name = "SMP_USER")
@NamedQueries({
        // case insesitive search
        @NamedQuery(name = "DBUser.getUserByUsernameInsensitive", query = "SELECT u FROM DBUser u WHERE lower(u.username) = lower(:username)"),
        @NamedQuery(name = "DBUser.getUserByCertificateId", query = "SELECT u FROM DBUser u WHERE u.certificate.certificateId = :certificateId"),
})
public class DBUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_generator")
    @SequenceGenerator(name = "usr_generator", sequenceName = "SMP_USER_SEQ")
    @Column(name = "ID")
    Long id;

    @Column(name = "USERNAME", length = CommonColumnsLengths.MAX_USERNAME_LENGTH, unique = true)
    private String username;
    @Column(name = "PASSWORD", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    private String password;
    @Column(name = "EMAIL", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    private String email;

    @Column(name = "PASSWORD_CHANGED")
    LocalDateTime passwordChanged;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active = true;
    // user can have only one of the role smp_admin, servicegroup_admin, system_admin
    @Column(name = "ROLE", length = CommonColumnsLengths.MAX_USER_ROLE_LENGTH)
    private String role;

    @OneToOne(mappedBy = "dbUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
    private DBCertificate certificate;

    @Column(name = "CREATED_ON" , nullable = false)
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    LocalDateTime lastUpdatedOn;

    public DBUser() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public DBCertificate getCertificate() {
        return certificate;
    }

    public void setCertificate(DBCertificate certificate) {
        if (certificate == null) {
            if (this.certificate != null) {
                this.certificate.setDbUser(null);
            }
        }
        else {
            certificate.setDbUser(this);
        }
        this.certificate = certificate;
    }

    public String getEmail() {        return email;    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(LocalDateTime passwordChanged) {
        this.passwordChanged = passwordChanged;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DBUser dbUser = (DBUser) o;

        return Objects.equals(id, dbUser.id) &&
                StringUtils.equalsIgnoreCase(username, dbUser.username) &&
                Objects.equals(certificate, dbUser.certificate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, username, certificate);
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
