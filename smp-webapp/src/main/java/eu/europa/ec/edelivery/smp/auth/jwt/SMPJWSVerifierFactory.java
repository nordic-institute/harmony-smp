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

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import org.apache.commons.lang3.Strings;

import java.security.Key;
import java.security.interfaces.EdECPublicKey;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SMPJWSVerifierFactory extends DefaultJWSVerifierFactory {

    /**
     * The supported JWS algorithms.
     */
    public static final Set<JWSAlgorithm> SMP_SUPPORTED_ALGORITHMS;


    static {
        Set<JWSAlgorithm> algs = new LinkedHashSet<>();
        algs.addAll(DefaultJWSVerifierFactory.SUPPORTED_ALGORITHMS);
        algs.addAll(EdDSAProvider.SUPPORTED_ALGORITHMS);
        // the Ed448 is not defined in the JWA spec, but is supported by Nimbus
        //algs.add(JWSAlgorithm.Ed448);

        SMP_SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }


    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {

        return SMP_SUPPORTED_ALGORITHMS;
    }

    @Override
    public JWSVerifier createJWSVerifier(final JWSHeader header, final Key key)
            throws JOSEException {


        if (super.supportedJWSAlgorithms().contains(header.getAlgorithm())) {
            return super.createJWSVerifier(header, key);
        }

        JWSVerifier verifier;
        if (EdDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {

            if (!(key instanceof EdECPublicKey)) {
                throw new KeyTypeException(EdECPublicKey.class);
            }
            EdECPublicKey edPublicKey = (EdECPublicKey) key;
            NamedParameterSpec paramSpec = (NamedParameterSpec) edPublicKey.getParams();
            String curveName = paramSpec.getName();
            boolean isED448 = Strings.CI.equals("Ed448", curveName);
            Curve crv = isED448 ? Curve.Ed448 : Curve.Ed25519;
            int keyLen = isED448 ? 57 : 32;  // Ed448 is 57 bytes;
            byte[] spki = edPublicKey.getEncoded();
            byte[] x = Arrays.copyOfRange(spki, spki.length - keyLen, spki.length);

            OctetKeyPair octetKey = new OctetKeyPair.Builder(crv, Base64URL.encode(x))
                    .keyIDFromThumbprint()
                    .build();
            verifier = new Ed25519Verifier(octetKey);
        } else {
            throw new JOSEException("Unsupported JWS algorithm: " + header.getAlgorithm());
        }

        // Apply JCA context, SecureRandom expensive and not needed for verification (iss #385)
        verifier.getJCAContext().setProvider(getJCAContext().getProvider());

        return verifier;
    }
}
