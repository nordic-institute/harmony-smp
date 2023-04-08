package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.time.OffsetDateTime;
import java.util.Collection;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630023L;

    private String userId;
    private String username;
    private boolean active = true;
    private ApplicationRoleType role;
    private String emailAddress;
    private String fullName;
    private String smpTheme;
    private String smpLocale;
    // operational UI data
    private boolean casAuthenticated = false;
    private String casUserDataUrl;


    private OffsetDateTime passwordExpireOn;
    private Integer sequentialLoginFailureCount;
    private OffsetDateTime lastFailedLoginAttempt;
    private OffsetDateTime suspendedUtil;
    private String accessTokenId;
    private OffsetDateTime accessTokenExpireOn;
    private Integer sequentialTokenLoginFailureCount;
    private OffsetDateTime lastTokenFailedLoginAttempt;
    private OffsetDateTime tokenSuspendedUtil;

    private Collection<SMPAuthority> authorities;

    private CertificateRO certificate;
    private int statusPassword = EntityROStatus.PERSISTED.getStatusNumber();
    private boolean passwordExpired = false;
    private boolean showPasswordExpirationWarning = false;
    private boolean forceChangeExpiredPassword = false;


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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ApplicationRoleType getRole() {
        return role;
    }

    public void setRole(ApplicationRoleType role) {
        this.role = role;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSmpTheme() {
        return smpTheme;
    }

    public void setSmpTheme(String smpTheme) {
        this.smpTheme = smpTheme;
    }


    public String getSmpLocale() {
        return smpLocale;
    }

    public void setSmpLocale(String smpLocale) {
        this.smpLocale = smpLocale;
    }

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }


    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
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
