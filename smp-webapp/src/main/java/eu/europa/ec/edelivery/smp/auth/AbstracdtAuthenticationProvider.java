package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertSuspensionMomentEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

abstract class AbstracdtAuthenticationProvider implements AuthenticationProvider {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(AbstracdtAuthenticationProvider.class);
    protected static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION = new BadCredentialsException("Login failed; Invalid userID or password");
    private static final BadCredentialsException SUSPENDED_CREDENTIALS_EXCEPTION = new BadCredentialsException("The user is suspended. Please try again later or contact your administrator.");
    final UserDao mUserDao;
    final CredentialDao mCredentialDao;
    final ConversionService conversionService;
    final CRLVerifierService crlVerifierService;
    final UITruststoreService truststoreService;
    final ConfigurationService configurationService;
    final CredentialsAlertService alertService;

    protected AbstracdtAuthenticationProvider(UserDao mUserDao, CredentialDao mCredentialDao, ConversionService conversionService, CRLVerifierService crlVerifierService, UITruststoreService truststoreService, ConfigurationService configurationService, CredentialsAlertService alertService) {
        this.mUserDao = mUserDao;
        this.mCredentialDao = mCredentialDao;
        this.conversionService = conversionService;
        this.crlVerifierService = crlVerifierService;
        this.truststoreService = truststoreService;
        this.configurationService = configurationService;
        this.alertService = alertService;
    }

    public void delayResponse(CredentialType credentialType, long startTime) {
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

    public void loginAttemptFailedAndThrowError(DBCredential credential, boolean notYetSuspended, long startTime) {

        CredentialType credentialType = credential.getCredentialType();
        credential.setSequentialLoginFailureCount(credential.getSequentialLoginFailureCount() != null ? credential.getSequentialLoginFailureCount() + 1 : 1);
        credential.setLastFailedLoginAttempt(OffsetDateTime.now());
        mCredentialDao.update(credential);
        String username = credential.getUser().getUsername();
        LOG.securityWarn(SMPMessageCode.SEC_INVALID_USER_CREDENTIALS, username,
                credential.getName(), credential.getCredentialType(),
                credential.getCredentialTarget());

        boolean isUserSuspended = credential.getSequentialLoginFailureCount() >= getLoginMaxAttempts(credentialType);
        if (isUserSuspended) {
            LOG.info("User [{}] failed sequential attempt exceeded the max allowed attempts [{}]!", username, getLoginMaxAttempts(credentialType));
            // at notYetSuspended alert is sent for all settings AT_LOGON, WHEN_BLOCKED
            if (notYetSuspended ||
                    getAlertBeforeUserSuspendedAlertMoment() == AlertSuspensionMomentEnum.AT_LOGON) {
                alertService.alertCredentialsSuspended(credential.getUser(), credential.getCredentialType());
            }
        } else {
            // always invoke the method. The method handles the smp.alert.user.login_failure.enabled
            alertService.alertCredentialVerificationFailed(credential.getUser(), credential.getCredentialType());
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
    public void validateIfCredentialIsSuspended(DBCredential credential, long startTime) {

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

    public Integer getLoginMaxAttempts(CredentialType credentialType) {
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

    public Integer getLoginSuspensionTimeInSeconds(CredentialType credentialType) {
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

    public AlertSuspensionMomentEnum getAlertBeforeUserSuspendedAlertMoment() {
        // the same for all credential types
        return configurationService.getAlertBeforeUserSuspendedAlertMoment();
    }

    public Integer getLoginFailDelayInMilliSeconds(CredentialType credentialType) {
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
