package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.utils.SMPConstants;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO extends BaseRO implements UserDetails {

    static final long serialVersionUID = 2821447495333163882L;

    String username;

    String password;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    OffsetDateTime passwordExpireOn;
    String accessTokenId;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    OffsetDateTime accessTokenExpireOn;
    String emailAddress;
    Collection<SMPAuthority> authorities;
    boolean active = true;
    String role;
    String userId;
    CertificateRO certificate;
    int statusPassword = EntityROStatus.PERSISTED.getStatusNumber();
    boolean passwordExpired;

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

    @Override
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

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return active;
    }
}
