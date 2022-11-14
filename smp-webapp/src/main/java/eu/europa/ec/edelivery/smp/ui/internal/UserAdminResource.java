package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.services.ui.filters.UserFilter;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_USER;
import static eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils.decryptEntityId;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_USER)
public class UserAdminResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UserAdminResource.class);

    protected UIUserService uiUserService;
    protected UITruststoreService uiTruststoreService;
    protected SMPAuthorizationService authorizationService;

    public UserAdminResource(UIUserService uiUserService, UITruststoreService uiTruststoreService, SMPAuthorizationService authorizationService) {
        this.uiUserService = uiUserService;
        this.uiTruststoreService = uiTruststoreService;
        this.authorizationService = authorizationService;
    }

    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN})
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

    @PutMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateUserList(@RequestBody UserRO[] updateEntities) {
        LOG.info("Update user list, count: {}", updateEntities.length);
        // Pass the users and mark the passwords of the ones being updated as expired by passing the passwordChange as null
        uiUserService.updateUserList(Arrays.asList(updateEntities), null);
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
        dres.getListIds().addAll(query.stream().map(id -> SessionSecurityUtils.encryptedEntityId(id)).collect(Collectors.toList()));
        return uiUserService.validateDeleteRequest(dres);
    }

    @PostMapping(path = "/{user-id}/generate-access-token-for/{update-user-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public AccessTokenRO generateAccessTokenForUser(
            @PathVariable("user-id") String userId,
            @PathVariable("update-user-id") String regenerateForUserId,
            @RequestBody(required = false) String password) {
        Long authorizedUserId = decryptEntityId(userId);
        Long changeUserId = decryptEntityId(regenerateForUserId);
        LOG.info("Generated access token for user:[{}] with id:[{}] by the system user with id [{}]", userId, regenerateForUserId, authorizedUserId);

        SMPUserDetails currentUser = SessionSecurityUtils.getSessionUserDetails();
        if (currentUser == null) {
            throw new SessionAuthenticationException("User session expired!");
        }

        // no need to validate password if cas authenticated
        return uiUserService.generateAccessTokenForUser(authorizedUserId, changeUserId, password,!currentUser.isCasAuthenticated());
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
