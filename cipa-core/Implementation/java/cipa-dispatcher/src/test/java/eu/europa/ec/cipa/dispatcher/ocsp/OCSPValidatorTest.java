package eu.europa.ec.cipa.dispatcher.ocsp;

import eu.europa.ec.cipa.dispatcher.util.ConfigurationListener;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.*;
import java.security.cert.*;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OCSPValidator.class})
public class OCSPValidatorTest {

    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        new ConfigurationListener().contextInitialized(null);
    }

    /**
     * Mock that the OCSP validation returned a valid response
     * @throws Exception
     */
    @Test
    public void testCheckOk() throws Exception {
        PKIXCertPathValidatorResult pKIXCertPathValidatorResult = PowerMockito.mock(PKIXCertPathValidatorResult.class);
        CertPathValidator certPathValidator = PowerMockito.mock(CertPathValidator.class);
        TrustAnchor trustAnchor = PowerMockito.mock(TrustAnchor.class);
        PowerMockito.mockStatic(CertPathValidator.class);
        PowerMockito.when(CertPathValidator.getInstance(Mockito.anyString())).thenReturn(certPathValidator);
        PowerMockito.when(certPathValidator.validate((CertPath) Mockito.anyObject(), (CertPathParameters) Mockito.anyObject())).thenReturn(pKIXCertPathValidatorResult);
        PowerMockito.when(pKIXCertPathValidatorResult.getTrustAnchor()).thenReturn(trustAnchor);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(this.getClass().getResourceAsStream("/keystore.jks"), "keystore".toCharArray());
        X509Certificate caCert = (X509Certificate) keyStore.getCertificate("cipa");
        X509Certificate rootCert = (X509Certificate) keyStore.getCertificate("root");

        // the ocsp response is valid
        PowerMockito.when(trustAnchor.getTrustedCert()).thenReturn(rootCert);
        assertTrue(OCSPValidator.check(caCert, rootCert, "http://pki-ocsp.symauth.com:80"));
    }

    /**
     *  Mock that the OCSP validation returned an invalid response (trusted anchor is null)
     * @throws Exception
     */
    @Test
    public void testCheckNok() throws Exception {
        PKIXCertPathValidatorResult pKIXCertPathValidatorResult = PowerMockito.mock(PKIXCertPathValidatorResult.class);
        CertPathValidator certPathValidator = PowerMockito.mock(CertPathValidator.class);
        TrustAnchor trustAnchor = PowerMockito.mock(TrustAnchor.class);
        PowerMockito.mockStatic(CertPathValidator.class);
        PowerMockito.when(CertPathValidator.getInstance(Mockito.anyString())).thenReturn(certPathValidator);
        PowerMockito.when(certPathValidator.validate((CertPath) Mockito.anyObject(), (CertPathParameters) Mockito.anyObject())).thenReturn(pKIXCertPathValidatorResult);
        PowerMockito.when(pKIXCertPathValidatorResult.getTrustAnchor()).thenReturn(trustAnchor);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(this.getClass().getResourceAsStream("/keystore.jks"), "keystore".toCharArray());
        X509Certificate caCert = (X509Certificate) keyStore.getCertificate("cipa");
        X509Certificate rootCert = (X509Certificate) keyStore.getCertificate("root");
        assertFalse(OCSPValidator.check(caCert, rootCert, "http://pki-ocsp.symauth.com:80"));
    }

    /**
     * The root certificate that is provided is not the correct one, we expect the OCSP validation to fail
     */
    @Test
    public void testCheckWrongChain() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(this.getClass().getResourceAsStream("/keystore.jks"), "keystore".toCharArray());
            X509Certificate caCert = (X509Certificate) keyStore
                    .getCertificate("cipa");
            X509Certificate otherRootCert = (X509Certificate) keyStore
                    .getCertificate("rootca2");
            assertFalse(OCSPValidator.check(caCert, otherRootCert,
                    "http://pki-ocsp.symauth.com:80"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("an unexpected exception occurred");
        }
    }
}
