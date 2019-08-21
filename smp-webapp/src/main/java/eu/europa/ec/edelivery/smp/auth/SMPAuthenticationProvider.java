package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static java.util.Locale.US;

public class SMPAuthenticationProvider implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AuthenticationProvider.class);


    /**
     * thread safe validator
     */
    private static final ThreadLocal<DateFormat> dateFormatLocal = ThreadLocal.withInitial( () -> {
        return  new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US);
    } );

    @Autowired
    UserDao mUserDao;

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
        } else  if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
            authentication = authenticateByUsernameToken((UsernamePasswordAuthenticationToken)authenticationToken);
        }


       // set anonymous token
       if (authentication == null) {
           authentication = new AnonymousAuthenticationToken(authenticationToken.toString(), authenticationToken.getPrincipal(),
                   Collections.singleton(SMPAuthority.S_AUTHORITY_ANONYMOUS));
           authentication.setAuthenticated(false);
       }
           /*

            if (principal instanceof PreAuthenticatedCertificatePrincipal) {
                // get principal
                LOG.info("Authenticate: PreAuthenticatedCertificatePrincipal");
                authentication = authenticateCertificate((PreAuthenticatedCertificatePrincipal) principal);
            } else if (principal instanceof PreAuthenticatedTokenPrincipal) {
                authentication = authenticateSecurityToken((PreAuthenticatedTokenPrincipal) principal);

            } else if (principal instanceof PreAuthenticatedAnonymousPrincipal) {
                authentication = new UnsecureAuthentication();
                authentication.setAuthenticated(configurationBusiness.isUnsecureLoginEnabled());
            }
            else {
                // unknown principal type
                authentication = new UnsecureAuthentication();
                authentication.setAuthenticated(false);
            }*/


        return authentication;
    }


    /**
     * Authenticate by certificate token got by BlueCoat or X509Certificate authentication)
     * @param principal - certificate principal
     * @return authentication value.
     */
    public Authentication authenticateByCertificateToken(PreAuthenticatedCertificatePrincipal principal) {
        mUserDao.findUserByCertificateId(principal.getName());

        DBUser user;
        String userToken = principal.getName();
        try {

            Optional<DBUser> oUsr = mUserDao.findUserByCertificateId(userToken);
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
        // check if certificate is valid
        Date currentDate = Calendar.getInstance().getTime();
        // validate  dates
        if (principal.getNotBefore().after(currentDate)) {
            throw new AuthenticationServiceException("Invalid certificate: NotBefore: " + dateFormatLocal.get().format(principal.getNotBefore()));
        } else if (principal.getNotAfter().before(currentDate)) {
            throw new AuthenticationServiceException("Invalid certificate:  NotAfter: " + dateFormatLocal.get().format(principal.getNotAfter()));
        }
        // check if issuer is on trust list.

        // Check crl list
        String url = user.getCertificate().getCrlUrl();
        if (url!= null) {

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

        // get user
        // test credentials
        // get and return  user roles.


        String username = auth.getName();
        String password = auth.getCredentials().toString();

        DBUser user;
        try {
            Optional<DBUser> oUsr = mUserDao.findUserByIdentifier(username);

            if (!oUsr.isPresent()) {
                LOG.securityWarn(SMPMessageCode.SEC_USER_NOT_EXISTS, username);
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
        String role = user.getRole();
        try {
            if (!BCrypt.checkpw(password, user.getPassword())) {
                LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, username);
                throw new BadCredentialsException("Login failed; Invalid userID or password");
            }

        } catch (java.lang.IllegalArgumentException ex) {
            // password is not hashed;
            LOG.securityWarn(SMPMessageCode.SEC_INVALID_PASSWORD, ex, username);
            throw new BadCredentialsException("Login failed; Invalid userID or password");
        }
        LOG.securityInfo(SMPMessageCode.SEC_USER_AUTHENTICATED, username, role);
        return new SMPAuthenticationToken(username, password, Collections.singletonList(new SMPAuthority(role)), user);
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
