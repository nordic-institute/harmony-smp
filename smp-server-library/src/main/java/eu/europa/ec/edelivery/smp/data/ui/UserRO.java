package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import javax.persistence.Column;
import java.time.OffsetDateTime;
import java.util.Collection;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO extends BaseRO {

    static final long serialVersionUID = 2821447495333163882L;

    String username;
    String password;
    OffsetDateTime passwordExpireOn;
    Integer sequentialLoginFailureCount;
    OffsetDateTime lastFailedLoginAttempt;
    OffsetDateTime suspendedUtil;
    String accessTokenId;
    OffsetDateTime accessTokenExpireOn;
    Integer sequentialTokenLoginFailureCount;
    OffsetDateTime lastTokenFailedLoginAttempt;
    OffsetDateTime tokenSuspendedUtil;
    String emailAddress;
    Collection<SMPAuthority> authorities;
    boolean active = true;
    String role;
    String userId;
    CertificateRO certificate;
    int statusPassword = EntityROStatus.PERSISTED.getStatusNumber();
    boolean passwordExpired = false;
    boolean showPasswordExpirationWarning = false;
    boolean forceChangeExpiredPassword = false;
    boolean casAuthenticated = false;

    String casUserDataUrl;

    /**
     * Get DB user hash value. It can be used as unique ID for the user. Use hash value for the webservice/ui and do not
     * expose internal database user identity
     *
     * @return hash value of database user entity.
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public CertificateRO getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateRO certificate) {
        this.certificate = certificate;
    }

    public Collection<SMPAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<SMPAuthority> authorities) {
        this.authorities = authorities;
    }

    public int getStatusPassword() {
        return statusPassword;
    }

    public void setStatusPassword(int statusPassword) {
        this.statusPassword = statusPassword;
    }

    public boolean isShowPasswordExpirationWarning() {
        return showPasswordExpirationWarning;
    }

    public void setShowPasswordExpirationWarning(boolean showPasswordExpirationWarning) {
        this.showPasswordExpirationWarning = showPasswordExpirationWarning;
    }

    public boolean isForceChangeExpiredPassword() {
        return forceChangeExpiredPassword;
    }

    public void setForceChangePassword(boolean forceChangeExpiredPassword) {
        this.forceChangeExpiredPassword = forceChangeExpiredPassword;
    }

    public String getCasUserDataUrl() {
        return casUserDataUrl;
    }

    public void setCasUserDataUrl(String casUserDataUrl) {
        this.casUserDataUrl = casUserDataUrl;
    }

    public boolean isCasAuthenticated() {
        return casAuthenticated;
    }

    public void setCasAuthenticated(boolean casAuthenticated) {
        this.casAuthenticated = casAuthenticated;
    }

    public Integer getSequentialLoginFailureCount() {
        return sequentialLoginFailureCount;
    }

    public void setSequentialLoginFailureCount(Integer sequentialLoginFailureCount) {
        this.sequentialLoginFailureCount = sequentialLoginFailureCount;
    }

    public OffsetDateTime getLastFailedLoginAttempt() {
        return lastFailedLoginAttempt;
    }

    public void setLastFailedLoginAttempt(OffsetDateTime lastFailedLoginAttempt) {
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
    }

    public OffsetDateTime getSuspendedUtil() {
        return suspendedUtil;
    }

    public void setSuspendedUtil(OffsetDateTime suspendedUtil) {
        this.suspendedUtil = suspendedUtil;
    }

    public Integer getSequentialTokenLoginFailureCount() {
        return sequentialTokenLoginFailureCount;
    }

    public void setSequentialTokenLoginFailureCount(Integer sequentialTokenLoginFailureCount) {
        this.sequentialTokenLoginFailureCount = sequentialTokenLoginFailureCount;
    }

    public OffsetDateTime getLastTokenFailedLoginAttempt() {
        return lastTokenFailedLoginAttempt;
    }

    public void setLastTokenFailedLoginAttempt(OffsetDateTime lastTokenFailedLoginAttempt) {
        this.lastTokenFailedLoginAttempt = lastTokenFailedLoginAttempt;
    }

    public OffsetDateTime getTokenSuspendedUtil() {
        return tokenSuspendedUtil;
    }

    public void setTokenSuspendedUtil(OffsetDateTime tokenSuspendedUtil) {
        this.tokenSuspendedUtil = tokenSuspendedUtil;
    }
}
