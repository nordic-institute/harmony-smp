/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
    private OffsetDateTime passwordUpdatedOn;

    private Collection<SMPAuthority> authorities;

    private int statusPassword = EntityROStatus.PERSISTED.getStatusNumber();
    private boolean passwordExpired = false;
    private boolean showPasswordExpirationWarning = false;
    private boolean forceChangeExpiredPassword = false;
    private int sessionMaxIntervalTimeoutInSeconds;

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

    public OffsetDateTime getPasswordUpdatedOn() {
        return passwordUpdatedOn;
    }

    public void setPasswordUpdatedOn(OffsetDateTime passwordUpdatedOn) {
        this.passwordUpdatedOn = passwordUpdatedOn;
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

    public void setSessionMaxIntervalTimeoutInSeconds(int sessionMaxIntervalTimeoutInSeconds) {
        this.sessionMaxIntervalTimeoutInSeconds = sessionMaxIntervalTimeoutInSeconds;
    }

    public int getSessionMaxIntervalTimeoutInSeconds() {
        return sessionMaxIntervalTimeoutInSeconds;
    }
}
