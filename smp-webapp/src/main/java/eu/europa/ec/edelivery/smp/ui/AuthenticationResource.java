package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.auth.*;
import eu.europa.ec.edelivery.smp.data.ui.LoginRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.utils.SMPCookieWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.*;
import static eu.europa.ec.edelivery.smp.utils.SMPCookieWriter.SESSION_COOKIE_NAME;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY)
public class AuthenticationResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AuthenticationResource.class);
    public static final String RELATIVE_BASE_ENTRY = "../../../#/";

    protected SMPAuthenticationService authenticationService;

    protected SMPAuthorizationService authorizationService;

    private ConfigurationService configurationService;

    private CsrfTokenRepository csrfTokenRepository;

    SMPCookieWriter smpCookieWriter;

    @Autowired
    public AuthenticationResource(SMPAuthenticationService authenticationService
            , SMPAuthorizationService authorizationService
            , ConfigurationService configurationService
            , SMPCookieWriter smpCookieWriter
            , CsrfTokenRepository csrfTokenRepository
            , UIUserService uiUserService) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.configurationService = configurationService;
        this.smpCookieWriter = smpCookieWriter;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @PostMapping(value = "authentication")
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public UserRO authenticate(@RequestBody LoginRO loginRO, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("Authenticating user [{}]", loginRO.getUsername());
        // reset session id token and the Csrf Token at login
        recreatedSessionCookie(request, response);
        CsrfToken csfrToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csfrToken, request, response);

        UILoginAuthenticationToken authentication = (UILoginAuthenticationToken) authenticationService.authenticate(loginRO.getUsername(),
                loginRO.getPassword());
        SMPUserDetails user = authentication.getUserDetails();

        return authorizationService.getUserData(user.getUser());
    }

    @DeleteMapping(value = "authentication")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Logging out user for the session");
        authenticationService.logout(request, response);
    }

    /**
     * Resource is protected with CAS authentication. If user was successfully.
     * User is able to access the resource only if is SSO authenticates exists in SMP user table with appropriate roles.
     * Redirect to main page as authenticated user.
     *
     * @return Redirection object.
     */
    @GetMapping(value = "cas")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public RedirectView authenticateCAS() {
        LOG.debug("Authenticating cas");
        // if user was able to access resource - redirect back to main page
        return new RedirectView(RELATIVE_BASE_ENTRY);
    }

    @GetMapping(value = "user")
    @Secured({S_AUTHORITY_TOKEN_SYSTEM_ADMIN, S_AUTHORITY_TOKEN_USER})
    public UserRO getUser() {
        return authorizationService.getLoggedUserData();
    }


    /**
     * set cookie parameters https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie
     *
     * @param request
     * @param response
     */
    public void recreatedSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        // recreate session id  (first make sure it exists)
        String sessionId = request.getSession(true).getId();
        smpCookieWriter.writeCookieToResponse(SESSION_COOKIE_NAME,
                sessionId,
                configurationService.getSessionCookieSecure(),
                configurationService.getSessionCookieMaxAge(),
                configurationService.getSessionCookiePath(),
                configurationService.getSessionCookieSameSite(),
                request, response
        );
    }
}
