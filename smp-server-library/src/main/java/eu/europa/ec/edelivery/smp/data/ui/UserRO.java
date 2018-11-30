package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO extends BaseRO {

    private static final long serialVersionUID = 2821447495333163882L;

    private String username;
    private String password;
    private String emailAddress;
    private List<String> authorities;
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

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public int getStatusPassword() {
        return statusPassword;
    }

    public void setStatusPassword(int statusPassword) {
        this.statusPassword = statusPassword;
    }
}
