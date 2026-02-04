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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.auth.jwt.SMPBearerTokenAuthenticationConverter;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
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
@Order(2)
public class SMPAuthenticationProvider implements AuthenticationProvider {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProvider.class);
    protected final CredentialService credentialService;
    protected final SMPBearerTokenAuthenticationConverter bearerTokenAuthenticationConverter;


    @Autowired
    public SMPAuthenticationProvider(CredentialService credentialService,  @Nullable SMPBearerTokenAuthenticationConverter  jwtAuthenticationConverter) {

        this.credentialService = credentialService;
        this.bearerTokenAuthenticationConverter = jwtAuthenticationConverter;
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
        }  else if (authenticationToken instanceof UsernamePasswordAuthenticationToken) {
            LOG.info("try to authentication Token: [{}] with user:[{}]", authenticationToken.getClass(), authenticationToken.getPrincipal());
            authentication = authenticateByAuthenticationToken((UsernamePasswordAuthenticationToken) authenticationToken);
        } else if (authenticationToken instanceof BearerTokenAuthenticationToken) {
            String token = ((BearerTokenAuthenticationToken) authenticationToken).getToken();
            String fingerprint = DigestUtils.sha256Hex(token).substring(0, 12);
            LOG.info("try to authentication Bearer Token: [{}] with token fingerprint:[{}]", authenticationToken.getClass(), fingerprint);
            authentication = authenticateByBareTokenAuthenticationToken((BearerTokenAuthenticationToken) authenticationToken);
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

    public Authentication authenticateByBareTokenAuthenticationToken(BearerTokenAuthenticationToken authBearer)
            throws AuthenticationException {
        if (this.bearerTokenAuthenticationConverter == null) {
            LOG.warn("SMP does not support Bearer token authentication. No SMPBearerTokenAuthenticationConverter is configured.");
            throw new AuthenticationServiceException("SMP does not support Bearer token authentication.");
        }
        // validate the token and return the authentication
        SMPAuthenticationToken token = this.bearerTokenAuthenticationConverter.convert(authBearer);
        if (token.getDetails() == null) {
            token.setDetails(authBearer.getDetails());
        }
        LOG.info("Authenticated token [{}]",  token);
        return token;
    }

    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: [{}].", auth);
        boolean supportAuthentication = auth.equals(UsernamePasswordAuthenticationToken.class)
                || auth.equals(PreAuthenticatedAuthenticationToken.class)
                || auth.equals(BearerTokenAuthenticationToken.class)
                ;

        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: [{}].", auth);
        }
        return supportAuthentication;
    }
}
