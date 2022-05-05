package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.*;

/**
 * @author Sebastian-Ion TINCU
 */
@Service("smpAuthorizationService")
public class SMPAuthorizationService {
    private static final String ERR_INVALID_OR_NULL = "Invalid or null authentication for the session!";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthorizationService.class);

    final private ServiceGroupService serviceGroupService;

    public SMPAuthorizationService(ServiceGroupService serviceGroupService) {
        this.serviceGroupService = serviceGroupService;
    }

    public boolean isSystemAdministrator() {
        SMPAuthenticationToken authentication = getAndValidateSessionAuthentication();
        boolean hasSystemRole = hasSessionUserRole(S_AUTHORITY_TOKEN_SYSTEM_ADMIN, authentication);
        LOG.debug("Logged user [{}] is system administrator role [{}]", authentication.getUser().getUsername(), hasSystemRole);
        return hasSystemRole;
    }

    public boolean isSMPAdministrator() {
        SMPAuthenticationToken authentication = getAndValidateSessionAuthentication();
        boolean hasSystemRole = hasSessionUserRole(S_AUTHORITY_TOKEN_SMP_ADMIN, authentication);
        LOG.debug("Logged user [{}] is SMP administrator role [{}]", authentication.getUser().getUsername(), hasSystemRole);
        return hasSystemRole;
    }

    public boolean isCurrentlyLoggedIn(String userId) {
        SMPAuthenticationToken authentication = getAndValidateSessionAuthentication();
        Long entityId;
        try {
            entityId = SessionSecurityUtils.decryptEntityId(userId);
        } catch (SMPRuntimeException | NumberFormatException ex) {
            LOG.error("Error occurred while decrypting user-id:[" + userId + "]", ex);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        Long loggedUserId = authentication.getUser().getId();
        return entityId.equals(loggedUserId);

    }

    public boolean isAuthorizedForManagingTheServiceMetadataGroup(Long serviceMetadataId) {
        SMPAuthenticationToken authentication = getAndValidateSessionAuthentication();
        if (hasSessionUserRole(S_AUTHORITY_TOKEN_SMP_ADMIN, authentication)) {
            LOG.debug("SMP admin is authorized to manage service metadata: [{}]" + serviceMetadataId);
            return true;

        }
        if (!hasSessionUserRole(S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN, authentication)) {
            LOG.debug("User is Service group admin nor SMP admin. User is not allowed to manage service metadata: [{}]" + serviceMetadataId);
            return false;
        }
        Long userId = authentication.getUser().getId();
        return serviceGroupService.isServiceGroupOwnerForMetadataID(userId, serviceMetadataId);
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
        if (authentication!=null ){
            userRO.setAuthorities(authentication.getAuthorities().stream().map(val -> (SMPAuthority) val).collect(Collectors.toList()));
        }
        return userRO;
    }

    private Authentication getSessionAuthentication() {
        if (SecurityContextHolder.getContext() == null) {
            LOG.warn("No users is logged-in! Session security context is null!");
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            LOG.warn("No users is logged-in! Authentication is null or not authenticated!");
            return null;
        }
        if (!(authentication instanceof SMPAuthenticationToken
                || authentication instanceof CasAuthenticationToken)) {
            LOG.warn("User is logged and authenticated with not supported Authentication [{}]!", authentication.getClass());
            return null;
        }
        return authentication;
    }

    private Authentication getAndValidateSessionAuthentication() {
        Authentication authentication = getSessionAuthentication();
        if (authentication == null) {
            throw new SessionAuthenticationException(ERR_INVALID_OR_NULL);
        }
        return authentication;
    }

    private boolean hasSessionUserRole(String role, SMPAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority ->
                StringUtils.equals(role, grantedAuthority.getAuthority())
        );
    }
}
