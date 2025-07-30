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
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Predicate;

@Configuration
public class SMPJwtConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SMPJwtConfig.class);

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


    @Bean
    public SMPBearerTokenAuthenticationConverter smpBearerTokenAuthenticationConverter(@Nullable JwtDecoder jwtDecoder,
                                                                                       CredentialService credentialService,
                                                                                       DomainDao domainDao) throws Exception {
        if (jwtDecoder == null) {
            LOG.info("SMP JWT Authentication is not enabled. Skipping SMPBearerTokenAuthenticationConverter configuration.");
            return null; // No JWT authentication configured
        }
        return new SMPBearerTokenAuthenticationConverter(jwtDecoder, credentialService, domainDao);
    }

    public static RSAPublicKey loadRSAPublicKey(String pemKey) throws Exception {
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);

    }


    private static final class NotNullClaimValidator implements OAuth2TokenValidator<Jwt> {

        private final String claimName;

        NotNullClaimValidator(String claimName) {
            this.claimName = claimName;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            if (token.getClaim(this.claimName) == null) {
                return OAuth2TokenValidatorResult
                        .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, this.claimName + " must have a value",
                                "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure"));
            }
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2TokenValidator<Jwt> isEqualTo(String value) {
            return and(satisfies((jwt) -> StringUtils.isNotBlank(jwt.getClaim(this.claimName))));
        }

        OAuth2TokenValidator<Jwt> satisfies(Predicate<Jwt> predicate) {
            return and((jwt) -> {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, this.claimName + " is not valid",
                        "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure");
                if (predicate.test(jwt)) {
                    return OAuth2TokenValidatorResult.success();
                }
                return OAuth2TokenValidatorResult.failure(error);
            });
        }

        OAuth2TokenValidator<Jwt> and(OAuth2TokenValidator<Jwt> that) {
            return (jwt) -> {
                OAuth2TokenValidatorResult result = validate(jwt);
                return (result.hasErrors()) ? result : that.validate(jwt);
            };
        }

    }
}
