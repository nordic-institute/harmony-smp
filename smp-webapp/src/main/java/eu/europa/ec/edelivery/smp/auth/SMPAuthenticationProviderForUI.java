package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.AlertService;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

/**
 * Authentication provider for the UI authentication.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPAuthenticationProviderForUI implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProviderForUI.class);

    final UserDao mUserDao;
    final CRLVerifierService crlVerifierService;
    final UITruststoreService truststoreService;
    final ConfigurationService configurationService;
    final AlertService alertService;


    @Autowired
    public SMPAuthenticationProviderForUI(UserDao mUserDao,
                                          CRLVerifierService crlVerifierService,
                                          AlertService alertService,
                                          UITruststoreService truststoreService,
                                          ConfigurationService configurationService) {
        this.mUserDao = mUserDao;
        this.crlVerifierService = crlVerifierService;
        this.alertService = alertService;
        this.truststoreService = truststoreService;
        this.configurationService = configurationService;
    }

    @Override
    public Authentication authenticate(Authentication authenticationToken)
            throws AuthenticationException {

        Authentication authentication = null;
        // PreAuthentication token for the rest service certificate authentication
        if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
            authentication = authenticateByUsernamePassword((UsernamePasswordAuthenticationToken) authenticationToken);
        }
        return authentication;
    }

    public Authentication authenticateByUsernamePassword(UsernamePasswordAuthenticationToken auth)
            throws AuthenticationException {

        String username = auth.getName();
        String userCredentialToken = auth.getCredentials().toString();

        DBUser user;
        try {
            Optional<DBUser> oUsr = mUserDao.findUserByUsername(username);
            if (!oUsr.isPresent()) {
                LOG.debug("User with username does not exists [{}], continue with next authentication provider");
                return null;
            }

            user = oUsr.get();
        } catch (AuthenticationException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, username, ExceptionUtils.getRootCause(ex), ex);
            throw ex;

        } catch (RuntimeException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, username, ExceptionUtils.getRootCause(ex), ex);
            throw new AuthenticationServiceException("Internal server error occurred while user authentication!");

        }

        validateIfUserAccountIsSuspended(user);

        String role = user.getRole();
        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(username, userCredentialToken, Collections.singletonList(new SMPAuthority(role)), user);
        try {
            if (!BCrypt.checkpw(userCredentialToken, user.getPassword())) {
                loginAttemptForUserFailed(user);
            }
            user.setSequentialLoginFailureCount(0);
            user.setLastFailedLoginAttempt(null);
            mUserDao.update(user);
        } catch (IllegalArgumentException ex) {
            // password is not hashed;
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, ex, username);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, role);
        return smpAuthenticationToken;
    }

    public void loginAttemptForUserFailed(DBUser user) {
        user.setSequentialLoginFailureCount(user.getSequentialLoginFailureCount() != null ? user.getSequentialLoginFailureCount() + 1 : 1);
        user.setLastFailedLoginAttempt(OffsetDateTime.now());
        mUserDao.update(user);
        LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, user.getUsername());
        if (user.getSequentialLoginFailureCount() >= configurationService.getLoginMaxAttempts()) {
            LOG.info("User [{}] failed sequential attempt exceeded the max allowed attempts [{}]!", user.getUsername(), configurationService.getLoginMaxAttempts());
            alertService.alertUsernamePasswordCredentialsSuspended(user);
        }
        throw new BadCredentialsException("Login failed; Invalid userID or password");
    }

    /**
     * Method tests if user account Suspended
     *
     * @param user
     */
    public void validateIfUserAccountIsSuspended(DBUser user) {
        if (user.getSequentialLoginFailureCount() == null
                || user.getSequentialLoginFailureCount() < 0) {
            LOG.trace("User has no previous failed attempts");
            return;
        }
        if (configurationService.getLoginMaxAttempts() == null
                || configurationService.getLoginMaxAttempts() < 0) {
            LOG.warn("Max login attempts [{}] is not set", SMPPropertyEnum.USER_MAX_FAILED_ATTEMPTS.getProperty());
            return;
        }

        if (user.getLastFailedLoginAttempt() == null) {
            LOG.warn("User [{}] has failed attempts [{}] but null last Failed login attempt!", user.getUsername(), user.getLastFailedLoginAttempt());
            return;
        }
        // check if the last failed attempt is already expired. If yes just clear the attempts
        if (configurationService.getLoginSuspensionTimeInSeconds() != null && configurationService.getLoginSuspensionTimeInSeconds() > 0
                && ChronoUnit.SECONDS.between(OffsetDateTime.now(), user.getLastFailedLoginAttempt()) > configurationService.getLoginSuspensionTimeInSeconds()) {
            LOG.warn("User [{}] suspension is expired! Clear failed login attempts and last failed login attempt", user.getUsername());
            user.setLastFailedLoginAttempt(null);
            user.setSequentialLoginFailureCount(0);
            mUserDao.update(user);
            return;
        }

        if (user.getSequentialLoginFailureCount() < configurationService.getLoginMaxAttempts()) {
            LOG.warn("User [{}] failed login attempt [{}]! did not reach the max failed attempts [{}]", user.getUsername(), user.getSequentialLoginFailureCount(), configurationService.getLoginMaxAttempts());
            return;
        }
        LOG.securityWarn(SMPMessageCode.SEC_USER_SUSPENDED, user.getUsername());
        throw new BadCredentialsException("The user is suspended. Please try again later or contact your administrator.");
    }

    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: " + auth);
        boolean supportAuthentication = auth.equals(UsernamePasswordAuthenticationToken.class);
        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: " + auth);
        }
        return supportAuthentication;
    }
}
