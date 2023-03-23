package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationService;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.AccessTokenRO;
import eu.europa.ec.edelivery.smp.data.ui.PasswordChangeRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_USER;
import static eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils.decryptEntityId;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(path = CONTEXT_PATH_PUBLIC_USER)
public class UserResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UserResource.class);
    protected UIUserService uiUserService;
    protected SMPAuthorizationService authorizationService;
    protected SMPAuthenticationService authenticationService;

    public UserResource(UIUserService uiUserService, SMPAuthorizationService authorizationService, SMPAuthenticationService authenticationService) {
        this.uiUserService = uiUserService;
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PostMapping(path = "/{user-id}/generate-access-token", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public AccessTokenRO generateAccessToken(@PathVariable("user-id") String userId, @RequestBody(required = false)  String password) {
        Long entityId = decryptEntityId(userId);
        SMPUserDetails currentUser = SessionSecurityUtils.getSessionUserDetails();
        LOG.info("Generated access token for user:[{}] with id:[{}] ", userId, entityId);
        if (currentUser == null) {
            throw new SessionAuthenticationException("User session expired!");
        }

        // no need to validate password if CAS authenticated
        return uiUserService.generateAccessTokenForUser(entityId, entityId, password, !currentUser.isCasAuthenticated());
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(path = "/{user-id}/change-password", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public boolean changePassword(@PathVariable("user-id") String userId, @RequestBody PasswordChangeRO newPassword, HttpServletRequest request, HttpServletResponse response) {
        Long entityId = decryptEntityId(userId);
        LOG.info("Validating the password of the currently logged in user:[{}] with id:[{}] ", userId, entityId);
        // when user changing password the current password must be verified even if cas authenticated
        DBUser result = uiUserService.updateUserPassword(entityId, entityId, newPassword.getCurrentPassword(), newPassword.getNewPassword());
        if (result!=null) {
            LOG.info("Password successfully changed. Logout the user, to be able to login with the new password!");
            authenticationService.logout(request, response);
        }
        return result!=null;
    }

    /**
     * Update the details of the currently logged in user (e.g. update the role, the credentials or add certificate details).
     *
     * @param userId the identifier of the user being updated; it must match the currently logged in user's identifier
     * @param user   the updated details
     * @throws org.springframework.security.access.AccessDeniedException when trying to update the details of another user, different than the one being currently logged in
     */
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(path = "/{user-id}")
    public UserRO updateCurrentUser(@PathVariable("user-id") String userId, @RequestBody UserRO user) {
        LOG.info("Update current user: {}", user);
        Long entityId = decryptEntityId(userId);
        // Update the user and mark the password as changed at this very instant of time
        uiUserService.updateUserdata(entityId, user);

        DBUser updatedUser = uiUserService.findUser(entityId);
        UserRO userRO = uiUserService.convertToRo(updatedUser);

        return authorizationService.sanitize(userRO);
    }
}
