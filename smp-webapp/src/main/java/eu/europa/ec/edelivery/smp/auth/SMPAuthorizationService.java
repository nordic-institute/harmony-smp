package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Sebastian-Ion TINCU
 */
@Service("smpAuthorizationService")
public class SMPAuthorizationService {

    public boolean isCurrentlyLoggedIn(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof SMPAuthenticationToken) {
            Long loggedInUserId = ((SMPAuthenticationToken) authentication).getUser().getId();
            return loggedInUserId.equals(userId);
        }

        return false;
    }

    private DBUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SMPAuthenticationToken authToken = (SMPAuthenticationToken) authentication;
        return authToken.getUser();
    }
}
