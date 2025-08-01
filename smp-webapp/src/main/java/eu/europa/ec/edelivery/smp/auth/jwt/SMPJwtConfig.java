/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.auth.jwt;

import eu.europa.ec.edelivery.smp.auth.enums.SMPAutomationAuthenticationTypes;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * \
 * Configuration for JWT authorization. If JWT token authentication is enabled, the class provides the
 * JwtDecoder and a custom authentication converter for handling JWT tokens.
 * It also includes a custom claim validator to ensure that specific claims are present in the JWT.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Configuration
public class SMPJwtConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SMPJwtConfig.class);

    /**
     * Configures the JwtDecoder bean if JWT authentication is enabled in the configuration service.
     * It loads the RSA public key from the configuration and sets up the JWT decoder with custom validators.
     *
     * @param configurationService the service to access configuration settings
     * @return a configured JwtDecoder or null if JWT authentication is not enabled
     * @throws Exception if there is an error loading the RSA public key
     */
    @Bean
    public JwtDecoder jwtDecoder(ConfigurationService configurationService) throws Exception {
        if (!configurationService.getAutomationAuthenticationTypes().contains(SMPAutomationAuthenticationTypes.JWT)) {
            LOG.info("SMP JWT Authentication is not enabled. Skipping JWT decoder configuration.");
            return null; // No JWT authentication configured
        }
        String jwtSignatureKey = configurationService.getJWTSignatureKey();
        RSAPublicKey publicKey = loadRSAPublicKey(jwtSignatureKey);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.from(configurationService.getJWTSignatureAlgorithm());

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey)
                .signatureAlgorithm(signatureAlgorithm)
                .validateType(false) // Disable type validation to allow custom claims at-jwt from  rfc9068
                .build();
        // Combine validators: default + your custom validator
        JwtValidators.AtJwtBuilder atJwtBuilder = JwtValidators.createAtJwtValidator();
        if (StringUtils.isNotBlank(configurationService.getJWTIssuer())) {
            atJwtBuilder.issuer(configurationService.getJWTIssuer());
        }
        if (StringUtils.isNotBlank(configurationService.getJWTAudience())) {
            atJwtBuilder.audience(configurationService.getJWTAudience());
        }
        atJwtBuilder.validators((v) -> v.put("client_id", new NotNullClaimValidator("client_id")));
        OAuth2TokenValidator<Jwt> validator = atJwtBuilder.build();
        decoder.setJwtValidator(validator);

        return decoder;
    }

    /**
     * Configures the SMPBearerTokenAuthenticationConverter bean if JWT authentication is enabled.
     * This converter is responsible for converting bearer tokens into SMPAuthenticationToken.
     *
     * @param jwtDecoder        the JwtDecoder bean, or null if JWT authentication is not enabled
     * @param credentialService the service to access credentials for the authenticated user by client_id in the JWT token
     * @param domainDao         the DAO for domain operations
     * @return a configured SMPBearerTokenAuthenticationConverter or null if JWT authentication is not enabled
     */
    @Bean
    public SMPBearerTokenAuthenticationConverter smpBearerTokenAuthenticationConverter(@Nullable JwtDecoder jwtDecoder,
                                                                                       CredentialService credentialService,
                                                                                       DomainDao domainDao) {
        if (jwtDecoder == null) {
            LOG.info("SMP JWT Authentication is not enabled. Skipping SMPBearerTokenAuthenticationConverter configuration.");
            return null; // No JWT authentication configured
        }
        return new SMPBearerTokenAuthenticationConverter(jwtDecoder, credentialService, domainDao);
    }

    /**
     * Loads an RSA public key from a PEM formatted string.
     *
     * @param pemKey the PEM formatted RSA public key
     * @return the RSAPublicKey object
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public static RSAPublicKey loadRSAPublicKey(String pemKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);

    }


    /**
     * A custom OAuth2TokenValidator that checks if a specific claim is present in the JWT.
     * If the claim is not present, it returns an error indicating that the claim must have a value.
     */
    protected static final class NotNullClaimValidator implements OAuth2TokenValidator<Jwt> {

        private final String claimName;

        NotNullClaimValidator(String claimName) {
            this.claimName = claimName;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            if (StringUtils.isBlank(token.getClaim(this.claimName))) {
                return OAuth2TokenValidatorResult
                        .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, this.claimName + " must have a value",
                                "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure"));
            }
            return OAuth2TokenValidatorResult.success();
        }
    }
}
