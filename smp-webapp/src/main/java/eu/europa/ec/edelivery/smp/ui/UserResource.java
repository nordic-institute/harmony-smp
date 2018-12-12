package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.services.ui.filters.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = "/ui/rest/user")
public class UserResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UIUserService uiUserService;

    @Autowired
    protected SMPAuthorizationService authorizationService;

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET)
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

        return  uiUserService.getTableList(page,pageSize, orderBy, orderType, filter);
    }

    /**
     * Update the details of the currently logged in user (e.g. update the role, the credentials or add certificate details).
     *
     * @param id the identifier of the user being updated; it must match the currently logged in user's identifier
     * @param user the updated details
     *
     * @throws org.springframework.security.access.AccessDeniedException when trying to update the details of another user, different than the one being currently logged in
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public UserRO updateCurrentUser(@PathVariable("id") Long id, @RequestBody UserRO user) {
        LOG.info("Update current user: {}", user);

        // Update the user and mark the password as changed at this very instant of time
        uiUserService.updateUserList(Arrays.asList(user), LocalDateTime.now());

        DBUser updatedUser = uiUserService.findUser(id);
        UserRO userRO = uiUserService.convertToRo(updatedUser);

        return authorizationService.sanitize(userRO);
    }

    @PutMapping(produces = {"application/json"})
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateUserList(@RequestBody UserRO[] updateEntities ){
        LOG.info("Update user list, count: {}", updateEntities.length);
        // Pass the users and mark the passwords of the ones being updated as expired by passing the passwordChange as null
        uiUserService.updateUserList(Arrays.asList(updateEntities), null);
    }

    @PostMapping(value = "/{id}/certdata" ,produces = {"application/json"},consumes = {"application/octet-stream"})
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public CertificateRO uploadFile(@PathVariable("id") Long id, @RequestBody byte[] data) {
        LOG.info("Got certificate data size: {}", data.length);


        try {
            return uiUserService.getCertificateData(data);
        } catch (IOException | CertificateException e) {
            LOG.error("Error occurred while parsing certificate.", e);
        }
        return null;
    }

    @PostMapping(path = "/{id}/samePreviousPasswordUsed", produces = {"application/json"})
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public boolean samePreviousPasswordUsed(@PathVariable("id") Long id, @RequestBody String password) {
        LOG.info("Validating the password of the currently logged in user: {} ", id);
        DBUser user = uiUserService.findUser(getCurrentUser().getId());
        return BCrypt.checkpw(password, user.getPassword());
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(path = "validateDelete", method = RequestMethod.POST)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public DeleteEntityValidation validateDeleteUsers(@RequestBody List<Long> query) {
        DBUser user = getCurrentUser();
        DeleteEntityValidation dres = new DeleteEntityValidation();
        if (query.contains(user.getId())){
            dres.setValidOperation(false);
            dres.setStringMessage("Could not delete logged user!");
            return dres;
        }
        dres.getListIds().addAll(query);
        return uiUserService.validateDeleteRequest(dres);
    }

    private DBUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SMPAuthenticationToken authToken = (SMPAuthenticationToken) authentication;
        return authToken.getUser();
    }
}
