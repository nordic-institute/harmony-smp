package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO extends BaseRO implements UserDetails {

    private static final long serialVersionUID = 2821447495333163882L;

    private String username;
    private String password;
    private String accessTokenId;
    private String emailAddress;
    private Collection<SMPAuthority> authorities;
    private boolean active = true;
    private String role;
    private Long id;
    private CertificateRO certificate;
    private int statusPassword = EntityROStatus.PERSISTED.getStatusNumber();
    private boolean passwordExpired;

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
