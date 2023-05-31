package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.services.ui.filters.UserFilter;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.PARAM_PAGINATION_FILTER;
import static eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils.decryptEntityId;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_USER)
public class UserAdminController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UserAdminController.class);
    protected UIUserService uiUserService;
    protected UITruststoreService uiTruststoreService;
    protected SMPAuthorizationService authorizationService;

    public UserAdminController(UIUserService uiUserService, UITruststoreService uiTruststoreService, SMPAuthorizationService authorizationService) {
        this.uiUserService = uiUserService;
        this.uiTruststoreService = uiTruststoreService;
        this.authorizationService = authorizationService;
    }

    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public ServiceResult<UserRO> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "roles", required = false) String roleList
    ) {
        UserFilter filter = null;
        if (roleList != null) {
            filter = new UserFilter();
            filter.setRoleList(Arrays.asList(roleList.split(",")));
        }
        return uiUserService.getTableList(page, pageSize, orderBy, orderType, filter);
    }

    @GetMapping(path = "/{user-enc-id}/search", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public ServiceResult<SearchUserRO> getDomainMemberList(
            @PathVariable("user-enc-id") String userEncId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {

        LOG.info("Search user with filter  [{}], paging: [{}/{}], user: {}",filter,  page, pageSize, userEncId);
        return uiUserService.searchUsers(page, pageSize,  filter);
    }

    @GetMapping(path = "/{user-enc-id}/{managed-user-enc-id}/retrieve", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public UserRO getUserData(@PathVariable("user-enc-id") String userEncId,
                              @PathVariable("managed-user-enc-id") String managedUserEncId) {
        Long managedUserId = decryptEntityId(managedUserEncId);
        return uiUserService.getUserById(managedUserId);
    }

    @PostMapping(path = "/{user-enc-id}/{managed-user-enc-id}/update",  produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public UserRO updateUser(@PathVariable("user-enc-id") String userEncId,
                           @PathVariable("managed-user-enc-id") String managedUserEncId,
                            @RequestBody UserRO user) {

        Long userId = decryptEntityId(userEncId);
        Long managedUserId = decryptEntityId(managedUserEncId);
        LOG.info("UpdateUserData adminId: [{}], managedUserId: [{}]", userId, managedUserId);
        // Update the user and mark the password as changed at this very instant of time
        uiUserService.adminUpdateUserData(managedUserId, user);
        // refresh user from DB
        UserRO userRO = uiUserService.getUserById(managedUserId);
        // return clean user to UI
        return authorizationService.sanitize(userRO);
    }


    @DeleteMapping(path = "/{user-enc-id}/{managed-user-enc-id}/delete",  produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public UserRO deleteUser(@PathVariable("user-enc-id") String userEncId,
                           @PathVariable("managed-user-enc-id") String managedUserEncId) {

        Long userId = decryptEntityId(userEncId);
        Long managedUserId = decryptEntityId(managedUserEncId);
        LOG.info("DeleteUserData adminId: [{}], managedUserId: [{}]", userId, managedUserId);
        // Update the user and mark the password as changed at this very instant of time
        UserRO deleted = uiUserService.adminDeleteUserData(managedUserId);
        // return clean user to UI
        return authorizationService.sanitize(deleted);
    }

    @PutMapping(path = "/{user-enc-id}/create",  produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public UserRO createUser(@PathVariable("user-enc-id") String userEncId,
                             @RequestBody UserRO user) {

        Long userId = decryptEntityId(userEncId);
        LOG.info("createUserData adminId: [{}], managedUserId: [{}]", userId);
        // Update the user and mark the password as changed at this very instant of time
        return uiUserService.adminCreateUserData(user);
    }

    @PostMapping(value = "validate-delete", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public DeleteEntityValidation validateDeleteUsers(@RequestBody List<String> queryEncIds) {
        SMPUserDetails userDetails = getLoggedUserData();
        List<Long> query = queryEncIds.stream().map(SessionSecurityUtils::decryptEntityId).collect(Collectors.toList());
        DeleteEntityValidation dres = new DeleteEntityValidation();
        if (query.contains(userDetails.getUser().getId())) {
            dres.setValidOperation(false);
            dres.setStringMessage("Could not delete logged user!");
            return dres;
        }
        dres.getListIds().addAll(query.stream().map(SessionSecurityUtils::encryptedEntityId).collect(Collectors.toList()));
        return uiUserService.validateDeleteRequest(dres);
    }

    @PutMapping(path = "/{user-id}/change-password-for/{update-user-id}", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public UserRO changePassword(@PathVariable("user-id") String userId,
                                 @PathVariable("update-user-id") String regenerateForUserId,
                                 @RequestBody PasswordChangeRO newPassword) {
        Long authorizedUserId = decryptEntityId(userId);
        Long changeUserId = decryptEntityId(regenerateForUserId);
        LOG.info("change the password of the currently logged in user:[{}] with id:[{}] ", changeUserId, regenerateForUserId);

        SMPUserDetails currentUser = SessionSecurityUtils.getSessionUserDetails();
        if (currentUser == null) {
            throw new SessionAuthenticationException("User session expired!");
        }

        DBUser user = uiUserService.updateUserPassword(authorizedUserId, changeUserId, newPassword.getCurrentPassword(), newPassword.getNewPassword(),!currentUser.isCasAuthenticated());
        return authorizationService.sanitize(uiUserService.convertToRo(user));
    }

    private SMPUserDetails getLoggedUserData() {
        return authorizationService.getAndValidateUserDetails();
    }
}
