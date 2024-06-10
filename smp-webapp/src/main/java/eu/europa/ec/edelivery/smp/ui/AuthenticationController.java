/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationService;
import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.auth.UILoginAuthenticationToken;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.ui.CredentialRequestResetRO;
import eu.europa.ec.edelivery.smp.data.ui.CredentialResetRO;
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

import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN;
import static eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority.S_AUTHORITY_TOKEN_USER;
import static eu.europa.ec.edelivery.smp.utils.SMPCookieWriter.SESSION_COOKIE_NAME;

/**
 * The AuthenticationController class is a REST controller that provides endpoints for user authentication actions as
 * login, logout, logged user data retrieval, request credential reset.
 *
 * @author Sebastian-Ion TINCU
 * @since 4.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY)
public class AuthenticationController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AuthenticationController.class);
    public static final String RELATIVE_BASE_ENTRY = "../../../#/";

    protected SMPAuthenticationService authenticationService;

    protected SMPAuthorizationService authorizationService;

    private final ConfigurationService configurationService;

    private final CsrfTokenRepository csrfTokenRepository;

    SMPCookieWriter smpCookieWriter;

    @Autowired
    public AuthenticationController(SMPAuthenticationService authenticationService
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

    @PostMapping(value = ResourceConstants.PATH_ACTION_AUTHENTICATION)
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

        return authorizationService.getUserData(user.getUser(), authentication.getAuthorities());
    }

    /**
     * Request reset of the credentials. The method generates a reset token and sends an email to the user.
     *
     * @param requestResetRO - the request object containing the credential name and type
     */
    @PostMapping(value = ResourceConstants.PATH_ACTION_RESET_CREDENTIAL_REQUEST )
    public void requestResetCredentials(@RequestBody CredentialRequestResetRO requestResetRO) {
        LOG.debug("credentialRequestResetRO  [{}]", requestResetRO.getCredentialName());
        if (requestResetRO.getCredentialType() == CredentialType.USERNAME_PASSWORD) {
            authenticationService.requestResetUsername(requestResetRO.getCredentialName());
        } else {
            LOG.warn("Invalid or null credential type [{}] not supported for reset!",
                    requestResetRO.getCredentialType());
            throw new IllegalArgumentException("Invalid request!");

        }
    }

    /**
     * Reset the credentials. The method validates the reset token and updates the credentials.
     *
     * @param resetRO - the reset object containing the credential name, type, reset token and new credential value
     */
    @PostMapping(value = ResourceConstants.PATH_ACTION_RESET_CREDENTIAL )
    public void resetCredentials(@RequestBody CredentialResetRO resetRO) {
        LOG.debug("credentialResetRO  [{}]", resetRO.getCredentialName());
        if (resetRO.getCredentialType() == CredentialType.USERNAME_PASSWORD) {

            authenticationService.resetUsernamePassword(resetRO.getCredentialName(),
                    resetRO.getResetToken(),
                    resetRO.getCredentialValue());
        } else {
            LOG.warn("Invalid or null credential type [{}] not supported for reset!",
                    resetRO.getCredentialType());
            throw new IllegalArgumentException("Invalid request!");

        }
    }

    @DeleteMapping(value = ResourceConstants.PATH_ACTION_AUTHENTICATION)
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
