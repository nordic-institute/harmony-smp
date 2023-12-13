/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

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
public class SMPAuthenticationProvider implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProvider.class);

    final CredentialService credentialService;

    @Autowired
    public SMPAuthenticationProvider(CredentialService credentialService) {

        this.credentialService = credentialService;
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
                LOG.warn("Unknown or null PreAuthenticatedAuthenticationToken principal type: [{}]", principal);
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
        return credentialService.authenticateByCertificateToken(principal);
    }


    public Authentication authenticateByAuthenticationToken(UsernamePasswordAuthenticationToken auth)
            throws AuthenticationException {

        return credentialService.authenticateByAuthenticationToken(auth.getName(), auth.getCredentials().toString());
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
