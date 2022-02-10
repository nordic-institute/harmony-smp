package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN;

/**
 * @author Sebastian-Ion TINCU
 */
@Service("smpAuthorizationService")
public class SMPAuthorizationService {

    public boolean isSystemAdministrator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof SMPAuthenticationToken
                && authentication.getAuthorities().stream().anyMatch(grantedAuthority -> S_AUTHORITY_TOKEN_SYSTEM_ADMIN.equals(grantedAuthority.getAuthority()));
    }

    public boolean isCurrentlyLoggedIn(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof SMPAuthenticationToken) {
            Long loggedInUserId = ((SMPAuthenticationToken) authentication).getUser().getId();
            return loggedInUserId.equals(userId);
        }
        return false;
    }

    /**
     * Returns a user resource with password credentials removed and authorities populated for use in the front-end.
     *
     * @param userRO The user resource to sanitize for use in the front-end.
     * @return the sanitized user resource
     */
    public UserRO sanitize(UserRO userRO) {
        userRO.setPassword("");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRO.setAuthorities(authentication.getAuthorities().stream().map(val -> (SMPAuthority) val).collect(Collectors.toList()));
        return userRO;
    }
}
