package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class SMPAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationToken.class);
    DBUser user;
    // session encryption key to encrypt sensitive data
    // at the moment used for UI sessions
    SecurityUtils.Secret secret=null;

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

    public SecurityUtils.Secret getSecret(){
        if (secret==null) {
            LOG.debug("Secret does not yet exist. Create user session secret!");
            secret = SecurityUtils.generatePrivateSymmetricKey();
            LOG.debug("User session secret created!");
        }
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAuthenticationToken)) return false;
        if (!super.equals(o)) return false;
        SMPAuthenticationToken that = (SMPAuthenticationToken) o;
        // also check super equals (roles..) which is implemented in AbstractAuthenticationToken
        return Objects.equals(user, that.user) && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}