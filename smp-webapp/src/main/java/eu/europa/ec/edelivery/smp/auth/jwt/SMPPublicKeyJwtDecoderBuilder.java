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

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;

import java.security.PublicKey;
import java.util.function.Consumer;

/**
 * Because Numbus.PublicKeyJwtDecoderBuilder support only RS256 algorithm, this is a custom builder that supports all algorithms
 * HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512, EdDSA
 * A builder for creating {@link NimbusJwtDecoder} instances based on a public key and JWS signing algorithm.
 * The class is heavily based on  {@link NimbusJwtDecoder#withPublicKey} from org.springframework.security:spring-security-oauth2-jose:6.5.3
 *
 * @author Joze Rihtarsic
 * @since 5.8
 *
 */
public class SMPPublicKeyJwtDecoderBuilder {

    private static final JOSEObjectTypeVerifier<SecurityContext> JWT_TYPE_VERIFIER = new DefaultJOSEObjectTypeVerifier<>(
            JOSEObjectType.JWT, null);

    private static final JOSEObjectTypeVerifier<SecurityContext> NO_TYPE_VERIFIER = (header, context) -> {
    };

    private JWSAlgorithm jwsAlgorithm;

    private JOSEObjectTypeVerifier<SecurityContext> typeVerifier = NO_TYPE_VERIFIER;

    private final PublicKey key;

    private Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer;

    public SMPPublicKeyJwtDecoderBuilder(PublicKey key, JWSAlgorithm jwsAlgorithm) {
        Assert.notNull(key, "key cannot be null");
        Assert.notNull(jwsAlgorithm, "key jwsAlgorithm cannot be null");
        this.jwsAlgorithm = jwsAlgorithm;
        this.key = key;
        this.jwtProcessorCustomizer = (processor) -> {
        };
    }

    /**
     * Whether to use Nimbus's typ header verification. This is {@code false} by
     * default to allow type at-jwt from rfc9068
     */
    public SMPPublicKeyJwtDecoderBuilder validateType(boolean shouldValidateTypHeader) {
        this.typeVerifier = shouldValidateTypHeader ? JWT_TYPE_VERIFIER : NO_TYPE_VERIFIER;
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
    public SMPPublicKeyJwtDecoderBuilder jwtProcessorCustomizer(
            Consumer<ConfigurableJWTProcessor<SecurityContext>> jwtProcessorCustomizer) {
        Assert.notNull(jwtProcessorCustomizer, "jwtProcessorCustomizer cannot be null");
        this.jwtProcessorCustomizer = jwtProcessorCustomizer;
        return this;
    }

    JWTProcessor<SecurityContext> processor() {
        JWSKeySelector<SecurityContext> jwsKeySelector = new SingleKeyJWSKeySelector<>(this.jwsAlgorithm, this.key);
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