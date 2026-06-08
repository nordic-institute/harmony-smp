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

import com.nimbusds.jose.JWSAlgorithm;
import eu.europa.ec.edelivery.smp.auth.enums.SMPAutomationAuthenticationTypes;
import eu.europa.ec.edelivery.smp.auth.jwt.validators.CertificateBindValidator;
import eu.europa.ec.edelivery.smp.auth.jwt.validators.NotEmptyClaimValidator;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.EdECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.*;

/**
 *
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
     */
    @Bean
    public JwtDecoder jwtDecoder(ConfigurationService configurationService, UITruststoreService truststoreService) {
        if (!configurationService.getAutomationAuthenticationTypes().contains(SMPAutomationAuthenticationTypes.JWT)) {
            LOG.info("SMP JWT Authentication is not enabled. Skipping JWT decoder configuration.");
            return null; // No JWT authentication configured
        }
        NimbusJwtDecoder decoder = getNimbusJwtDecoderWithPublicKey(configurationService);
        if (decoder == null) {
            decoder = getNimbusJwtDecoderWithJwksUri(configurationService, truststoreService);
        }
        if (decoder == null) {
            decoder = getNimbusJwtDecoderWithIssuerLocation(configurationService, truststoreService);
        }
        if (decoder == null) {
            LOG.error("Can not configure JWT authentication. Missing JWT public key or JWKS_URI or Issuer location");
            return null;
        }

        // Combine validators: default + your custom validator
        JwtValidators.AtJwtBuilder atJwtBuilder = JwtValidators.createAtJwtValidator();
        if (isNotBlank(configurationService.getJWTIssuer())) {
            atJwtBuilder.issuer(configurationService.getJWTIssuer());
        }
        if (isNotBlank(configurationService.getJWTAudience())) {
            atJwtBuilder.audience(configurationService.getJWTAudience());
        }
        if (configurationService.isJwtMTLSCertificateBoundRequired()) {
            LOG.info("SMP JWT MTLS Certificate bound is required.");
            // Add custom validator to check for the presence of the "cnf" claim - validate that claim is present
            atJwtBuilder.validators((v) -> v.put("cnf", new CertificateBindValidator()));
        }

        // Add custom validator to check for the presence of the "client_id" claim - validate that claim is present
        atJwtBuilder.validators((v)
                -> v.put("client_id", new NotEmptyClaimValidator("client_id")));

        try {
            OAuth2TokenValidator<Jwt> validator = atJwtBuilder.build();
            decoder.setJwtValidator(validator);
            return decoder;
        } catch (IllegalArgumentException ex) {
            LOG.error("Illegal JWT configuration [{}]. JWT decoder not configured!", ExceptionUtils.getRootCauseMessage(ex));
        }
        return null;
    }

    private static NimbusJwtDecoder getNimbusJwtDecoderWithIssuerLocation(ConfigurationService configurationService, UITruststoreService truststoreService) {
        URL issuerLocationUri = configurationService.getJWTIssuerLocationUri();
        String sigJWTAlg = configurationService.getJWTSignatureAlgorithm();

        if (StringUtils.isBlank(sigJWTAlg)) {
            LOG.debug("SMP JWT Signature Key is blank, skipping JWT decoder configuration based on public key.");
            return null;
        }

        JWSAlgorithm signatureAlgorithm = JWSAlgorithm.parse(sigJWTAlg);
        if (issuerLocationUri != null) {
            LOG.info("Initiate JWT decoder using JWKS_URI [{}]", issuerLocationUri);
            try {
                return SMPJwtDecoderBuilder.withJWKIssuerLocation(issuerLocationUri, signatureAlgorithm)
                        .validateType(false)  // Disable type validation to allow custom claims at-jwt from  rfc9068
                        .truststoreManagers(truststoreService.getTrustManagers())
                        .build();
            } catch (SMPRuntimeException ex) {
                LOG.error("Error configuring JWT decoder with issuer location [{}]: [{}]", issuerLocationUri, ExceptionUtils.getRootCauseMessage(ex));
            }
        }
        return null;
    }

    private static NimbusJwtDecoder getNimbusJwtDecoderWithJwksUri(ConfigurationService configurationService, UITruststoreService truststoreService) {
        URL jwksUri = configurationService.getJWTJwksUri();
        String sigJWTAlg = configurationService.getJWTSignatureAlgorithm();

        if (StringUtils.isBlank(sigJWTAlg)) {
            LOG.debug("SMP JWT Signature Key is blank, skipping JWT decoder configuration based on jwksUri [{}].", jwksUri);
            return null;
        }

        JWSAlgorithm signatureAlgorithm = JWSAlgorithm.parse(sigJWTAlg);
        if (jwksUri != null) {
            LOG.info("Initiate JWT decoder using JWKS_URI [{}] and algorithm [{}]", jwksUri, sigJWTAlg);
            try {
                return SMPJwtDecoderBuilder.withJwkSetUri(jwksUri, signatureAlgorithm)
                        .validateType(false)  // Disable type validation to allow custom claims at-jwt from  rfc9068
                        .truststoreManagers(truststoreService.getTrustManagers())
                        .build();
            } catch (SMPRuntimeException ex) {
                LOG.error("Error configuring JWT decoder with JWKS location [{}]: [{}]", jwksUri, ExceptionUtils.getRootCauseMessage(ex));
            }
        }
        return null;
    }

    private static NimbusJwtDecoder getNimbusJwtDecoderWithPublicKey(ConfigurationService configurationService) {
        String jwtSignatureKey = configurationService.getJWTSignatureKey();
        if (StringUtils.isBlank(jwtSignatureKey)) {
            LOG.debug("SMP JWT Signature Key is blank, skipping JWT decoder configuration based on public key.");
            return null;
        }
        String sigJWTAlg = configurationService.getJWTSignatureAlgorithm();
        if (StringUtils.isBlank(sigJWTAlg)) {
            LOG.debug("SMP JWT Signature algorithm is blank, skipping JWT decoder configuration based on public key.");
            return null;
        }
        JWSAlgorithm signatureAlgorithm = JWSAlgorithm.parse(sigJWTAlg);
        PublicKey publicKey;
        try {
            publicKey = loadPublicKey(jwtSignatureKey, sigJWTAlg);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            LOG.error("Error occurred while loading/parsing JWT Public Key", e);
            return null;
        }
        LOG.info("Initiate JWT decoder using public key with signature algorithm [{}]", sigJWTAlg);
        return SMPJwtDecoderBuilder.withPublicKey(publicKey, signatureAlgorithm)
                .validateType(false) // Disable type validation to allow custom claims at-jwt from  rfc9068
                .build();
    }

    /**
     * Configures the SMPBearerTokenAuthenticationConverter bean if JWT authentication is enabled.
     * This converter is responsible for converting bearer tokens into SMPAuthenticationToken.
     *
     * @param jwtDecoder                  the JwtDecoder bean, or null if JWT authentication is not enabled
     * @param credentialService           the service to access credentials for the authenticated user by client_id in the JWT token
     * @param domainDao                   the DAO for domain operations
     * @param smpExceptionLanguageService the SMP exception translator
     * @return a configured SMPBearerTokenAuthenticationConverter or null if JWT authentication is not enabled
     */
    @Bean
    public SMPBearerTokenAuthenticationConverter smpBearerTokenAuthenticationConverter(@Nullable JwtDecoder jwtDecoder,
                                                                                       CredentialService credentialService,
                                                                                       DomainDao domainDao,
                                                                                       SMPExceptionLanguageService smpExceptionLanguageService) {
        if (jwtDecoder == null) {
            LOG.info("SMP JWT Authentication is not enabled. Skipping SMPBearerTokenAuthenticationConverter configuration.");
            return null; // No JWT authentication configured
        }
        return new SMPBearerTokenAuthenticationConverter(jwtDecoder, credentialService, domainDao, smpExceptionLanguageService);
    }


    public static PublicKey loadPublicKey(String pemKey, String signatureAlgorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        switch (lowerCase(trim(signatureAlgorithm))) {
            case "rs256", "rs384", "rs512", "ps256", "ps384", "ps512" -> {
                return loadRSAPublicKey(pemKey);
            }
            case "es256", "es384", "es512" -> {
                return loadECPublicKey(pemKey);
            }
            case "eddsa", "ed25519", "ed448" -> {
                return loadEdPublicKey(pemKey, signatureAlgorithm);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported signature algorithm: [" + signatureAlgorithm + "]");

        }
    }

    /**
     * Loads an Ed25519  or Ed448 public key from a PEM formatted string.
     *
     * @param pemKey       the PEM formatted Ed25519/Ed448 public key
     * @param keyAlgorithm the key algorithm
     * @return the PublicKey object
     * @throws NoSuchAlgorithmException if the Ed25519 algorithm is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    protected static EdECPublicKey loadEdPublicKey(String pemKey, String keyAlgorithm) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // Decode the hex  string to get the binary DER representation
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (EdECPublicKey) KeyFactory.getInstance(keyAlgorithm).generatePublic(keySpec);
    }

    /**
     * Loads an EC public key from a PEM formatted string.
     *
     * @param pemKey the PEM formatted EC public key
     * @return the ECPublicKey object
     * @throws NoSuchAlgorithmException if the EC algorithm is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    protected static ECPublicKey loadECPublicKey(String pemKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(keySpec);
    }

    /**
     * Loads an RSA public key from a PEM formatted string.
     *
     * @param pemKey the PEM formatted RSA public key
     * @return the RSAPublicKey object
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    protected static RSAPublicKey loadRSAPublicKey(String pemKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);

    }
}
