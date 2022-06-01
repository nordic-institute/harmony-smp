package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.config.SMPSecurityConstants;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static eu.europa.ec.edelivery.smp.utils.SMPCookieWriter.CSRF_COOKIE_NAME;
import static eu.europa.ec.edelivery.smp.utils.SMPCookieWriter.SESSION_COOKIE_NAME;

/**
 * The UI authentication services for login ,logout, retrieving current session user etc.. The services are intended for
 * stateful UI service calls.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Service
public class SMPAuthenticationService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationService.class);

    private final AuthenticationManager authenticationManager;

    public SMPAuthenticationService(@Qualifier(SMPSecurityConstants.SMP_UI_AUTHENTICATION_MANAGER_BEAN) AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Transactional(noRollbackFor = AuthenticationException.class)
    public Authentication authenticate(String username, String password) {
        LOG.debug("Authenticate: [{}]", username);
        UILoginAuthenticationToken token = new UILoginAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            LOG.debug("Cannot perform logout: no user is authenticated");
            return;
        }
        LOG.info("Logging out user [{}]", auth.getName());
        new CookieClearingLogoutHandler(SESSION_COOKIE_NAME, CSRF_COOKIE_NAME).logout(request, response, null);
        LOG.info("Cleared cookies");
        new SecurityContextLogoutHandler().logout(request, response, auth);
        LOG.info("Logged out");
    }
}