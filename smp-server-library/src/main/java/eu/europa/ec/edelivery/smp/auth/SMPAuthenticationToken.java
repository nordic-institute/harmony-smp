package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Objects;

/**
 * UI and web service authentication token. The authentication is created by the authentication provider
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationToken.class);
    SMPUserDetails userDetails;

    public SMPAuthenticationToken(Object principal, Object credentials, SMPUserDetails userDetails) {
        super(principal, credentials, userDetails.getAuthorities());
        setDetails(userDetails);
        this.userDetails = userDetails;
    }

    public SecurityUtils.Secret getSecret() {

        if (userDetails == null) {
            LOG.warn("Can not retrieve security token for session. User details is null!");
            return null;
        }
        return userDetails.getSessionSecret();
    }

    public SMPUserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAuthenticationToken)) return false;
        if (!super.equals(o)) return false;
        SMPAuthenticationToken that = (SMPAuthenticationToken) o;
        // also check super equals (roles..) which is implemented in AbstractAuthenticationToken
        return Objects.equals(getDetails(), that.getDetails()) && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDetails());
    }
}