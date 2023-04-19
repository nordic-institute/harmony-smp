package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.auth.enums.SMPUserAuthenticationTypes;
import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN;
import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.S_AUTHORITY_TOKEN_USER;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.1
 */
@Service("smpAuthorizationService")
public class SMPAuthorizationService {
    private static final String ERR_INVALID_OR_NULL = "Invalid or Expired session! Please login again.";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthorizationService.class);

    private final UserDao userDao;
    private final DomainMemberDao domainMemberDao;
    private final GroupMemberDao groupMemberDao;
    private final ResourceMemberDao resourceMemberDao;

    private final ConversionService conversionService;
    private final ConfigurationService configurationService;


    public SMPAuthorizationService(UserDao userDao,
                                   DomainMemberDao domainMemberDao,
                                   GroupMemberDao groupMemberDao,
                                   ResourceMemberDao resourceMemberDao,
                                   ConversionService conversionService,
                                   ConfigurationService configurationService) {
        this.userDao = userDao;
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
        this.conversionService = conversionService;
        this.configurationService = configurationService;
    }

    public boolean isSystemAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        boolean hasSystemRole = hasSessionUserRole(S_AUTHORITY_TOKEN_SYSTEM_ADMIN, userDetails);
        LOG.debug("Logged user [{}] is system administrator role [{}]", userDetails.getUsername(), hasSystemRole);
        return hasSystemRole;
    }

    public boolean isDomainAdministrator(String domainEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long domainId;
        try {
            domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        } catch (SMPRuntimeException | NumberFormatException ex) {
            LOG.error("Error occurred while decrypting domain-id:[" + domainEncId + "]", ex);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        return domainMemberDao.isUserDomainMemberWithRole(userDetails.getUser().getId(), Collections.singletonList(domainId), MembershipRoleType.ADMIN);
    }

    public boolean isGroupAdministrator(String groupEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long groupId  = getIdFromEncryptedString(groupEncId, false);
        return groupMemberDao.isUserGroupMemberWithRole(userDetails.getUser().getId(), Collections.singletonList(groupId), MembershipRoleType.ADMIN);
    }

    public boolean isResourceAdministrator(String resourceEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long resourceId  = getIdFromEncryptedString(resourceEncId, false);
        return resourceMemberDao.isUserResourceMemberWithRole(userDetails.getUser().getId(), resourceId, MembershipRoleType.ADMIN);
    }

    public boolean isResourceMember(String resourceEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long resourceId  = getIdFromEncryptedString(resourceEncId, false);
        return resourceMemberDao.isUserResourceMember(userDetails.getUser().getId(), resourceId);
    }

    public boolean isAnyDomainAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        return domainMemberDao.isUserAnyDomainAdministrator(userDetails.getUser().getId());
    }

    public boolean isAnyGroupAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        return groupMemberDao.isUserGroupAdministrator(userDetails.getUser().getId());
    }

    /**
     * Returns true if logged user is administrator for any of the domain group
     * @param domainEncId
     * @return true if logged user is group administrator in domain
     */
    public boolean isAnyDomainGroupAdministrator(String domainEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long domainId = getIdFromEncryptedString(domainEncId, false);
        return groupMemberDao.isUserAnyDomainGroupResourceMemberWithRole(userDetails.getUser().getId(), domainId, MembershipRoleType.ADMIN);
    }

    /**
     * Returns true if logged user is administrator for any of the resources on group
     * @param groupEncId
     * @return true if logged user is resource administrator in the group
     */
    public boolean isAnyGroupResourceAdministrator(String groupEncId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long groupId = getIdFromEncryptedString(groupEncId, false);
        return resourceMemberDao.isUserAnyGroupResourceMemberWithRole(userDetails.getUser().getId(), groupId, MembershipRoleType.ADMIN);
    }

    public boolean isAnyResourceAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        return domainMemberDao.isUserResourceAdministrator(userDetails.getUser().getId());
    }

    public boolean isSMPAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        boolean hasRole = hasSessionUserRole(S_AUTHORITY_TOKEN_USER, userDetails);
        LOG.debug("Logged user [{}] is SMP administrator role [{}]", userDetails.getUsername(), hasRole);
        return hasRole;
    }

    public boolean isCurrentlyLoggedIn(String userId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long entityId = getIdFromEncryptedString(userId, true);
        return entityId.equals(userDetails.getUser().getId());

    }

    public boolean isAuthorizedForManagingTheServiceMetadataGroup(Long serviceMetadataId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        if (hasSessionUserRole(S_AUTHORITY_TOKEN_USER, userDetails)) {
            LOG.debug("SMP admin is authorized to manage service metadata: [{}]", serviceMetadataId);
            return true;

        }
        Long userId = userDetails.getUser().getId();
        //return serviceGroupService.isServiceGroupOwnerForMetadataID(userId, serviceMetadataId);
        return false;

    }


    private boolean hasSessionUserRole(String role, SMPUserDetails userDetails) {
        return userDetails.getAuthorities().stream().anyMatch(grantedAuthority ->
                StringUtils.equals(role, grantedAuthority.getAuthority())
        );
    }

    /**
     * Returns a user resource with password credentials removed and authorities populated for use in the front-end.
     *
     * @param userRO The user resource to sanitize for use in the front-end.
     * @return the sanitized user resource
     */
    public UserRO sanitize(UserRO userRO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            userRO.setAuthorities(authentication.getAuthorities().stream().map(SMPAuthority.class::cast).collect(Collectors.toList()));
        }
        return userRO;
    }

    public SMPUserDetails getAndValidateUserDetails() {
        SMPUserDetails userDetails = SessionSecurityUtils.getSessionUserDetails();
        if (userDetails == null) {
            throw new SessionAuthenticationException(ERR_INVALID_OR_NULL);
        }
        return userDetails;
    }

    public UserRO getLoggedUserData() {
        SMPUserDetails userDetails = getAndValidateUserDetails();

        // refresh data from database!
        DBUser dbUser = userDao.find(userDetails.getUser().getId());
        if (dbUser == null || !dbUser.isActive()) {
            LOG.warn("User: [{}] with id [{}] does not exists anymore or is not active.",
                    userDetails.getUser().getId(),
                    userDetails.getUser().getUsername());
            return null;
        }
        UserRO userRO = getUserData(dbUser);
        userRO.setCasAuthenticated(userDetails.isCasAuthenticated());
        return userRO;
    }

    public UserRO getUserData(DBUser user) {
        UserRO userRO = conversionService.convert(user, UserRO.class);
        return getUpdatedUserData(userRO);
    }

    /**
     * Method updates data with "show expire dialog" flag, forces the password change flag and
     * sanitize ui data/
     *
     * @param userRO
     * @return updated user data according to SMP configuration
     */
    protected UserRO getUpdatedUserData(UserRO userRO) {
        userRO.setShowPasswordExpirationWarning(userRO.getPasswordExpireOn() != null &&
                OffsetDateTime.now().plusDays(configurationService.getPasswordPolicyUIWarningDaysBeforeExpire())
                        .isAfter(userRO.getPasswordExpireOn()));

        userRO.setForceChangePassword(userRO.isPasswordExpired() && configurationService.getPasswordPolicyForceChangeIfExpired());
        // set cas authentication data
        if (configurationService.getUIAuthenticationTypes().contains(SMPUserAuthenticationTypes.SSO.name())) {
            URL casUrlData = configurationService.getCasUserDataURL();
            userRO.setCasUserDataUrl(casUrlData != null ? casUrlData.toString() : null);
        }

        return sanitize(userRO);
    }

    protected Long getIdFromEncryptedString(String entityId, boolean userEntity) {
        try {
            return SessionSecurityUtils.decryptEntityId(entityId);
        } catch (SMPRuntimeException | NumberFormatException ex) {
            LOG.error("Error occurred while decrypting entity-id:[" + entityId + "]", ex);
            if (userEntity) {
                throw new BadCredentialsException(ErrorCode.UNAUTHORIZED_INVALID_USER_IDENTIFIER.getMessage());
            }
            throw new BadCredentialsException(ErrorCode.UNAUTHORIZED_INVALID_IDENTIFIER.getMessage());
        }
    }
}
