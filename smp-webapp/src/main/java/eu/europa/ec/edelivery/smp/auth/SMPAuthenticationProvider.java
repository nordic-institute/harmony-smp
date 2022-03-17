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
 * Authentication provider for the Accounts supporting automated application functionalities. The account are used in SMP for
 * webservice access as application to application integration with SMP. Authentication provider supports following
 *  {@link org.springframework.security.core.Authentication} implementation:
 *      - {@link org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken} implementation using
 *
 *
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Import({SmpAppConfig.class})
@Component
public class SMPAuthenticationProvider implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProvider.class);
    /**
     * thread safe validator
     */
    private static final ThreadLocal<DateFormat> dateFormatLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US));

    // generate dummyPassword hash just to mimic password validation to disable attacker to discover
    // usernames because of different response times if password or username is wrong
    private final String dummyPasswordHash;
    private final String dummyPassword;

    final UserDao mUserDao;
    final CRLVerifierService crlVerifierService;
    final UITruststoreService truststoreService;
    final ConfigurationService configurationService;

    @Autowired
    public SMPAuthenticationProvider(UserDao mUserDao, CRLVerifierService crlVerifierService, UITruststoreService truststoreService, ConfigurationService configurationService) {
        this.dummyPassword = UUID.randomUUID().toString();
        this.dummyPasswordHash = BCrypt.hashpw(dummyPassword, BCrypt.gensalt());
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
        if (authenticationToken instanceof PreAuthenticatedAuthenticationToken) {
            Object principal = authenticationToken.getPrincipal();
            if (principal instanceof PreAuthenticatedCertificatePrincipal) {
                authentication = authenticateByCertificateToken((PreAuthenticatedCertificatePrincipal) principal);
            } else {
                LOG.warn("Unknown or null PreAuthenticatedAuthenticationToken principal type: " + principal);
            }
        } else if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
            authentication = authenticateByUsernameToken((UsernamePasswordAuthenticationToken) authenticationToken);
        }

        // set anonymous token
        if (authentication == null) {
            authentication = new AnonymousAuthenticationToken(authenticationToken.toString(), authenticationToken.getPrincipal(),
                    Collections.singleton(SMPAuthority.S_AUTHORITY_ANONYMOUS));
            authentication.setAuthenticated(false);
        }

        return authentication;
    }


    /**
     * Authenticated using the X509Certificate or ClientCert header certificate)
     *
     * @param principal - certificate principal
     * @return authentication value.
     */
    public Authentication authenticateByCertificateToken(PreAuthenticatedCertificatePrincipal principal) {
        LOG.info("authenticateByCertificateToken:" + principal.getName());

        KeyStore truststore = truststoreService.getTrustStore();

        DBUser user;
        X509Certificate x509Certificate = principal.getCertificate();
        String userToken = principal.getName();

        if (truststore != null && x509Certificate != null) {
            CertificateValidator certificateValidator = new CertificateValidator(
                    null, truststore, null);
            try {
                certificateValidator.validateCertificate(x509Certificate);
            } catch (CertificateException e) {
                throw new BadCredentialsException("Certificate is not trusted!");
            }
        }

        try {

            Optional<DBUser> oUsr = mUserDao.findUserByCertificateId(userToken, true);
            if (!oUsr.isPresent()) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, userToken);
                //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
                // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }
            user = oUsr.get();
        } catch (AuthenticationException ex) {
            throw ex;

        } catch (RuntimeException ex) {
            LOG.error("Database connection error", ex);
            throw new AuthenticationServiceException("Internal server error occurred while user authentication!");
        }

        DBCertificate certificate = user.getCertificate();
        // check if certificate is valid
        Date currentDate = Calendar.getInstance().getTime();
        // validate  dates
        if (principal.getNotBefore() == null) {
            String msg = "Invalid certificate configuration: 'Not Before' value is missing!";
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, msg);
            throw new AuthenticationServiceException(msg);
        }

        if (principal.getNotAfter() == null) {
            String msg = "Invalid certificate configuration: 'Not After' value is missing!";
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, msg);
            throw new AuthenticationServiceException(msg);
        }

        if (principal.getNotAfter().before(currentDate)) {
            String msg = "Invalid certificate:  Not After: " + dateFormatLocal.get().format(principal.getNotAfter());
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, msg);
            throw new AuthenticationServiceException(msg);
        }
        // check if issuer or subject are in trusted list
        if (!(truststoreService.isSubjectOnTrustedList(principal.getSubjectOriginalDN())
                || truststoreService.isSubjectOnTrustedList(principal.getIssuerDN()))) {
            String msg = "Non of the Certificate: '" + principal.getSubjectOriginalDN() + "'" +
                    " or issuer: '" + principal.getIssuerDN() + "' are trusted!";
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, msg);
            throw new AuthenticationServiceException(msg);
        }
        // Check crl list
        String url = certificate.getCrlUrl();
        if (!StringUtils.isBlank(url)) {
            try {
                crlVerifierService.verifyCertificateCRLs(certificate.getSerialNumber(), url);
            } catch (CertificateRevokedException ex) {
                String msg = "Certificate: '" + principal.getSubjectOriginalDN() + "'" +
                        ", issuer: '" + principal.getIssuerDN() + "' is revoked!";
                LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, msg);
                throw new AuthenticationServiceException(msg);
            } catch (Throwable th) {
                String msg = "Error occurred while validating CRL for certificate!";
                LOG.error(SMPLogger.SECURITY_MARKER, msg + "Err: " + ExceptionUtils.getRootCauseMessage(th), th);
                throw new AuthenticationServiceException(msg);
            }
        }
        // get role
        String role = user.getRole();
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, userToken, role);
        SMPCertificateAuthentication authentication = new SMPCertificateAuthentication(principal, Collections.singletonList(new SMPAuthority(role)), user);

        authentication.setAuthenticated(true);
        return authentication;
    }


    public Authentication authenticateByUsernameToken(UsernamePasswordAuthenticationToken auth)
            throws AuthenticationException {

        String authenticationTokenId = auth.getName();
        String authenticationTokenValue = auth.getCredentials().toString();

        DBUser user;
        try {
            Optional<DBUser> oUsr = mUserDao.findUserByAuthenticationToken(authenticationTokenId);

            if (!oUsr.isPresent()) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, authenticationTokenId);
                //run validation on dummy password to achieve similar response time
                // as it would be if the password is invalid
                BCrypt.checkpw(dummyPassword, dummyPasswordHash);

                //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
                // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }

            user = oUsr.get();
        } catch (AuthenticationException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, authenticationTokenId, ExceptionUtils.getRootCause(ex), ex);
            throw ex;

        } catch (RuntimeException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, authenticationTokenId, ExceptionUtils.getRootCause(ex), ex);
            throw new AuthenticationServiceException("Internal server error occurred while user authentication!");

        }
        try {
            if (!BCrypt.checkpw(authenticationTokenValue, user.getPatValue())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, authenticationTokenId);
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }
        } catch (java.lang.IllegalArgumentException ex) {
            // password is not hashed;
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, ex, authenticationTokenId);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        String role = "WS_"+user.getRole();
        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(authenticationTokenId, authenticationTokenValue, Collections.singletonList(new SMPAuthority(role)), user);

        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, authenticationTokenId, role);
        return smpAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: " + auth);
        boolean supportAuthentication = auth.equals(UsernamePasswordAuthenticationToken.class) || auth.equals(PreAuthenticatedAuthenticationToken.class);
        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: " + auth);
        }
        return supportAuthentication;
    }
}
