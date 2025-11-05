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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import jakarta.persistence.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Object contains Session details for logged user. For the UI it also generated the session secret for encrypting the
 * session sensitive data.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPUserDetails implements UserDetails {
    private final DBUser user;
    @Transient
    private final SecurityUtils.Secret sessionSecret;
    private boolean casAuthenticated = false;
    private boolean jwtAuthenticated = false;
    @Transient
    private final List<String> authorizedScopes;
    @Transient
    private final List<SMPAuthority> smpAuthorities;

    public SMPUserDetails(DBUser user, SecurityUtils.Secret sessionSecret, List<SMPAuthority> smpAuthorities) {
        this(user, sessionSecret, smpAuthorities, null);
    }

    public SMPUserDetails(DBUser user, SecurityUtils.Secret sessionSecret, List<SMPAuthority> smpAuthorities, List<String> authorizedScopes) {
        this.user = user;
        //make unmodifiable list of smpAuthorities
        this.smpAuthorities = smpAuthorities == null ? Collections.emptyList() : Collections.unmodifiableList(smpAuthorities);
        //make unmodifiable list of scopes
        this.authorizedScopes = authorizedScopes == null ? Collections.emptyList() : Collections.unmodifiableList(authorizedScopes);
        this.sessionSecret = sessionSecret;
    }

    public DBUser getUser() {
        return user;
    }

    public SecurityUtils.Secret getSessionSecret() {
        return sessionSecret;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return smpAuthorities;
    }

    public boolean isCasAuthenticated() {
        return casAuthenticated;
    }

    public void setCasAuthenticated(boolean casAuthenticated) {
        this.casAuthenticated = casAuthenticated;
    }

    public boolean isJwtAuthenticated() {
        return jwtAuthenticated;
    }

    public void setJwtAuthenticated(boolean jwtAuthenticated) {
        this.jwtAuthenticated = jwtAuthenticated;
    }

    public List<String> getAuthorizedScopes() {
        return authorizedScopes;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.user != null ? this.user.getUsername() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }

    @Override
    public String toString() {
        return "SMPUserDetails{" +
                "username=" + getUsername() +
                "user=" + getUser() +
                '}';
    }
}
