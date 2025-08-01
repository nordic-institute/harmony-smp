/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2025 - 2025 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth.jwt;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter that transforms a BearerTokenAuthenticationToken into an SMPAuthenticationToken.
 * It validates the JWT token, extracts the necessary claims, and creates an SMPAuthenticationToken
 * with the user's details and authorities.
 * <p>
 * This class is used in the context of DomiSMP  authentication,
 *  for handling JWT bearer tokens.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
public class SMPBearerTokenAuthenticationConverter implements Converter<BearerTokenAuthenticationToken, SMPAuthenticationToken> {
    private static final Logger LOG = LoggerFactory.getLogger(SMPBearerTokenAuthenticationConverter.class);
    protected static final BadCredentialsException UNAUTHORIZED_INVALID_BEARER_TOKEN = new BadCredentialsException(ErrorCode.UNAUTHORIZED_INVALID_BEARER_TOKEN.getMessage());

    private String principalClaimName = "client_id"; // Default claim name for the principal, can be overridden

    protected final JwtDecoder jwtDecoder;
    protected final CredentialService credentialService;
    protected final DomainDao domainDao;

    public SMPBearerTokenAuthenticationConverter(JwtDecoder jwtDecoder,
                                                 CredentialService credentialService,
                                                 DomainDao domainDao) {
        Assert.notNull(jwtDecoder, "jwtDecoder cannot be null");
        Assert.notNull(credentialService, "credentialService cannot be null");
        Assert.notNull(domainDao, "domainDao cannot be null");
        this.jwtDecoder = jwtDecoder;
        this.credentialService = credentialService;
        this.domainDao = domainDao;
    }

    /**
     * Converts a BearerTokenAuthenticationToken to an SMPAuthenticationToken.
     * This method extracts the JWT from the BearerTokenAuthenticationToken,
     * validates it, and creates an SMPAuthenticationToken with the necessary details.
     *
     * @param authBearer The BearerTokenAuthenticationToken to convert
     * @return An SMPAuthenticationToken containing the authenticated user's details
     */
    @Override
    public final SMPAuthenticationToken convert(BearerTokenAuthenticationToken authBearer) {
        LOG.info("Converting BearerTokenAuthenticationToken to SMPAuthenticationToken: [{}]", authBearer.getToken());
        Jwt jwt = this.getJwt(authBearer);
        if (StringUtils.isBlank(jwt.getSubject())) {
            LOG.debug("Failed to authenticate since the JWT subject is empty");
            throw UNAUTHORIZED_INVALID_BEARER_TOKEN;
        }
        List<SMPAuthority> authorities = getGrantedAuthorities(jwt);
        String principalClaimValue = jwt.getClaimAsString(getPrincipalClaimName());
        String claimScope = jwt.getClaim("scope");
        if (StringUtils.isBlank(claimScope)) {
            LOG.warn("JWT does not contain 'scope' claim");
            throw new AuthenticationServiceException("JWT does not contain 'scope' claim");
        }
        // Split the scope claim into a list of scopes
        List<String> scopes = Arrays.asList(claimScope.split(" "));
        SMPUserDetails userDetails = new SMPUserDetails(null,
                SecurityUtils.generatePrivateSymmetricKey(true),
                authorities, scopes);
        userDetails.setJwtAuthenticated(true);

        return new SMPAuthenticationToken(principalClaimValue, jwt, userDetails);
    }


    /**
     * Extracts and decodes the JWT from the BearerTokenAuthenticationToken.
     * If the JWT is invalid, it throws an InvalidBearerTokenException or AuthenticationServiceException.
     *
     * @param bearer The BearerTokenAuthenticationToken containing the JWT
     * @return The decoded Jwt object
     */
    private Jwt getJwt(BearerTokenAuthenticationToken bearer) {
        LOG.info("Decoding JWT token: [{}]", bearer.getToken());
        try {
            return this.jwtDecoder.decode(bearer.getToken());
        } catch (BadJwtException failed) {
            LOG.info("Failed to authenticate since the JWT was invalid [{}]", failed.getMessage());
            throw new InvalidBearerTokenException(failed.getMessage(), failed);
        } catch (JwtException failed) {
            LOG.info("Failed to authenticate since the JWT was invalid: [{}]", failed.getMessage());
            throw new AuthenticationServiceException(failed.getMessage(), failed);
        }
    }

    /**
     * Converts the JWT claims into a list of GrantedAuthorities.
     * It checks the 'scope' claim in the JWT and verifies if it contains valid domain scopes.
     * If valid, it returns a list with the WS_USER authority; otherwise, it throws an AuthenticationServiceException.
     *
     * @param jwt The decoded JWT containing user claims
     * @return A list of GrantedAuthorities based on the JWT claims
     */
    public List<SMPAuthority> getGrantedAuthorities(Jwt jwt) {
        LOG.info("Converting JWT to GrantedAuthorities: {}", jwt.getClaims());
        //  get scop claim as string and split it into a list
        String scopeClaim = jwt.getClaimAsString("scope");
        if (StringUtils.isBlank(scopeClaim)) {
            String message = "JWT does not contain 'scope' claim";
            LOG.warn("Failed to authenticate since the JWT was invalid: [{}]", message);
            throw new AuthenticationServiceException(message);
        }
        List<String> scopes = Arrays.asList(scopeClaim.split(" "));
        LOG.info("JWT contains scopes: [{}]", String.join(", ", scopes));
        List<String> domainScopes = getDomainScopes();

        // if any scope contains a domain scopes the request is authorized
        if (scopes.stream().anyMatch(domainScopes::contains)) {
            LOG.info("JWT contains valid domain scope");
            return Collections.singletonList(SMPAuthority.S_AUTHORITY_WS_USER);
        }

        //
        String message = "JWT with scopes [" + scopeClaim +
                "] not contain valid domain scope. Valid scopes are: [" + String.join(", ", domainScopes + "]");
        LOG.warn("Failed to authenticate since the JWT was invalid: [{}]", message);
        throw new AuthenticationServiceException(message);
    }

    /**
     * Retrieves all domain scopes from the database.
     * It fetches all domains and maps them to their domain codes.
     *
     * @return A list of domain codes representing the valid domain scopes
     */
    public List<String> getDomainScopes() {
        return domainDao.getAllDomains().stream()
                .map(DBDomain::getDomainCode)
                .collect(Collectors.toList());
    }

    public String getPrincipalClaimName() {
        return principalClaimName;
    }

    public void setPrincipalClaimName(String principalClaimName) {
        this.principalClaimName = principalClaimName;
    }
}
