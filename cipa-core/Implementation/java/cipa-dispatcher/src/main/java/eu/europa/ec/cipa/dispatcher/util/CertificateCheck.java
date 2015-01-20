package eu.europa.ec.cipa.dispatcher.util;

import eu.europa.ec.cipa.dispatcher.exception.CertRevokedException;
import eu.europa.ec.cipa.dispatcher.ocsp.OCSPValidator;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.security.*;
import java.security.cert.*;
import java.util.*;

public abstract class CertificateCheck {

    static final Logger s_aLogger = Logger.getLogger(CertificateCheck.class);

    public static void doCheck(X509Certificate cert, boolean ocps) throws CertificateNotYetValidException, CertificateExpiredException, CertRevokedException {
        // check the certificate is not expired
        try {
            cert.checkValidity();
        } catch (CertificateNotYetValidException e) {
            s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED + "The certificate used to sign is not yet valid");
            throw e;
        } catch (CertificateExpiredException e) {
            s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED + " The certificate used to sign has expired");
            throw e;
        }

        // OCSP validation of the certificate used to sign this message
        boolean valid = true;
        if (ocps) {
            try {
                valid = OCSPValidator.certificateValidate(cert);
            } catch (Exception e) {
                s_aLogger.error("Unable to validate the incoming certificate", e);
                valid = false;
            }
            if (!valid) {
                s_aLogger.error("The certificate used to sign has been revoked");
                throw new CertRevokedException();
            }
        }
    }

    /**
     * Validate keychain
     *
     * @param client   is the client X509Certificate
     * @param keyStore containing all trusted certificate
     * @param rootCert the expected root certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws java.security.KeyStoreException
     * @throws java.security.cert.CertificateException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.NoSuchProviderException
     */

    public static boolean validateKeyChain(X509Certificate client, KeyStore keyStore, X509Certificate rootCert)
            throws KeyStoreException, CertificateException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        X509Certificate[] certs = new X509Certificate[keyStore.size()];
        int i = 0;
        Enumeration<String> alias = keyStore.aliases();
        while (alias.hasMoreElements()) {
            certs[i++] = (X509Certificate) keyStore.getCertificate(alias.nextElement());
        }
        return validateKeyChain(client, rootCert, certs);
    }

    /**
     * Validate keychain
     *
     * @param client       is the client X509Certificate
     * @param rootCert     the expected root certificate
     * @param trustedCerts is Array containing all trusted X509Certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws CertificateException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */

    public static boolean validateKeyChain(X509Certificate client, X509Certificate rootCert, X509Certificate... trustedCerts) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        boolean found = false;
        int i = trustedCerts.length;
        TrustAnchor anchor;
        Set anchors;
        PKIXParameters params;
        while (!found && i > 0) {
            anchor = new TrustAnchor(trustedCerts[--i], null);
            anchors = Collections.singleton(anchor);
            params = new PKIXParameters(anchors);
            params.setRevocationEnabled(false);
            if (client.getIssuerX500Principal().equals(trustedCerts[i].getSubjectX500Principal())) {
                if (isSelfSigned(trustedCerts[i]) && rootCert.equals(trustedCerts[i])) {
                    // found root ca
                    found = true;
                    s_aLogger.info("validating root: " + trustedCerts[i].getSubjectX500Principal().getName());
                } else if (!client.equals(trustedCerts[i])) {
                    // find parent ca
                    s_aLogger.info("validating via: " + trustedCerts[i].getSubjectX500Principal().getName());
                    found = validateKeyChain(trustedCerts[i], rootCert, trustedCerts);
                }
            } else {
                s_aLogger.debug("Comparing client.getIssuerX500Principal(): " + client.getIssuerX500Principal() + " with trustedCerts[i].getSubjectX500Principal(): " + trustedCerts[i].getSubjectDN() + ". Not equals");
            }
        }
        return found;
    }

    /**
     * @param cert is X509Certificate that will be tested
     * @return true if cert is self signed, false otherwise
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */

    public static boolean isSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException sigEx) {
            return false;
        } catch (InvalidKeyException keyEx) {
            return false;
        }
    }

}