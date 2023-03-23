package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;

/**
 * Authentication provider for the UI authentication.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPAuthenticationProviderForUI extends AbstracdtAuthenticationProvider {

    @Autowired
    public SMPAuthenticationProviderForUI(UserDao mUserDao,
                                          CredentialDao mCredentialDao,
                                          ConversionService conversionService,
                                          CRLVerifierService crlVerifierService,
                                          CredentialsAlertService alertService,
                                          UITruststoreService truststoreService,
                                          ConfigurationService configurationService) {
        super(mUserDao, mCredentialDao, conversionService, crlVerifierService, truststoreService, configurationService, alertService);
    }

    @Override
    public Authentication authenticate(Authentication authenticationToken)
            throws AuthenticationException {

        Authentication authentication = null;
        // PreAuthentication token for the rest service certificate authentication
        LOG.debug("Authenticate authentication token type: [{}]", authenticationToken.getClass());
        if (authenticationToken instanceof UILoginAuthenticationToken) {
            authentication = authenticateByUsernamePassword((UILoginAuthenticationToken) authenticationToken);
        }
        return authentication;
    }

    public Authentication authenticateByUsernamePassword(UILoginAuthenticationToken auth)
            throws AuthenticationException {

        long startTime = Calendar.getInstance().getTimeInMillis();

        String username = auth.getName();
        String userCredentialToken = auth.getCredentials().toString();

        DBCredential credential;
        try {
            Optional<DBCredential> dbCredential = mCredentialDao.findUsernamePasswordCredentialForUsernameAndUI(username);
            if (!dbCredential.isPresent() || !dbCredential.get().getUser().isActive()) {
                LOG.debug("User with username does not exists [{}], continue with next authentication provider");
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, "Username does not exits", username);
                delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
                throw BAD_CREDENTIALS_EXCEPTION;
            }
            credential = dbCredential.get();
        } catch (AuthenticationException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, username, ExceptionUtils.getRootCause(ex), ex);
            delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
            throw BAD_CREDENTIALS_EXCEPTION;

        } catch (RuntimeException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, username, ExceptionUtils.getRootCause(ex), ex);
            delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
            throw BAD_CREDENTIALS_EXCEPTION;
        }


        validateIfCredentialIsSuspended(credential, startTime);
        DBUser user = credential.getUser();


        SMPAuthority authority = SMPAuthority.getAuthorityByApplicationRole(user.getApplicationRole());
        // the webservice authentication does not support session set the session secret is null!
        SMPUserDetails userDetails = new SMPUserDetails(user,
                SecurityUtils.generatePrivateSymmetricKey(SMPEnvironmentProperties.getInstance().isSMPStartupInDevMode()),
                Collections.singletonList(authority));

        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(username, userCredentialToken,
                userDetails);
        try {
            if (!BCrypt.checkpw(userCredentialToken, credential.getValue())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, username);
                loginAttemptFailedAndThrowError(credential, true, startTime);
            }
            credential.setSequentialLoginFailureCount(0);
            credential.setLastFailedLoginAttempt(null);
            mCredentialDao.update(credential);
        } catch (IllegalArgumentException ex) {
            // password is not hashed
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, ex, username);
            loginAttemptFailedAndThrowError(credential, true, startTime);
        }
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, user.getApplicationRole());
        return smpAuthenticationToken;
    }


    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: [{}]." , auth);
        boolean supportAuthentication = auth.equals(UILoginAuthenticationToken.class);
        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: [{}].", auth);
        }
        return supportAuthentication;
    }
}
