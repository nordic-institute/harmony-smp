package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.*;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Locale.US;

/**
 * An AuthenticationProvider is an abstraction for fetching user information from a specific repository
 * (like a database, LDAP, custom third party source, etc. ). It uses the fetched user information to validate the supplied credentials.
 * The current Authentication provider is intented for the accounts supporting automated application functionalities .
 * The account are used in SMP for webservice access as application to application integration with SMP. Authentication provider supports following
 * {@link org.springframework.security.core.Authentication} implementation:
 * - {@link org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken} implementation using
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Component
public class SMPAuthenticationProvider  extends AbstracdtAuthenticationProvider  {

    protected static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION = new BadCredentialsException("Login failed; Invalid userID or password");
    protected static final BadCredentialsException SUSPENDED_CREDENTIALS_EXCEPTION = new BadCredentialsException("The user is suspended. Please try again later or contact your administrator.");


    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProvider.class);
    /**
     * thread safe validator
     */
    private static final ThreadLocal<DateFormat> dateFormatLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US));


    @Autowired
    public SMPAuthenticationProvider(UserDao userDao,
                                     CredentialDao credentialDao,
                                     @Lazy  ConversionService conversionService,
                                     CRLVerifierService crlVerifierService,
                                     UITruststoreService truststoreService,
                                     ConfigurationService configurationService,
                                     CredentialsAlertService alertService) {
        super(userDao, credentialDao, conversionService, crlVerifierService, truststoreService, configurationService, alertService);
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
            LOG.info("try to authentication Token: [{}] with user:[{}]", authenticationToken.getClass(), authenticationToken.getPrincipal());
            if (CasAuthenticationFilter.CAS_STATEFUL_IDENTIFIER.equalsIgnoreCase((String) authenticationToken.getPrincipal())
                    || CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER.equalsIgnoreCase((String) authenticationToken.getPrincipal())) {
                LOG.debug("Ignore CAS authentication and leave it to cas authentication module");
                return null;
            }
            authentication = authenticateByAuthenticationToken((UsernamePasswordAuthenticationToken) authenticationToken);
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

        X509Certificate x509Certificate = principal.getCertificate();
        String userToken = principal.getName();
        long startTime = Calendar.getInstance().getTimeInMillis();


        if (x509Certificate != null) {
            try {
                truststoreService.validateCertificateWithTruststore(x509Certificate);
            } catch (CertificateException e) {
                String message = "Certificate is not trusted!";
                LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, userToken, message
                        + " The cert chain is not in truststore or either subject regexp or allowed cert policies does not match");
                throw new BadCredentialsException(message);
            }
        }
        DBCredential credential;
        try {
            Optional<DBCredential> optCredential = mCredentialDao.findUserByCertificateId(userToken, true);
            if (!optCredential.isPresent() || !optCredential.get().getUser().isActive() ) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, userToken);
                //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
                // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
                delayResponse(CredentialType.CERTIFICATE, startTime);
                throw BAD_CREDENTIALS_EXCEPTION;
            }
            credential = optCredential.get();
        } catch (AuthenticationException ex) {
            throw ex;

        } catch (RuntimeException ex) {
            LOG.error("Database connection error", ex);
            throw new AuthenticationServiceException("Internal server error occurred while user authentication!");
        }

        DBCertificate certificate = credential.getCertificate();

        // check if certificate is valid
        Date currentDate = Calendar.getInstance().getTime();
        // this is legacy code because some setups does not have truststore configured
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

        validateCertificatePolicyMatchLegacy(userToken, principal.getPolicyOids());
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
        DBUser user = credential.getUser();
        SMPAuthority authority = SMPAuthority.getAuthorityByRoleName(user.getApplicationRole().apiName());
        // the webservice authentication does not support session set the session secret is null!
        SMPUserDetails userDetails = new SMPUserDetails(user, null, Collections.singletonList(authority));

        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(principal,
                userToken,
                userDetails);


        return smpAuthenticationToken;
    }


    /**
     * Method validates if the certificate contains one of allowed Certificate policy. At the moment it does not validates
     * the whole chain. Because in some configuration cases does not use the truststore
     *
     * @param certificateId
     * @throws CertificateException
     */
    protected void validateCertificatePolicyMatchLegacy(String certificateId, List<String> certPolicyList) throws AuthenticationServiceException {

        // allowed list
        List<String> allowedCertificatePolicyOIDList = configurationService.getAllowedCertificatePolicies();
        if (allowedCertificatePolicyOIDList == null || allowedCertificatePolicyOIDList.isEmpty()) {
            LOG.debug("Certificate policy is not configured. Skip Certificate policy validation!");
            return;
        }
        // certificate list
        if (certPolicyList.isEmpty()) {
            String excMessage = String.format("Certificate [%s] does not have CertificatePolicy extension.", certificateId);
            throw new AuthenticationServiceException(excMessage);
        }

        Optional<String> result = certPolicyList.stream().filter(allowedCertificatePolicyOIDList::contains).findFirst();
        if (result.isPresent()) {
            LOG.debug("Certificate [{}] is trusted with certificate policy [{}]",certificateId,  result.get());
            return;
        }
        String excMessage = String.format("Certificate policy verification failed. Certificate [%s] does not contain any of the mandatory policy: [%s]", certificateId, allowedCertificatePolicyOIDList);
        throw new AuthenticationServiceException(excMessage);
    }

    public Authentication authenticateByAuthenticationToken(UsernamePasswordAuthenticationToken auth)
            throws AuthenticationException {

        String authenticationTokenId = auth.getName();
        LOG.debug("Got authentication token:" + authenticationTokenId);
        String authenticationTokenValue = auth.getCredentials().toString();
        long startTime = Calendar.getInstance().getTimeInMillis();

        DBCredential credential;
        try {
            Optional<DBCredential> dbCredential = mCredentialDao.findAccessTokenCredentialForAPI(authenticationTokenId);

            if (!dbCredential.isPresent() || !dbCredential.get().getUser().isActive()) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, authenticationTokenId);
                //https://www.owasp.org/index.php/Authentication_Cheat_Sheet
                // Do not reveal the status of an existing account. Not to use UsernameNotFoundException
                delayResponse(CredentialType.ACCESS_TOKEN, startTime);
                throw BAD_CREDENTIALS_EXCEPTION;
            }
            credential = dbCredential.get();
        } catch (RuntimeException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, authenticationTokenId, ExceptionUtils.getRootCause(ex), ex);
            delayResponse(CredentialType.ACCESS_TOKEN, startTime);
            throw BAD_CREDENTIALS_EXCEPTION;

        }

        validateIfCredentialIsSuspended(credential, startTime);

        DBUser user = credential.getUser();

        try {
            if (!BCrypt.checkpw(authenticationTokenValue, credential.getValue())) {
                loginAttemptFailedAndThrowError(credential, true, startTime);
            }
            credential.setSequentialLoginFailureCount(0);
            credential.setLastFailedLoginAttempt(null);
            mCredentialDao.update(credential);
        } catch (java.lang.IllegalArgumentException ex) {
            // password is not hashed
            loginAttemptFailedAndThrowError(credential, true, startTime);
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, ex, authenticationTokenId);
        }
        SMPAuthority authority = SMPAuthority.getAuthorityByRoleName(user.getApplicationRole().apiName());
        // the webservice authentication does not support session set the session secret is null!
        SMPUserDetails userDetails = new SMPUserDetails(user, null, Collections.singletonList(authority));

        SMPAuthenticationToken smpAuthenticationToken = new SMPAuthenticationToken(authenticationTokenId,
                authenticationTokenValue,
                userDetails);

        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, authenticationTokenId, authority.getRole());
        return smpAuthenticationToken;

    }

    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: [{}].", auth);
        boolean supportAuthentication = auth.equals(UsernamePasswordAuthenticationToken.class) || auth.equals(PreAuthenticatedAuthenticationToken.class);
        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: [{}].", auth);
        }
        return supportAuthentication;
    }
}
