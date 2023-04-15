package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationService;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
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

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_USER;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.PARAM_PAGINATION_FILTER;
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
    public AccessTokenRO generateAccessToken(@PathVariable("user-id") String userId, @RequestBody(required = false) String password) {
        Long entityId = decryptEntityId(userId);
        SMPUserDetails currentUser = SessionSecurityUtils.getSessionUserDetails();
        LOG.info("Generated access token for user:[{}] with id:[{}] ", userId, entityId);
        if (currentUser == null) {
            throw new SessionAuthenticationException("User session expired!");
        }

        // no need to validate password if CAS authenticated
        //return uiUserService.generateAccessTokenForUser(entityId, entityId, password, !currentUser.isCasAuthenticated());
        return null;

    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(path = "/{user-id}/change-password", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public boolean changePassword(@PathVariable("user-id") String userId, @RequestBody PasswordChangeRO newPassword, HttpServletRequest request, HttpServletResponse response) {
        Long entityId = decryptEntityId(userId);
        LOG.info("Validating the password of the currently logged in user:[{}] with id:[{}] ", userId, entityId);
        // when user changing password the current password must be verified even if cas authenticated
        DBUser result = uiUserService.updateUserPassword(entityId, entityId, newPassword.getCurrentPassword(), newPassword.getNewPassword());
        if (result != null) {
            LOG.info("Password successfully changed. Logout the user, to be able to login with the new password!");
            authenticationService.logout(request, response);
        }
        return result != null;
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @GetMapping(path = "/{user-id}/search", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<SearchUserRO> lookupUsers(@PathVariable("user-id") String userId,
                              @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter ) {
        Long entityId = decryptEntityId(userId);
        LOG.info("Validating the password of the currently logged in user:[{}] with id:[{}] ", userId, entityId);

        //  return first 10 results
        return  uiUserService.searchUsers(0, 10, filter).getServiceEntities();
    }

    /**
     * Update the details of the currently logged-in user (e.g. update the role, the credentials or add certificate details).
     *
     * @param userId the identifier of the user being updated; it must match the currently logged-in user's identifier
     * @param user   the updated details
     * @throws org.springframework.security.access.AccessDeniedException when trying to update the details of another user, different than the one being currently logged in
     */
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(path = "/{user-id}")
    public UserRO updateCurrentUserProfile(@PathVariable("user-id") String userId, @RequestBody UserRO user) {
        LOG.info("Update current user: {}", user);
        Long entityId = decryptEntityId(userId);
        // Update the user and mark the password as changed at this very instant of time
        uiUserService.updateUserProfile(entityId, user);
        // refresh user from DB
        UserRO userRO = uiUserService.getUserById(entityId);
        // return clean user to UI
        return authorizationService.sanitize(userRO);
    }

    /**
     * Update the details of the currently logged in user (e.g. update the role, the credentials or add certificate details).
     *
     * @param userId the identifier of the user being updated; it must match the currently logged in user's identifier
     * @throws org.springframework.security.access.AccessDeniedException if user is not logged in
     */
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @GetMapping(path = "/{user-id}/navigation-tree")
    public NavigationTreeNodeRO getUserNavigationTree(@PathVariable("user-id") String userId) {
        LOG.info("get User Navigation tree for user ID: {}", userId);
        Long entityId = decryptEntityId(userId);
        DBUser user = uiUserService.findUser(entityId);
        NavigationTreeNodeRO home = new NavigationTreeNodeRO("home", "Home", "home", "");
        home.addChild(createPublicNavigationTreeNode());
        // create administration nodes for domains, groups and resources
        NavigationTreeNodeRO adminNodes = createEditNavigationTreeNode();
        if (!adminNodes.getChildren().isEmpty()) {
            home.addChild(adminNodes);
        }
        if (user.getApplicationRole() == ApplicationRoleType.SYSTEM_ADMIN) {
            home.addChild(createSystemAdminNavigationTreeNode());
        }
        home.addChild(createUserProfileNavigationTreeNode());
        return home;
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @GetMapping(path = "/{user-id}/username-credential-status")
    public CredentialRO getUsernameCredentialStatus(@PathVariable("user-id") String userId) {
        LOG.debug("Get user credential status for user: [{}]", userId);
        Long entityId = decryptEntityId(userId);
        // Update the user and mark the password as changed at this very instant of time
        List<CredentialRO> credentialROList = uiUserService.getUserCredentials(entityId,
                CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);

        return credentialROList.isEmpty()?null:credentialROList.get(0);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @GetMapping(path = "/{user-id}/access-token-credentials")
    public List<CredentialRO> getAccessTokenCredentials(@PathVariable("user-id") String encUserId) {
        LOG.debug("Get access token credential status for user:: [{}]", encUserId);
        Long userId = decryptEntityId(encUserId);
        // Update the user and mark the password as changed at this very instant of time
        return uiUserService.getUserCredentials(userId,
                CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @DeleteMapping(path = "/{user-id}/access-token-credential/{credential-id}")
    public CredentialRO deleteAccessTokenCredentials(@PathVariable("user-id") String encUserId,
                                                        @PathVariable("credential-id") String encAccessTokenId) {
        LOG.debug("Delete User [{}] access token credential: [{}]", encUserId, encAccessTokenId);
        Long userId = decryptEntityId(encUserId);
        Long accessTokenId = decryptEntityId(encAccessTokenId);

        // delete user credential
        return uiUserService.deleteUserCredentials(userId,
                accessTokenId, CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @PostMapping(path = "/{user-id}/access-token-credential/{credential-id}")
    public CredentialRO updateAccessTokenCredentials(@PathVariable("user-id") String encUserId,
                                                     @PathVariable("credential-id") String encAccessTokenId,
                                                     @RequestBody CredentialRO credentialRO) {
        LOG.debug("Update User [{}] access token credential: [{}]", encUserId, encAccessTokenId);
        Long userId = decryptEntityId(encUserId);
        Long accessTokenId = decryptEntityId(encAccessTokenId);

        // delete user credential
        return uiUserService.updateUserCredentials(userId,
                accessTokenId,
                CredentialType.ACCESS_TOKEN,
                CredentialTargetType.REST_API,
                credentialRO);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @PutMapping(path = "/{user-id}/access-token-credential/{credential-id}")
    public AccessTokenRO generateAccessTokenCredential(@PathVariable("user-id") String encUserId,
                                                     @PathVariable("credential-id") String encAccessTokenId,
                                                     @RequestBody CredentialRO credentialRO) {
        LOG.debug("Update User [{}] access token credential: [{}]", encUserId, encAccessTokenId);
        Long userId = decryptEntityId(encUserId);
        return uiUserService.createAccessTokenForUser(userId, credentialRO);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @GetMapping(path = "/{user-id}/certificate-credentials")
    public List<CredentialRO> getCertificateCredentials(@PathVariable("user-id") String encUserId) {
        LOG.debug("get User credential status: [{}]", encUserId);
        Long userId = decryptEntityId(encUserId);
        // Update the user and mark the password as changed at this very instant of time
        return  uiUserService.getUserCredentials(userId,
                CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @DeleteMapping(path = "/{user-id}/certificate-credential/{credential-id}")
    public CredentialRO deleteCertificateCredential(@PathVariable("user-id") String encUserId,
                                                     @PathVariable("credential-id") String encCredentialId) {
        LOG.debug("Delete User [{}] access certificate credential: [{}]", encUserId, encCredentialId);
        Long userId = decryptEntityId(encUserId);
        Long credentialId = decryptEntityId(encCredentialId);
        // delete user credential
        return uiUserService.deleteUserCredentials(userId,
                credentialId, CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @PostMapping(path = "/{user-id}/certificate-credential/{credential-id}")
    public CredentialRO updateCertificateCredential(@PathVariable("user-id") String encUserId,
                                                     @PathVariable("credential-id") String encCredentialId,
                                                     @RequestBody CredentialRO credentialRO) {
        LOG.debug("Update User [{}] access token credential: [{}]", encUserId, encCredentialId);
        Long userId = decryptEntityId(encUserId);
        Long credentialId = decryptEntityId(encCredentialId);
        // delete user credential
        return uiUserService.updateUserCredentials(userId,
                credentialId,
                CredentialType.CERTIFICATE,
                CredentialTargetType.REST_API,
                credentialRO);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @GetMapping(path = "/{user-id}/certificate-credential/{credential-id}")
    public CredentialRO getCertificateCredential(@PathVariable("user-id") String encUserId,
                                                     @PathVariable("credential-id") String encCredentialId) {
        LOG.debug("Update User [{}] access token credential: [{}]", encUserId, encCredentialId);
        Long userId = decryptEntityId(encUserId);
        Long credentialId = decryptEntityId(encCredentialId);
        return uiUserService.getUserCertificateCredential(userId, credentialId);
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @PutMapping(path = "/{user-id}/certificate-credential/{credential-id}")
    public CredentialRO storeCertificateCredential(@PathVariable("user-id") String encUserId,
                                                       @PathVariable("credential-id") String credentialId,
                                                       @RequestBody CredentialRO credentialRO) {
        LOG.debug("Store credential for user [{}] certificate  credential: [{}]", encUserId, credentialId);
        Long userId = decryptEntityId(encUserId);
        return uiUserService.storeCertificateCredentialForUser(userId, credentialRO);
    }


    protected NavigationTreeNodeRO createPublicNavigationTreeNode() {
        NavigationTreeNodeRO node = new NavigationTreeNodeRO("search-tools", "Search", "search", "public");
        node.addChild(new NavigationTreeNodeRO("search-resources", "Resources", "find_in_page", "search-resource","Search registered resources"));
        //node.addChild(new NavigationTreeNodeRO("search-lookup", "DNS lookup", "dns", "dns-lookup" , "DNS lookup tools"));
        return node;
    }

    protected NavigationTreeNodeRO createUserProfileNavigationTreeNode() {
        NavigationTreeNodeRO node = new NavigationTreeNodeRO("user-data", "User Settings", "account_circle", "user-settings");
        node.addChild(new NavigationTreeNodeRO("user-data-profile", "Profile", "account_circle", "user-profile"));
        node.addChild(new NavigationTreeNodeRO("user-data-access-token", "Access tokens", "key", "user-access-token"));
        node.addChild(new NavigationTreeNodeRO("user-data-certificates", "Certificates", "article", "user-certificate"));
  //      node.addChild(new NavigationTreeNodeRO("user-data-membership", "Membership", "person", "user-membership"));
        return node;
    }

    protected NavigationTreeNodeRO createSystemAdminNavigationTreeNode() {
        NavigationTreeNodeRO node = new NavigationTreeNodeRO("system-settings", "System settings", "admin_panel_settings", "system-settings");
        node.addChild(new NavigationTreeNodeRO("system-admin-user", "Users", "people", "user"));
        node.addChild(new NavigationTreeNodeRO("system-admin-domain", "Domains", "domain", "domain"));
        node.addChild(new NavigationTreeNodeRO("system-admin-keystore", "Keystore", "key", "keystore"));
        node.addChild(new NavigationTreeNodeRO("system-admin-truststore", "Truststore", "article", "truststore"));
        node.addChild(new NavigationTreeNodeRO("system-admin-extension", "Extensions", "extension", "extension"));
        node.addChild(new NavigationTreeNodeRO("system-admin-properties", "Properties", "settings", "properties"));
       // node.addChild(new NavigationTreeNodeRO("system-admin-authentication", "Authentication", "shield", "authentication"));
        node.addChild(new NavigationTreeNodeRO("system-admin-alert", "Alerts", "notifications", "alert"));
        return node;
    }

    protected NavigationTreeNodeRO createEditNavigationTreeNode() {
        NavigationTreeNodeRO node = new NavigationTreeNodeRO("edit", "Administration", "tune", "edit");
        // is domain admin
        if (authorizationService.isAnyDomainAdministrator()) {
            node.addChild(new NavigationTreeNodeRO("edit-domain", "Edit domains", "account_circle", "edit-domain"));
        }
        if (authorizationService.isAnyGroupAdministrator()) {
            // is group admin
            node.addChild(new NavigationTreeNodeRO("edit-group", "Edit groups", "group", "edit-group"));
        }
        if (authorizationService.isAnyResourceAdministrator()) {
            // is resource admin
            node.addChild(new NavigationTreeNodeRO("edit-resource", "Edit resources", "article", "edit-resource"));
        }
        return node;
    }
}
