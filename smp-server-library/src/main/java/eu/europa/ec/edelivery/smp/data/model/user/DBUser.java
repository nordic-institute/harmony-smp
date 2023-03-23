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
package eu.europa.ec.edelivery.smp.data.model.user;

import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Audited
@Table(name = "SMP_USER")
@org.hibernate.annotations.Table(appliesTo = "SMP_USER", comment = "SMP can handle multiple domains. This table contains domain specific data")
@NamedQueries({
        // case insensitive search
        @NamedQuery(name = "DBUser.getUserByUsernameInsensitive", query = "SELECT u FROM DBUser u WHERE upper(u.username) = upper(:username)"),
       // @NamedQuery(name = "DBUser.getUserByCertificateId", query = "SELECT u FROM DBUser u WHERE u.certificate.certificateId = :certificateId"),
        //@NamedQuery(name = "DBUser.getUserByPatId", query = "SELECT u FROM DBUser u WHERE u.accessTokenIdentifier = :patId"),
        //@NamedQuery(name = "DBUser.getUserByCertificateIdCaseInsensitive", query = "SELECT u FROM DBUser u WHERE upper(u.certificate.certificateId) = upper(:certificateId)"),
        /*@NamedQuery(name = "DBUser.getUsersForBeforePasswordExpireAlerts",
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
/*
        @NamedQuery(name = "DBUser.getUsersForBeforeCertificateExpireAlerts",
                query = "SELECT u FROM DBUser u WHERE u.certificate IS NOT NULL" +
                        " AND u.certificate.validTo IS NOT NULL " +
                        " AND u.certificate.validTo <= :startAlertDate " +
                        " AND u.certificate.validTo > :expireDate" +
                        " AND (u.certificate.certificateLastExpireAlertOn IS NULL " +
                        "       OR u.certificate.certificateLastExpireAlertOn < :lastSendAlertDate )"),
        @NamedQuery(name = "DBUser.getUsersForCertificateExpiredAlerts",
                query = "SELECT u FROM DBUser u WHERE u.certificate IS NOT NULL" +
                        " AND u.certificate.validTo IS NOT NULL " +
                        " AND u.certificate.validTo > :endAlertDate " +
                        " AND u.certificate.validTo <= :expireDate" +
                        " AND (u.certificate.certificateLastExpireAlertOn IS NULL " +
                        "     OR u.certificate.certificateLastExpireAlertOn <= u.certificate.validTo " +
                        "     OR u.certificate.certificateLastExpireAlertOn < :lastSendAlertDate )")
        */

})

@NamedNativeQueries({
        @NamedNativeQuery(name = "DBUserDeleteValidation.validateUsersForOwnership",
                resultSetMapping = "DBUserDeleteValidationMapping",
                query = "SELECT S.ID as ID, S.USERNAME as USERNAME, " +
                        "    C.CERTIFICATE_ID as certificateId, COUNT(S.ID) as  ownedCount  FROM " +
                        " SMP_USER S LEFT JOIN SMP_CERTIFICATE C ON (S.ID=C.ID) " +
                        " INNER JOIN SMP_RESOURCE_MEMBER SG ON (S.ID = SG.FK_USER_ID) " +
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
    @Column(name = "ACTIVE", nullable = false)
    @ColumnDescription(comment = "Is user active")
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "APPLICATION_ROLE", length = CommonColumnsLengths.MAX_USER_ROLE_LENGTH)
    @ColumnDescription(comment = "User application role as USER, SYSTEM_ADMIN")
    private ApplicationRoleType applicationRole;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public ApplicationRoleType getApplicationRole() {
        return applicationRole;
    }

    public void setApplicationRole(ApplicationRoleType applicationRole) {
        this.applicationRole = applicationRole;
    }

    @Override
    public String toString() {
        return "DBUser{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", username='" + username + '\'' +
                ", active=" + active +
                ", applicationRole=" + applicationRole +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DBUser dbUser = (DBUser) o;

        return Objects.equals(id, dbUser.id) &&
                StringUtils.equalsIgnoreCase(username, dbUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, username);
    }
}
