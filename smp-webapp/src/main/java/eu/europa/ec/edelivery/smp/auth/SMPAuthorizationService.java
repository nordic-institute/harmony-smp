package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.auth.enums.SMPUserAuthenticationTypes;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
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
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.*;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.1
 */
@Service("smpAuthorizationService")
public class SMPAuthorizationService {
    private static final String ERR_INVALID_OR_NULL = "Invalid or null authentication for the session!";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthorizationService.class);

    private final ServiceGroupService serviceGroupService;
    private final ConversionService conversionService;
    private final ConfigurationService configurationService;
    private final UserDao userDao;

    public SMPAuthorizationService(ServiceGroupService serviceGroupService,
                                   ConversionService conversionService,
                                   ConfigurationService configurationService,
                                   UserDao userDao) {
        this.serviceGroupService = serviceGroupService;
        this.conversionService = conversionService;
        this.configurationService = configurationService;
        this.userDao = userDao;
    }

    public boolean isSystemAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        boolean hasSystemRole = hasSessionUserRole(S_AUTHORITY_TOKEN_SYSTEM_ADMIN, userDetails);
        LOG.debug("Logged user [{}] is system administrator role [{}]", userDetails.getUsername(), hasSystemRole);
        return hasSystemRole;
    }

    public boolean isSMPAdministrator() {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        boolean hasRole = hasSessionUserRole(S_AUTHORITY_TOKEN_USER, userDetails);
        LOG.debug("Logged user [{}] is SMP administrator role [{}]", userDetails.getUsername(), hasRole);
        return hasRole;
    }

    public boolean isCurrentlyLoggedIn(String userId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        Long entityId;
        try {
            entityId = SessionSecurityUtils.decryptEntityId(userId);
        } catch (SMPRuntimeException | NumberFormatException ex) {
            LOG.error("Error occurred while decrypting user-id:[" + userId + "]", ex);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        return entityId.equals(userDetails.getUser().getId());

    }

    public boolean isAuthorizedForManagingTheServiceMetadataGroup(Long serviceMetadataId) {
        SMPUserDetails userDetails = getAndValidateUserDetails();
        if (hasSessionUserRole(S_AUTHORITY_TOKEN_USER, userDetails)) {
            LOG.debug("SMP admin is authorized to manage service metadata: [{}]" + serviceMetadataId);
            return true;

        }
        Long userId = userDetails.getUser().getId();
        return serviceGroupService.isServiceGroupOwnerForMetadataID(userId, serviceMetadataId);
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
        userRO.setPassword("");

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
            userRO.setCasUserDataUrl(casUrlData!=null?casUrlData.toString():null);
        }

        return sanitize(userRO);
    }
}
