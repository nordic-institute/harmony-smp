package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;

import java.time.OffsetDateTime;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class CredentialRO extends BaseRO {

    static final long serialVersionUID = 2821447495333163889L;

    String credentialId;
    String name;
    boolean active;
    boolean expired;
    String description;

    CredentialType credentialType;
    OffsetDateTime updatedOn;
    OffsetDateTime expireOn;
    OffsetDateTime activeFrom;
    Integer sequentialLoginFailureCount;
    OffsetDateTime lastFailedLoginAttempt;
    OffsetDateTime suspendedUtil;

    CertificateRO certificate;

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(CredentialType credentialType) {
        this.credentialType = credentialType;
    }

    public OffsetDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(OffsetDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public OffsetDateTime getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(OffsetDateTime activeFrom) {
        this.activeFrom = activeFrom;
    }

    public OffsetDateTime getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(OffsetDateTime expireOn) {
        this.expireOn = expireOn;
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

    public CertificateRO getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateRO certificate) {
        this.certificate = certificate;
    }
}
