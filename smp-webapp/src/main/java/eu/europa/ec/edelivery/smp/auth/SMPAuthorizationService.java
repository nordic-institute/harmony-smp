package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static eu.europa.ec.edelivery.smp.auth.SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN;

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
        if(authentication instanceof SMPAuthenticationToken) {
            Long loggedInUserId = ((SMPAuthenticationToken) authentication).getUser().getId();
            return loggedInUserId.equals(userId);
        }

        return false;
    }

}
