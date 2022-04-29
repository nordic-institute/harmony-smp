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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.OffsetDateTime;
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
        @NamedQuery(name = "DBUser.getUsersForBeforePasswordExpireAlerts",
                query = "SELECT u FROM DBUser u WHERE u.passwordExpireOn IS NOT NULL" +
                        " AND u.passwordExpireOn <= :startAlertDate " +
                        " AND u.passwordExpireOn > :expireDate" +
                        " AND (u.passwordExpireAlertOn IS NULL OR u.passwordExpireAlertOn < :lastSendAlertDate )"),
        @NamedQuery(name = "DBUser.getUsersForPasswordExpiredAlerts",
                query = "SELECT u FROM DBUser u WHERE u.passwordExpireOn IS NOT NULL" +
                        " AND u.passwordExpireOn > :endAlertDate " +
                        " AND u.passwordExpireOn <= :expireDate" +
                        " AND (u.passwordExpireAlertOn IS NULL " +
                        "   OR u.passwordExpireAlertOn <= u.passwordExpireOn " +
                        "   OR u.passwordExpireAlertOn < :lastSendAlertDate )"),

        @NamedQuery(name = "DBUser.getUsersForBeforeAccessTokenExpireAlerts",
                query = "SELECT u FROM DBUser u WHERE u.accessTokenExpireOn IS NOT NULL" +
                        " AND u.accessTokenExpireOn <= :startAlertDate " +
                        " AND u.accessTokenExpireOn > :expireDate" +
                        " AND (u.accessTokenExpireAlertOn IS NULL OR u.accessTokenExpireAlertOn < :lastSendAlertDate )"),
        @NamedQuery(name = "DBUser.getUsersForAccessTokenExpiredAlerts",
                query = "SELECT u FROM DBUser u WHERE u.accessTokenExpireOn IS NOT NULL" +
                        " AND u.accessTokenExpireOn > :endAlertDate " +
                        " AND u.accessTokenExpireOn <= :expireDate" +
                        " AND (u.accessTokenExpireAlertOn IS NULL " +
                        "   OR u.accessTokenExpireAlertOn <= u.accessTokenExpireOn " +
                        "   OR u.accessTokenExpireAlertOn < :lastSendAlertDate )"),

        @NamedQuery(name = "DBUser.getUsersForBeforeCertificateExpireAlerts",
                query = "SELECT u FROM DBUser u WHERE u.certificate IS NOT NULL" +
                        " AND u.certificate.validTo IS NOT NULL " +
                        " AND u.certificate.validTo <= :startAlertDate " +
                        " AND u.certificate.validTo > :expireDate" +
                        " AND (u.certificate.certificateLastExpireAlertOn IS NULL OR u.certificate.certificateLastExpireAlertOn < :lastSendAlertDate )"),
        @NamedQuery(name = "DBUser.getUsersForCertificateExpiredAlerts",
                query = "SELECT u FROM DBUser u WHERE u.certificate IS NOT NULL" +
                        " AND u.certificate.validTo IS NOT NULL " +
                        " AND u.certificate.validTo > :endAlertDate " +
                        " AND u.certificate.validTo <= :expireDate" +
                        " AND (u.certificate.certificateLastExpireAlertOn IS NULL " +
                        "     OR u.certificate.certificateLastExpireAlertOn <= u.certificate.validTo " +
                        "     OR u.certificate.certificateLastExpireAlertOn < :lastSendAlertDate )")

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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_USER_SEQ")
    @GenericGenerator(name = "SMP_USER_SEQ", strategy = "native")
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
    OffsetDateTime passwordChanged;
    @Column(name = "PASSWORD_EXPIRE_ON")
    @ColumnDescription(comment = "Date when password will expire")
    OffsetDateTime passwordExpireOn;
    @Column(name = "PASSWORD_LAST_ALERT_ON")
    @ColumnDescription(comment = "Generated last password expire alert")
    OffsetDateTime passwordExpireAlertOn;


    @Column(name = "LOGIN_FAILURE_COUNT")
    @ColumnDescription(comment = "Sequential login failure count")
    Integer sequentialLoginFailureCount;
    @Column(name = "LAST_FAILED_LOGIN_ON")
    @ColumnDescription(comment = "Last failed login attempt")
    OffsetDateTime lastFailedLoginAttempt;

    // Personal access token
    @Column(name = "ACCESS_TOKEN_ID", length = CommonColumnsLengths.MAX_USERNAME_LENGTH, unique = true)
    @ColumnDescription(comment = "Personal access token id")
    private String accessTokenIdentifier;
    @Column(name = "ACCESS_TOKEN", length = CommonColumnsLengths.MAX_PASSWORD_LENGTH)
    @ColumnDescription(comment = "BCrypted personal access token")
    private String accessToken;
    @Column(name = "ACCESS_TOKEN_GENERATED_ON")
    @ColumnDescription(comment = "Date when personal access token was generated")
    OffsetDateTime accessTokenGeneratedOn;
    @Column(name = "ACCESS_TOKEN_EXPIRE_ON")
    @ColumnDescription(comment = "Date when personal access token will expire")
    OffsetDateTime accessTokenExpireOn;
    @Column(name = "ACCESS_TOKEN_LAST_ALERT_ON")
    @ColumnDescription(comment = "Generated last access token expire alert")
    OffsetDateTime accessTokenExpireAlertOn;
    @Column(name = "AT_LOGIN_FAILURE_COUNT")
    @ColumnDescription(comment = "Sequential token login failure count")
    Integer sequentialTokenLoginFailureCount;
    @Column(name = "AT_LAST_FAILED_LOGIN_ON")
    @ColumnDescription(comment = "Last failed token login attempt")
    OffsetDateTime lastTokenFailedLoginAttempt;

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

    public OffsetDateTime getLastFailedLoginAttempt() {
        return lastFailedLoginAttempt;
    }

    public void setLastFailedLoginAttempt(OffsetDateTime lastFailedLoginAttempt) {
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
    }

    public OffsetDateTime getLastTokenFailedLoginAttempt() {
        return lastTokenFailedLoginAttempt;
    }

    public void setLastTokenFailedLoginAttempt(OffsetDateTime lastTokenFailedLoginAttempt) {
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

    public OffsetDateTime getAccessTokenGeneratedOn() {
        return accessTokenGeneratedOn;
    }

    public void setAccessTokenGeneratedOn(OffsetDateTime accessTokenGeneratedOn) {
        this.accessTokenGeneratedOn = accessTokenGeneratedOn;
    }

    public OffsetDateTime getPasswordExpireOn() {
        return passwordExpireOn;
    }

    public void setPasswordExpireOn(OffsetDateTime passwordExpireOn) {
        this.passwordExpireOn = passwordExpireOn;
    }

    public OffsetDateTime getAccessTokenExpireOn() {
        return accessTokenExpireOn;
    }

    public void setAccessTokenExpireOn(OffsetDateTime accessTokenExpireOn) {
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

    public OffsetDateTime getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(OffsetDateTime passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public OffsetDateTime getPasswordExpireAlertOn() {
        return passwordExpireAlertOn;
    }

    public void setPasswordExpireAlertOn(OffsetDateTime passwordExpireAlertOn) {
        this.passwordExpireAlertOn = passwordExpireAlertOn;
    }

    public OffsetDateTime getAccessTokenExpireAlertOn() {
        return accessTokenExpireAlertOn;
    }

    public void setAccessTokenExpireAlertOn(OffsetDateTime accessTokenExpireAlertOn) {
        this.accessTokenExpireAlertOn = accessTokenExpireAlertOn;
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
