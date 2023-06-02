package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.auth.UILoginAuthenticationToken;
import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertSuspensionMomentEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Locale.US;

@Service
public class CredentialService {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(CredentialService.class);
    protected static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION = new BadCredentialsException(ErrorCode.UNAUTHORIZED_INVALID_USERNAME_PASSWORD.getMessage());
    protected static final BadCredentialsException SUSPENDED_CREDENTIALS_EXCEPTION = new BadCredentialsException(ErrorCode.UNAUTHORIZED_CREDENTIAL_SUSPENDED.getMessage());
    final UserDao mUserDao;
    final CredentialDao mCredentialDao;
    final ConversionService conversionService;
    final CRLVerifierService crlVerifierService;
    final UITruststoreService truststoreService;
    final ConfigurationService configurationService;
    final CredentialsAlertService alertService;

    /**
     * thread safe validator
     */
    private static final ThreadLocal<DateFormat> dateFormatLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US));


    public CredentialService(UserDao mUserDao, CredentialDao mCredentialDao, ConversionService conversionService, CRLVerifierService crlVerifierService, UITruststoreService truststoreService, ConfigurationService configurationService, CredentialsAlertService alertService) {
        this.mUserDao = mUserDao;
        this.mCredentialDao = mCredentialDao;
        this.conversionService = conversionService;
        this.crlVerifierService = crlVerifierService;
        this.truststoreService = truststoreService;
        this.configurationService = configurationService;
        this.alertService = alertService;
    }

    @Transactional(noRollbackFor = {AuthenticationException.class, SMPRuntimeException.class, RuntimeException.class})
    public Authentication authenticateByUsernamePassword(String username, String userCredentialToken)
            throws AuthenticationException {

        long startTime = Calendar.getInstance().getTimeInMillis();
        LOG.info("authenticateByUsernamePassword: start [{}]", username);
        DBCredential credential;
        try {
            LOG.info("authenticateByUsernamePassword: get credentials [{}]", username);
            Optional<DBCredential> dbCredential = mCredentialDao.findUsernamePasswordCredentialForUsernameAndUI(username);
            if (!dbCredential.isPresent() || isNotValidCredential(dbCredential.get())) {
                LOG.debug("User with username does not exists [{}], continue with next authentication provider");
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, "Username does not exits", username);
                delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
                throw BAD_CREDENTIALS_EXCEPTION;
            }
            credential = dbCredential.get();
        } catch (RuntimeException ex) {
            LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_AUTHENTICATED, username, ExceptionUtils.getRootCause(ex), ex);
            delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
            throw BAD_CREDENTIALS_EXCEPTION;

        }
        LOG.info("authenticateByUsernamePassword: before validation [{}]", username);
        validateIfCredentialIsSuspended(credential, startTime);
        LOG.info("authenticateByUsernamePassword: Validated [{}]", username);
        DBUser user = credential.getUser();

        LOG.info("authenticateByUsernamePassword: get SMPAuthority [{}]", username);
        SMPAuthority authority = SMPAuthority.getAuthorityByApplicationRole(user.getApplicationRole());
        // the webservice authentication does not support session set the session secret is null!
        LOG.info("authenticateByUsernamePassword:create details [{}]", username);
        SMPUserDetails userDetails = new SMPUserDetails(user,
                SecurityUtils.generatePrivateSymmetricKey(SMPEnvironmentProperties.getInstance().isSMPStartupInDevMode()),
                Collections.singletonList(authority));
        LOG.info("authenticateByUsernamePassword:create UILoginAuthenticationToken [{}]", username);
        UILoginAuthenticationToken smpAuthenticationToken = new UILoginAuthenticationToken(username, userCredentialToken,
                userDetails);
        try {
            LOG.info("authenticateByUsernamePassword:validate security token [{}]", username);
            if (!BCrypt.checkpw(userCredentialToken, credential.getValue())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, username, credential.getName(), credential.getCredentialType(), credential.getCredentialTarget());
                loginAttemptFailedAndThrowError(credential, true, startTime);
            }
            LOG.info("authenticateByUsernamePassword:update values [{}]", username);
            credential.setSequentialLoginFailureCount(0);
            credential.setLastFailedLoginAttempt(null);
            LOG.info("authenticateByUsernamePassword:update values [{}]", username);
            //mCredentialDao.update(credential);
            //LOG.info("authenticateByUsernamePassword: done updating [{}]", username);
        } catch (IllegalArgumentException ex) {
            // password is not hashed
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, ex, username);
            loginAttemptFailedAndThrowError(credential, true, startTime);
        }
        LOG.info("authenticateByUsernamePassword: done updating [{}]", username);
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, user.getApplicationRole());
        return smpAuthenticationToken;
    }


    @Transactional(noRollbackFor = {AuthenticationException.class, BadCredentialsException.class, SMPRuntimeException.class})
    public Authentication authenticateByAuthenticationToken(String authenticationTokenId, String authenticationTokenValue)
            throws AuthenticationException {

        LOG.debug("Got authentication token: [{}]", authenticationTokenId);
        long startTime = Calendar.getInstance().getTimeInMillis();

        DBCredential credential;
        try {
            Optional<DBCredential> dbCredential = mCredentialDao.findAccessTokenCredentialForAPI(authenticationTokenId);

            if (!dbCredential.isPresent() || isNotValidCredential(dbCredential.get())) {
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

    protected boolean isNotValidCredential(DBCredential credential) {
        if (!credential.isActive()) {
            LOG.debug("User credential [{}] is not active", credential);
            return true;
        }
        if (!credential.getUser().isActive()) {
            LOG.debug("User credential [{}] is not valid because user is not active", credential);
            return true;
        }

        OffsetDateTime dateTimeNow = OffsetDateTime.now();
        if (credential.getActiveFrom() != null && dateTimeNow.isBefore(credential.getActiveFrom())) {
            LOG.debug("User credential [{}] is not yet valid active from [{}]", credential, credential.getActiveFrom());
            return true;
        }

        if (credential.getExpireOn() != null && dateTimeNow.isAfter(credential.getExpireOn())) {
            LOG.debug("User credential [{}] is expired from [{}]", credential, credential.getActiveFrom());
            return true;
        }
        return false;
    }

    @Transactional(noRollbackFor = {AuthenticationException.class, BadCredentialsException.class, SMPRuntimeException.class})
    public Authentication authenticateByCertificateToken(PreAuthenticatedCertificatePrincipal principal) {
        LOG.info("authenticateByCertificateToken:" + principal.getName());


        X509Certificate x509Certificate = principal.getCertificate();
        String certificateIdentifier = principal.getName();


        long startTime = Calendar.getInstance().getTimeInMillis();


        if (x509Certificate != null) {
            try {
                truststoreService.validateCertificateWithTruststore(x509Certificate);
            } catch (CertificateException e) {
                String message = "Certificate is not trusted! Error: " + ExceptionUtils.getRootCauseMessage(e);
                LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, message
                        + " The cert chain is not in truststore or either subject regexp or allowed cert policies does not match");
                throw new BadCredentialsException(message);
            }
        }
        DBCredential credential;
        try {
            Optional<DBCredential> optCredential = mCredentialDao.findUserByCertificateId(certificateIdentifier, true);
            if (!optCredential.isPresent() || isNotValidCredential(optCredential.get())) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, certificateIdentifier);
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
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, msg);
            throw new AuthenticationServiceException(msg);
        }

        if (principal.getNotAfter() == null) {
            String msg = "Invalid certificate configuration: 'Not After' value is missing!";
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, msg);
            throw new AuthenticationServiceException(msg);
        }

        if (principal.getNotAfter().before(currentDate)) {
            String msg = "Invalid certificate:  Not After: " + dateFormatLocal.get().format(principal.getNotAfter());
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, msg);
            throw new AuthenticationServiceException(msg);
        }

        // check if issuer or subject are in trusted list
        if (!(truststoreService.isSubjectOnTrustedList(principal.getSubjectOriginalDN())
                || truststoreService.isSubjectOnTrustedList(principal.getIssuerDN()))) {
            String msg = "Non of the Certificate: '" + principal.getSubjectOriginalDN() + "'" +
                    " or issuer: '" + principal.getIssuerDN() + "' are trusted!";
            LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, msg);
            throw new AuthenticationServiceException(msg);
        }

        validateCertificatePolicyMatchLegacy(certificateIdentifier, principal.getPolicyOids());
        // Check crl list
        String url = certificate.getCrlUrl();
        if (!StringUtils.isBlank(url)) {
            try {
                crlVerifierService.verifyCertificateCRLs(certificate.getSerialNumber(), url);
            } catch (CertificateRevokedException ex) {
                String msg = "Certificate: '" + principal.getSubjectOriginalDN() + "'" +
                        ", issuer: '" + principal.getIssuerDN() + "' is revoked!";
                LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, certificateIdentifier, msg);
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
                certificateIdentifier,
                userDetails);

        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, principal, authority.getRole());
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
            LOG.debug("Certificate [{}] is trusted with certificate policy [{}]", certificateId, result.get());
            return;
        }
        String excMessage = String.format("Certificate policy verification failed. Certificate [%s] does not contain any of the mandatory policy: [%s]", certificateId, allowedCertificatePolicyOIDList);
        throw new AuthenticationServiceException(excMessage);
    }


    protected void delayResponse(CredentialType credentialType, long startTime) {
        int delayInMS = getLoginFailDelayInMilliSeconds(credentialType) - (int) (Calendar.getInstance().getTimeInMillis() - startTime);
        if (delayInMS > 0) {
            try {
                LOG.debug("Delay response for [{}] ms to mask password/username login failures!", delayInMS);
                Thread.sleep(delayInMS);
            } catch (InterruptedException ie) {
                LOG.debug("Thread interrupted during sleep.", ie);
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void loginAttemptFailedAndThrowError(DBCredential credential, boolean notYetSuspended, long startTime) {

        CredentialType credentialType = credential.getCredentialType();
        credential.setSequentialLoginFailureCount(credential.getSequentialLoginFailureCount() != null ? credential.getSequentialLoginFailureCount() + 1 : 1);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        mCredentialDao.update(credential);
        String username = credential.getUser().getUsername();
        LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, username,
                credential.getName(),
                credential.getCredentialType(),
                credential.getCredentialTarget());

        boolean isUserSuspended = credential.getSequentialLoginFailureCount() >= getLoginMaxAttempts(credentialType);
        if (isUserSuspended) {
            LOG.info("User [{}] failed sequential attempt exceeded the max allowed attempts [{}]!", username, getLoginMaxAttempts(credentialType));
            // at notYetSuspended alert is sent for all settings AT_LOGON, WHEN_BLOCKED
            if (notYetSuspended ||
                    getAlertBeforeUserSuspendedAlertMoment() == AlertSuspensionMomentEnum.AT_LOGON) {
                alertService.alertCredentialsSuspended(credential);
            }
        } else {
            // always invoke the method. The method handles the smp.alert.user.login_failure.enabled
            alertService.alertCredentialVerificationFailed(credential);
        }
        delayResponse(credentialType, startTime);
        if (isUserSuspended) {
            throw SUSPENDED_CREDENTIALS_EXCEPTION;
        } else {
            throw BAD_CREDENTIALS_EXCEPTION;
        }

    }

    /**
     * Method tests if user account Suspended
     *
     * @param credential
     * @param startTime
     */
    protected void validateIfCredentialIsSuspended(DBCredential credential, long startTime) {

        String username = credential.getUser().getUsername();
        CredentialType credentialType = credential.getCredentialType();

        if (credential.getSequentialLoginFailureCount() == null
                || credential.getSequentialLoginFailureCount() < 0) {
            LOG.trace("User [{}] has no previous failed attempts for credential [{}:{}]", username, credentialType, credential.getName());
            return;
        }

        Integer maxLoginAttempts = getLoginMaxAttempts(credentialType);
        if (maxLoginAttempts == null || maxLoginAttempts < 0) {
            LOG.warn("Max login attempts is not set for credentialType [{}]!", credentialType);
            return;
        }

        if (credential.getLastFailedLoginAttempt() == null) {
            LOG.warn("User [{}] has failed attempts [{}] for credential [{}:{}] but null last Failed login attempt date!", username, credential.getLastFailedLoginAttempt(), credentialType, credential.getName());
            return;
        }
        // check if the last failed attempt is already expired. If yes just clear the attempts
        Integer logSuspension = getLoginSuspensionTimeInSeconds(credentialType);
        if (logSuspension != null && logSuspension > 0
                && ChronoUnit.SECONDS.between(credential.getLastFailedLoginAttempt(), OffsetDateTime.now()) > logSuspension) {
            LOG.warn("User [{}] for credential [{}:{}] suspension is expired! Clear failed login attempts and last failed login attempt", credential.getName(), credentialType, credential.getName());
            credential.setLastFailedLoginAttempt(null);
            credential.setSequentialLoginFailureCount(-1);
            mCredentialDao.update(credential);
            return;
        }

        if (credential.getSequentialLoginFailureCount() < maxLoginAttempts) {
            LOG.debug("User [{}] for credential [{}:{}]  failed login attempt [{}]! did not reach the max failed attempts [{}]", username, credentialType, credential.getName(), credential.getSequentialLoginFailureCount(), maxLoginAttempts);
            return;
        }
        LOG.securityWarn(SMPMessageCode.SEC_USER_SUSPENDED, credential.getName());
        loginAttemptFailedAndThrowError(credential, false, startTime);
    }

    protected Integer getLoginMaxAttempts(CredentialType credentialType) {
        switch (credentialType) {
            case USERNAME_PASSWORD:
                return configurationService.getLoginMaxAttempts();
            case ACCESS_TOKEN:
            case CERTIFICATE:
                return configurationService.getAccessTokenLoginMaxAttempts();
            default:
                LOG.debug("Unknown credential type [{}] - return max attempts for username password!", credentialType);
                return configurationService.getLoginMaxAttempts();
        }
    }

    protected Integer getLoginSuspensionTimeInSeconds(CredentialType credentialType) {
        switch (credentialType) {
            case USERNAME_PASSWORD:
                return configurationService.getLoginSuspensionTimeInSeconds();
            case ACCESS_TOKEN:
            case CERTIFICATE:
                return configurationService.getAccessTokenLoginSuspensionTimeInSeconds();
            default:
                LOG.debug("Unknown credential type [{}] - return LoginSuspensionTimeInSeconds for username password!", credentialType);
                return configurationService.getLoginSuspensionTimeInSeconds();
        }
    }

    protected AlertSuspensionMomentEnum getAlertBeforeUserSuspendedAlertMoment() {
        // the same for all credential types
        return configurationService.getAlertBeforeUserSuspendedAlertMoment();
    }

    protected Integer getLoginFailDelayInMilliSeconds(CredentialType credentialType) {
        // the same for all credential types
        switch (credentialType) {
            case USERNAME_PASSWORD:
                return configurationService.getLoginFailDelayInMilliSeconds();
            case ACCESS_TOKEN:
            case CERTIFICATE:
                return configurationService.getAccessTokenLoginFailDelayInMilliSeconds();
            default:
                LOG.debug("Unknown credential type [{}] - return LoginFailDelayInMilliSeconds for username password!", credentialType);
                return configurationService.getLoginFailDelayInMilliSeconds();
        }
    }
}
