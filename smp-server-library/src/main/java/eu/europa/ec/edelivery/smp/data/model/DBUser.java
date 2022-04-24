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

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Audited
@Table(name = "SMP_USER")
@org.hibernate.annotations.Table(appliesTo = "SMP_USER", comment = "SMP can handle multiple domains. This table contains domain specific data")
@NamedQueries({
        // case insesitive search
        @NamedQuery(name = "DBUser.getUserByUsernameInsensitive", query = "SELECT u FROM DBUser u WHERE lower(u.username) = lower(:username)"),
        @NamedQuery(name = "DBUser.getUserByCertificateId", query = "SELECT u FROM DBUser u WHERE u.certificate.certificateId = :certificateId"),
        @NamedQuery(name = "DBUser.getUserByPatId", query = "SELECT u FROM DBUser u WHERE u.accessTokenIdentifier = :patId"),
        @NamedQuery(name = "DBUser.getUserByCertificateIdCaseInsensitive", query = "SELECT u FROM DBUser u WHERE lower(u.certificate.certificateId) = lower(:certificateId)"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DBUserDeleteValidation.validateUsersForOwnership",
                resultSetMapping = "DBUserDeleteValidationMapping",
                query = "SELECT S.ID as ID, S.USERNAME as USERNAME, " +
                        "    C.CERTIFICATE_ID as certificateId, COUNT(S.ID) as  ownedCount  FROM " +
                        " SMP_USER S LEFT JOIN SMP_CERTIFICATE C ON (S.ID=C.ID) " +
                        " INNER JOIN SMP_OWNERSHIP SG ON (S.ID = SG.FK_USER_ID) " +
                        " WHERE S.ID IN (:idList)" +
                        " GROUP BY S.ID, S.USERNAME, C.CERTIFICATE_ID"),
})
@SqlResultSetMapping(name = "DBUserDeleteValidationMapping", classes = {
        @ConstructorResult(targetClass = DBUserDeleteValidation.class,
                columns = {@ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "certificateId", type = String.class),
                        @ColumnResult(name = "ownedCount", type = Integer.class)})
})

public class DBUser extends BaseEntity {

    @Id
    @SequenceGenerator(name = "usr_generator", sequenceName = "SMP_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_generator")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique user id")
    Long id;

    @Column(name = "EMAIL", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    @ColumnDescription(comment = "User email")
    private String emailAddress;
    // username
    @Column(name = "USERNAME", length = CommonColumnsLengths.MAX_USERNAME_LENGTH, unique = true, nullable = false)
    @ColumnDescription(comment = "Unique username identifier. The Username must not be null")
    private String username;
    @Column(name = "PASSWORD", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    @ColumnDescription(comment = "BCrypted password for username/password login")
    private String password;
    @Column(name = "PASSWORD_CHANGED")
    @ColumnDescription(comment = "Last date when password was changed")
    LocalDateTime passwordChanged;
    @Column(name = "PASSWORD_EXPIRE_ON")
    @ColumnDescription(comment = "Date when password will expire")
    LocalDateTime passwordExpireOn;
    @Column(name = "LOGIN_FAILURE_COUNT")
    @ColumnDescription(comment = "Sequential login failure count")
    Integer sequentialLoginFailureCount;
    @Column(name = "LAST_FAILED_LOGIN_ON")
    @ColumnDescription(comment = "Last failed login attempt")
    LocalDateTime lastFailedLoginAttempt;

    // Personal access token
    @Column(name = "ACCESS_TOKEN_ID", length = CommonColumnsLengths.MAX_USERNAME_LENGTH, unique = true)
    @ColumnDescription(comment = "Personal access token id")
    private String accessTokenIdentifier;
    @Column(name = "ACCESS_TOKEN", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    @ColumnDescription(comment = "BCrypted personal access token")
    private String accessToken;
    @Column(name = "ACCESS_TOKEN_GENERATED_ON")
    @ColumnDescription(comment = "Date when personal access token was generated")
    LocalDateTime accessTokenGeneratedOn;
    @Column(name = "ACCESS_TOKEN_EXPIRE_ON")
    @ColumnDescription(comment = "Date when personal access token will expire")
    LocalDateTime accessTokenExpireOn;
    @Column(name = "AT_LOGIN_FAILURE_COUNT")
    @ColumnDescription(comment = "Sequential token login failure count")
    Integer sequentialTokenLoginFailureCount;
    @Column(name = "AT_LAST_FAILED_LOGIN_ON")
    @ColumnDescription(comment = "Last failed token login attempt")
    LocalDateTime lastTokenFailedLoginAttempt;

    @Column(name = "ACTIVE", nullable = false)
    @ColumnDescription(comment = "Is user active")
    private boolean active = true;
    // user can have only one of the role smp_admin, servicegroup_admin, system_admin
    @Column(name = "ROLE", length = CommonColumnsLengths.MAX_USER_ROLE_LENGTH)
    @ColumnDescription(comment = "User role")
    private String role;

    @OneToOne(mappedBy = "dbUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true,
            orphanRemoval = true)
    private DBCertificate certificate;

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

    public LocalDateTime getLastFailedLoginAttempt() {
        return lastFailedLoginAttempt;
    }

    public void setLastFailedLoginAttempt(LocalDateTime lastFailedLoginAttempt) {
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
    }

    public LocalDateTime getLastTokenFailedLoginAttempt() {
        return lastTokenFailedLoginAttempt;
    }

    public void setLastTokenFailedLoginAttempt(LocalDateTime lastTokenFailedLoginAttempt) {
        this.lastTokenFailedLoginAttempt = lastTokenFailedLoginAttempt;
    }

    public String getAccessTokenIdentifier() {
        return accessTokenIdentifier;
    }

    public void setAccessTokenIdentifier(String accessTokenIdentifier) {
        this.accessTokenIdentifier = accessTokenIdentifier;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getAccessTokenGeneratedOn() {
        return accessTokenGeneratedOn;
    }

    public void setAccessTokenGeneratedOn(LocalDateTime accessTokenGeneratedOn) {
        this.accessTokenGeneratedOn = accessTokenGeneratedOn;
    }

    public LocalDateTime getPasswordExpireOn() {
        return passwordExpireOn;
    }

    public void setPasswordExpireOn(LocalDateTime passwordExpireOn) {
        this.passwordExpireOn = passwordExpireOn;
    }

    public LocalDateTime getAccessTokenExpireOn() {
        return accessTokenExpireOn;
    }

    public void setAccessTokenExpireOn(LocalDateTime accessTokenExpireOn) {
        this.accessTokenExpireOn = accessTokenExpireOn;
    }

    public Integer getSequentialLoginFailureCount() {
        return sequentialLoginFailureCount;
    }

    public void setSequentialLoginFailureCount(Integer sequentialLoginFailureCount) {
        this.sequentialLoginFailureCount = sequentialLoginFailureCount;
    }

    public Integer getSequentialTokenLoginFailureCount() {
        return sequentialTokenLoginFailureCount;
    }

    public void setSequentialTokenLoginFailureCount(Integer sequentialTokenLoginFailureCount) {
        this.sequentialTokenLoginFailureCount = sequentialTokenLoginFailureCount;
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
        } else {
            certificate.setDbUser(this);
        }
        this.certificate = certificate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
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
        return Objects.hash(super.hashCode(), id, username);
    }
}
