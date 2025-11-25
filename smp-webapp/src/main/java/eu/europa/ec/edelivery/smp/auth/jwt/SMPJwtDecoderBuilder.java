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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Because Nimbus.PublicKeyJwtDecoderBuilder support only RS256 algorithm, this is a custom builder that supports  algorithms
 * HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512, EdDSA for Public key configuration.
 * When using Issuer location or JWKS URI, the EdDSA is not supported!
 * A builder for creating {@link NimbusJwtDecoder} instances based on a public key and JWS signing algorithm.
 * The class is heavily based on  {@link NimbusJwtDecoder#withPublicKey} from org.springframework.security:spring-security-oauth2-jose:6.5.3
 *
 * @author Joze Rihtarsic
 * @since 5.8
 *
 */
public class SMPJwtDecoderBuilder {
    private static final String OIDC_METADATA_PATH = "/.well-known/openid-configuration";
    private static final Logger LOG = LoggerFactory.getLogger(SMPJwtDecoderBuilder.class);
    private static final JOSEObjectTypeVerifier<SecurityContext> JWT_TYPE_VERIFIER = new DefaultJOSEObjectTypeVerifier<>(
            JOSEObjectType.JWT, null);

    private static final JOSEObjectTypeVerifier<SecurityContext> NO_TYPE_VERIFIER = (header, context) -> {
    };

    private JWSAlgorithm jwsAlgorithm;

    private JOSEObjectTypeVerifier<SecurityContext> typeVerifier = NO_TYPE_VERIFIER;

    private PublicKey key;

    private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;
    // If  jwkSetUri
    URL jwkSetUri;
    URL issuerLocation;
    TrustManager[] trustManagers;

    /**
     * Creates a new instance
     *
     * @param key          the public key used to verify the JWT's signature
     * @param jwsAlgorithm the JWS algorithm used to sign the JWT
     */
    protected SMPJwtDecoderBuilder(PublicKey key, JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(key, "key cannot be null");
        Assert.notNull(jwsAlgorithm, "key jwsAlgorithm cannot be null");
        this.jwsAlgorithm = jwsAlgorithm;
        this.key = key;
        this.jwtProcessorCustomizer = (processor) -> {
        };
    }

    /**
     * Creates a new instance based on JWKS or Issuer location. Please note that EdDSA keys are not y
     * yet supported by using dynamic fetch of JWT signer key!
     *
     * @param path         the URL pointing to the JWK Set used to verify the JWT's signature
     * @param jwsAlgorithm the JWS algorithm used to sign the JWT
     */
    private SMPJwtDecoderBuilder(boolean isJWKS, URL path, JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(path, "path (jwks or issuer location) cannot be empty");
        Assert.notNull(jwsAlgorithm, "key jwsAlgorithm cannot be null");
        this.jwsAlgorithm = jwsAlgorithm;
        if (isJWKS) {
            this.jwkSetUri = path;
        } else {
            this.issuerLocation = path;
        }
        this.jwtProcessorCustomizer = (processor) -> {
        };
    }

    public static SMPJwtDecoderBuilder withPublicKey(PublicKey key, JWSAlgorithm jwsAlgorithm) {
        return new SMPJwtDecoderBuilder(key, jwsAlgorithm);
    }

    public static SMPJwtDecoderBuilder withJwkSetUri(URL jwksUri, JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(jwksUri, "jwksUri cannot be empty");
        Assert.notNull(jwsAlgorithm, "jwsAlgorithm cannot be null");
        return new SMPJwtDecoderBuilder(true, jwksUri, jwsAlgorithm);
    }

    public static SMPJwtDecoderBuilder withJWKIssuerLocation(URL jwkIssuerLocation, JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(jwkIssuerLocation, "jwkIssuerLocation cannot be empty");
        Assert.notNull(jwsAlgorithm, "jwsAlgorithm cannot be null");
        return new SMPJwtDecoderBuilder(false, jwkIssuerLocation, jwsAlgorithm);
    }


    /**
     * Whether to use Nimbus's typ header verification. This is {@code false} by
     * default to allow type at-jwt from rfc9068
     */
    public SMPJwtDecoderBuilder validateType(boolean shouldValidateTypHeader) {
        this.typeVerifier = shouldValidateTypHeader ? JWT_TYPE_VERIFIER : NO_TYPE_VERIFIER;
        return this;
    }

    public SMPJwtDecoderBuilder truststoreManagers(TrustManager[] trustManagers) {
        this.trustManagers = trustManagers;
        return this;
    }

    /**
     * Use the given {@link Consumer} to customize the {@link JWTProcessor
     * ConfigurableJWTProcessor} before passing it to the build
     * {@link NimbusJwtDecoder}.
     *
     * @param jwtProcessorCustomizer the callback used to alter the processor
     * @return a {@link NimbusJwtDecoder.PublicKeyJwtDecoderBuilder} for further configurations
     * @since 5.4
     */
    public SMPJwtDecoderBuilder jwtProcessorCustomizer(
            Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
        Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
        this.jwtProcessorCustomizer = jwtProcessorCustomizer;
        return this;
    }

    JWKSource<SecurityContext> jwkSource() {
        if (this.jwkSetUri == null) {
            readDataFromIssuerLocation();
        }

        if (this.trustManagers == null) {
            LOG.info("JWKS is set but trustManagers is null, use default/system mlts settings to retrieve url data");
            return JWKSourceBuilder.create(this.jwkSetUri)
                    .refreshAheadCache(false)
                    .rateLimited(false)
                    .build();
        }

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new SMPRuntimeException(ErrorMessageType.INTERNAL, e);
        }

        DefaultResourceRetriever retriever = new DefaultResourceRetriever(500,
                500, 51200,
                true,
                sslContext.getSocketFactory());

        return JWKSourceBuilder.create(this.jwkSetUri, retriever)
                .refreshAheadCache(false)
                .rateLimited(false)
                .build();
    }

    private void readDataFromIssuerLocation() {
        LOG.info("Retrieving JWKS from issuer location: {}", this.issuerLocation);
        try {
            String body = getOidcMetadata(this.issuerLocation);
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            Map<?, ?> jsonMap = mapper.readValue(body, Map.class);
            String jwksUriString = (String) jsonMap.get("jwks_uri");
            this.jwkSetUri = java.util.Optional.ofNullable(jwksUriString).map(s -> {
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new SMPRuntimeException(ErrorMessageType.UNAUTHORIZED_INVALID_BEARER_TOKEN, e);
                }
            }).orElseThrow(() -> new SMPRuntimeException(ErrorMessageType.UNAUTHORIZED_INVALID_BEARER_TOKEN));
            LOG.info("Retrieved JWKS URI: {}", this.jwkSetUri);
        } catch (URISyntaxException | IOException e) {
            throw new SMPRuntimeException(ErrorMessageType.UNAUTHORIZED_INVALID_BEARER_TOKEN, e);
        }
    }

    protected String getOidcMetadata(URL issuerLocation) throws IOException, URISyntaxException {
        URL metadataUrl = issuerLocation.toURI().resolve(issuerLocation.getPath() + OIDC_METADATA_PATH).toURL();
        String body;
        int status;
        if (this.trustManagers != null) {
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, this.trustManagers, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new SMPRuntimeException(ErrorMessageType.INTERNAL, e);
            }
            HttpsURLConnection conn = (HttpsURLConnection) metadataUrl.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);
            conn.setRequestMethod("GET");
            status = conn.getResponseCode();
            try (InputStream is = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is)) {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[4096];
                int n;
                while ((n = isr.read(buf)) != -1) {
                    sb.append(buf, 0, n);
                }
                body = sb.toString();
            }
        } else {
            RequestEntity<Void> request = RequestEntity.get(metadataUrl.toURI()).build();
            ResponseEntity<String> response = SMPJwtDecoderRestTemplate.exchange(request, String.class);
            status = response.getStatusCodeValue();
            body = response.getBody();
        }
        if (status < 200 || status >= 300 || body == null) {
            LOG.error("Failed to retrieve OIDC metadata from {}: status {}, body {}", metadataUrl, status, body);
            throw new SMPRuntimeException(ErrorMessageType.UNAUTHORIZED_INVALID_BEARER_TOKEN);
        }
        return body;
    }

    protected JWSKeySelector<SecurityContext> jwsKeySelector() {
        if (this.key != null) {
            new SingleKeyJWSKeySelector<>(this.jwsAlgorithm, this.key);
        } else if (jwkSetUri != null || issuerLocation != null) {
            return new JWSVerificationKeySelector<>(jwsAlgorithm, jwkSource());
        }
        return null;
    }

    JWTProcessor<SecurityContext> processor() {
        JWSKeySelector<SecurityContext> jwsKeySelector = jwsKeySelector();
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSTypeVerifier(this.typeVerifier);
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        jwtProcessor.setJWSVerifierFactory(new SMPJWSVerifierFactory());
        // Spring Security validates the claim set no need to validate it  from Nimbus
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
        this.jwtProcessorCustomizer.accept(jwtProcessor);
        return jwtProcessor;
    }

    /**
     * Build the configured {@link NimbusJwtDecoder}.
     *
     * @return the configured {@link NimbusJwtDecoder}
     */
    public NimbusJwtDecoder build() {
        return new NimbusJwtDecoder(processor());
    }
}