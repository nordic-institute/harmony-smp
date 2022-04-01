package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.security.cert.CertificateValidator;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Locale.US;

/**
 * Authentication provider for the UI authentication.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Import({SmpAppConfig.class})
@Component
public class SMPAuthenticationProviderForUI implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProviderForUI.class);

    final UserDao mUserDao;
    final CRLVerifierService crlVerifierService;
    final UITruststoreService truststoreService;
    final ConfigurationService configurationService;


    @Autowired
    public SMPAuthenticationProviderForUI(UserDao mUserDao, CRLVerifierService crlVerifierService, UITruststoreService truststoreService, ConfigurationService configurationService) {
        this.mUserDao = mUserDao;
        this.crlVerifierService = crlVerifierService;
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
        String role = user.getRole();
        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(username, userCredentialToken, Collections.singletonList(new SMPAuthority(role)), user);
        try {
            if (!BCrypt.checkpw(userCredentialToken, user.getPassword())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, username);
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }
        } catch (IllegalArgumentException ex) {
            // password is not hashed;
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, ex, username);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, role);
        return smpAuthenticationToken;
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
