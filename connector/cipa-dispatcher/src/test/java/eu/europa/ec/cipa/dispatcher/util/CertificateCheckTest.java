package eu.europa.ec.cipa.dispatcher.util;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 16/01/2015.
 */
public class CertificateCheckTest {

    /**
     * Test the validation of the certificate chain
     * clientint21int2 has rootca1 in its root chain --> validation expected
     * clientint21int2 hasn't rootca2 in its root chain --> no validation expected
     *
     * @throws Exception
     */
    @Test
    public void testValidateKeyChain() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(this.getClass().getResourceAsStream("/keystore.jks"), "keystore".toCharArray());
        X509Certificate caCert = (X509Certificate) keyStore.getCertificate("cipa");
        X509Certificate rootCert = (X509Certificate) keyStore.getCertificate("root");

        // Verify the current certificate using the right root cert
        Assert.assertTrue(CertificateCheck.validateKeyChain(caCert, keyStore, rootCert));

        // Verify the current certificate using a wrong root cert
        X509Certificate wrongRootCert = (X509Certificate) keyStore.getCertificate("rootca2");
        Assert.assertFalse(CertificateCheck.validateKeyChain(caCert, keyStore, wrongRootCert));

    }
}
