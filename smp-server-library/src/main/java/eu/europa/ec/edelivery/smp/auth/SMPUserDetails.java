package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Object contains Session details for logged user. For the UI it also generated the session secret for encrypting the
 * session sensitive data.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPUserDetails implements UserDetails {
    final DBUser user;
    final SecurityUtils.Secret sessionSecret;
    boolean casAuthenticated = false;
    List<SMPAuthority> smpAuthorities = new ArrayList<>();

    public SMPUserDetails(DBUser user, SecurityUtils.Secret sessionSecret, List<SMPAuthority> smpAuthorities) {
        this.user = user;
        if (smpAuthorities != null) {
            this.smpAuthorities.addAll(smpAuthorities);
        }
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
                "user=" + getUsername() +
                '}';
    }
}
