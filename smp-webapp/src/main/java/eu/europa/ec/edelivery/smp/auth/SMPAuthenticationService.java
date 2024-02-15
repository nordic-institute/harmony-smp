/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.config.SMPSecurityConstants;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.CredentialService;
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
import java.util.Calendar;

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
    private final CredentialService credentialService;

    public SMPAuthenticationService(@Qualifier(SMPSecurityConstants.SMP_UI_AUTHENTICATION_MANAGER_BEAN) AuthenticationManager authenticationManager,
                                    CredentialService credentialService) {
        this.authenticationManager = authenticationManager;
        this.credentialService = credentialService;
    }

    @Transactional(noRollbackFor = AuthenticationException.class)
    public Authentication authenticate(String username, String password) {
        LOG.debug("Authenticate: [{}]", username);
        UILoginAuthenticationToken token = new UILoginAuthenticationToken(username, password, null);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }


    /**
     * Method retrieves user credentials by username. First it validates if credentials have already active reset token
     * and if not it creates new one.
     *
     * @param username
     */
    public void requestResetUsername(String username) {
        LOG.info("requestResetUsername  [{}]", username);
        // retrieve user Optional credentials by username
        long startTime = Calendar.getInstance().getTimeInMillis();
        credentialService.requestResetUsername(username);
        // delay response to prevent timing attack
        credentialService.delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
    }

    public void resetUsernamePassword(String username, String resetToken, String newPassword) {
        LOG.info("resetUsernamePassword  [{}]", username);
        // retrieve user Optional credentials by username
        long startTime = Calendar.getInstance().getTimeInMillis();
        credentialService.resetUsernamePassword(username, resetToken, newPassword);
        credentialService.delayResponse(CredentialType.USERNAME_PASSWORD, startTime);
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
