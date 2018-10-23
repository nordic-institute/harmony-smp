package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SMPAuthenticationToken extends UsernamePasswordAuthenticationToken {
    DBUser user;

    public SMPAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal,credentials, authorities );
    }

    public SMPAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, DBUser user) {
        super(principal,credentials, authorities );
        this.user = user;
    }

    public DBUser getUser() {
        return user;
    }
}
