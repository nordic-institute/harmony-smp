/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util.to_be_removed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.security.SecureRandom;

/**
 * A secure random generator initialized with another secure random generator.
 *
 * @author Philip Helger
 */
@Immutable
public final class VerySecureRandom
{
    private static final SecureRandom s_aSecureRandom;

    private static SecureRandom _getSecureRandomInstance ()
    {
        SecureRandom aSecureRandom;
        try
        {
            // IBM JCE
            // http://www.ibm.com/developerworks/java/jdk/security/50/secguides/JceDocs/api_users_guide.html
            aSecureRandom = SecureRandom.getInstance ("IBMSecureRandom", "IBMJCE");
        }
        catch (final Throwable t)
        {
            try
            {
                // SUN
                // http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html
                // http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
                aSecureRandom = SecureRandom.getInstance ("SHA1PRNG");
            }
            catch (final Throwable t2)
            {
                // Default
                aSecureRandom = new SecureRandom ();
            }
        }
        return aSecureRandom;
    }

    static
    {
        // Initialize SecureRandom
        // This is a lengthy operation, to be done only upon
        // initialization of the application. Especial with Java <= 1.5 this whole
        // block takes more or less forever.
        final SecureRandom aSecureRandom = _getSecureRandomInstance ();

        // Get 128 random bytes
        aSecureRandom.nextBytes (new byte [128]);

        // Create secure number generators with the random seed
        final byte [] aSeed = aSecureRandom.generateSeed (10);

        // Initialize main secure random
        s_aSecureRandom = _getSecureRandomInstance ();
        s_aSecureRandom.setSeed (aSeed);
    }

    private static final VerySecureRandom s_aInstance = new VerySecureRandom ();

    private VerySecureRandom ()
    {}

    @Nonnull
    public static SecureRandom getInstance ()
    {
        return s_aSecureRandom;
    }
}